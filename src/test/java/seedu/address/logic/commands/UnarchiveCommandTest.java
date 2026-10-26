package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.testutil.TypicalPersons.ALICE;
import static seedu.address.testutil.TypicalPersons.DANIEL;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.Messages;
import seedu.address.logic.parser.AddressBookParser;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.Person;
import seedu.address.storage.JsonAddressBookStorage;
import seedu.address.testutil.PersonBuilder;

public class UnarchiveCommandTest {
    @TempDir
    public Path testFolder;

    private final AddressBookParser parser = new AddressBookParser();
    private final Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());

    @Test
    public void execute_archivedContact_restoresDetailsAndPersists() throws Exception {
        Person original = new PersonBuilder(ALICE).withCheckupAppointment("01/01/2030 09:00")
                .withRemarks("Needs assistance").build();
        model.setPerson(ALICE, original.copyWithArchivedStatus(true));
        parser.parseCommand("list archive").execute(model);
        UnarchiveCommand command = new UnarchiveCommand(Index.fromOneBased(1));

        CommandResult result = command.execute(model);

        assertTrue(command.modifiesData());
        assertEquals(String.format(UnarchiveCommand.MESSAGE_UNARCHIVE_PERSON_SUCCESS,
                Messages.format(original)), result.getFeedbackToUser());
        assertTrue(model.getFilteredPersonList().isEmpty());
        assertTrue(model.getAddressBook().getPersonList().contains(original));
        assertEquals(getTypicalAddressBook().getPersonList().size(), model.getAddressBook().getPersonList().size());
        parser.parseCommand("list").execute(model);
        assertTrue(model.getFilteredPersonList().contains(original));

        JsonAddressBookStorage storage = new JsonAddressBookStorage(testFolder.resolve("contacts.json"));
        storage.saveAddressBook(model.getAddressBook());
        Model restored = new ModelManager(storage.readAddressBook().orElseThrow(), new UserPrefs());
        assertEquals(new AddressBook(model.getAddressBook()), new AddressBook(restored.getAddressBook()));
        assertTrue(restored.getFilteredPersonList().contains(original));
    }

    @Test
    public void execute_mixedCaseWithWhitespace_unarchivesContact() throws Exception {
        for (String input : List.of("unarchive 1", "Unarchive 1", "UNARCHIVE 1", "  uNaRcHiVe\t001  ")) {
            model.setPerson(ALICE, ALICE.copyWithArchivedStatus(true));
            parser.parseCommand("list archive").execute(model);
            parser.parseCommand(input).execute(model);
            assertTrue(model.getFilteredPersonList().isEmpty());
            assertTrue(model.getAddressBook().getPersonList().contains(ALICE));
        }
    }

    @Test
    public void execute_filteredSortedArchive_usesDisplayedIndex() throws Exception {
        model.setPerson(ALICE, ALICE.copyWithArchivedStatus(true));
        model.setPerson(DANIEL, DANIEL.copyWithArchivedStatus(true));
        parser.parseCommand("list archive").execute(model);
        parser.parseCommand("filter /tag HIGH").execute(model);
        parser.parseCommand("sort name desc").execute(model);
        assertEquals(DANIEL.copyWithArchivedStatus(true), model.getFilteredPersonList().get(0));

        parser.parseCommand("unarchive 1").execute(model);

        assertEquals(List.of(ALICE.copyWithArchivedStatus(true)), model.getFilteredPersonList());
        assertTrue(model.getAddressBook().getPersonList().contains(DANIEL));
        parser.parseCommand("unarchive 1").execute(model);
        assertTrue(model.getFilteredPersonList().isEmpty());
        parser.parseCommand("filter /tag HIGH").execute(model);
        assertTrue(model.getFilteredPersonList().isEmpty());
    }

    @Test
    public void execute_activeContact_rejectsWithoutChangingData() {
        assertCommandFailure(new UnarchiveCommand(Index.fromOneBased(1)), model,
                UnarchiveCommand.MESSAGE_NOT_ARCHIVED);
    }

    @Test
    public void execute_emptyArchive_rejectsWithoutChangingData() throws Exception {
        parser.parseCommand("list archive").execute(model);
        assertCommandFailure(new UnarchiveCommand(Index.fromOneBased(1)), model,
                UnarchiveCommand.MESSAGE_EMPTY_LIST);
    }

    @Test
    public void execute_indexBeyondDisplayedArchive_rejectsWithoutChangingData() throws Exception {
        model.setPerson(ALICE, ALICE.copyWithArchivedStatus(true));
        parser.parseCommand("list archive").execute(model);
        assertCommandFailure(new UnarchiveCommand(Index.fromOneBased(2)), model,
                String.format(UnarchiveCommand.MESSAGE_INDEX_OUT_OF_RANGE, 1));
    }

    @Test
    public void parse_mixedCaseWithExtraArguments_rejectsInput() {
        for (String input : List.of("Unarchive", "UNARCHIVE 1 extra", "Unarchive 1 2", "uNaRcHiVe 0")) {
            assertThrows(ParseException.class, () -> parser.parseCommand(input));
        }
    }

    @Test
    public void equals_comparedCommands_returnsExpectedResult() {
        UnarchiveCommand command = new UnarchiveCommand(Index.fromOneBased(1));
        assertTrue(command.equals(command));
        assertTrue(command.equals(new UnarchiveCommand(Index.fromOneBased(1))));
        assertFalse(command.equals(new UnarchiveCommand(Index.fromOneBased(2))));
        assertFalse(command.equals(new ArchiveCommand(Index.fromOneBased(1))));
        assertFalse(command.equals(null));
    }

    @Test
    public void toString_includesTargetIndex() {
        Index index = Index.fromOneBased(3);
        assertEquals(UnarchiveCommand.class.getCanonicalName() + "{targetIndex=" + index + "}",
                new UnarchiveCommand(index).toString());
    }
}
