package seedu.address.ui;

import java.util.Comparator;
import java.util.List;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import seedu.address.model.person.Person;

/**
 * A UI component that displays information of a {@code Person}.
 */
public class PersonCard extends UiPart<Region> {

    private static final String FXML = "PersonListCard.fxml";

    /**
     * Note: Certain keywords such as "location" and "resources" are reserved keywords in JavaFX.
     * As a consequence, UI elements' variable names cannot be set to such keywords
     * or an exception will be thrown by JavaFX during runtime.
     *
     * @see <a href="https://github.com/se-edu/addressbook-level4/issues/336">The issue on AddressBook level 4</a>
     */

    public final Person person;

    @FXML
    private HBox cardPane;
    @FXML
    private Label name;
    @FXML
    private Label id;
    @FXML
    private Label checkupAppointment;
    @FXML
    private Label patientNo;
    @FXML
    private Label familyNo;
    @FXML
    private Label email;
    @FXML
    private Label address;
    @FXML
    private FlowPane tags;
    @FXML
    private Label remark;

    /**
     * Creates a {@code PersonCard} with the given {@code Person} and index to display.
     */
    public PersonCard(Person person, int displayedIndex) {
        super(FXML);
        this.person = person;
        id.setText(displayedIndex + ". ");
        name.setText(getNameText(person));
        checkupAppointment.setText(getCheckupAppointmentText(person));
        checkupAppointment.setVisible(person.getCheckupAppointment().isPresent());
        checkupAppointment.setManaged(person.getCheckupAppointment().isPresent());
        patientNo.setText(getPatientNoText(person));
        familyNo.setText(getFamilyNoText(person));
        email.setText(getEmailText(person));
        updateEmailVisibility(email, email.getText());
        address.setText(person.getAddress().value);
        getTagTexts(person).forEach(tagName -> tags.getChildren().add(new Label(tagName)));
        remark.setText(getRemarkText(person));
        // an empty remark takes up no space on the card
        remark.setManaged(!person.getRemark().isEmpty());
        remark.setVisible(!person.getRemark().isEmpty());
    }

    static String getNameText(Person person) {
        if (person.getCheckupAppointment().isEmpty()) {
            return person.getName().fullName;
        }
        return person.getName().fullName + (person.isCheckupMarked() ? " [X]" : " [ ]");
    }

    static String getCheckupAppointmentText(Person person) {
        return person.getCheckupAppointment().map(appointment -> "Check up Appointment: " + appointment).orElse("");
    }

    /**
     * Removes an empty email row from the layout and restores it when email text is present.
     */
    static void updateEmailVisibility(Node emailRow, String emailText) {
        boolean hasEmail = !emailText.isEmpty();
        emailRow.setVisible(hasEmail);
        emailRow.setManaged(hasEmail);
    }

    static String getPatientNoText(Person person) {
        return "Patient HP: " + person.getPatientNo().value;
    }

    static String getFamilyNoText(Person person) {
        return "Family HP: " + person.getFamilyNo().value;
    }

    static String getEmailText(Person person) {
        StringBuilder builder = new StringBuilder();
        person.getPatientEmail().ifPresent(patientEmail ->
                builder.append("Patient Email: ").append(patientEmail.value));
        person.getFamilyEmail().ifPresent(familyEmail -> {
            if (builder.length() > 0) {
                builder.append(" | ");
            }
            builder.append("Family Email: ").append(familyEmail.value);
        });
        return builder.toString();
    }

    static List<String> getTagTexts(Person person) {
        return person.getTags().stream()
                .sorted(Comparator.comparing(tag -> tag.tagName))
                .map(tag -> tag.tagName)
                .toList();
    }

    static String getRemarkText(Person person) {
        if (person.getRemark().isEmpty()) {
            return "";
        }
        return "Remark:\n" + String.join("\n", person.getRemark().getNumberedNotes());
    }
}
