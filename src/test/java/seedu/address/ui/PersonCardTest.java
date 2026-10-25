package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import javafx.scene.Group;
import javafx.scene.Node;
import seedu.address.model.person.Person;
import seedu.address.testutil.PersonBuilder;

public class PersonCardTest {

    @Test
    public void updateEmailVisibility_noEmail_hidesRowAndRemovesLayoutSpace() {
        Node emailRow = new Group();
        Person person = new PersonBuilder().withoutEmail().build();

        PersonCard.updateEmailVisibility(emailRow, PersonCard.getEmailText(person));

        assertFalse(emailRow.isVisible());
        assertFalse(emailRow.isManaged());
    }

    @Test
    public void updateEmailVisibility_eitherEmailPresent_restoresRowAndLayoutSpace() {
        Node emailRow = new Group();
        List<Person> persons = List.of(
                new PersonBuilder().withPatientEmail("patient@example.com").build(),
                new PersonBuilder().withoutEmail().withFamilyEmail("family@example.com").build(),
                new PersonBuilder().withPatientEmail("patient@example.com")
                        .withFamilyEmail("family@example.com").build());
        for (Person person : persons) {
            PersonCard.updateEmailVisibility(emailRow, "");
            PersonCard.updateEmailVisibility(emailRow, PersonCard.getEmailText(person));

            assertTrue(emailRow.isVisible());
            assertTrue(emailRow.isManaged());
        }
    }

    @Test
    public void getCheckupAppointmentText_formatsAppointmentAndOmitsAbsentAppointment() {
        assertEquals("", PersonCard.getCheckupAppointmentText(new PersonBuilder().build()));
        assertEquals("Check up Appointment: 01/01/2030 09:05", PersonCard.getCheckupAppointmentText(
                new PersonBuilder().withCheckupAppointment("01/01/2030 09:05").build()));
    }

    @Test
    public void getPhoneText_validPerson_returnsFormattedPhoneTexts() {
        Person person = new PersonBuilder()
                .withPatientNo("91234567")
                .withFamilyNo("87654321")
                .build();

        assertEquals("Patient HP: 91234567", PersonCard.getPatientNoText(person));
        assertEquals("Family HP: 87654321", PersonCard.getFamilyNoText(person));
    }

    @Test
    public void getEmailText_patientAndFamilyEmails_returnsFormattedCombinedEmailText() {
        Person person = new PersonBuilder()
                .withPatientEmail("patient@example.com")
                .withFamilyEmail("family@example.com")
                .build();

        assertEquals("Patient Email: patient@example.com | Family Email: family@example.com",
                PersonCard.getEmailText(person));
    }

    @Test
    public void getEmailText_onlyPatientEmail_returnsPatientEmailText() {
        Person person = new PersonBuilder()
                .withPatientEmail("patient@example.com")
                .build();

        assertEquals("Patient Email: patient@example.com", PersonCard.getEmailText(person));
    }

    @Test
    public void getEmailText_onlyFamilyEmail_returnsFamilyEmailText() {
        Person person = new PersonBuilder()
                .withoutEmail()
                .withFamilyEmail("family@example.com")
                .build();

        assertEquals("Family Email: family@example.com", PersonCard.getEmailText(person));
    }

    @Test
    public void getEmailText_noEmail_returnsEmptyText() {
        Person person = new PersonBuilder().withoutEmail().build();

        assertEquals("", PersonCard.getEmailText(person));
    }

    @Test
    public void getRemarkText_notes_returnsNumberedNotesOnSeparateLines() {
        Person person = new PersonBuilder().withRemarks("Allergic to penicillin", "Visits after 3pm").build();

        assertEquals("Remark:\n1. Allergic to penicillin\n2. Visits after 3pm", PersonCard.getRemarkText(person));
    }

    @Test
    public void getRemarkText_noNotes_returnsEmptyText() {
        assertEquals("", PersonCard.getRemarkText(new PersonBuilder().build()));
    }

    @Test
    public void getTagTexts_noTags_returnsEmptyList() {
        Person person = new PersonBuilder().withTags().build();

        assertEquals(List.of(), PersonCard.getTagTexts(person));
    }

    @Test
    public void getTagTexts_validPerson_returnsPriorityTagText() {
        Person person = new PersonBuilder()
                .withTags("MEDIUM")
                .build();

        assertEquals(List.of("MEDIUM"), PersonCard.getTagTexts(person));
    }
}
