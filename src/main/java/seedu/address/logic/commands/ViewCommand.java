package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.List;

import seedu.address.commons.core.index.Index;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.person.Person;

/**
 * Displays the full details of a person identified using their displayed index.
 */
public class ViewCommand extends Command {

    public static final String COMMAND_WORD = "view";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Displays full details of the person at the specified index.\n"
            + "Parameters: INDEX (must be a positive integer)\n"
            + "Example: " + COMMAND_WORD + " 1";

    public static final String MESSAGE_VIEW_PERSON_SUCCESS = "Viewing person: %1$s";

    public static final String MESSAGE_EMPTY_LIST = "No contacts are currently displayed. "
            + "Use list to show all contacts, or find KEYWORD to search.";

    public static final String MESSAGE_INDEX_OUT_OF_RANGE = "Index %1$d is out of range. "
            + "The displayed list has %2$d %3$s. Enter an index from 1 to %2$d.";

    private final Index targetIndex;

    /**
     * Creates a {@code ViewCommand} to display the person at the specified {@code targetIndex}.
     */
    public ViewCommand(Index targetIndex) {
        this.targetIndex = targetIndex;
    }

    @Override
    public boolean modifiesData() {
        return false;
    }

    @Override
    public boolean preservesPersonSelection() {
        return true;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        List<Person> lastShownList = model.getFilteredPersonList();

        validateTargetIndex(lastShownList.size());

        Person personToView = lastShownList.get(targetIndex.getZeroBased());
        model.selectPerson(personToView);
        return new CommandResult(String.format(MESSAGE_VIEW_PERSON_SUCCESS, personToView.getName().fullName));
    }

    /**
     * Checks the displayed index and explains how to recover when the contact cannot be selected.
     *
     * @throws CommandException if the list is empty or the index is outside its range.
     */
    private void validateTargetIndex(int displayedCount) throws CommandException {
        if (displayedCount == 0) {
            throw new CommandException(MESSAGE_EMPTY_LIST);
        }
        if (targetIndex.getZeroBased() >= displayedCount) {
            String contactNoun = displayedCount == 1 ? "contact" : "contacts";
            throw new CommandException(String.format(MESSAGE_INDEX_OUT_OF_RANGE,
                    targetIndex.getOneBased(), displayedCount, contactNoun));
        }
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof ViewCommand otherViewCommand)) {
            return false;
        }

        return targetIndex.equals(otherViewCommand.targetIndex);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("targetIndex", targetIndex)
                .toString();
    }
}
