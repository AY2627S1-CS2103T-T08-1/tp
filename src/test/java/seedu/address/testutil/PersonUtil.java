package seedu.address.testutil;

import static seedu.address.logic.parser.CliSyntax.PREFIX_ADDRESS;
import static seedu.address.logic.parser.CliSyntax.PREFIX_EMAIL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_FAMILY_PHONE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PATIENT_PHONE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TAG;

import java.util.Set;

import seedu.address.logic.commands.AddCommand;
import seedu.address.logic.commands.EditCommand.EditPersonDescriptor;
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
        person.getEmail().ifPresent(email -> sb.append(PREFIX_EMAIL).append(" ").append(email.value).append(" "));
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
        descriptor.getEmail().ifPresent(email -> sb.append(PREFIX_EMAIL).append(" ").append(email.value).append(" "));
        descriptor.getAddress().ifPresent(address -> sb.append(PREFIX_ADDRESS).append(" ")
                .append(address.value).append(" "));
        if (descriptor.getTags().isPresent()) {
            Set<Tag> tags = descriptor.getTags().get();
            if (tags.isEmpty()) {
                sb.append(PREFIX_TAG);
            } else {
                tags.forEach(s -> sb.append(PREFIX_TAG).append(" ").append(s.tagName).append(" "));
            }
        }
        return sb.toString();
    }
}
