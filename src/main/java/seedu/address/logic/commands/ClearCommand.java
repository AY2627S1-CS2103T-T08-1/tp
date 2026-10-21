package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.model.Model.PREDICATE_SHOW_NO_PERSONS;

import seedu.address.model.Model;

/**
 * Clears the displayed contact list without deleting stored contacts.
 */
public class ClearCommand extends Command {

    public static final String COMMAND_WORD = "clear";
    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Clears the display without deleting contacts. Takes no arguments.\n"
            + "To delete a contact, use delete INDEX.\n"
            + "Example: " + COMMAND_WORD;

    public static final String MESSAGE_SUCCESS = "Display cleared. No contacts were deleted.\n"
            + "Use list to show all contacts again, or find to search.";

    @Override
    public boolean modifiesData() {
        return false;
    }

    @Override
    public CommandResult execute(Model model) {
        requireNonNull(model);
        model.updateFilteredPersonList(PREDICATE_SHOW_NO_PERSONS);
        return new CommandResult(MESSAGE_SUCCESS);
    }
}
