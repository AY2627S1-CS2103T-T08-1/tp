package seedu.address.storage;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.model.tag.Tag;

/**
 * Jackson-friendly version of {@link Tag}.
 */
class JsonAdaptedTag {

    private final String tagName;

    /**
     * Constructs a {@code JsonAdaptedTag} with the given {@code tagName}.
     * Validation is deferred until {@link #toModelType()} is called.
     */
    @JsonCreator
    public JsonAdaptedTag(String tagName) {
        this.tagName = tagName;
    }

    /**
     * Converts a given {@code Tag} into this class for Jackson use.
     *
     * @param source The tag to serialize.
     * @throws NullPointerException If {@code source} is null.
     */
    public JsonAdaptedTag(Tag source) {
        tagName = source.tagName;
    }

    /**
     * Returns the stored tag name as the JSON string value.
     */
    @JsonValue
    public String getTagName() {
        return tagName;
    }

    /**
     * Converts this Jackson-friendly adapted tag object into the model's {@code Tag} object.
     *
     * @return A tag with the stored priority name.
     * @throws IllegalValueException If the stored name is not exactly HIGH, MEDIUM, or LOW.
     * @throws NullPointerException If the stored name is null.
     */
    public Tag toModelType() throws IllegalValueException {
        if (!Tag.isValidTagName(tagName)) {
            throw new IllegalValueException(Tag.MESSAGE_CONSTRAINTS);
        }
        return new Tag(tagName);
    }

}
