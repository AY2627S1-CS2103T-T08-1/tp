package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.FxTestUtil.runOnFxThread;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.junit.jupiter.api.Test;

import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.ArchiveCommand;
import seedu.address.logic.commands.CheckupCommand;
import seedu.address.model.ModelManager;
import seedu.address.model.person.Person;
import seedu.address.testutil.PersonBuilder;

public class PersonCardTest {

    @Test
    public void deletedCheckup_hidesAppointmentRowAndStatus() throws Exception {
        Person scheduled = new PersonBuilder().withName("Kim Lee")
                .withCheckupAppointment("01/01/2030 09:05").build().withMarkedCheckup();
        ModelManager model = new ModelManager();
        model.addPerson(scheduled);
        CheckupCommand.delete(Index.fromOneBased(1)).execute(model);
        Person cleared = model.getFilteredPersonList().get(0);
        runOnFxThread(() -> {
            PersonCard card = new PersonCard(cleared, 1);
            Label appointment = (Label) card.getRoot().lookup("#checkupAppointment");
            assertEquals("", appointment.getText());
            assertFalse(appointment.isVisible());
            assertFalse(appointment.isManaged());
            assertEquals("Kim Lee", PersonCard.getNameText(cleared));
        });
    }

    @Test
    public void archivedFutureUnmarkedCheckup_hidesAppointmentAndMarkIndicator() throws Exception {
        String tomorrow = LocalDate.now().plusDays(1).format(DateTimeFormatter.ofPattern("dd/MM/uuuu"));
        Person scheduled = new PersonBuilder().withName("Kim Lee")
                .withCheckupAppointment(tomorrow + " 09:05").build();
        ModelManager model = new ModelManager();
        model.addPerson(scheduled);
        new ArchiveCommand(Index.fromOneBased(1)).execute(model);
        Person archived = model.getAddressBook().getPersonList().get(0);
        runOnFxThread(() -> {
            PersonCard card = new PersonCard(archived, 1);
            Label appointment = (Label) card.getRoot().lookup("#checkupAppointment");
            assertEquals("", appointment.getText());
            assertFalse(appointment.isVisible());
            assertFalse(appointment.isManaged());
            assertEquals("Kim Lee", PersonCard.getNameText(archived));
        });
    }

    @Test
    public void getNameText_checkupStatus_doesNotChangeName() {
        Person person = new PersonBuilder().withName("Carlyn Goh").build();
        assertEquals("Carlyn Goh", PersonCard.getNameText(person));
        Person scheduled = new PersonBuilder(person).withCheckupAppointment("01/01/2030 09:05").build();
        assertEquals("Carlyn Goh", PersonCard.getNameText(scheduled));
        assertEquals("Carlyn Goh", PersonCard.getNameText(scheduled.withMarkedCheckup()));
        assertEquals("Carlyn Goh", PersonCard.getNameText(scheduled.withMarkedCheckup().withUnmarkedCheckup()));
    }

    @Test
    public void setShown_noEmail_hidesRowAndRemovesLayoutSpace() {
        Node emailRow = new Group();
        Person person = new PersonBuilder().withoutEmail().build();

        PersonCard.setShown(emailRow, !PersonCard.getEmailText(person).isEmpty());

        assertFalse(emailRow.isVisible());
        assertFalse(emailRow.isManaged());
    }

    @Test
    public void setShown_eitherEmailPresent_restoresRowAndLayoutSpace() {
        Node emailRow = new Group();
        List<Person> persons = List.of(
                new PersonBuilder().withPatientEmail("patient@example.com").build(),
                new PersonBuilder().withoutEmail().withFamilyEmail("family@example.com").build(),
                new PersonBuilder().withPatientEmail("patient@example.com")
                        .withFamilyEmail("family@example.com").build());
        for (Person person : persons) {
            PersonCard.setShown(emailRow, false);
            PersonCard.setShown(emailRow, !PersonCard.getEmailText(person).isEmpty());

            assertTrue(emailRow.isVisible());
            assertTrue(emailRow.isManaged());
        }
    }

    @Test
    public void getCheckupAppointmentText_formatsAppointmentAndOmitsAbsentAppointment() {
        assertEquals("", PersonCard.getCheckupAppointmentText(new PersonBuilder().build()));
        assertEquals("Checkup appointment: 01/01/2030 09:05 [ ]", PersonCard.getCheckupAppointmentText(
                new PersonBuilder().withCheckupAppointment("01/01/2030 09:05").build()));
    }

    @Test
    public void getCheckupAppointmentText_markAndUnmark_showsCurrentStatus() {
        Person scheduled = new PersonBuilder().withCheckupAppointment("01/01/2030 09:05").build();
        assertEquals("Checkup appointment: 01/01/2030 09:05 [X]",
                PersonCard.getCheckupAppointmentText(scheduled.withMarkedCheckup()));
        assertEquals("Checkup appointment: 01/01/2030 09:05 [ ]",
                PersonCard.getCheckupAppointmentText(scheduled.withMarkedCheckup().withUnmarkedCheckup()));
    }

    @Test
    public void longName_narrowCard_wrapsCompleteAppointmentAndStatus() throws Exception {
        Person scheduled = new PersonBuilder()
                .withName("Muhammad Abdul Rahman bin Abdullah Al Hakeem Al Mahmood")
                .withCheckupAppointment("01/01/2030 09:05").build().withMarkedCheckup();
        runOnFxThread(() -> {
            PersonCard card = new PersonCard(scheduled, 1);
            StackPane container = new StackPane(card.getRoot());
            new Scene(container, 300, 450);
            container.applyCss();
            Label name = (Label) card.getRoot().lookup("#name");
            Label appointment = (Label) card.getRoot().lookup("#checkupAppointment");
            // Measure this platform's unwrapped text rather than assuming a particular font width or height.
            double wideWidth = appointment.prefWidth(-1) + 40;
            container.resize(wideWidth, 450);
            container.layout();
            double singleLineHeight = appointment.getHeight();
            container.resize(wideWidth / 2, 450);
            container.layout();
            assertEquals(scheduled.getName().fullName, name.getText());
            assertEquals("Checkup appointment: 01/01/2030 09:05 [X]", appointment.getText());
            assertTrue(appointment.isWrapText());
            assertTrue(showsFullText(appointment));
            assertTrue(appointment.getHeight() > singleLineHeight);
        });
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

    /**
     * Returns true if {@code label} shows its whole text without an ellipsis.
     * {@code Labeled#isTextTruncated()} would do this, but it needs JavaFX 23 and CareBook uses JavaFX 17.
     */
    private static boolean showsFullText(Label label) {
        Text shownText = (Text) label.lookup(".text");
        return shownText != null && shownText.getText().equals(label.getText());
    }
}
