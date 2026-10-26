package seedu.address.ui;

import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import seedu.address.model.person.Person;

/** Displays complete, wrapping contact details independently of the contact list. */
public class PersonDetailsPanel extends UiPart<Region> {
    private static final String FXML = "PersonDetailsPanel.fxml";
    private static final double MINIMUM_HEIGHT = 48;
    private static final double MAXIMUM_HEIGHT = 260;

    private final PanelHeightSupport heightSupport;

    @FXML
    private Label title;
    @FXML
    private Label details;
    @FXML
    private ScrollPane detailsScroll;
    @FXML
    private VBox detailsContent;
    @FXML
    private Region resizeHandle;

    /** Creates a panel which follows changes to the model's transient selection. */
    public PersonDetailsPanel(ReadOnlyObjectProperty<Person> selectedPerson) {
        super(FXML);
        heightSupport = new PanelHeightSupport(getRoot(), resizeHandle, MINIMUM_HEIGHT, MAXIMUM_HEIGHT,
                this::measureContentHeight);
        detailsScroll.widthProperty().addListener((observable, previous, current) -> heightSupport.resizeToContent());
        showPerson(selectedPerson.get());
        selectedPerson.addListener((observable, previous, current) -> showPerson(current));
    }

    /** Measures wrapping labels at the current viewport width, including padding and the resize handle. */
    private double measureContentHeight() {
        return detailsContent.prefHeight(Math.max(1, detailsScroll.getWidth() - 18)) + 10;
    }

    /** Refreshes all fields, including the empty-selection state, without retaining stale text. */
    private void showPerson(Person person) {
        getRoot().setVisible(person != null);
        getRoot().setManaged(person != null);
        title.setText(person == null ? "" : "Selected contact: " + person.getName().fullName);
        details.setText(PersonDetailsFormatter.format(person));
        details.setVisible(person != null);
        details.setManaged(person != null);
        detailsScroll.setVvalue(0);
        heightSupport.resizeToContent();
    }
}
