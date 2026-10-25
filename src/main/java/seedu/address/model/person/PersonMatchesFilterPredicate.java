package seedu.address.model.person;

import static java.util.Objects.requireNonNull;

import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.function.Predicate;

import seedu.address.commons.util.ToStringBuilder;

/**
 * Tests that a {@code Person} satisfies every non-empty criterion of a filter.
 * Within one criterion, matching any one of the values is enough (OR);
 * a person must satisfy all criteria that are given (AND).
 * <ul>
 *     <li>Name: the name contains any keyword, ignoring case and allowing partial matches.</li>
 *     <li>Tag: the person has any of the tags, ignoring case.</li>
 *     <li>Address: the address contains any of the phrases, ignoring case and allowing partial matches.</li>
 * </ul>
 * An empty list means that criterion is not applied.
 */
public class PersonMatchesFilterPredicate implements Predicate<Person> {
    private final List<String> nameKeywords;
    private final List<String> tags;
    private final List<String> addressPhrases;

    /**
     * Creates a predicate from the given criteria. An empty list means the criterion is not applied.
     */
    public PersonMatchesFilterPredicate(List<String> nameKeywords, List<String> tags, List<String> addressPhrases) {
        requireNonNull(nameKeywords);
        requireNonNull(tags);
        requireNonNull(addressPhrases);
        this.nameKeywords = List.copyOf(nameKeywords);
        this.tags = List.copyOf(tags);
        this.addressPhrases = List.copyOf(addressPhrases);
    }

    @Override
    public boolean test(Person person) {
        return matchesName(person) && matchesTag(person) && matchesAddress(person);
    }

    private boolean matchesName(Person person) {
        return nameKeywords.isEmpty() || new NameContainsKeywordsPredicate(nameKeywords).test(person);
    }

    private boolean matchesTag(Person person) {
        return tags.isEmpty() || tags.stream().anyMatch(tag -> new PersonHasTagPredicate(tag).test(person));
    }

    private boolean matchesAddress(Person person) {
        if (addressPhrases.isEmpty()) {
            return true;
        }
        String address = person.getAddress().value.toLowerCase(Locale.ROOT);
        return addressPhrases.stream().anyMatch(phrase -> address.contains(phrase.toLowerCase(Locale.ROOT)));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof PersonMatchesFilterPredicate otherPredicate)) {
            return false;
        }

        return nameKeywords.equals(otherPredicate.nameKeywords)
                && lowerCased(tags).equals(lowerCased(otherPredicate.tags))
                && addressPhrases.equals(otherPredicate.addressPhrases);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nameKeywords, lowerCased(tags), addressPhrases);
    }

    private static List<String> lowerCased(List<String> values) {
        return values.stream().map(value -> value.toLowerCase(Locale.ROOT)).toList();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("nameKeywords", nameKeywords)
                .add("tags", tags)
                .add("addressPhrases", addressPhrases)
                .toString();
    }
}
