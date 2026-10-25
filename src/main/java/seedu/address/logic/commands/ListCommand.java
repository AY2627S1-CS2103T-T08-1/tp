package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.model.Model.PREDICATE_SHOW_ALL_PERSONS;
import static seedu.address.model.Model.PREDICATE_SHOW_ARCHIVED_PERSONS;

import seedu.address.model.Model;

/**
 * Lists active or archived persons in the address book to the user.
 */
public class ListCommand extends Command {

    public static final String COMMAND_WORD = "list";

    public static final String MESSAGE_SUCCESS = "Listed all persons.";

    public static final String MESSAGE_ARCHIVE_SUCCESS = "Listed archived persons.";

    /* Error string for list command */
    public static final String MESSAGE_INVALID_FORMAT = "Invalid command format. List command format: list [archive]";


    private final boolean showArchive;

    public ListCommand() {
        this(false);
    }

    public ListCommand(boolean showArchive) {
        this.showArchive = showArchive;
    }

    @Override
    public boolean modifiesData() {
        return false;
    }

    @Override
    public CommandResult execute(Model model) {
        requireNonNull(model);
        model.updateFilteredPersonList(showArchive ? PREDICATE_SHOW_ARCHIVED_PERSONS : PREDICATE_SHOW_ALL_PERSONS);
        return new CommandResult(showArchive ? MESSAGE_ARCHIVE_SUCCESS : MESSAGE_SUCCESS);
    }
}
