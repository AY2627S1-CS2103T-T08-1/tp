package seedu.address.testutil;

import static seedu.address.logic.parser.CliSyntax.PREFIX_ADDRESS;
import static seedu.address.logic.parser.CliSyntax.PREFIX_FAMILY_EMAIL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_FAMILY_PHONE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PATIENT_EMAIL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PATIENT_PHONE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TAG;

import java.util.Optional;
import java.util.Set;

import seedu.address.logic.commands.AddCommand;
import seedu.address.logic.commands.EditCommand.EditPersonDescriptor;
import seedu.address.logic.parser.Prefix;
import seedu.address.model.person.Email;
import seedu.address.model.person.Person;
import seedu.address.model.tag.Tag;

/**
 * A utility class for Person.
 */
public class PersonUtil {

    /**
     * Returns an add command string for adding the {@code person}.
     */
    public static String getAddCommand(Person person) {
        return AddCommand.COMMAND_WORD + " " + getPersonDetails(person);
    }

    /**
     * Returns the part of command string for the given {@code person}'s details.
     */
    public static String getPersonDetails(Person person) {
        StringBuilder sb = new StringBuilder();
        sb.append(PREFIX_NAME).append(" ").append(person.getName().fullName).append(" ");
        sb.append(PREFIX_PATIENT_PHONE).append(" ").append(person.getPatientNo().value).append(" ");
        sb.append(PREFIX_FAMILY_PHONE).append(" ").append(person.getFamilyNo().value).append(" ");
        person.getPatientEmail().ifPresent(email -> sb.append(PREFIX_PATIENT_EMAIL).append(" ")
                .append(email.value).append(" "));
        person.getFamilyEmail().ifPresent(email -> sb.append(PREFIX_FAMILY_EMAIL).append(" ")
                .append(email.value).append(" "));
        sb.append(PREFIX_ADDRESS).append(" ").append(person.getAddress().value).append(" ");
        person.getTags().stream().forEach(
            s -> sb.append(PREFIX_TAG).append(" ").append(s.tagName).append(" ")
        );
        return sb.toString();
    }

    /**
     * Returns the part of command string for the given {@code EditPersonDescriptor}'s details.
     */
    public static String getEditPersonDescriptorDetails(EditPersonDescriptor descriptor) {
        StringBuilder sb = new StringBuilder();
        descriptor.getName().ifPresent(name -> sb.append(PREFIX_NAME).append(" ").append(name.fullName).append(" "));
        descriptor.getPatientNo().ifPresent(phone -> sb.append(PREFIX_PATIENT_PHONE).append(" ")
                .append(phone.value).append(" "));
        descriptor.getFamilyNo().ifPresent(fPhone -> sb.append(PREFIX_FAMILY_PHONE).append(" ")
                .append(fPhone.value).append(" "));
        appendEmail(sb, PREFIX_PATIENT_EMAIL, descriptor.isPatientEmailEdited(),
                descriptor.getPatientEmail());
        appendEmail(sb, PREFIX_FAMILY_EMAIL, descriptor.isFamilyEmailEdited(),
                descriptor.getFamilyEmail());
        descriptor.getAddress().ifPresent(address -> sb.append(PREFIX_ADDRESS).append(" ")
                .append(address.value).append(" "));
        descriptor.getTags().ifPresent(tags -> appendTags(sb, tags));
        return sb.toString();
    }

    /**
     * Appends {@code prefix} and {@code email} to {@code sb} if the email is edited.
     * A removed email is written as the prefix with no value.
     */
    private static void appendEmail(StringBuilder sb, Prefix prefix, boolean isEdited,
            Optional<Email> email) {
        if (!isEdited) {
            return;
        }
        sb.append(prefix).append(" ").append(email.map(value -> value.value).orElse("")).append(" ");
    }

    /**
     * Appends each tag in {@code tags} to {@code sb}, or a bare tag prefix if {@code tags} is empty.
     */
    private static void appendTags(StringBuilder sb, Set<Tag> tags) {
        if (tags.isEmpty()) {
            sb.append(PREFIX_TAG);
            return;
        }
        tags.forEach(tag -> sb.append(PREFIX_TAG).append(" ").append(tag.tagName).append(" "));
    }
}
