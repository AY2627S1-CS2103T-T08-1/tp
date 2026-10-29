package seedu.address.model;

import java.util.Comparator;
import java.util.function.Predicate;

import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.collections.ObservableList;
import seedu.address.commons.core.GuiSettings;
import seedu.address.model.person.Person;

/**
 * The API of the Model component.
 */
public interface Model {
    /** Predicate that selects all active persons. */
    Predicate<Person> PREDICATE_SHOW_ALL_PERSONS = person -> !person.isArchived();

    /** Predicate that selects archived persons. */
    Predicate<Person> PREDICATE_SHOW_ARCHIVED_PERSONS = Person::isArchived;

    /** {@code Predicate} that always evaluates to false, hiding all persons without deleting them. */
    Predicate<Person> PREDICATE_SHOW_NO_PERSONS = unused -> false;

    /**
     * Returns the user prefs.
     */
    ReadOnlyUserPrefs getUserPrefs();

    /**
     * Returns the user prefs' GUI settings.
     */
    GuiSettings getGuiSettings();

    /**
     * Sets the user prefs' GUI settings.
     */
    void setGuiSettings(GuiSettings guiSettings);

    /**
     * Replaces address book data with the data in {@code addressBook}.
     */
    void setAddressBook(ReadOnlyAddressBook addressBook);

    /**
     * Returns an action that puts the address book and the displayed list, including its scope, search and order,
     * back to how they are now, and clears the selection.
     */
    Runnable createRestorePoint();

    /** Returns the AddressBook */
    ReadOnlyAddressBook getAddressBook();

    /**
     * Returns true if a person with the same identity as {@code person} exists in the address book.
     */
    boolean hasPerson(Person person);

    /**
     * Deletes the given person.
     * The person must exist in the address book.
     */
    void deletePerson(Person target);

    /**
     * Adds the given person.
     * {@code person} must not already exist in the address book.
     */
    void addPerson(Person person);

    /**
     * Replaces the given person {@code target} with {@code editedPerson}.
     * {@code target} must exist in the address book.
     * The person identity of {@code editedPerson} must not be the same as another existing person in the address book.
     * In a sorted list, {@code editedPerson} takes the position of {@code target} until the list is sorted or
     * filtered again.
     */
    void setPerson(Person target, Person editedPerson);

    /**
     * Replaces {@code target} with {@code editedPerson} like {@link #setPerson(Person, Person)}, and keeps
     * {@code editedPerson} in the displayed list even if it no longer matches the current search.
     * The rest of the displayed list and its order are unchanged.
     */
    void setPersonKeepingDisplayed(Person target, Person editedPerson);

    /** Returns an unmodifiable view of the filtered person list, in the current sort order */
    ObservableList<Person> getFilteredPersonList();

    /** Returns whether the displayed list is scoped to archived contacts, including search and sort results. */
    boolean isShowingArchiveList();

    /**
     * Selects the active or archive scope and shows all contacts in it, preserving the current sort order.
     * Subsequent search filters do not change this scope.
     */
    void setArchiveScope(boolean archiveScope);

    /** Returns the selected contact, or null when no contact is selected, as a read-only property. */
    ReadOnlyObjectProperty<Person> selectedPersonProperty();

    /** Selects a contact in the displayed list, or clears selection when {@code person} is null. */
    void selectPerson(Person person);

    /**
     * Updates the displayed filter without changing the active or archive scope.
     * Use {@link #setArchiveScope(boolean)} to switch lists, or {@link #filterPersonsInCurrentList(Predicate)}
     * to apply search criteria within the selected scope.
     * @throws NullPointerException if {@code predicate} is null.
     */
    void updateFilteredPersonList(Predicate<Person> predicate);

    /**
     * Filters contacts within the current active or archived list, replacing any previous search criteria.
     */
    void filterPersonsInCurrentList(Predicate<Person> predicate);

    /**
     * Orders the displayed person list by {@code comparator}. The stored data is not reordered.
     * @throws NullPointerException if {@code comparator} is null.
     */
    void updateSortedPersonList(Comparator<Person> comparator);

    /**
     * Orders the displayed person list with the most recently added person first.
     * The stored data is not reordered.
     */
    void sortPersonsByMostRecent();

    /**
     * Restores the displayed person list to the order in which persons were added.
     */
    void resetSortedPersonList();
}
