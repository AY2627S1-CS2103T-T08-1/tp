package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.logic.commands.CommandTestUtil.showPersonAtIndex;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;
import static seedu.address.testutil.TypicalIndexes.INDEX_SECOND_PERSON;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.Person;

public class ViewCommandTest {

    private final Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());

    @Test
    public void execute_validIndexUnfilteredList_success() {
        Person personToView = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        ViewCommand viewCommand = new ViewCommand(INDEX_FIRST_PERSON);
        String expectedMessage = String.format(ViewCommand.MESSAGE_VIEW_PERSON_SUCCESS,
                personToView.getName().fullName);

        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.selectPerson(personToView);

        assertCommandSuccess(viewCommand, model, new CommandResult(expectedMessage, false, false, true), expectedModel);
        assertEquals(personToView, model.selectedPersonProperty().get());
        assertEquals(expectedModel.getFilteredPersonList(), model.getFilteredPersonList());
    }

    @Test
    public void execute_invalidIndexUnfilteredList_throwsCommandException() {
        Index outOfBoundIndex = Index.fromOneBased(model.getFilteredPersonList().size() + 1);
        ViewCommand viewCommand = new ViewCommand(outOfBoundIndex);

        assertCommandFailure(viewCommand, model, String.format(ViewCommand.MESSAGE_INDEX_OUT_OF_RANGE,
                outOfBoundIndex.getOneBased(), model.getFilteredPersonList().size(), "contacts"));
    }

    @Test
    public void execute_validIndexFilteredList_success() {
        showPersonAtIndex(model, INDEX_FIRST_PERSON);
        Person personToView = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        ViewCommand viewCommand = new ViewCommand(INDEX_FIRST_PERSON);
        String expectedMessage = String.format(ViewCommand.MESSAGE_VIEW_PERSON_SUCCESS,
                personToView.getName().fullName);

        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.updateFilteredPersonList(person -> person.equals(personToView));
        expectedModel.selectPerson(personToView);

        assertCommandSuccess(viewCommand, model, new CommandResult(expectedMessage, false, false, true), expectedModel);
        assertEquals(personToView, model.selectedPersonProperty().get());
    }

    @Test
    public void execute_repeatedViews_preservesListAndChangesSelection() throws Exception {
        List<Person> original = List.copyOf(model.getFilteredPersonList());
        new ViewCommand(INDEX_FIRST_PERSON).execute(model);
        new ViewCommand(INDEX_SECOND_PERSON).execute(model);
        assertEquals(original.get(1), model.selectedPersonProperty().get());
        assertEquals(original, model.getFilteredPersonList());
    }

    @Test
    public void execute_filteredSecondIndex_selectsSecondMatchWithoutResettingFilter() throws Exception {
        List<Person> original = List.copyOf(model.getFilteredPersonList());
        model.updateFilteredPersonList(person -> !person.equals(original.getFirst()));
        List<Person> matches = List.copyOf(model.getFilteredPersonList());
        new ViewCommand(INDEX_SECOND_PERSON).execute(model);
        assertEquals(matches.get(1), model.selectedPersonProperty().get());
        assertEquals(matches, model.getFilteredPersonList());
    }

    @Test
    public void execute_invalidIndex_preservesExistingSelection() throws Exception {
        new ViewCommand(INDEX_FIRST_PERSON).execute(model);
        Person selected = model.selectedPersonProperty().get();
        Index invalid = Index.fromZeroBased(model.getFilteredPersonList().size());
        assertCommandFailure(new ViewCommand(invalid), model, String.format(ViewCommand.MESSAGE_INDEX_OUT_OF_RANGE,
                invalid.getOneBased(), model.getFilteredPersonList().size(), "contacts"));
        assertEquals(selected, model.selectedPersonProperty().get());
    }

    @Test
    public void execute_emptyList_hasNoSelection() {
        model.updateFilteredPersonList(Model.PREDICATE_SHOW_NO_PERSONS);
        assertCommandFailure(new ViewCommand(INDEX_FIRST_PERSON), model,
                ViewCommand.MESSAGE_EMPTY_LIST);
        assertNull(model.selectedPersonProperty().get());
    }

    @Test
    public void execute_invalidIndexFilteredList_throwsCommandException() {
        showPersonAtIndex(model, INDEX_FIRST_PERSON);
        ViewCommand viewCommand = new ViewCommand(INDEX_SECOND_PERSON);

        assertCommandFailure(viewCommand, model,
                "Index 2 is out of range. The displayed list has 1 contact. Enter an index from 1 to 1.");
    }

    @Test
    public void execute_twoDisplayedContacts_reportsDisplayedCountAndRange() {
        List<Person> displayed = List.copyOf(model.getFilteredPersonList().subList(0, 2));
        model.updateFilteredPersonList(displayed::contains);
        assertCommandFailure(new ViewCommand(Index.fromOneBased(3)), model,
                "Index 3 is out of range. The displayed list has 2 contacts. Enter an index from 1 to 2.");
        assertNull(model.selectedPersonProperty().get());
    }

    @Test
    public void execute_afterClear_explainsEmptyDisplayWithoutSelectingStoredContact() throws Exception {
        new ViewCommand(INDEX_FIRST_PERSON).execute(model);
        new ClearCommand().execute(model);
        assertCommandFailure(new ViewCommand(INDEX_FIRST_PERSON), model,
                "No contacts are currently displayed. Use list to show all contacts, or find KEYWORD to search.");
        assertNull(model.selectedPersonProperty().get());
        assertFalse(model.getAddressBook().getPersonList().isEmpty());
    }

    @Test
    public void execute_noMatches_explainsEmptyDisplayWithoutResettingFilter() {
        model.updateFilteredPersonList(person -> false);
        assertCommandFailure(new ViewCommand(INDEX_SECOND_PERSON), model, ViewCommand.MESSAGE_EMPTY_LIST);
        assertTrue(model.getFilteredPersonList().isEmpty());
    }

    @Test
    public void execute_emptyAddressBook_explainsEmptyDisplay() {
        Model emptyModel = new ModelManager();
        assertCommandFailure(new ViewCommand(INDEX_FIRST_PERSON), emptyModel, ViewCommand.MESSAGE_EMPTY_LIST);
    }

    @Test
    public void execute_maximumIndex_reportsRangeWithoutOverflow() {
        Index maximumIndex = Index.fromOneBased(Integer.MAX_VALUE);
        assertCommandFailure(new ViewCommand(maximumIndex), model,
                String.format(ViewCommand.MESSAGE_INDEX_OUT_OF_RANGE,
                        maximumIndex.getOneBased(), model.getFilteredPersonList().size(), "contacts"));
    }

    @Test
    public void modifiesData_view_returnsFalse() {
        assertFalse(new ViewCommand(INDEX_FIRST_PERSON).modifiesData());
    }

    @Test
    public void equals() {
        ViewCommand viewFirstCommand = new ViewCommand(INDEX_FIRST_PERSON);
        ViewCommand viewSecondCommand = new ViewCommand(INDEX_SECOND_PERSON);

        assertTrue(viewFirstCommand.equals(viewFirstCommand));
        assertTrue(viewFirstCommand.equals(new ViewCommand(INDEX_FIRST_PERSON)));
        assertFalse(viewFirstCommand.equals(viewSecondCommand));
        assertFalse(viewFirstCommand.equals(1));
        assertFalse(viewFirstCommand.equals(null));
    }

    @Test
    public void toStringMethod() {
        ViewCommand viewCommand = new ViewCommand(INDEX_FIRST_PERSON);
        String expected = ViewCommand.class.getCanonicalName() + "{targetIndex=" + INDEX_FIRST_PERSON + "}";
        assertEquals(expected, viewCommand.toString());
    }
}
