package seedu.address.model.person;

import java.util.List;
import java.util.Locale;
import java.util.function.Predicate;

import seedu.address.commons.util.ToStringBuilder;

/**
 * Tests that a {@code Person}'s {@code Name} contains any keyword, ignoring case and allowing partial matches.
 */
public class NameContainsKeywordsPredicate implements Predicate<Person> {
    private final List<String> keywords;

    /**
     * Creates a predicate that searches names for any of the given {@code keywords}.
     */
    public NameContainsKeywordsPredicate(List<String> keywords) {
        this.keywords = keywords;
    }

    /**
     * Returns whether the person's name contains any keyword, ignoring case and allowing partial matches.
     */
    @Override
    public boolean test(Person person) {
        String name = person.getName().fullName.toLowerCase(Locale.ROOT);
        return keywords.stream()
                .anyMatch(keyword -> name.contains(keyword.toLowerCase(Locale.ROOT)));
    }

    /**
     * Returns whether {@code other} is a name predicate with the same keywords.
     */
    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof NameContainsKeywordsPredicate otherNameContainsKeywordsPredicate)) {
            return false;
        }

        return keywords.equals(otherNameContainsKeywordsPredicate.keywords);
    }

    /**
     * Returns a string representation of this predicate and its keywords.
     */
    @Override
    public String toString() {
        return new ToStringBuilder(this).add("keywords", keywords).toString();
    }
}
