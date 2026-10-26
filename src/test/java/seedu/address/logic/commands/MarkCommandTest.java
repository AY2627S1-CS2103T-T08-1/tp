package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;

import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.LogicManager;
import seedu.address.logic.parser.AddressBookParser;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.person.Person;
import seedu.address.storage.JsonAddressBookStorage;
import seedu.address.storage.JsonUserPrefsStorage;
import seedu.address.storage.StorageManager;
import seedu.address.testutil.PersonBuilder;

public class MarkCommandTest {
    @TempDir
    public Path testFolder;

    private final Model model = new ModelManager();
    private final AddressBookParser parser = new AddressBookParser();
    private final Person alice = new PersonBuilder().withName("Alice").withTags("LOW")
            .withCheckupAppointment("01/01/2030 09:05").withRemarks("Needs assistance").build();
    private final Person bob = new PersonBuilder().withName("Bob").withTags("HIGH")
            .withCheckupAppointment("01/01/2030 10:00").build();

    @Test
    public void execute_throughLogic_savesMarkedStateForReload() throws Exception {
        model.addPerson(alice);
        StorageManager storage = new StorageManager(new JsonAddressBookStorage(testFolder.resolve("contacts.json")),
                new JsonUserPrefsStorage(testFolder.resolve("preferences.json")));
        new LogicManager(model, storage).execute("mark 1");
        assertEquals(List.of(alice.withMarkedCheckup()), storage.readAddressBook().orElseThrow().getPersonList());
    }

    @Test
    public void execute_marksContactAndPreservesDetailsAndSelection() throws Exception {
        model.addPerson(alice);
        new ViewCommand(Index.fromOneBased(1)).execute(model);
        MarkCommand command = new MarkCommand(Index.fromOneBased(1));
        assertTrue(command.modifiesData());
        assertEquals("Marked contact: Alice", command.execute(model).getFeedbackToUser());
        assertEquals(List.of(alice.withMarkedCheckup()), model.getAddressBook().getPersonList());
        assertEquals(alice.withMarkedCheckup(), model.selectedPersonProperty().get());
    }

    @Test
    public void execute_filteredAndSortedLists_useDisplayedIndex() throws Exception {
        model.addPerson(alice);
        model.addPerson(bob);
        parser.parseCommand("filter /tag HIGH").execute(model);
        parser.parseCommand("mark 1").execute(model);
        assertEquals(List.of(bob.withMarkedCheckup()), model.getFilteredPersonList());
        assertFalse(model.getAddressBook().getPersonList().get(0).isCheckupMarked());

        parser.parseCommand("list").execute(model);
        parser.parseCommand("sort name desc").execute(model);
        parser.parseCommand("mark 2").execute(model);
        assertEquals(List.of(bob.withMarkedCheckup(), alice.withMarkedCheckup()), model.getFilteredPersonList());
    }

    @Test
    public void execute_archivedContact_preservesArchivedStatus() throws Exception {
        model.addPerson(alice.copyWithArchivedStatus(true));
        parser.parseCommand("list archive").execute(model);
        parser.parseCommand("mark 1").execute(model);
        assertEquals(List.of(alice.copyWithArchivedStatus(true).withMarkedCheckup()), model.getFilteredPersonList());
    }

    @Test
    public void execute_repeatedMark_showsExactErrorWithoutChangingModel() throws Exception {
        model.addPerson(alice);
        parser.parseCommand("mark 1").execute(model);
        assertCommandFailure(new MarkCommand(Index.fromOneBased(1)), model,
                "Marked contact cannot be marked again.");
    }

    @Test
    public void execute_withoutCheckup_failsWithoutChangingModel() {
        model.addPerson(new PersonBuilder().build());
        assertCommandFailure(new MarkCommand(Index.fromOneBased(1)), model, Person.MESSAGE_MARK_WITHOUT_CHECKUP);
    }

    @Test
    public void execute_invalidDisplayedIndex_showsExactError() throws Exception {
        assertCommandFailure(new MarkCommand(Index.fromOneBased(1)), model, "Invalid index.");
        model.addPerson(alice);
        model.addPerson(bob);
        parser.parseCommand("filter /tag HIGH").execute(model);
        assertCommandFailure(new MarkCommand(Index.fromOneBased(2)), model, "Invalid index.");
    }

    @Test
    public void execute_remarkAndEdit_preserveMarkedState() throws Exception {
        model.addPerson(alice.withMarkedCheckup());
        parser.parseCommand("remark 1 New note").execute(model);
        assertTrue(model.getFilteredPersonList().get(0).isCheckupMarked());
        parser.parseCommand("edit 1 /address New Street").execute(model);
        assertTrue(model.getFilteredPersonList().get(0).isCheckupMarked());
    }

    @Test
    public void equals_comparesDisplayedIndex() {
        MarkCommand command = new MarkCommand(Index.fromOneBased(1));
        assertEquals(command, command);
        assertEquals(command, new MarkCommand(Index.fromOneBased(1)));
        assertFalse(command.equals(new MarkCommand(Index.fromOneBased(2))));
        assertFalse(command.equals(null));
        assertFalse(command.equals("mark"));
    }
}
