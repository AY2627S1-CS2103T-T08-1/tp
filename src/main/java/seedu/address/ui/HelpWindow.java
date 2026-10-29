package seedu.address.ui;

import java.util.logging.Logger;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.stage.Stage;
import seedu.address.commons.core.LogsCenter;
import seedu.address.logic.commands.AddCommand;
import seedu.address.logic.commands.CheckupCommand;
import seedu.address.logic.commands.ClearCommand;
import seedu.address.logic.commands.DeleteCommand;
import seedu.address.logic.commands.EditCommand;
import seedu.address.logic.commands.ExitCommand;
import seedu.address.logic.commands.FilterCommand;
import seedu.address.logic.commands.FindCommand;
import seedu.address.logic.commands.HelpCommand;
import seedu.address.logic.commands.ListCommand;
import seedu.address.logic.commands.MarkCommand;
import seedu.address.logic.commands.RemarkCommand;
import seedu.address.logic.commands.SortCommand;
import seedu.address.logic.commands.UnmarkCommand;
import seedu.address.logic.commands.ViewCommand;

/**
 * Controls the help window displaying available commands.
 */
public class HelpWindow extends UiPart<Stage> {

    public static final String USERGUIDE_URL = "https://ay2627s1-cs2103t-t08-1.github.io/tp/UserGuide.html";
    public static final String HELP_MESSAGE = "Refer to the user guide: " + USERGUIDE_URL;

    // Usage copied from branch-archive until its command classes are integrated.
    private static final String ARCHIVE_USAGE = "archive: Archives the person identified by the index number "
            + "used in the displayed person list.\n"
            + "Parameters: INDEX (must be a positive integer)\n"
            + "The contact is retained in the archive. Use list archive to view archived contacts.\n"
            + "Example: archive 1";
    private static final String UNARCHIVE_USAGE = "unarchive: Unarchives the person identified by the index number "
            + "used in the displayed person list.\n"
            + "Parameters: INDEX (must be a positive integer)\n"
            + "Run list archive first, then use the displayed index to restore a contact.\n"
            + "Example: unarchive 1";
    private static final String LIST_ARCHIVE_USAGE = "list archive: Lists archived contacts and resets filters "
            + "and sorting. Use list to return to active contacts.\nExample: list archive";

    private static final String COMMAND_HELP = String.join("\n\n",
            AddCommand.MESSAGE_USAGE,
            EditCommand.MESSAGE_USAGE,
            DeleteCommand.MESSAGE_USAGE,
            ARCHIVE_USAGE,
            UNARCHIVE_USAGE,
            FindCommand.COMMAND_WORD + ": " + FindCommand.MESSAGE_USAGE,
            FilterCommand.MESSAGE_USAGE,
            ListCommand.MESSAGE_USAGE + "\n" + LIST_ARCHIVE_USAGE,
            SortCommand.MESSAGE_USAGE,
            ViewCommand.MESSAGE_USAGE,
            CheckupCommand.MESSAGE_USAGE,
            MarkCommand.MESSAGE_USAGE,
            UnmarkCommand.MESSAGE_USAGE,
            RemarkCommand.MESSAGE_USAGE,
            ClearCommand.MESSAGE_USAGE,
            HelpCommand.MESSAGE_USAGE,
            ExitCommand.MESSAGE_USAGE);

    private static final Logger logger = LogsCenter.getLogger(HelpWindow.class);
    private static final String FXML = "HelpWindow.fxml";

    @FXML
    private Button copyButton;

    @FXML
    private Label helpMessage;

    @FXML
    private TextArea commandList;

    /**
     * Creates a new HelpWindow.
     *
     * @param root Stage to use as the root of the help window.
     */
    public HelpWindow(Stage root) {
        super(FXML, root);
        helpMessage.setText(HELP_MESSAGE);
        commandList.setText(COMMAND_HELP);
    }

    /**
     * Creates a new HelpWindow.
     */
    public HelpWindow() {
        this(new Stage());
    }

    /**
     * Shows the help window.
     * @throws IllegalStateException
     *     <ul>
     *         <li>
     *             if this method is called on a thread other than the JavaFX Application Thread.
     *         </li>
     *         <li>
     *             if this method is called during animation or layout processing.
     *         </li>
     *         <li>
     *             if this method is called on the primary stage.
     *         </li>
     *         <li>
     *             if {@code dialogStage} is already showing.
     *         </li>
     *     </ul>
     */
    public void show() {
        logger.fine("Showing help page about the application.");
        getRoot().show();
        getRoot().centerOnScreen();
    }

    /**
     * Returns true if the help window is currently being shown.
     */
    public boolean isShowing() {
        return getRoot().isShowing();
    }

    /**
     * Hides the help window.
     */
    public void hide() {
        getRoot().hide();
    }

    /**
     * Restores the help window if minimised and brings it to the front.
     */
    public void focus() {
        getRoot().setIconified(false);
        getRoot().toFront();
        getRoot().requestFocus();
    }

    /**
     * Copies the URL to the user guide to the clipboard.
     */
    @FXML
    private void copyUrl() {
        final Clipboard clipboard = Clipboard.getSystemClipboard();
        final ClipboardContent url = new ClipboardContent();
        url.putString(USERGUIDE_URL);
        clipboard.setContent(url);
    }
}
