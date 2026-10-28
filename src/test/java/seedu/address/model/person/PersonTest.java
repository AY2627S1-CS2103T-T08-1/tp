package seedu.address.model.person;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.VALID_ADDRESS_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_EMAIL_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_FAMILY_PHONE_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_NAME_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_PHONE_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_TAG_LOW;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalPersons.ALICE;
import static seedu.address.testutil.TypicalPersons.BOB;

import org.junit.jupiter.api.Test;

import seedu.address.testutil.PersonBuilder;

public class PersonTest {

    @Test
    public void markCheckup_requiresAppointmentAndPreservesOtherDetails() {
        Person noCheckup = new PersonBuilder().build();
        assertThrows(IllegalArgumentException.class, Person.MESSAGE_MARK_WITHOUT_CHECKUP, noCheckup::withMarkedCheckup);
        Person scheduled = new PersonBuilder().withCheckupAppointment("01/01/2030 09:05")
                .withRemarks("Needs assistance").build();
        Person marked = scheduled.withMarkedCheckup();
        assertFalse(scheduled.isCheckupMarked());
        assertTrue(marked.isCheckupMarked());
        assertFalse(scheduled.equals(marked));
        assertTrue(scheduled.isSamePerson(marked));
        assertEquals(marked, new PersonBuilder(marked).build());
        assertEquals(marked.hashCode(), new PersonBuilder(marked).build().hashCode());
        assertTrue(marked.withRemark(Remark.EMPTY).isCheckupMarked());
        assertTrue(marked.copyWithArchivedStatus(true).isCheckupMarked());
        assertEquals(scheduled.getCheckupAppointment(), marked.getCheckupAppointment());
    }

    @Test
    public void checkupAppointment_isOptionalAndAffectsDataEquality() {
        Person original = new PersonBuilder().build();
        Person scheduled = new PersonBuilder(original).withCheckupAppointment("01/01/2030 12:00").build();
        Person copy = new PersonBuilder(scheduled).build();
        assertTrue(original.getCheckupAppointment().isEmpty());
        assertEquals(new CheckupAppointment("01/01/2030 12:00"), scheduled.getCheckupAppointment().orElseThrow());
        assertFalse(original.equals(scheduled));
        assertTrue(original.isSamePerson(scheduled));
        assertEquals(scheduled, copy);
        assertEquals(scheduled.hashCode(), copy.hashCode());
    }

    @Test
    public void constructor_multiplePriorities_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, Person.MESSAGE_TAG_COUNT_CONSTRAINTS, () ->
                new PersonBuilder().withTags("HIGH", "LOW").build());
    }

    @Test
    public void constructor_noPriority_success() {
        assertTrue(new PersonBuilder().withTags().build().getTags().isEmpty());
    }

    @Test
    public void asObservableList_modifyList_throwsUnsupportedOperationException() {
        Person person = new PersonBuilder().build();
        assertThrows(UnsupportedOperationException.class, () -> person.getTags().remove(0));
    }

    @Test
    public void isSamePerson() {
        // same object -> returns true
        assertTrue(ALICE.isSamePerson(ALICE));

        // null -> returns false
        assertFalse(ALICE.isSamePerson(null));

        // same name, all other attributes different -> returns false
        Person editedAlice = new PersonBuilder(ALICE).withPhone(VALID_PHONE_BOB).withEmail(VALID_EMAIL_BOB)
                .withFamilyNo(VALID_FAMILY_PHONE_BOB).withAddress(VALID_ADDRESS_BOB).withTags(VALID_TAG_LOW)
                .build();
        assertFalse(ALICE.isSamePerson(editedAlice));

        // different name, same patient phone -> returns true
        editedAlice = new PersonBuilder(ALICE).withName(VALID_NAME_BOB).build();
        assertTrue(ALICE.isSamePerson(editedAlice));

        // name differs in case, same patient phone -> returns true
        Person editedBob = new PersonBuilder(BOB).withName(VALID_NAME_BOB.toLowerCase()).build();
        assertTrue(BOB.isSamePerson(editedBob));

        // name has trailing spaces, same patient phone -> returns true
        String nameWithTrailingSpaces = VALID_NAME_BOB + " ";
        editedBob = new PersonBuilder(BOB).withName(nameWithTrailingSpaces).build();
        assertTrue(BOB.isSamePerson(editedBob));
    }

    @Test
    public void equals() {
        // same values -> returns true
        Person aliceCopy = new PersonBuilder(ALICE).build();
        assertTrue(ALICE.equals(aliceCopy));

        // same object -> returns true
        assertTrue(ALICE.equals(ALICE));

        // null -> returns false
        assertFalse(ALICE.equals(null));

        // different type -> returns false
        assertFalse(ALICE.equals(5));

        // different person -> returns false
        assertFalse(ALICE.equals(BOB));

        // different name -> returns false
        Person editedAlice = new PersonBuilder(ALICE).withName(VALID_NAME_BOB).build();
        assertFalse(ALICE.equals(editedAlice));

        // different phone -> returns false
        editedAlice = new PersonBuilder(ALICE).withPhone(VALID_PHONE_BOB).build();
        assertFalse(ALICE.equals(editedAlice));

        // different family phone -> returns false
        editedAlice = new PersonBuilder(ALICE).withFamilyNo(VALID_FAMILY_PHONE_BOB).build();
        assertFalse(ALICE.equals(editedAlice));

        // different email -> returns false
        editedAlice = new PersonBuilder(ALICE).withEmail(VALID_EMAIL_BOB).build();
        assertFalse(ALICE.equals(editedAlice));

        // different address -> returns false
        editedAlice = new PersonBuilder(ALICE).withAddress(VALID_ADDRESS_BOB).build();
        assertFalse(ALICE.equals(editedAlice));

        // different tags -> returns false
        editedAlice = new PersonBuilder(ALICE).withTags(VALID_TAG_LOW).build();
        assertFalse(ALICE.equals(editedAlice));

        // different remark -> returns false, but still the same person
        editedAlice = new PersonBuilder(ALICE).withRemarks("Allergic to penicillin").build();
        assertFalse(ALICE.equals(editedAlice));
        assertTrue(ALICE.isSamePerson(editedAlice));
    }

    @Test
    public void withRemark_newRemark_keepsOtherFields() {
        Person withNotes = new PersonBuilder(ALICE).withRemarks("Visits after 3pm").build();
        assertEquals(withNotes, ALICE.withRemark(withNotes.getRemark()));
        assertEquals(ALICE, withNotes.withRemark(Remark.EMPTY));
    }

    @Test
    public void toStringMethod() {
        String expected = Person.class.getCanonicalName() + "{name=" + ALICE.getName()
                + ", patientNo=" + ALICE.getPatientNo()
                + ", familyNo=" + ALICE.getFamilyNo()
                + ", patientEmail=" + ALICE.getPatientEmail().orElse(null)
                + ", familyEmail=" + ALICE.getFamilyEmail().orElse(null)
                + ", address=" + ALICE.getAddress() + ", tags=" + ALICE.getTags()
                + ", checkupAppointment=null, remark=" + ALICE.getRemark() + ", archived=false, checkupMarked=false}";
        assertEquals(expected, ALICE.toString());
    }
}
