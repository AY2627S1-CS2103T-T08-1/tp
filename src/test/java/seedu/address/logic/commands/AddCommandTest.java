package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalPersons.ALICE;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;

import org.junit.jupiter.api.Test;

import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.collections.ObservableList;
import seedu.address.commons.core.GuiSettings;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.ReadOnlyAddressBook;
import seedu.address.model.ReadOnlyUserPrefs;
import seedu.address.model.person.Person;
import seedu.address.testutil.PersonBuilder;

public class AddCommandTest {

    @Test
    public void execute_sameNameWithDifferentPatientDetails_addsContact() throws Exception {
        Model model = new ModelManager();
        model.addPerson(ALICE);
        Person anotherAlice = new PersonBuilder(ALICE).withPhone("91234567")
                .withAddress("Another Street").build();
        new AddCommand(anotherAlice).execute(model);
        assertEquals(List.of(ALICE, anotherAlice), model.getAddressBook().getPersonList());
    }

    @Test
    public void execute_samePatientPhoneOrEmail_rejectsContact() {
        Person existing = new PersonBuilder(ALICE).withPatientEmail("alice@example.com").build();
        Model model = new ModelManager();
        model.addPerson(existing);
        Person samePhone = new PersonBuilder(existing).withName("Another Patient")
                .withPatientEmail("other@example.com").withAddress("Another Street").build();
        Person sameEmail = new PersonBuilder(existing).withName("Another Patient").withPhone("91234567")
                .withPatientEmail("ALICE@EXAMPLE.COM").withAddress("Another Street").build();
        assertCommandFailure(new AddCommand(samePhone), model, AddCommand.MESSAGE_DUPLICATE_PERSON);
        assertCommandFailure(new AddCommand(sameEmail), model, AddCommand.MESSAGE_DUPLICATE_PERSON);
    }

    @Test
    public void execute_samePatientPhoneAsArchivedContact_mentionsArchive() {
        Model model = new ModelManager();
        model.addPerson(ALICE.copyWithArchivedStatus(true));
        Person samePhone = new PersonBuilder(ALICE).withName("Another Patient").withAddress("Another Street").build();

        assertCommandFailure(new AddCommand(samePhone), model,
                AddCommand.MESSAGE_DUPLICATE_PERSON + " " + Messages.MESSAGE_CLASHING_PERSON_ARCHIVED);
    }

    @Test
    public void execute_sameNameAndAddressIgnoringCaseAndSpacing_requiresDifferentiator() throws Exception {
        Model model = new ModelManager();
        Person existing = new PersonBuilder(ALICE).withAddress("Block 123 Main Street #01-02").build();
        model.addPerson(existing.copyWithArchivedStatus(true));
        Person sameAddress = new PersonBuilder(existing).withPhone("91234567")
                .withAddress("bLoCk123   MAINstreet#01-02  ").build();
        // the existing contact is archived, so the message says where to find it
        assertCommandFailure(new AddCommand(sameAddress), model,
                Person.MESSAGE_NAME_ADDRESS_CONFLICT + " " + Messages.MESSAGE_CLASHING_PERSON_ARCHIVED);
        Person differentiated = new PersonBuilder(sameAddress).withName("Alice Pauline 2").build();
        new AddCommand(differentiated).execute(model);
        assertEquals(2, model.getAddressBook().getPersonList().size());
    }

    @Test
    public void constructor_nullPerson_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new AddCommand(null));
    }

    @Test
    public void execute_personAcceptedByModel_addSuccessful() throws Exception {
        ModelStubAcceptingPersonAdded modelStub = new ModelStubAcceptingPersonAdded();
        Person validPerson = new PersonBuilder().build();

        CommandResult commandResult = new AddCommand(validPerson).execute(modelStub);

        assertEquals(String.format(AddCommand.MESSAGE_SUCCESS, Messages.format(validPerson)),
                commandResult.getFeedbackToUser());
        assertEquals(List.of(validPerson), modelStub.personsAdded);
    }

    @Test
    public void execute_duplicatePerson_throwsCommandException() {
        Person validPerson = new PersonBuilder().build();
        AddCommand addCommand = new AddCommand(validPerson);
        ModelStub modelStub = new ModelStubWithPerson(validPerson);

        assertThrows(CommandException.class, AddCommand.MESSAGE_DUPLICATE_PERSON, () -> addCommand.execute(modelStub));
    }

    @Test
    public void equals() {
        Person alice = new PersonBuilder().withName("Alice").build();
        Person bob = new PersonBuilder().withName("Bob").build();
        AddCommand addAliceCommand = new AddCommand(alice);
        AddCommand addBobCommand = new AddCommand(bob);

        // same object -> returns true
        assertTrue(addAliceCommand.equals(addAliceCommand));

        // same values -> returns true
        AddCommand addAliceCommandCopy = new AddCommand(alice);
        assertTrue(addAliceCommand.equals(addAliceCommandCopy));

        // different types -> returns false
        assertFalse(addAliceCommand.equals(1));

        // null -> returns false
        assertFalse(addAliceCommand.equals(null));

        // different person -> returns false
        assertFalse(addAliceCommand.equals(addBobCommand));
    }

    @Test
    public void toStringMethod() {
        AddCommand addCommand = new AddCommand(ALICE);
        String expected = AddCommand.class.getCanonicalName() + "{toAdd=" + ALICE + "}";
        assertEquals(expected, addCommand.toString());
    }

    /**
     * A default model stub that has all of the methods failing.
     */
    private class ModelStub implements Model {
        @Override
        public ReadOnlyObjectProperty<Person> selectedPersonProperty() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void selectPerson(Person person) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public ReadOnlyUserPrefs getUserPrefs() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public GuiSettings getGuiSettings() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void setGuiSettings(GuiSettings guiSettings) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void addPerson(Person person) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void setAddressBook(ReadOnlyAddressBook newData) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public Runnable createRestorePoint() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public ReadOnlyAddressBook getAddressBook() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public boolean hasPerson(Person person) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void deletePerson(Person target) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void setPerson(Person target, Person editedPerson) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void setPersonKeepingDisplayed(Person target, Person editedPerson) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public ObservableList<Person> getFilteredPersonList() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void filterPersonsInCurrentList(Predicate<Person> predicate) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public boolean isShowingArchiveList() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void setArchiveScope(boolean archiveScope) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void updateFilteredPersonList(Predicate<Person> predicate) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void updateSortedPersonList(Comparator<Person> comparator) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void sortPersonsByMostRecent() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void resetSortedPersonList() {
            throw new AssertionError("This method should not be called.");
        }
    }

    /**
     * A Model stub that contains a single person.
     */
    private class ModelStubWithPerson extends ModelStub {
        private final Person person;

        ModelStubWithPerson(Person person) {
            requireNonNull(person);
            this.person = person;
        }

        @Override
        public boolean hasPerson(Person person) {
            requireNonNull(person);
            return this.person.isSamePerson(person);
        }

        @Override
        public ReadOnlyAddressBook getAddressBook() {
            AddressBook addressBook = new AddressBook();
            addressBook.addPerson(person);
            return addressBook;
        }
    }

    /**
     * A Model stub that always accepts the person being added.
     */
    private class ModelStubAcceptingPersonAdded extends ModelStub {
        final ArrayList<Person> personsAdded = new ArrayList<>();

        @Override
        public boolean hasPerson(Person person) {
            requireNonNull(person);
            return personsAdded.stream().anyMatch(person::isSamePerson);
        }

        @Override
        public void addPerson(Person person) {
            requireNonNull(person);
            personsAdded.add(person);
        }

        @Override
        public ReadOnlyAddressBook getAddressBook() {
            return new AddressBook();
        }
    }

}
