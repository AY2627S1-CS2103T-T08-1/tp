package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.testutil.TypicalPersons.ALICE;
import static seedu.address.testutil.TypicalPersons.BENSON;
import static seedu.address.testutil.TypicalPersons.CARL;
import static seedu.address.testutil.TypicalPersons.DANIEL;
import static seedu.address.testutil.TypicalPersons.ELLE;
import static seedu.address.testutil.TypicalPersons.FIONA;
import static seedu.address.testutil.TypicalPersons.GEORGE;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.SortCommand.Criterion;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.PersonComparators;
import seedu.address.model.person.PersonHasTagPredicate;

/**
 * Contains integration tests (interaction with the Model) and unit tests for {@code SortCommand}.
 * The typical address book holds, in added order: ALICE (HIGH), BENSON (LOW), CARL, DANIEL (HIGH),
 * ELLE, FIONA and GEORGE, where only ALICE, BENSON and DANIEL have a priority tag.
 */
public class SortCommandTest {

    private Model model;
    private Model expectedModel;

    @BeforeEach
    public void setUp() {
        model = new ModelManager(getTypicalAddressBook(), new UserPrefs());
        expectedModel = new ModelManager(getTypicalAddressBook(), new UserPrefs());
    }

    @Test
    public void equals() {
        SortCommand priorityDesc = new SortCommand(Criterion.PRIORITY, false);

        // same object -> returns true
        assertTrue(priorityDesc.equals(priorityDesc));

        // same values -> returns true
        assertTrue(priorityDesc.equals(new SortCommand(Criterion.PRIORITY, false)));

        // direction is ignored for recent -> returns true
        assertTrue(new SortCommand(Criterion.RECENT, true).equals(new SortCommand(Criterion.RECENT, false)));

        // different types -> returns false
        assertFalse(priorityDesc.equals(1));

        // null -> returns false
        assertFalse(priorityDesc.equals(null));

        // different direction or criterion -> returns false
        assertFalse(priorityDesc.equals(new SortCommand(Criterion.PRIORITY, true)));
        assertFalse(priorityDesc.equals(new SortCommand(Criterion.NAME, false)));
    }

    @Test
    public void execute_nullModel_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new SortCommand(Criterion.NAME, true).execute(null));
    }

    @Test
    public void execute_priorityDescending_highFirstUntaggedLast() {
        expectedModel.updateSortedPersonList(PersonComparators.byPriority(true));
        assertCommandSuccess(new SortCommand(Criterion.PRIORITY, false), model,
                SortCommand.MESSAGE_SORTED_PRIORITY_DESC, expectedModel);
        assertEquals(List.of(ALICE, DANIEL, BENSON, CARL, ELLE, FIONA, GEORGE), model.getFilteredPersonList());
    }

    @Test
    public void execute_priorityAscending_lowFirstUntaggedLast() {
        expectedModel.updateSortedPersonList(PersonComparators.byPriority(false));
        assertCommandSuccess(new SortCommand(Criterion.PRIORITY, true), model,
                SortCommand.MESSAGE_SORTED_PRIORITY_ASC, expectedModel);
        assertEquals(List.of(BENSON, ALICE, DANIEL, CARL, ELLE, FIONA, GEORGE), model.getFilteredPersonList());
    }

    @Test
    public void execute_nameAscending_sortsAToZ() {
        expectedModel.updateSortedPersonList(PersonComparators.byName(true));
        assertCommandSuccess(new SortCommand(Criterion.NAME, true), model,
                SortCommand.MESSAGE_SORTED_NAME_ASC, expectedModel);
        assertEquals(List.of(ALICE, BENSON, CARL, DANIEL, ELLE, FIONA, GEORGE), model.getFilteredPersonList());
    }

    @Test
    public void execute_nameDescending_sortsZToA() {
        expectedModel.updateSortedPersonList(PersonComparators.byName(false));
        assertCommandSuccess(new SortCommand(Criterion.NAME, false), model,
                SortCommand.MESSAGE_SORTED_NAME_DESC, expectedModel);
        assertEquals(List.of(GEORGE, FIONA, ELLE, DANIEL, CARL, BENSON, ALICE), model.getFilteredPersonList());
    }

    @Test
    public void execute_recent_newestFirst() {
        expectedModel.sortPersonsByMostRecent();
        assertCommandSuccess(new SortCommand(Criterion.RECENT, false), model,
                SortCommand.MESSAGE_SORTED_RECENT, expectedModel);
        assertEquals(List.of(GEORGE, FIONA, ELLE, DANIEL, CARL, BENSON, ALICE), model.getFilteredPersonList());
    }

    @Test
    public void execute_afterFilter_sortsOnlyFilteredPersons() {
        PersonHasTagPredicate high = new PersonHasTagPredicate("HIGH");
        model.updateFilteredPersonList(high);
        expectedModel.updateFilteredPersonList(high);
        expectedModel.updateSortedPersonList(PersonComparators.byName(false));

        assertCommandSuccess(new SortCommand(Criterion.NAME, false), model,
                SortCommand.MESSAGE_SORTED_NAME_DESC, expectedModel);
        assertEquals(List.of(DANIEL, ALICE), model.getFilteredPersonList());
    }

    @Test
    public void execute_doesNotReorderStoredData() throws Exception {
        new SortCommand(Criterion.NAME, false).execute(model);
        assertEquals(getTypicalAddressBook().getPersonList(), model.getAddressBook().getPersonList());
    }

    @Test
    public void modifiesData_returnsFalse() {
        assertFalse(new SortCommand(Criterion.NAME, true).modifiesData());
    }

    @Test
    public void toStringMethod() {
        SortCommand command = new SortCommand(Criterion.PRIORITY, true);
        String expected = SortCommand.class.getCanonicalName() + "{criterion=PRIORITY, ascending=true}";
        assertEquals(expected, command.toString());
    }
}
