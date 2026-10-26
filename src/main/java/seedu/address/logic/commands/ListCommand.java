package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.model.Model.PREDICATE_SHOW_ALL_PERSONS;

import seedu.address.model.Model;

/**
 * Lists all persons in the address book to the user, in the order they were added.
 * Clears any filter and any sort order.
 */
public class ListCommand extends Command {

    public static final String COMMAND_WORD = "list";
    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Lists all persons and resets filters and sorting.\nExample: " + COMMAND_WORD;

    public static final String MESSAGE_SUCCESS = "Listed all persons.";

    /* Error string for list command */
    public static final String MESSAGE_INVALID_FORMAT = "Invalid command format. List command format: list";


    @Override
    public boolean modifiesData() {
        return false;
    }

    @Override
    public CommandResult execute(Model model) {
        requireNonNull(model);
        model.updateFilteredPersonList(PREDICATE_SHOW_ALL_PERSONS);
        model.resetSortedPersonList();
        return new CommandResult(MESSAGE_SUCCESS);
    }
}
