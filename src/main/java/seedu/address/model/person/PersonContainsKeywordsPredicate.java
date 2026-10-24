package seedu.address.model.person;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Stream;

import seedu.address.commons.util.ToStringBuilder;

/**
 * Matches any keyword against the selected contact field using case-insensitive substrings.
 */
public class PersonContainsKeywordsPredicate implements Predicate<Person> {
    public static final Set<String> SUPPORTED_CRITERIA = Set.of(
            "name", "patientNo", "familyNo", "address", "pEmail", "fEmail", "email", "tag");

    private final String criterion;
    private final List<String> keywords;

    /**
     * Creates a predicate for a supported criterion and its search keywords.
     * Phone keyword validation is performed by the command parser.
     */
    public PersonContainsKeywordsPredicate(String criterion, List<String> keywords) {
        requireNonNull(criterion);
        checkArgument(SUPPORTED_CRITERIA.contains(criterion), "Unsupported find criterion");
        this.criterion = criterion;
        this.keywords = List.copyOf(keywords);
    }

    @Override
    public boolean test(Person person) {
        return fieldValues(person).anyMatch(value -> keywords.stream()
                .anyMatch(keyword -> value.toLowerCase(Locale.ROOT).contains(keyword.toLowerCase(Locale.ROOT))));
    }

    private Stream<String> fieldValues(Person person) {
        return switch (criterion) {
            case "name" -> Stream.of(person.getName().fullName);
            case "patientNo" -> Stream.of(person.getPatientNo().value);
            case "familyNo" -> Stream.of(person.getFamilyNo().value);
            case "address" -> Stream.of(person.getAddress().value);
            case "pEmail" -> person.getPatientEmail().stream().map(email -> email.value);
            case "fEmail" -> person.getFamilyEmail().stream().map(email -> email.value);
            case "email" -> Stream.concat(person.getPatientEmail().stream(), person.getFamilyEmail().stream())
                    .map(email -> email.value);
            case "tag" -> person.getTags().stream().map(tag -> tag.tagName);
            default -> throw new IllegalStateException("Unsupported find criterion: " + criterion);
        };
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        return other instanceof PersonContainsKeywordsPredicate otherPredicate
                && criterion.equals(otherPredicate.criterion)
                && keywords.equals(otherPredicate.keywords);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).add("criterion", criterion).add("keywords", keywords).toString();
    }
}
