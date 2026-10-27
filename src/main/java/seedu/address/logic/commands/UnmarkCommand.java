package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.List;

import seedu.address.commons.core.index.Index;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.person.Person;

/**
 * Unmarks the checkup of the contact at the specified displayed index.
 */
public class UnmarkCommand extends Command {
    public static final String COMMAND_WORD = "unmark";
    public static final String MESSAGE_USAGE = COMMAND_WORD
            + " INDEX: Unmarks a contact with a scheduled checkup appointment.\nExample: unmark 1";
    public static final String MESSAGE_INVALID_INDEX = "Invalid index.";
    public static final String MESSAGE_ALREADY_UNMARKED = "Unmarked contact cannot be unmarked again.";
    public static final String MESSAGE_WITHOUT_CHECKUP = "Contacts with no checkup appointments cannot be unmarked.";
    public static final String MESSAGE_SUCCESS = "Unmarked contact: %1$s";

    private final Index targetIndex;

    /**
     * Creates a command to unmark the contact at {@code targetIndex} in the displayed list.
     */
    public UnmarkCommand(Index targetIndex) {
        this.targetIndex = requireNonNull(targetIndex);
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        List<Person> displayedPersons = model.getFilteredPersonList();
        if (targetIndex.getZeroBased() >= displayedPersons.size()) {
            throw new CommandException(MESSAGE_INVALID_INDEX);
        }
        Person person = displayedPersons.get(targetIndex.getZeroBased());
        if (person.getCheckupAppointment().isEmpty()) {
            throw new CommandException(MESSAGE_WITHOUT_CHECKUP);
        }
        if (!person.isCheckupMarked()) {
            throw new CommandException(MESSAGE_ALREADY_UNMARKED);
        }
        model.setPerson(person, person.withUnmarkedCheckup());
        return new CommandResult(String.format(MESSAGE_SUCCESS, person.getName()));
    }

    @Override
    public boolean equals(Object other) {
        return other == this || other instanceof UnmarkCommand otherCommand
                && targetIndex.equals(otherCommand.targetIndex);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).add("targetIndex", targetIndex).toString();
    }
}
