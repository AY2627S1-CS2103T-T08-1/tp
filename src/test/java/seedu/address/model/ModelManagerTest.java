package seedu.address.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.model.Model.PREDICATE_SHOW_ALL_PERSONS;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalPersons.ALICE;
import static seedu.address.testutil.TypicalPersons.BENSON;
import static seedu.address.testutil.TypicalPersons.CARL;
import static seedu.address.testutil.TypicalPersons.DANIEL;
import static seedu.address.testutil.TypicalPersons.GEORGE;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.GuiSettings;
import seedu.address.model.person.NameContainsKeywordsPredicate;
import seedu.address.model.person.Person;
import seedu.address.model.person.PersonComparators;
import seedu.address.model.person.PersonHasTagPredicate;
import seedu.address.testutil.AddressBookBuilder;
import seedu.address.testutil.PersonBuilder;

public class ModelManagerTest {

    private ModelManager modelManager = new ModelManager();

    @Test
    public void setArchiveScope_switchesListsClearsFiltersAndPreservesSort() {
        Person archivedBenson = BENSON.copyWithArchivedStatus(true);
        Person archivedCarl = CARL.copyWithArchivedStatus(true);
        modelManager.addPerson(ALICE);
        modelManager.addPerson(archivedBenson);
        modelManager.addPerson(archivedCarl);
        assertFalse(modelManager.isShowingArchiveList());

        modelManager.setArchiveScope(true);
        modelManager.updateSortedPersonList(PersonComparators.byName(false));
        modelManager.filterPersonsInCurrentList(archivedBenson::equals);
        assertEquals(List.of(archivedBenson), modelManager.getFilteredPersonList());

        modelManager.setArchiveScope(true);
        assertTrue(modelManager.isShowingArchiveList());
        assertEquals(List.of(archivedCarl, archivedBenson), modelManager.getFilteredPersonList());
        modelManager.selectPerson(archivedBenson);
        modelManager.setArchiveScope(false);
        assertFalse(modelManager.isShowingArchiveList());
        assertEquals(List.of(ALICE), modelManager.getFilteredPersonList());
        assertNull(modelManager.selectedPersonProperty().get());
    }

    @Test
    public void updateFilteredPersonList_neverChangesExplicitScope() {
        Person archived = BENSON.copyWithArchivedStatus(true);
        modelManager.addPerson(ALICE);
        modelManager.addPerson(archived);
        modelManager.setArchiveScope(true);

        // Even a list predicate is only a display filter; switching scope requires the explicit setter.
        modelManager.updateFilteredPersonList(PREDICATE_SHOW_ALL_PERSONS);
        assertTrue(modelManager.isShowingArchiveList());
        modelManager.updateFilteredPersonList(person -> false);
        modelManager.sortPersonsByMostRecent();
        assertTrue(modelManager.getFilteredPersonList().isEmpty());
        modelManager.filterPersonsInCurrentList(person -> true);
        assertEquals(List.of(archived), modelManager.getFilteredPersonList());
    }

    @Test
    public void addPerson_fromArchiveScope_restoresActiveScope() {
        Person archived = BENSON.copyWithArchivedStatus(true);
        modelManager.addPerson(archived);
        modelManager.setArchiveScope(true);
        modelManager.addPerson(ALICE);

        assertFalse(modelManager.isShowingArchiveList());
        assertEquals(List.of(ALICE), modelManager.getFilteredPersonList());
    }

    @Test
    public void equals_differentScopesWithEmptyResults_areNotEqual() {
        ModelManager archiveModel = new ModelManager();
        archiveModel.setArchiveScope(true);
        assertFalse(modelManager.equals(archiveModel));
        modelManager.setArchiveScope(true);
        assertEquals(modelManager, archiveModel);
    }

    @Test
    public void constructor() {
        assertEquals(new UserPrefs(), modelManager.getUserPrefs());
        assertEquals(new GuiSettings(), modelManager.getGuiSettings());
        assertEquals(new AddressBook(), new AddressBook(modelManager.getAddressBook()));
    }

    @Test
    public void constructor_validUserPrefs_copiesUserPrefs() {
        UserPrefs userPrefs = new UserPrefs();
        userPrefs.setGuiSettings(new GuiSettings(1, 2, 3, 4));
        modelManager = new ModelManager(new AddressBook(), userPrefs);
        assertEquals(userPrefs, modelManager.getUserPrefs());

        // Modifying userPrefs should not modify modelManager's userPrefs
        UserPrefs oldUserPrefs = new UserPrefs(userPrefs);
        userPrefs.setGuiSettings(new GuiSettings(5, 6, 7, 8));
        assertEquals(oldUserPrefs, modelManager.getUserPrefs());
    }

    @Test
    public void setGuiSettings_nullGuiSettings_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> modelManager.setGuiSettings(null));
    }

    @Test
    public void setGuiSettings_validGuiSettings_setsGuiSettings() {
        GuiSettings guiSettings = new GuiSettings(1, 2, 3, 4);
        modelManager.setGuiSettings(guiSettings);
        assertEquals(guiSettings, modelManager.getGuiSettings());
    }

    @Test
    public void hasPerson_nullPerson_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> modelManager.hasPerson(null));
    }

    @Test
    public void hasPerson_personNotInAddressBook_returnsFalse() {
        assertFalse(modelManager.hasPerson(ALICE));
    }

    @Test
    public void hasPerson_personInAddressBook_returnsTrue() {
        modelManager.addPerson(ALICE);
        assertTrue(modelManager.hasPerson(ALICE));
    }

    @Test
    public void getFilteredPersonList_modifyList_throwsUnsupportedOperationException() {
        assertThrows(UnsupportedOperationException.class, () -> modelManager.getFilteredPersonList().remove(0));
    }

    @Test
    public void updateSortedPersonList_nullComparator_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> modelManager.updateSortedPersonList(null));
    }

    @Test
    public void updateSortedPersonList_ordersDisplayedListOnly() {
        // added in the order DANIEL, ALICE, CARL, BENSON
        modelManager = modelWithPersonsAddedInOrder();

        modelManager.updateSortedPersonList(PersonComparators.byName(true));
        assertEquals(List.of(ALICE, BENSON, CARL, DANIEL), modelManager.getFilteredPersonList());

        modelManager.updateSortedPersonList(PersonComparators.byName(false));
        assertEquals(List.of(DANIEL, CARL, BENSON, ALICE), modelManager.getFilteredPersonList());

        // stored order is untouched
        assertEquals(List.of(DANIEL, ALICE, CARL, BENSON), modelManager.getAddressBook().getPersonList());
    }

    @Test
    public void sortPersonsByMostRecent_newestFirst() {
        modelManager = modelWithPersonsAddedInOrder();

        modelManager.sortPersonsByMostRecent();
        assertEquals(List.of(BENSON, CARL, ALICE, DANIEL), modelManager.getFilteredPersonList());

        // a person added afterwards appears first
        modelManager.addPerson(GEORGE);
        assertEquals(GEORGE, modelManager.getFilteredPersonList().get(0));

        // stored order is untouched
        assertEquals(List.of(DANIEL, ALICE, CARL, BENSON, GEORGE),
                modelManager.getAddressBook().getPersonList());
    }

    @Test
    public void sortPersonsByMostRecent_editedPersonKeepsPosition() {
        modelManager = modelWithPersonsAddedInOrder();
        modelManager.setPerson(ALICE, new PersonBuilder(ALICE).withTags("LOW").build());

        modelManager.sortPersonsByMostRecent();
        assertEquals(BENSON, modelManager.getFilteredPersonList().get(0));
        assertEquals(DANIEL, modelManager.getFilteredPersonList().get(3));
    }

    @Test
    public void sort_combinedWithFilter_sortsOnlyMatchingPersons() {
        modelManager = modelWithPersonsAddedInOrder();

        modelManager.updateFilteredPersonList(new PersonHasTagPredicate("HIGH"));
        modelManager.updateSortedPersonList(PersonComparators.byName(true));
        assertEquals(List.of(ALICE, DANIEL), modelManager.getFilteredPersonList());

        // clearing the filter keeps the sort
        modelManager.updateFilteredPersonList(PREDICATE_SHOW_ALL_PERSONS);
        assertEquals(List.of(ALICE, BENSON, CARL, DANIEL), modelManager.getFilteredPersonList());
    }

    @Test
    public void resetSortedPersonList_restoresAddedOrder() {
        modelManager = modelWithPersonsAddedInOrder();
        modelManager.updateSortedPersonList(PersonComparators.byName(true));

        modelManager.resetSortedPersonList();
        assertEquals(List.of(DANIEL, ALICE, CARL, BENSON), modelManager.getFilteredPersonList());
    }

    private static ModelManager modelWithPersonsAddedInOrder() {
        AddressBook addressBook = new AddressBookBuilder()
                .withPerson(DANIEL).withPerson(ALICE).withPerson(CARL).withPerson(BENSON).build();
        return new ModelManager(addressBook, new UserPrefs());
    }

    @Test
    public void equals() {
        AddressBook addressBook = new AddressBookBuilder().withPerson(ALICE).withPerson(BENSON).build();
        AddressBook differentAddressBook = new AddressBook();
        UserPrefs userPrefs = new UserPrefs();

        // same values -> returns true
        modelManager = new ModelManager(addressBook, userPrefs);
        ModelManager modelManagerCopy = new ModelManager(addressBook, userPrefs);
        assertTrue(modelManager.equals(modelManagerCopy));

        // same object -> returns true
        assertTrue(modelManager.equals(modelManager));

        // null -> returns false
        assertFalse(modelManager.equals(null));

        // different types -> returns false
        assertFalse(modelManager.equals(5));

        // different addressBook -> returns false
        assertFalse(modelManager.equals(new ModelManager(differentAddressBook, userPrefs)));

        // different filteredList -> returns false
        String[] keywords = ALICE.getName().fullName.split("\\s+");
        modelManager.updateFilteredPersonList(new NameContainsKeywordsPredicate(List.of(keywords)));
        assertFalse(modelManager.equals(new ModelManager(addressBook, userPrefs)));

        // resets modelManager to initial state for upcoming tests
        modelManager.updateFilteredPersonList(PREDICATE_SHOW_ALL_PERSONS);

        // different userPrefs -> returns false
        UserPrefs differentUserPrefs = new UserPrefs();
        differentUserPrefs.setGuiSettings(new GuiSettings(1, 2, 3, 4));
        assertFalse(modelManager.equals(new ModelManager(addressBook, differentUserPrefs)));
    }
}
