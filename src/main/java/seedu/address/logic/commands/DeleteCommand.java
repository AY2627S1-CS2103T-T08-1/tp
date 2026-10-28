package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.List;

import seedu.address.commons.core.index.Index;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.person.Person;

/**
 * Deletes a person identified using its displayed index from the address book.
 */
public class DeleteCommand extends Command {

    public static final String COMMAND_WORD = "delete";
    public static final String CONFIRM_FLAG = "/confirm";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Deletes the person identified by the index number used in the displayed person list.\n"
            + "Parameters: INDEX (must be a positive integer) [" + CONFIRM_FLAG + "]\n"
            + "Deleting is permanent, so the command first shows a warning. Add " + CONFIRM_FLAG + " to confirm.\n"
            + "Examples: " + COMMAND_WORD + " 1, " + COMMAND_WORD + " 1 " + CONFIRM_FLAG;

    public static final String MESSAGE_DELETE_PERSON_SUCCESS = "Deleted person: %1$s";
    public static final String MESSAGE_CONFIRM_DELETE = "Warning: this will permanently delete the person below. "
            + "This cannot be undone.\n%1$s\n"
            + "To confirm, enter: " + COMMAND_WORD + " %2$d " + CONFIRM_FLAG;
    public static final String MESSAGE_EMPTY_LIST = "There are no persons in the displayed list to delete.";
    public static final String MESSAGE_INDEX_OUT_OF_RANGE = "The index provided is out of range. "
            + "The displayed list has %1$d person(s), so the index must be from 1 to %1$d.";

    private final Index targetIndex;
    private final boolean isConfirmed;

    /**
     * Creates an unconfirmed command: executing it only shows a warning and deletes nothing.
     */
    public DeleteCommand(Index targetIndex) {
        this(targetIndex, false);
    }

    /**
     * Creates a command that deletes the person at {@code targetIndex} only if {@code isConfirmed} is true.
     */
    public DeleteCommand(Index targetIndex, boolean isConfirmed) {
        this.targetIndex = targetIndex;
        this.isConfirmed = isConfirmed;
    }

    @Override
    public boolean modifiesData() {
        return isConfirmed;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        List<Person> lastShownList = model.getFilteredPersonList();

        if (lastShownList.isEmpty()) {
            throw new CommandException(MESSAGE_EMPTY_LIST);
        }

        if (targetIndex.getZeroBased() >= lastShownList.size()) {
            throw new CommandException(String.format(MESSAGE_INDEX_OUT_OF_RANGE, lastShownList.size()));
        }

        Person personToDelete = lastShownList.get(targetIndex.getZeroBased());
        if (!isConfirmed) {
            return new CommandResult(String.format(MESSAGE_CONFIRM_DELETE,
                    Messages.format(personToDelete), targetIndex.getOneBased()));
        }

        model.deletePerson(personToDelete);
        return new CommandResult(String.format(MESSAGE_DELETE_PERSON_SUCCESS, Messages.format(personToDelete)));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof DeleteCommand otherDeleteCommand)) {
            return false;
        }

        return targetIndex.equals(otherDeleteCommand.targetIndex)
                && isConfirmed == otherDeleteCommand.isConfirmed;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("targetIndex", targetIndex)
                .add("isConfirmed", isConfirmed)
                .toString();
    }
}
