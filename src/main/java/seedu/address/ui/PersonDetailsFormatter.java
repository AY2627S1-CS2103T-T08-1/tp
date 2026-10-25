package seedu.address.ui;

import java.util.stream.Collectors;

import seedu.address.model.person.Person;

/** Formats full contact details without abbreviating long values or omitting absent optional fields. */
final class PersonDetailsFormatter {
    private PersonDetailsFormatter() {
    }

    /** Returns readable details, or no body text when no contact is selected. */
    static String format(Person person) {
        if (person == null) {
            return "";
        }
        String priority = person.getTags().stream().map(tag -> tag.tagName).sorted()
                .collect(Collectors.joining(", "));
        return "Patient phone: " + person.getPatientNo().value
                + "\nFamily phone: " + person.getFamilyNo().value
                + "\nPatient email: " + person.getPatientEmail().map(email -> email.value).orElse("Not provided")
                + "\nFamily email: " + person.getFamilyEmail().map(email -> email.value).orElse("Not provided")
                + "\nAddress: " + person.getAddress().value
                + "\nPriority: " + (priority.isEmpty() ? "Not assigned" : priority);
    }
}
