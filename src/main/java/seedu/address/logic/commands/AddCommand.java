package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ADDRESS;
import static seedu.address.logic.parser.CliSyntax.PREFIX_FAMILY_EMAIL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_FAMILY_PHONE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PATIENT_EMAIL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PATIENT_PHONE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TAG;

import java.util.List;
import java.util.Optional;

import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.person.Person;

/**
 * Adds a person to the address book.
 */
public class AddCommand extends Command {

    public static final String COMMAND_WORD = "add";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Adds a person to CareBook. "
            + "Parameters: "
            + PREFIX_NAME + " NAME "
            + PREFIX_PATIENT_PHONE + " PATIENT_PHONE "
            + PREFIX_FAMILY_PHONE + " FAMILY_PHONE "
            + "[" + PREFIX_PATIENT_EMAIL + " PATIENT_EMAIL] "
            + "[" + PREFIX_FAMILY_EMAIL + " FAMILY_EMAIL] "
            + PREFIX_ADDRESS + " ADDRESS "
            + "[" + PREFIX_TAG + " PRIORITY]\n"
            + "Note: The '/' character is reserved for valid prefixes and is not allowed in any field value.\n"
            + "Separate each prefix from its value with a space.\n"
            + "Example: " + COMMAND_WORD + " "
            + PREFIX_NAME + " Aunty Rosalind "
            + PREFIX_PATIENT_PHONE + " 81268767 "
            + PREFIX_FAMILY_PHONE + " 90783658 "
            + PREFIX_ADDRESS + " Goonvale Street Block 2 #01-09";

    public static final String MESSAGE_SUCCESS = "New person added: %1$s";
    public static final String MESSAGE_DUPLICATE_PERSON = Messages.MESSAGE_DUPLICATE_PERSON;

    private final Person toAdd;

    /**
     * Creates an AddCommand to add the specified {@code Person}
     */
    public AddCommand(Person person) {
        requireNonNull(person);
        toAdd = person;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);

        requireNoDuplicate(toAdd, model.getAddressBook().getPersonList());
        model.addPerson(toAdd);
        return new CommandResult(String.format(MESSAGE_SUCCESS, Messages.format(toAdd)));
    }

    /**
     * Checks that {@code person} is not the same person as any of {@code otherPersons}. Archived persons count too.
     *
     * @throws CommandException if a person in {@code otherPersons} has the same patient contact details as
     *     {@code person}, or else the same name and address.
     */
    static void requireNoDuplicate(Person person, List<Person> otherPersons) throws CommandException {
        Optional<Person> sameContactDetails = otherPersons.stream()
                .filter(person::hasSamePatientContactDetails).findFirst();
        if (sameContactDetails.isPresent()) {
            throw new CommandException(Messages.withArchivedHint(MESSAGE_DUPLICATE_PERSON, sameContactDetails.get()));
        }

        Optional<Person> sameNameAndAddress = otherPersons.stream()
                .filter(person::hasSameNameAndAddress).findFirst();
        if (sameNameAndAddress.isPresent()) {
            throw new CommandException(
                    Messages.withArchivedHint(Person.MESSAGE_NAME_ADDRESS_CONFLICT, sameNameAndAddress.get()));
        }
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof AddCommand otherAddCommand)) {
            return false;
        }

        return toAdd.equals(otherAddCommand.toAdd);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("toAdd", toAdd)
                .toString();
    }
}
