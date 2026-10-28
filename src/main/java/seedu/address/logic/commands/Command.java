package seedu.address.logic.commands;

import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;

/**
 * Represents a command with hidden internal logic and the ability to be executed.
 */
public abstract class Command {

    /**
     * Executes the command and returns the result message.
     *
     * @param model {@code Model} which the command should operate on.
     * @return feedback message of the operation result for display
     * @throws CommandException If an error occurs during command execution.
     */
    public abstract CommandResult execute(Model model) throws CommandException;

    /**
     * Returns true if this command may change the stored data, so the data should be saved after it runs.
     */
    public boolean modifiesData() {
        return true;
    }

    /**
     * Returns whether the selected contact remains displayed after successful command processing.
     * Only view commands preserve the details panel; other commands return to their normal list display.
     */
    public boolean preservesPersonSelection() {
        return false;
    }

}
