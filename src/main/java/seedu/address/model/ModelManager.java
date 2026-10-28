package seedu.address.model;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.util.Comparator;
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

        addressBook.setPerson(target, editedPerson);
        if (target.equals(selectedPerson.get())) {
            selectedPerson.set(filteredPersons.contains(editedPerson) ? editedPerson : null);
        }
    }

    @Override
    public void setPersonKeepingDisplayed(Person target, Person editedPerson) {
        requireAllNonNull(target, editedPerson);

        Predicate<? super Person> displayedBefore = filteredPersons.getPredicate();
        filteredPersons.setPredicate(person -> person.equals(editedPerson)
                || displayedBefore == null || displayedBefore.test(person));
        setPerson(target, editedPerson);
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
        filteredPersons.setPredicate(predicate);
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
    }

    @Override
    public void sortPersonsByMostRecent() {
        // the address book keeps persons in the order they were added, so a later index means more recent
        ObservableList<Person> addedOrder = addressBook.getPersonList();
        sortedPersons.setComparator(Comparator.comparingInt(addedOrder::indexOf).reversed());
    }

    @Override
    public void resetSortedPersonList() {
        sortedPersons.setComparator(null);
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
