package seedu.address.model.person;

import static java.util.Objects.requireNonNull;

import java.util.function.Predicate;

import seedu.address.commons.util.ToStringBuilder;

/**
 * Tests that a {@code Person} has a {@code Tag} matching the given tag name.
 * Matching is case-insensitive.
 */
public class PersonHasTagPredicate implements Predicate<Person> {
    private final String tagName;

    /**
     * Constructs a {@code PersonHasTagPredicate}.
     *
     * @param tagName The tag name to match against.
     */
    public PersonHasTagPredicate(String tagName) {
        requireNonNull(tagName);
        this.tagName = tagName;
    }

    @Override
    public boolean test(Person person) {
        return person.getTags().stream().anyMatch(tag -> tag.tagName.equalsIgnoreCase(tagName));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof PersonHasTagPredicate otherPersonHasTagPredicate)) {
            return false;
        }

        return tagName.equalsIgnoreCase(otherPersonHasTagPredicate.tagName);
    }

    @Override
    public int hashCode() {
        return tagName.toLowerCase().hashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).add("tagName", tagName).toString();
    }
}
