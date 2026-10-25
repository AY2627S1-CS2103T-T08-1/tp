package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class PanelHeightSupportTest {
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
