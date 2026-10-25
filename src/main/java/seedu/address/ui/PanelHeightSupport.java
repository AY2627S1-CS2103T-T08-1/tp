package seedu.address.ui;

import java.util.function.DoubleSupplier;

import javafx.application.Platform;
import javafx.scene.layout.Region;

/** Sizes a panel to its content, with bounded manual resizing and a scrollable content area. */
final class PanelHeightSupport {
    private final Region panel;
    private final DoubleSupplier contentHeight;
    private final double minimumHeight;
    private final double maximumHeight;
    private double dragStartY;
    private double dragStartHeight;

    /** Installs a bottom-edge resize handle; content changes can restore automatic sizing. */
    PanelHeightSupport(Region panel, Region resizeHandle, double minimumHeight, double maximumHeight,
            DoubleSupplier contentHeight) {
        this.panel = panel;
        this.contentHeight = contentHeight;
        this.minimumHeight = minimumHeight;
        this.maximumHeight = maximumHeight;
        panel.setMinHeight(minimumHeight);
        panel.setMaxHeight(maximumHeight);
        panel.widthProperty().addListener((observable, previous, current) -> resizeToContent());
        panel.sceneProperty().addListener((observable, previous, current) -> {
            if (current != null) {
                Platform.runLater(this::resizeToContent);
            }
        });
        resizeHandle.setOnMousePressed(event -> {
            dragStartY = event.getSceneY();
            dragStartHeight = panel.getHeight();
            event.consume();
        });
        resizeHandle.setOnMouseDragged(event -> {
            panel.setPrefHeight(clampHeight(dragStartHeight + event.getSceneY() - dragStartY,
                    minimumHeight, maximumHeight));
            event.consume();
        });
        resizeHandle.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                resizeToContent();
            }
        });
        resizeToContent();
    }

    /** Restores content-based height, for example after replacing a long message with a short one. */
    void resizeToContent() {
        panel.setPrefHeight(clampHeight(contentHeight.getAsDouble(), minimumHeight, maximumHeight));
    }

    /** Keeps automatic and manually requested heights within the same limits. */
    static double clampHeight(double requested, double minimum, double maximum) {
        return Math.max(minimum, Math.min(maximum, requested));
    }
}
