package seedu.address.ui;

import java.util.logging.Logger;

import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.ObservableList;
import javafx.css.PseudoClass;
import javafx.fxml.FXML;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.Region;
import seedu.address.commons.core.LogsCenter;
import seedu.address.model.person.Person;

/**
 * Panel containing the list of persons.
 */
public class PersonListPanel extends UiPart<Region> {
    private static final String FXML = "PersonListPanel.fxml";
    private static final PseudoClass VIEWED = PseudoClass.getPseudoClass("viewed");
    private final Logger logger = LogsCenter.getLogger(PersonListPanel.class);
    private final ReadOnlyObjectProperty<Person> selectedPerson;

    @FXML
    private ListView<Person> personListView;

    /**
     * Creates a {@code PersonListPanel} with the given {@code ObservableList}.
     */
    public PersonListPanel(ObservableList<Person> personList) {
        this(personList, new ReadOnlyObjectWrapper<Person>().getReadOnlyProperty());
    }

    /** Creates the list with a highlight driven by the contact selected using {@code view INDEX}. */
    public PersonListPanel(ObservableList<Person> personList, ReadOnlyObjectProperty<Person> selectedPerson) {
        super(FXML);
        this.selectedPerson = selectedPerson;
        personListView.setItems(personList);
        // Selection belongs to the model, not to a row number which can change after filtering.
        personListView.setSelectionModel(null);
        personListView.setCellFactory(listView -> new PersonListViewCell());
        selectedPerson.addListener((observable, previous, current) -> {
            personListView.refresh();
            if (current != null) {
                personListView.scrollTo(current);
            }
        });
    }

    /**
     * Custom {@code ListCell} that displays the graphics of a {@code Person} using a {@code PersonCard}.
     */
    class PersonListViewCell extends ListCell<Person> {
        @Override
        protected void updateItem(Person person, boolean empty) {
            super.updateItem(person, empty);
            pseudoClassStateChanged(VIEWED, !empty && person != null && person.equals(selectedPerson.get()));

            if (empty || person == null) {
                setGraphic(null);
                setText(null);
            } else {
                setGraphic(new PersonCard(person, getIndex() + 1).getRoot());
            }
        }
    }

}
