package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import seedu.address.model.Model;

/**
 * Lists active or archived persons in the address book to the user, in the order they were added.
 * Clears any filter and any sort order.
 */
public class ListCommand extends Command {

    public static final String COMMAND_WORD = "list";
    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Lists all persons and resets filters and sorting.\nExample: " + COMMAND_WORD;

    public static final String MESSAGE_SUCCESS = "Listed all persons.";

    public static final String MESSAGE_NO_CONTACTS = "CareBook is empty";

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
        model.setArchiveScope(showArchive);
        model.resetSortedPersonList();
        if (model.getFilteredPersonList().isEmpty()) {
            return new CommandResult(MESSAGE_NO_CONTACTS);
        }
        return new CommandResult(showArchive ? MESSAGE_ARCHIVE_SUCCESS : MESSAGE_SUCCESS);
    }
}
