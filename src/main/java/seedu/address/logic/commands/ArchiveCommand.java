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
 * Archives a person identified using its displayed index from the address book.
 */
public class ArchiveCommand extends Command {

    public static final String COMMAND_WORD = "archive";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Archives the person identified by the index number used in the displayed person list.\n"
            + "Parameters: INDEX (must be a positive integer)\n"
            + "Example: " + COMMAND_WORD + " 1";

    public static final String MESSAGE_ARCHIVE_PERSON_SUCCESS = "Archived person: %1$s";
    public static final String MESSAGE_EMPTY_LIST = "There are no persons in the displayed list to archive.";
    public static final String MESSAGE_INDEX_OUT_OF_RANGE = "The index provided is out of range. "
            + "The displayed list has %1$d person(s), so the index must be from 1 to %1$d.";

    public static final String MESSAGE_ALREADY_ARCHIVED = "This person is already archived.";

    private final Index targetIndex;

    public ArchiveCommand(Index targetIndex) {
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

        Person personToArchive = lastShownList.get(targetIndex.getZeroBased());
        if (personToArchive.isArchived()) {
            throw new CommandException(MESSAGE_ALREADY_ARCHIVED);
        }
        model.setPerson(personToArchive, personToArchive.copyWithArchivedStatus(true));
        return new CommandResult(String.format(MESSAGE_ARCHIVE_PERSON_SUCCESS, Messages.format(personToArchive)));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof ArchiveCommand otherArchiveCommand)) {
            return false;
        }

        return targetIndex.equals(otherArchiveCommand.targetIndex);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("targetIndex", targetIndex)
                .toString();
    }
}
