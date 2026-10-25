package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import seedu.address.logic.commands.ClearCommand;
import seedu.address.logic.commands.FilterCommand;

public class HelpWindowTest {

    @BeforeAll
    public static void setUpJavaFx() throws Exception {
        CompletableFuture<Void> startup = new CompletableFuture<>();
        Platform.startup(() -> {
            Platform.setImplicitExit(false);
            startup.complete(null);
        });
        startup.get(10, TimeUnit.SECONDS);
    }

    @Test
    public void show_commandList_displaysAvailableCommands() throws Exception {
        FutureTask<Void> test = new FutureTask<>(() -> {
            HelpWindow helpWindow = new HelpWindow();
            try {
                helpWindow.show();
                assertTrue(helpWindow.isShowing());

                TextArea commandList = (TextArea) helpWindow.getRoot().getScene().lookup("#commandList");
                assertNotNull(commandList);
                assertFalse(commandList.isEditable());
                assertTrue(commandList.isWrapText());

                List<String> commandWords = Arrays.stream(commandList.getText().split("\n\n"))
                        .map(usage -> usage.substring(0, usage.indexOf(':')))
                        .toList();
                assertEquals(List.of("add", "edit", "delete", "find", "filter", "list", "view", "clear",
                        "help", "exit"), commandWords);
                assertTrue(commandList.getText().contains(FilterCommand.MESSAGE_USAGE));
                assertTrue(commandList.getText().contains(ClearCommand.MESSAGE_USAGE));

                Label helpMessage = (Label) helpWindow.getRoot().getScene().lookup("#helpMessage");
                assertNotNull(helpMessage);
                assertEquals(HelpWindow.HELP_MESSAGE, helpMessage.getText());
            } finally {
                helpWindow.hide();
            }
            assertFalse(helpWindow.isShowing());
            return null;
        });
        Platform.runLater(test);
        test.get(10, TimeUnit.SECONDS);
    }
}
