package seedu.address.ui;

import static java.util.Objects.requireNonNull;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.layout.Region;
import javafx.scene.text.Text;

/**
 * A UI component that displays the result of a command execution.
 */
public class ResultDisplay extends UiPart<Region> {

    private static final String FXML = "ResultDisplay.fxml";
    private static final double MINIMUM_HEIGHT = 42;
    private static final double MAXIMUM_HEIGHT = 160;

    private final PanelHeightSupport heightSupport;

    @FXML
    private TextArea resultDisplay;

    @FXML
    private Region resizeHandle;

    /** Creates a wrapping feedback panel which grows to fit messages up to its height cap. */
    public ResultDisplay() {
        super(FXML);
        heightSupport = new PanelHeightSupport(getRoot(), resizeHandle, MINIMUM_HEIGHT, MAXIMUM_HEIGHT,
                this::measureContentHeight);
        resultDisplay.textProperty().addListener((observable, previous, current) -> heightSupport.resizeToContent());
        resultDisplay.fontProperty().addListener((observable, previous, current) -> heightSupport.resizeToContent());
        resultDisplay.widthProperty().addListener((observable, previous, current) -> heightSupport.resizeToContent());
    }

    /** Replaces the feedback and starts its scroll position at the beginning. */
    public void setFeedbackToUser(String feedbackToUser) {
        requireNonNull(feedbackToUser);
        resultDisplay.setText(feedbackToUser);
        resultDisplay.setScrollTop(0);
    }

    /** Measures wrapped feedback using the same font, leaving room for padding and a scrollbar. */
    private double measureContentHeight() {
        Text measurement = new Text(resultDisplay.getText().isEmpty() ? " " : resultDisplay.getText());
        measurement.setFont(resultDisplay.getFont());
        measurement.setWrappingWidth(Math.max(1, resultDisplay.getWidth() - 32));
        return Math.ceil(measurement.getLayoutBounds().getHeight()) + 28;
    }

}
