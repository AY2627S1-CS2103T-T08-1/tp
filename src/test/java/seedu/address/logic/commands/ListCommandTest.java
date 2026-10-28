package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.logic.commands.CommandTestUtil.showPersonAtIndex;
import static seedu.address.model.Model.PREDICATE_SHOW_ARCHIVED_PERSONS;
import static seedu.address.model.Model.PREDICATE_SHOW_NO_PERSONS;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import seedu.address.logic.parser.AddressBookParser;
import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;

/**
 * Contains integration tests (interaction with the Model) and unit tests for ListCommand.
 */
public class ListCommandTest {

    private Model model;
    private Model expectedModel;

    @BeforeEach
    public void setUp() {
        model = new ModelManager(getTypicalAddressBook(), new UserPrefs());
        expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
    }

    @Test
    public void execute_listIsNotFiltered_showsSameList() {
        assertCommandSuccess(new ListCommand(), model, ListCommand.MESSAGE_SUCCESS, expectedModel);
    }

    @Test
    public void execute_listIsFiltered_showsEverything() {
        showPersonAtIndex(model, INDEX_FIRST_PERSON);
        assertCommandSuccess(new ListCommand(), model, ListCommand.MESSAGE_SUCCESS, expectedModel);
    }

    @Test
    public void execute_nullModel_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new ListCommand().execute(null));
    }

    @Test
    public void execute_emptyAddressBook_showsEmptyList() {
        model = new ModelManager();
        expectedModel = new ModelManager();
        assertCommandSuccess(new ListCommand(), model, "CareBook is empty", expectedModel);
    }

    @Test
    public void execute_onlyArchivedContacts_reportsNoContacts() {
        AddressBook addressBook = new AddressBook();
        addressBook.addPerson(model.getAddressBook().getPersonList().get(0).copyWithArchivedStatus(true));
        model = new ModelManager(addressBook, new UserPrefs());
        expectedModel = new ModelManager(addressBook, new UserPrefs());
        model.updateFilteredPersonList(PREDICATE_SHOW_ARCHIVED_PERSONS);

        assertCommandSuccess(new ListCommand(), model, ListCommand.MESSAGE_NO_CONTACTS, expectedModel);
    }

    @Test
    public void execute_noArchivedContacts_reportsNoContacts() {
        expectedModel.setArchiveScope(true);

        assertCommandSuccess(new ListCommand(true), model, ListCommand.MESSAGE_NO_CONTACTS, expectedModel);
    }

    @Test
    public void execute_singleHiddenPerson_showsPerson() {
        AddressBook addressBook = new AddressBook();
        addressBook.addPerson(model.getAddressBook().getPersonList().get(0));
        model = new ModelManager(addressBook, new UserPrefs());
        expectedModel = new ModelManager(addressBook, new UserPrefs());
        model.updateFilteredPersonList(PREDICATE_SHOW_NO_PERSONS);

        assertCommandSuccess(new ListCommand(), model, ListCommand.MESSAGE_SUCCESS, expectedModel);
    }

    @Test
    public void execute_afterFindWithMultipleMatches_restoresAllPersons() throws Exception {
        AddressBookParser parser = new AddressBookParser();
        parser.parseCommand("find Meier").execute(model);
        assertEquals(2, model.getFilteredPersonList().size());

        assertCommandSuccess(parser.parseCommand("list"), model, ListCommand.MESSAGE_SUCCESS, expectedModel);
    }

    @Test
    public void execute_afterFindWithNoMatches_restoresAllPersons() throws Exception {
        AddressBookParser parser = new AddressBookParser();
        parser.parseCommand("find NobodyMatchesThisName").execute(model);
        assertEquals(List.of(), model.getFilteredPersonList());

        assertCommandSuccess(parser.parseCommand("list"), model, ListCommand.MESSAGE_SUCCESS, expectedModel);
    }

    @Test
    public void execute_afterSort_restoresAddedOrder() throws Exception {
        AddressBookParser parser = new AddressBookParser();
        parser.parseCommand("sort name desc").execute(model);
        assertNotEquals(model.getAddressBook().getPersonList(), model.getFilteredPersonList());

        assertCommandSuccess(parser.parseCommand("list"), model, ListCommand.MESSAGE_SUCCESS, expectedModel);
        assertEquals(model.getAddressBook().getPersonList(), model.getFilteredPersonList());
    }

    @Test
    public void execute_repeatedly_showsSameList() {
        showPersonAtIndex(model, INDEX_FIRST_PERSON);
        ListCommand command = new ListCommand();

        assertCommandSuccess(command, model, ListCommand.MESSAGE_SUCCESS, expectedModel);
        assertCommandSuccess(command, model, ListCommand.MESSAGE_SUCCESS, expectedModel);
    }

    @Test
    public void execute_afterDeletion_doesNotRestoreDeletedPerson() {
        var deletedPerson = model.getFilteredPersonList().get(0);
        model.deletePerson(deletedPerson);
        expectedModel.deletePerson(deletedPerson);
        model.updateFilteredPersonList(PREDICATE_SHOW_NO_PERSONS);

        assertCommandSuccess(new ListCommand(), model, ListCommand.MESSAGE_SUCCESS, expectedModel);
    }

    @Test
    public void execute_filteredList_preservesDataPreferencesAndInsertionOrder() {
        AddressBook addressBook = new AddressBook();
        var persons = model.getAddressBook().getPersonList();
        addressBook.addPerson(persons.get(2));
        addressBook.addPerson(persons.get(0));
        addressBook.addPerson(persons.get(1));
        model = new ModelManager(addressBook, new UserPrefs());
        expectedModel = new ModelManager(addressBook, new UserPrefs());
        model.updateFilteredPersonList(PREDICATE_SHOW_NO_PERSONS);

        assertCommandSuccess(new ListCommand(), model, ListCommand.MESSAGE_SUCCESS, expectedModel);
        assertEquals(addressBook.getPersonList(), model.getFilteredPersonList());
    }
}
