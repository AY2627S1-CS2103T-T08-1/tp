package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.FxTestUtil.runOnFxThread;
import static seedu.address.testutil.TypicalPersons.ALICE;
import static seedu.address.testutil.TypicalPersons.BENSON;

import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import javafx.css.PseudoClass;
import javafx.event.ActionEvent;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.skin.VirtualFlow;
import javafx.scene.layout.Region;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import seedu.address.logic.LogicManager;
import seedu.address.model.ModelManager;
import seedu.address.model.person.Person;
import seedu.address.storage.JsonAddressBookStorage;
import seedu.address.storage.JsonUserPrefsStorage;
import seedu.address.storage.StorageManager;
import seedu.address.testutil.PersonBuilder;

/** Exercises the actual FXML panels and model listeners on the JavaFX application thread. */
public class ContactDetailsUiTest {
    private static final PseudoClass VIEWED = PseudoClass.getPseudoClass("viewed");

    @TempDir
    public Path temporaryFolder;

    @Test
    public void startup_noSelection_hidesPanelAndPlaceholder() throws Exception {
        withWindow(window -> {
            assertFalse(window.detailsPanel().isVisible());
            assertFalse(window.detailsPanel().isManaged());
            assertFalse(window.node("#personDetailsPlaceholder").isManaged());
            assertEquals("", window.detailsText().getText());
            assertTrue(window.feedback().isWrapText());
        });
    }

    @Test
    public void repeatedView_afterScrollingAway_scrollsBackWithoutChangingSelection() throws Exception {
        withWindow(window -> {
            for (int i = 0; i < 8; i++) {
                window.model.addPerson(new PersonBuilder().withName("Extra Person " + i)
                        .withPhone(Integer.toString(90000000 + i))
                        .withEmail("extra" + i + "@example.com").build());
            }
            ListView<?> list = (ListView<?>) window.node("#personListView");
            list.setMinHeight(140);
            list.setPrefHeight(140);
            list.setMaxHeight(140);
            window.enter("view 1");
            window.layout();
            Person selected = window.model.selectedPersonProperty().get();
            VirtualFlow<?> flow = (VirtualFlow<?>) list.lookup(".virtual-flow");
            list.scrollTo(8);
            window.layout();
            assertTrue(flow.getFirstVisibleCell().getIndex() > 0);

            window.enter("view 1");
            window.layout();

            assertEquals(0, flow.getFirstVisibleCell().getIndex());
            assertEquals(selected, window.model.selectedPersonProperty().get());
            assertEquals(10, window.model.getAddressBook().getPersonList().size());
        });
    }

    @Test
    public void viewThroughCommandBox_preservesListAndHighlightsSelectedContact() throws Exception {
        withWindow(window -> {
            window.enter("view 2");
            window.layout();
            assertEquals(List.of(ALICE, BENSON), window.model.getFilteredPersonList());
            assertEquals(BENSON, window.logic.selectedPersonProperty().get());
            assertTrue(window.detailsPanel().isVisible());
            assertTrue(window.node("#personDetailsPlaceholder").isManaged());
            assertTrue(window.title().getText().contains(BENSON.getName().fullName));
            assertTrue(window.detailsText().getText().contains(BENSON.getPatientNo().value));
            assertTrue(window.feedback().getText().contains(BENSON.getName().fullName));
            // lookupAll ignores pseudo-classes, so each cell's state is checked directly.
            assertEquals(1, window.stage.getScene().getRoot().lookupAll(".list-cell").stream()
                    .filter(cell -> cell.getPseudoClassStates().contains(VIEWED)).count());
        });
    }

    @Test
    public void viewFilteredList_usesFilteredIndexAndClearHidesDetails() throws Exception {
        withWindow(window -> {
            window.model.updateFilteredPersonList(BENSON::equals);
            window.enter("view 1");
            assertTrue(window.title().getText().contains(BENSON.getName().fullName));
            assertEquals(List.of(BENSON), window.model.getFilteredPersonList());
            window.enter("clear");
            window.layout();
            assertFalse(window.detailsPanel().isVisible());
            assertFalse(window.node("#personDetailsPlaceholder").isManaged());
            assertEquals("", window.detailsText().getText());
            window.enter("list");
            assertEquals(2, window.model.getFilteredPersonList().size());
            assertFalse(window.detailsPanel().isVisible());
        });
    }

    @Test
    public void editAndDeleteSelection_refreshesDetailsThenRemovesStaleText() throws Exception {
        withWindow(window -> {
            window.enter("view 2");
            Person edited = new PersonBuilder(BENSON).withName("Updated Benson").withPhone("98765432").build();
            window.model.setPerson(BENSON, edited);
            assertTrue(window.title().getText().contains("Updated Benson"));
            assertTrue(window.detailsText().getText().contains("98765432"));
            window.model.deletePerson(ALICE);
            assertTrue(window.detailsPanel().isVisible());
            assertEquals(edited, window.logic.selectedPersonProperty().get());
            window.model.deletePerson(edited);
            assertFalse(window.detailsPanel().isVisible());
            assertEquals("", window.detailsText().getText());
        });
    }

    @Test
    public void filterHidesSelection_hidesDetailsAndInvalidViewDoesNotRestoreIt() throws Exception {
        withWindow(window -> {
            window.enter("view 1");
            window.model.updateFilteredPersonList(BENSON::equals);
            assertFalse(window.detailsPanel().isVisible());
            window.enter("view 99");
            assertFalse(window.detailsPanel().isVisible());
            assertEquals("Index 99 is out of range. The displayed list has 1 contact. Enter an index from 1 to 1.",
                    window.feedback().getText());
        });
    }

    @Test
    public void viewAfterClear_explainsRecoveryWithoutOpeningDetails() throws Exception {
        withWindow(window -> {
            window.enter("view 1");
            window.enter("clear");
            window.enter("view 1");
            assertFalse(window.detailsPanel().isVisible());
            assertTrue(window.model.getFilteredPersonList().isEmpty());
            assertEquals("No contacts are currently displayed. "
                    + "Use list to show all contacts, or find KEYWORD to search.", window.feedback().getText());
        });
    }

    @Test
    public void feedback_longThenShort_obeysCapAndResetsScroll() throws Exception {
        withWindow(window -> {
            TextArea feedback = window.feedback();
            Region panel = (Region) feedback.getParent();
            feedback.setText("Long usage text. ".repeat(200));
            window.layout();
            assertEquals(160, panel.getPrefHeight());
            feedback.setScrollTop(50);
            window.enter("view 1");
            window.layout();
            assertTrue(panel.getPrefHeight() < 80);
            assertEquals(0, feedback.getScrollTop());
            feedback.setText("");
            feedback.setFont(Font.font(20));
            assertTrue(panel.getPrefHeight() >= 42);
        });
    }

    @Test
    public void details_longContact_obeysCapAndWraps() throws Exception {
        withWindow(window -> {
            Person longContact = new PersonBuilder(ALICE).withAddress("Block 123 Long Road ".repeat(100)).build();
            window.model.setPerson(ALICE, longContact);
            window.enter("view 1");
            window.layout();
            assertEquals(260, window.detailsPanel().getPrefHeight());
            assertTrue(window.detailsText().isWrapText());
            ScrollPane scroll = (ScrollPane) window.node("#detailsScroll");
            assertTrue(scroll.getContent().getBoundsInLocal().getHeight() > scroll.getViewportBounds().getHeight());
        });
    }

    @Test
    public void sortedView_thenRemarkChanges_refreshesNumberedNotesAndClearsOldText() throws Exception {
        withWindow(window -> {
            window.enter("sort name desc");
            window.enter("view 1");
            assertTrue(window.title().getText().contains(BENSON.getName().fullName));
            window.enter("remark 1 Bring medication list");
            window.enter("remark 1 Call family /tag HIGH is literal text");
            assertFalse(window.detailsPanel().isVisible());
            window.enter("view 1");
            assertTrue(window.detailsText().getText().contains("1. Bring medication list"));
            assertTrue(window.detailsText().getText().contains("2. Call family /tag HIGH is literal text"));
            assertEquals(2, window.model.getFilteredPersonList().size());
            window.enter("remark 1 /edit 1 Bring updated medication list");
            assertFalse(window.detailsPanel().isVisible());
            window.enter("view 1");
            assertTrue(window.detailsText().getText().contains("1. Bring updated medication list"));
            window.enter("remark 1 /clear");
            assertFalse(window.detailsPanel().isVisible());
            window.enter("view 1");
            assertTrue(window.detailsText().getText().contains("Remark: None"));
            assertFalse(window.detailsText().getText().contains("Call family"));
            assertTrue(window.detailsPanel().isVisible());
        });
    }

    @Test
    public void filterSortViewAndCheckup_useDisplayedIndexAndRefreshAppointment() throws Exception {
        withWindow(window -> {
            window.model.setPerson(ALICE, new PersonBuilder(ALICE).withTags("HIGH").build());
            window.model.setPerson(BENSON, new PersonBuilder(BENSON).withTags("HIGH").build());
            window.enter("filter /tag HIGH");
            window.enter("sort name desc");
            window.enter("view 1");
            String appointment = LocalDate.now().plusDays(1).format(DateTimeFormatter.ofPattern("dd/MM/uuuu"))
                    + " 09:30";
            window.enter("checkup 1 " + appointment);
            assertFalse(window.detailsPanel().isVisible());
            window.enter("view 1");
            assertTrue(window.detailsText().getText().contains("Checkup appointment: " + appointment));
            assertTrue(window.title().getText().contains(BENSON.getName().fullName));
            assertEquals(BENSON.getName(), window.model.selectedPersonProperty().get().getName());
            assertEquals(2, window.model.getFilteredPersonList().size());
            window.enter("filter /name Alice");
            assertFalse(window.detailsPanel().isVisible());
            window.enter("view 1");
            assertTrue(window.title().getText().contains(ALICE.getName().fullName));
            assertTrue(window.detailsText().getText().contains("Checkup appointment: Not scheduled"));
        });
    }

    /** Creates and closes an isolated window; all contacts remain in memory and no user files are opened. */
    private void withWindow(java.util.function.Consumer<TestWindow> assertions) throws Exception {
        runOnFxThread(() -> {
            TestWindow window = new TestWindow();
            try {
                assertions.accept(window);
            } finally {
                window.stage.hide();
            }
        });
        // Flush deferred scene-initialization height updates before starting the next test.
        runOnFxThread(() -> { });
    }

    /** Holds a fresh application window backed by two known contacts. */
    private class TestWindow {
        private final ModelManager model = new ModelManager();
        private final LogicManager logic = new LogicManager(model, new StorageManager(
                new JsonAddressBookStorage(temporaryFolder.resolve("contacts.json")),
                new JsonUserPrefsStorage(temporaryFolder.resolve("preferences.json"))));
        private final Stage stage = new Stage();

        TestWindow() {
            model.addPerson(ALICE);
            model.addPerson(BENSON);
            MainWindow mainWindow = new MainWindow(stage, logic, temporaryFolder.resolve("unused-test-data.json"));
            mainWindow.fillInnerParts();
            stage.setWidth(900);
            stage.setHeight(800);
            stage.show();
            layout();
        }

        void layout() {
            stage.getScene().getRoot().applyCss();
            stage.getScene().getRoot().layout();
        }

        Region node(String selector) {
            return (Region) stage.getScene().lookup(selector);
        }

        Label title() {
            return (Label) node("#title");
        }

        Label detailsText() {
            return (Label) node("#details");
        }

        Region detailsPanel() {
            return (Region) node("#detailsScroll").getParent();
        }

        TextArea feedback() {
            return (TextArea) node("#resultDisplay");
        }

        void enter(String command) {
            TextField input = (TextField) node("#commandTextField");
            input.setText(command);
            input.getOnAction().handle(new ActionEvent());
        }
    }
}
