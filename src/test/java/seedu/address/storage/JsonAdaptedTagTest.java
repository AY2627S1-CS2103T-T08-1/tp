package seedu.address.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.model.tag.Tag;

public class JsonAdaptedTagTest {

    @Test
    public void toModelType_validTagName_returnsTag() throws Exception {
        assertEquals(new Tag("HIGH"), new JsonAdaptedTag("HIGH").toModelType());
    }

    @Test
    public void toModelType_nullTagName_throwsIllegalValueException() {
        JsonAdaptedTag tag = new JsonAdaptedTag((String) null);
        assertThrows(IllegalValueException.class, Tag.MESSAGE_CONSTRAINTS, tag::toModelType);
    }

    @Test
    public void toModelType_invalidTagName_throwsIllegalValueException() {
        JsonAdaptedTag tag = new JsonAdaptedTag("high");
        assertThrows(IllegalValueException.class, Tag.MESSAGE_CONSTRAINTS, tag::toModelType);
    }
}
