package seedu.address.logic.commands;

import seedu.address.model.Model;

/**
 * Terminates the program.
 */
public class ExitCommand extends Command {

    public static final String COMMAND_WORD = "exit";

    public static final String MESSAGE_USAGE = "Exit takes no arguments.\n"
            + "To close CareBook, enter: " + COMMAND_WORD + "\n"
            + "No contacts have been changed.";

    public static final String MESSAGE_EXIT_ACKNOWLEDGEMENT = "Exiting CareBook as requested ...";

    @Override
    public boolean modifiesData() {
        return false;
    }

    @Override
    public CommandResult execute(Model model) {
        return new CommandResult(MESSAGE_EXIT_ACKNOWLEDGEMENT, false, true);
    }

}
