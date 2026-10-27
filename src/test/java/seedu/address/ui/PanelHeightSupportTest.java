package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import javafx.event.EventType;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;

public class PanelHeightSupportTest {
    @Test
    public void constructor_setsLimitsAndFitsContent() {
        Region panel = new Region();
        new PanelHeightSupport(panel, new Region(), 42, 160, () -> 95);
        assertEquals(42, panel.getMinHeight());
        assertEquals(160, panel.getMaxHeight());
        assertEquals(95, panel.getPrefHeight());
    }

    @Test
    public void resizeToContent_shortAfterLong_shrinksPanel() {
        Region panel = new Region();
        double[] height = {800};
        PanelHeightSupport support = new PanelHeightSupport(panel, new Region(), 42, 160, () -> height[0]);
        assertEquals(160, panel.getPrefHeight());
        height[0] = 50;
        support.resizeToContent();
        assertEquals(50, panel.getPrefHeight());
    }

    @Test
    public void widthChanged_recalculatesContentHeight() {
        Region panel = new Region();
        new PanelHeightSupport(panel, new Region(), 42, 160, () -> panel.getWidth());
        panel.resize(100, 50);
        assertEquals(100, panel.getPrefHeight());
    }

    @Test
    public void dragHandle_resizesFromOriginalHeightAndConsumesEvents() {
        Region panel = new Region();
        Region handle = new Region();
        new PanelHeightSupport(panel, handle, 42, 160, () -> 80);
        panel.resize(100, 80);
        MouseEvent press = mouseEvent(MouseEvent.MOUSE_PRESSED, 20, 1);
        MouseEvent drag = mouseEvent(MouseEvent.MOUSE_DRAGGED, 50, 1);
        handle.getOnMousePressed().handle(press);
        handle.getOnMouseDragged().handle(drag);
        assertEquals(110, panel.getPrefHeight());
        assertTrue(press.isConsumed());
        assertTrue(drag.isConsumed());
    }

    @Test
    public void dragHandle_beyondLimits_respectsCapAndMinimum() {
        Region panel = new Region();
        Region handle = new Region();
        new PanelHeightSupport(panel, handle, 42, 160, () -> 80);
        panel.resize(100, 80);
        handle.getOnMousePressed().handle(mouseEvent(MouseEvent.MOUSE_PRESSED, 0, 1));
        handle.getOnMouseDragged().handle(mouseEvent(MouseEvent.MOUSE_DRAGGED, 1000, 1));
        assertEquals(160, panel.getPrefHeight());
        handle.getOnMouseDragged().handle(mouseEvent(MouseEvent.MOUSE_DRAGGED, -1000, 1));
        assertEquals(42, panel.getPrefHeight());
    }

    @Test
    public void doubleClick_restoresContentHeightButSingleClickDoesNot() {
        Region panel = new Region();
        Region handle = new Region();
        new PanelHeightSupport(panel, handle, 42, 160, () -> 80);
        panel.setPrefHeight(150);
        handle.getOnMouseClicked().handle(mouseEvent(MouseEvent.MOUSE_CLICKED, 0, 1));
        assertEquals(150, panel.getPrefHeight());
        handle.getOnMouseClicked().handle(mouseEvent(MouseEvent.MOUSE_CLICKED, 0, 2));
        assertEquals(80, panel.getPrefHeight());
    }

    private static MouseEvent mouseEvent(EventType<MouseEvent> type, double y, int clicks) {
        return new MouseEvent(type, 0, y, 0, y, MouseButton.PRIMARY, clicks,
                false, false, false, false, true, false, false, false, false, false, null);
    }

    @Test
    public void clampHeight_shortContent_usesMinimumHeight() {
        assertEquals(42, PanelHeightSupport.clampHeight(10, 42, 160));
    }

    @Test
    public void clampHeight_contentWithinLimits_usesContentHeight() {
        assertEquals(95, PanelHeightSupport.clampHeight(95, 42, 160));
    }

    @Test
    public void clampHeight_longContent_stopsAtCap() {
        assertEquals(160, PanelHeightSupport.clampHeight(800, 42, 160));
    }
}
