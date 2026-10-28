package seedu.address.ui;

import java.util.Optional;
import java.util.stream.Collectors;

import seedu.address.model.person.CheckupAppointment;
import seedu.address.model.person.Email;
import seedu.address.model.person.Person;
import seedu.address.model.person.Remark;

/**
 * Formats full contact details without abbreviating long values or omitting absent optional fields.
 */
final class PersonDetailsFormatter {
    private PersonDetailsFormatter() {
    }

    /**
     * Returns readable details, or no body text when no contact is selected.
     */
    static String format(Person person) {
        if (person == null) {
            return "";
        }
        return "Patient phone: " + person.getPatientNo().value
                + "\nFamily phone: " + person.getFamilyNo().value
                + "\nPatient email: " + formatEmail(person.getPatientEmail())
                + "\nFamily email: " + formatEmail(person.getFamilyEmail())
                + "\nAddress: " + person.getAddress().value
                + "\nPriority: " + formatPriority(person)
                + "\nCheckup appointment: " + formatAppointment(person.getCheckupAppointment())
                + "\nRemark: " + formatRemarks(person.getRemark());
    }

    /**
     * Returns the email value or the placeholder for an absent optional email.
     */
    private static String formatEmail(Optional<Email> email) {
        return email.map(value -> value.value).orElse("Not provided");
    }

    /**
     * Returns the scheduled date and time, or the placeholder for a contact without an appointment.
     */
    private static String formatAppointment(Optional<CheckupAppointment> appointment) {
        return appointment.map(CheckupAppointment::toString).orElse("Not scheduled");
    }

    /**
     * Returns all numbered care notes on separate lines, or the placeholder for a contact without notes.
     */
    private static String formatRemarks(Remark remark) {
        return remark.isEmpty() ? "None" : "\n" + String.join("\n", remark.getNumberedNotes());
    }

    /**
     * Returns priority names in a stable order, or the placeholder for an unassigned priority.
     */
    private static String formatPriority(Person person) {
        String priority = person.getTags().stream()
                .map(tag -> tag.tagName)
                .sorted()
                .collect(Collectors.joining(", "));
        return priority.isEmpty() ? "Not assigned" : priority;
    }
}
