package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.FxTestUtil.runOnFxThread;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import org.junit.jupiter.api.Test;

import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import seedu.address.logic.commands.CheckupCommand;
import seedu.address.logic.commands.ClearCommand;
import seedu.address.logic.commands.FilterCommand;
import seedu.address.logic.commands.MarkCommand;
import seedu.address.logic.commands.RemarkCommand;
import seedu.address.logic.commands.SortCommand;

public class HelpWindowTest {

    @Test
    public void show_commandList_displaysAvailableCommands() throws Exception {
        runOnFxThread(() -> {
            HelpWindow helpWindow = new HelpWindow();
            try {
                helpWindow.show();
                assertTrue(helpWindow.isShowing());

                TextArea commandList = (TextArea) helpWindow.getRoot().getScene().lookup("#commandList");
                assertNotNull(commandList);
                assertFalse(commandList.isEditable());
                assertTrue(commandList.isWrapText());

                List<String> commandWords = Arrays.stream(commandList.getText().split("\n\n"))
                        .map(usage -> usage.split("[ :]")[0].toLowerCase(Locale.ROOT))
                        .toList();
                assertEquals(List.of("add", "edit", "delete", "archive", "unarchive", "find", "filter", "list",
                        "sort", "view", "checkup", "mark", "remark", "clear", "help", "exit"), commandWords);
                assertTrue(commandList.getText().contains("Example: archive 1"));
                assertTrue(commandList.getText().contains("Example: unarchive 1"));
                assertTrue(commandList.getText().contains("Example: list archive"));
                assertTrue(commandList.getText().contains("Run list archive first"));
                assertTrue(commandList.getText().contains(FilterCommand.MESSAGE_USAGE));
                assertTrue(commandList.getText().contains(CheckupCommand.MESSAGE_USAGE));
                assertTrue(commandList.getText().contains(MarkCommand.MESSAGE_USAGE));
                assertTrue(commandList.getText().contains(RemarkCommand.MESSAGE_USAGE));
                assertTrue(commandList.getText().contains(SortCommand.MESSAGE_USAGE));
                assertTrue(commandList.getText().contains(ClearCommand.MESSAGE_USAGE));

                Label helpMessage = (Label) helpWindow.getRoot().getScene().lookup("#helpMessage");
                assertNotNull(helpMessage);
                assertEquals(HelpWindow.HELP_MESSAGE, helpMessage.getText());
            } finally {
                helpWindow.hide();
            }
            assertFalse(helpWindow.isShowing());
        });
    }
}
