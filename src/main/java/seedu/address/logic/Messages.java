package seedu.address.logic;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import seedu.address.logic.parser.Prefix;
import seedu.address.model.person.Person;

/**
 * Container for user visible messages.
 */
public class Messages {

    public static final String MESSAGE_UNKNOWN_COMMAND = "Unknown command.";
    public static final String MESSAGE_INVALID_COMMAND_FORMAT = "Invalid command format!\n%1$s";
    public static final String MESSAGE_INVALID_PERSON_DISPLAYED_INDEX = "The person index provided is invalid.";
    public static final String MESSAGE_PERSONS_LISTED_OVERVIEW = "%1$d person(s) listed!";
    public static final String MESSAGE_DUPLICATE_FIELDS =
                "Multiple values specified for the following single-valued field(s): ";
    public static final String MESSAGE_LEGACY_EMAIL_PREFIX = "/email is no longer supported. "
            + "Use /pEmail for the patient's email or /fEmail for the family member's email.";

    /**
     * Returns an error message indicating the duplicate prefixes.
     */
    public static String getErrorMessageForDuplicatePrefixes(Prefix... duplicatePrefixes) {
        assert duplicatePrefixes.length > 0;

        String duplicateFields =
                Stream.of(duplicatePrefixes).distinct().map(Prefix::toString).collect(Collectors.joining(" "));

        return MESSAGE_DUPLICATE_FIELDS + duplicateFields;
    }

    /**
     * Formats the {@code person} for display to the user.
     */
    public static String format(Person person) {
        final StringBuilder builder = new StringBuilder();
        builder.append(person.getName())
                .append("; Patient Contact: ")
                .append(person.getPatientNo())
                .append("; Family Contact: ")
                .append(person.getFamilyNo());
        person.getPatientEmail().ifPresent(email -> builder.append("; Patient Email: ").append(email));
        person.getFamilyEmail().ifPresent(email -> builder.append("; Family Email: ").append(email));
        builder.append("; Address: ")
                .append(person.getAddress())
                .append("; Tags: ");
        person.getTags().forEach(builder::append);
        person.getCheckupAppointment()
                .ifPresent(appointment -> builder.append("; Checkup Appointment: ").append(appointment));
        if (!person.getRemark().isEmpty()) {
            builder.append("; Remark: ").append(person.getRemark());
        }
        return builder.toString();
    }

}
