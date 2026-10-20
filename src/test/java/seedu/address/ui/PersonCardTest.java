package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.model.person.Person;
import seedu.address.testutil.PersonBuilder;

public class PersonCardTest {

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
