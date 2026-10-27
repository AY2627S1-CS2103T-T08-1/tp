package seedu.address.ui;

import java.util.function.DoubleSupplier;

import javafx.application.Platform;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;

/**
 * Sizes a panel to its content, with bounded manual resizing and a scrollable content area.
 */
final class PanelHeightSupport {
    private final Region panel;
    private final DoubleSupplier contentHeight;
    private final double minimumHeight;
    private final double maximumHeight;
    private double dragStartY;
    private double dragStartHeight;

    /**
     * Installs a bottom-edge resize handle; content changes can restore automatic sizing.
     */
    PanelHeightSupport(Region panel, Region resizeHandle, double minimumHeight, double maximumHeight,
            DoubleSupplier contentHeight) {
        this.panel = panel;
        this.contentHeight = contentHeight;
        this.minimumHeight = minimumHeight;
        this.maximumHeight = maximumHeight;
        configureHeightLimits();
        observeLayoutChanges();
        installResizeHandlers(resizeHandle);
        resizeToContent();
    }

    /**
     * Applies the limits shared by automatic sizing and manual resizing.
     */
    private void configureHeightLimits() {
        panel.setMinHeight(minimumHeight);
        panel.setMaxHeight(maximumHeight);
    }

    /**
     * Restores automatic sizing when the panel width changes or the panel joins a scene.
     */
    private void observeLayoutChanges() {
        panel.widthProperty().addListener((observable, previous, current) -> resizeToContent());
        panel.sceneProperty().addListener((observable, previous, current) -> {
            if (current != null) {
                Platform.runLater(this::resizeToContent);
            }
        });
    }

    /**
     * Connects each handle gesture to its corresponding resize operation.
     */
    private void installResizeHandlers(Region resizeHandle) {
        resizeHandle.setOnMousePressed(this::startResize);
        resizeHandle.setOnMouseDragged(this::resizeFromDrag);
        resizeHandle.setOnMouseClicked(this::restoreHeightOnDoubleClick);
    }

    /**
     * Records the initial pointer position and panel height for a drag gesture.
     */
    private void startResize(MouseEvent event) {
        dragStartY = event.getSceneY();
        dragStartHeight = panel.getHeight();
        event.consume();
    }

    /**
     * Applies the pointer's vertical movement while keeping the requested height within its limits.
     */
    private void resizeFromDrag(MouseEvent event) {
        double requestedHeight = dragStartHeight + event.getSceneY() - dragStartY;
        panel.setPrefHeight(clampHeight(requestedHeight, minimumHeight, maximumHeight));
        event.consume();
    }

    /**
     * Restores content-based sizing on a double-click without changing single-click behaviour.
     */
    private void restoreHeightOnDoubleClick(MouseEvent event) {
        if (event.getClickCount() == 2) {
            resizeToContent();
        }
    }

    /**
     * Restores content-based height, for example after replacing a long message with a short one.
     */
    void resizeToContent() {
        panel.setPrefHeight(clampHeight(contentHeight.getAsDouble(), minimumHeight, maximumHeight));
    }

    /**
     * Keeps automatic and manually requested heights within the same limits.
     */
    static double clampHeight(double requested, double minimum, double maximum) {
        return Math.max(minimum, Math.min(maximum, requested));
    }
}
