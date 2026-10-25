package seedu.address.ui;

import java.util.Optional;
import java.util.stream.Collectors;

import seedu.address.model.person.Email;
import seedu.address.model.person.Person;

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
                + "\nPriority: " + formatPriority(person);
    }

    /**
     * Returns the email value or the placeholder for an absent optional email.
     */
    private static String formatEmail(Optional<Email> email) {
        return email.map(value -> value.value).orElse("Not provided");
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
