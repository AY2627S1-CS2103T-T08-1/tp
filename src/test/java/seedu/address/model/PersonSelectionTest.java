package seedu.address.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalPersons.ALICE;
import static seedu.address.testutil.TypicalPersons.BENSON;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import seedu.address.model.person.Person;
import seedu.address.model.person.exceptions.DuplicatePersonException;
import seedu.address.testutil.PersonBuilder;

/** Tests transient selection across contact mutations and changes to the displayed list. */
public class PersonSelectionTest {
    private final Model model = new ModelManager();

    @BeforeEach
    public void setUp() {
        model.addPerson(ALICE);
        model.addPerson(BENSON);
        model.selectPerson(ALICE);
    }

    @Test
    public void selectPerson_null_clearsSelection() {
        model.selectPerson(null);
        assertNull(model.selectedPersonProperty().get());
    }

    @Test
    public void selectPerson_hiddenContact_rejectsWithoutChangingSelection() {
        model.updateFilteredPersonList(ALICE::equals);
        assertThrows(IllegalArgumentException.class, () -> model.selectPerson(BENSON));
        assertEquals(ALICE, model.selectedPersonProperty().get());
    }

    @Test
    public void setPerson_selectedContact_updatesAllDetailsIncludingName() {
        Person edited = new PersonBuilder(ALICE).withName("Updated Alice").withPhone("98765432").build();
        model.setPerson(ALICE, edited);
        assertEquals(edited, model.selectedPersonProperty().get());
    }

    @Test
    public void setPerson_otherContact_keepsSelection() {
        model.setPerson(BENSON, new PersonBuilder(BENSON).withPhone("98765432").build());
        assertEquals(ALICE, model.selectedPersonProperty().get());
    }

    @Test
    public void setPerson_invalidReplacement_preservesSelection() {
        assertThrows(DuplicatePersonException.class, () -> model.setPerson(ALICE, BENSON));
        assertEquals(ALICE, model.selectedPersonProperty().get());
    }

    @Test
    public void deletePerson_selectedContact_clearsSelection() {
        model.deletePerson(ALICE);
        assertNull(model.selectedPersonProperty().get());
    }

    @Test
    public void deletePerson_otherContact_keepsSelectionDespiteIndexChange() {
        model.selectPerson(BENSON);
        model.deletePerson(ALICE);
        assertEquals(BENSON, model.selectedPersonProperty().get());
        assertEquals(BENSON, model.getFilteredPersonList().getFirst());
    }

    @Test
    public void updateFilter_selectedContactHidden_clearsSelection() {
        model.updateFilteredPersonList(BENSON::equals);
        assertNull(model.selectedPersonProperty().get());
    }

    @Test
    public void updateFilter_selectedContactVisible_keepsSelection() {
        model.updateFilteredPersonList(ALICE::equals);
        assertEquals(ALICE, model.selectedPersonProperty().get());
    }

    @Test
    public void clearThenList_doesNotRestoreStaleSelectionOrDeleteContacts() {
        model.updateFilteredPersonList(Model.PREDICATE_SHOW_NO_PERSONS);
        assertNull(model.selectedPersonProperty().get());
        model.updateFilteredPersonList(Model.PREDICATE_SHOW_ALL_PERSONS);
        assertEquals(2, model.getFilteredPersonList().size());
        assertNull(model.selectedPersonProperty().get());
    }

    @Test
    public void setAddressBook_replacement_clearsSelection() {
        model.setAddressBook(new AddressBook());
        assertNull(model.selectedPersonProperty().get());
    }
}
