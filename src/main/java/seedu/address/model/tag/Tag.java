package seedu.address.model.tag;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

/**
 * Represents a Tag in the address book.
 * Guarantees: immutable; name is valid as declared in {@link #isValidTagName(String)}
 */
public class Tag {

    public static final String MESSAGE_CONSTRAINTS = "Tag names must be HIGH, MEDIUM or LOW";

    public final String tagName;

    /**
     * Constructs a priority tag without trimming or changing the case of its name.
     *
     * @param tagName Exactly {@code HIGH}, {@code MEDIUM}, or {@code LOW}.
     * @throws NullPointerException If {@code tagName} is null.
     * @throws IllegalArgumentException If {@code tagName} is not an allowed priority.
     */
    public Tag(String tagName) {
        requireNonNull(tagName);
        checkArgument(isValidTagName(tagName), MESSAGE_CONSTRAINTS);
        this.tagName = tagName;
    }

    /**
     * Returns whether the given string names a {@link Priority} enum constant.
     * Matching is case-sensitive and does not ignore whitespace.
     *
     * @param test The tag name to validate.
     * @throws NullPointerException If {@code test} is null.
     */
    public static boolean isValidTagName(String test) {
        requireNonNull(test);
        try {
            Priority.valueOf(test);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    /**
     * Returns whether {@code other} is a tag with the same priority name.
     * Returns false for null or objects of other types.
     */
    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof Tag otherTag)) {
            return false;
        }

        return tagName.equals(otherTag.tagName);
    }

    /**
     * Returns a hash code based on the priority name, consistent with {@link #equals(Object)}.
     */
    @Override
    public int hashCode() {
        return tagName.hashCode();
    }

    /**
     * Returns the priority name enclosed in square brackets, for example {@code [HIGH]}.
     */
    public String toString() {
        return '[' + tagName + ']';
    }

}
