package seedu.address.model;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.logging.Logger;

import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import seedu.address.commons.core.GuiSettings;
import seedu.address.commons.core.LogsCenter;
import seedu.address.model.person.Person;

/**
 * Represents the in-memory model of the address book data.
 */
public class ModelManager implements Model {
    private static final Logger logger = LogsCenter.getLogger(ModelManager.class);

    private final AddressBook addressBook;
    private final UserPrefs userPrefs;
    private final FilteredList<Person> filteredPersons;
    private final SortedList<Person> sortedPersons;
    private boolean archiveScope;
    /** The predicate of the current list, search or filter. */
    private Predicate<? super Person> displayPredicate = PREDICATE_SHOW_ALL_PERSONS;
    /** Persons kept in the displayed list after a change, although they may not match {@link #displayPredicate}. */
    private final Set<Person> keptDisplayed = new HashSet<>();
    /** The order chosen with {@code sort}, or {@code null} for the order the persons were added in. */
    private Comparator<? super Person> sortOrder;
    /**
     * Positions in the sorted list of the persons shown when a person was first changed after sorting, or
     * {@code null} if nobody has been changed since. While set, changed persons keep their positions.
     */
    private Map<Person, Integer> keptPositions;
    /** Transient display selection; never written to the address book file. */
    private final ReadOnlyObjectWrapper<Person> selectedPerson = new ReadOnlyObjectWrapper<>();

    /**
     * Initializes a ModelManager with the given addressBook and userPrefs.
     */
    public ModelManager(ReadOnlyAddressBook addressBook, ReadOnlyUserPrefs userPrefs) {
        requireAllNonNull(addressBook, userPrefs);

        logger.fine("Initializing with address book: " + addressBook + " and user prefs " + userPrefs);

        this.addressBook = new AddressBook(addressBook);
        this.userPrefs = new UserPrefs(userPrefs);
        filteredPersons = new FilteredList<>(this.addressBook.getPersonList(), PREDICATE_SHOW_ALL_PERSONS);
        filteredPersons.setPredicate(this::isDisplayed);
        sortedPersons = new SortedList<>(filteredPersons);
    }

    public ModelManager() {
        this(new AddressBook(), new UserPrefs());
    }

    //=========== UserPrefs ==================================================================================

    @Override
    public ReadOnlyUserPrefs getUserPrefs() {
        return userPrefs;
    }

    @Override
    public GuiSettings getGuiSettings() {
        return userPrefs.getGuiSettings();
    }

    @Override
    public void setGuiSettings(GuiSettings guiSettings) {
        requireNonNull(guiSettings);
        userPrefs.setGuiSettings(guiSettings);
    }

    //=========== AddressBook ================================================================================

    @Override
    public void setAddressBook(ReadOnlyAddressBook addressBook) {
        applySortOrder();
        keptDisplayed.clear();
        this.addressBook.resetData(addressBook);
        selectedPerson.set(null);
    }

    @Override
    public ReadOnlyAddressBook getAddressBook() {
        return addressBook;
    }

    @Override
    public boolean hasPerson(Person person) {
        requireNonNull(person);
        return addressBook.hasPerson(person);
    }

    @Override
    public void deletePerson(Person target) {
        keptDisplayed.remove(target);
        addressBook.removePerson(target);
        if (target.equals(selectedPerson.get())) {
            selectedPerson.set(null);
        }
    }

    @Override
    public void addPerson(Person person) {
        addressBook.addPerson(person);
        setArchiveScope(false);
    }

    @Override
    public void setPerson(Person target, Person editedPerson) {
        requireAllNonNull(target, editedPerson);

        // a kept person stays shown after another change, unless the change moves it out of the list's scope
        if (keptDisplayed.remove(target) && getScopePredicate().test(editedPerson)) {
            keptDisplayed.add(editedPerson);
        }
        keepPositionInSortedList(target, editedPerson);
        addressBook.setPerson(target, editedPerson);
        if (target.equals(selectedPerson.get())) {
            selectedPerson.set(filteredPersons.contains(editedPerson) ? editedPerson : null);
        }
    }

    @Override
    public void setPersonKeepingDisplayed(Person target, Person editedPerson) {
        requireAllNonNull(target, editedPerson);

        if (filteredPersons.contains(target)) {
            keptDisplayed.add(target);
        }
        setPerson(target, editedPerson);
    }

    /**
     * Makes {@code editedPerson} take the place of {@code target} in a sorted list, so that changing a person does
     * not change the indices shown. The list is sorted again by the next {@code sort}, {@code list}, {@code find}
     * or {@code filter}.
     */
    private void keepPositionInSortedList(Person target, Person editedPerson) {
        if (sortOrder == null) {
            // without a sort order, the list follows the address book, where a replaced person keeps its place
            return;
        }
        if (keptPositions == null) {
            keptPositions = new HashMap<>();
            for (int i = 0; i < sortedPersons.size(); i++) {
                keptPositions.put(sortedPersons.get(i), i);
            }
            sortedPersons.setComparator(this::compareKeepingPositions);
        }
        Integer position = keptPositions.remove(target);
        if (position != null) {
            keptPositions.put(editedPerson, position);
        }
    }

    /**
     * Orders persons with a kept position by that position, before any other persons, which follow the sort order.
     */
    private int compareKeepingPositions(Person first, Person second) {
        // persons without a kept position share the last position, and are ordered among themselves by the sort order
        int firstPosition = keptPositions.getOrDefault(first, Integer.MAX_VALUE);
        int secondPosition = keptPositions.getOrDefault(second, Integer.MAX_VALUE);
        if (firstPosition != secondPosition) {
            return Integer.compare(firstPosition, secondPosition);
        }
        return sortOrder.compare(first, second);
    }

    /** Sorts the list by {@link #sortOrder} again, dropping any positions kept for changed persons. */
    private void applySortOrder() {
        if (keptPositions != null) {
            keptPositions = null;
            sortedPersons.setComparator(sortOrder);
        }
    }

    private boolean isDisplayed(Person person) {
        return keptDisplayed.contains(person) || displayPredicate.test(person);
    }

    //=========== Filtered Person List Accessors =============================================================

    /**
     * Returns an unmodifiable view of the list of {@code Person} backed by the internal list of
     * {@code addressBook}, filtered by the current predicate and ordered by the current comparator.
     */
    @Override
    public ObservableList<Person> getFilteredPersonList() {
        return sortedPersons;
    }

    @Override
    public boolean isShowingArchiveList() {
        return archiveScope;
    }

    @Override
    public void setArchiveScope(boolean archiveScope) {
        this.archiveScope = archiveScope;
        updateFilteredPersonList(getScopePredicate());
    }

    private Predicate<Person> getScopePredicate() {
        return archiveScope ? PREDICATE_SHOW_ARCHIVED_PERSONS : PREDICATE_SHOW_ALL_PERSONS;
    }

    @Override
    public ReadOnlyObjectProperty<Person> selectedPersonProperty() {
        return selectedPerson.getReadOnlyProperty();
    }

    @Override
    public void selectPerson(Person person) {
        if (person != null && !filteredPersons.contains(person)) {
            throw new IllegalArgumentException("Only a displayed contact can be selected.");
        }
        selectedPerson.set(person);
    }

    @Override
    public void updateFilteredPersonList(Predicate<Person> predicate) {
        requireNonNull(predicate);
        applySortOrder();
        keptDisplayed.clear();
        displayPredicate = predicate;
        // a new predicate object, so that the list is filtered again
        filteredPersons.setPredicate(this::isDisplayed);
        if (!filteredPersons.contains(selectedPerson.get())) {
            selectedPerson.set(null);
        }
    }

    @Override
    public void filterPersonsInCurrentList(Predicate<Person> predicate) {
        requireNonNull(predicate);
        updateFilteredPersonList(getScopePredicate().and(predicate));
    }

    //=========== Sorted Person List Accessors ===============================================================

    @Override
    public void updateSortedPersonList(Comparator<Person> comparator) {
        requireNonNull(comparator);
        sortedPersons.setComparator(comparator);
        rememberSortOrder();
    }

    @Override
    public void sortPersonsByMostRecent() {
        // the address book keeps persons in the order they were added, so a later index means more recent
        ObservableList<Person> addedOrder = addressBook.getPersonList();
        sortedPersons.setComparator(Comparator.comparingInt(addedOrder::indexOf).reversed());
        rememberSortOrder();
    }

    @Override
    public void resetSortedPersonList() {
        sortedPersons.setComparator(null);
        rememberSortOrder();
    }

    /** Records the comparator just given to the sorted list as the sort order, dropping any kept positions. */
    private void rememberSortOrder() {
        sortOrder = sortedPersons.getComparator();
        keptPositions = null;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof ModelManager otherModelManager)) {
            return false;
        }

        return addressBook.equals(otherModelManager.addressBook)
                && userPrefs.equals(otherModelManager.userPrefs)
                && archiveScope == otherModelManager.archiveScope
                && sortedPersons.equals(otherModelManager.sortedPersons);
    }

}
