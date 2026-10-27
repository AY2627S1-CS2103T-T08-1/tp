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
 * Restores an archived person identified using its displayed index from the address book.
 */
public class UnarchiveCommand extends Command {

    public static final String COMMAND_WORD = "unarchive";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Unarchives the person identified by the index number used in the displayed person list.\n"
            + "Parameters: INDEX (must be a positive integer)\n"
            + "Example: " + COMMAND_WORD + " 1";

    public static final String MESSAGE_UNARCHIVE_PERSON_SUCCESS = "Unarchived person: %1$s";
    public static final String MESSAGE_EMPTY_LIST = "There are no persons in the displayed list to unarchive.";
    public static final String MESSAGE_INDEX_OUT_OF_RANGE = "The index provided is out of range. "
            + "The displayed list has %1$d person(s), so the index must be from 1 to %1$d.";

    public static final String MESSAGE_NOT_ARCHIVED =
            "This person is not archived. Use list archive to select an archived contact.";

    private final Index targetIndex;

    public UnarchiveCommand(Index targetIndex) {
        this.targetIndex = requireNonNull(targetIndex);
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

        Person personToUnarchive = lastShownList.get(targetIndex.getZeroBased());
        if (!personToUnarchive.isArchived()) {
            throw new CommandException(MESSAGE_NOT_ARCHIVED);
        }
        model.setPerson(personToUnarchive, personToUnarchive.copyWithArchivedStatus(false));
        return new CommandResult(String.format(MESSAGE_UNARCHIVE_PERSON_SUCCESS, Messages.format(personToUnarchive)));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof UnarchiveCommand otherUnarchiveCommand)) {
            return false;
        }

        return targetIndex.equals(otherUnarchiveCommand.targetIndex);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("targetIndex", targetIndex)
                .toString();
    }
}
