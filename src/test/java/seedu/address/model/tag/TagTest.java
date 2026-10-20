package seedu.address.model.tag;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

public class TagTest {

    @Test
    public void constructor_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new Tag(null));
    }

    @Test
    public void constructor_invalidTagName_throwsIllegalArgumentException() {
        for (String invalidTagName : new String[] {"", "friends", "high", "Medium", "low", " HIGH", "LOW ",
            "HIGH\n", "HIGH LOW", "HIGH1"}) {
            assertThrows(IllegalArgumentException.class, () -> new Tag(invalidTagName));
        }
    }

    @Test
    public void constructor_validPriority_preservesTagName() {
        for (Priority priority : Priority.values()) {
            assertEquals(priority.name(), new Tag(priority.name()).tagName);
        }
    }

    @Test
    public void isValidTagName_validPriorities_returnsTrue() {
        assertTrue(Tag.isValidTagName("HIGH"));
        assertTrue(Tag.isValidTagName("MEDIUM"));
        assertTrue(Tag.isValidTagName("LOW"));
    }

    @Test
    public void isValidTagName_otherInputs_returnsFalse() {
        for (String invalidTagName : new String[] {"", "friends", "high", "Medium", "low", " HIGH", "LOW ",
            "HIGH\n", "HIGH LOW", "HIGH1"}) {
            assertFalse(Tag.isValidTagName(invalidTagName));
        }
    }

    @Test
    public void isValidTagName() {
        // null tag name
        assertThrows(NullPointerException.class, () -> Tag.isValidTagName(null));
    }

}
