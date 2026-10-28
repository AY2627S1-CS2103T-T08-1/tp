package seedu.address.model.person;

import static java.util.Objects.requireNonNull;

import java.util.List;
import java.util.Locale;
import java.util.function.Predicate;
import java.util.stream.Stream;

import seedu.address.commons.util.ToStringBuilder;

/**
 * Matches any keyword against the selected contact field using case-insensitive substrings.
 */
public class PersonContainsKeywordsPredicate implements Predicate<Person> {
    private final FindCriterion criterion;
    private final List<String> keywords;

    /**
     * Creates a predicate for a supported criterion and its search keywords.
     * Phone keyword validation is performed by the command parser.
     */
    public PersonContainsKeywordsPredicate(FindCriterion criterion, List<String> keywords) {
        this.criterion = requireNonNull(criterion);
        this.keywords = List.copyOf(keywords);
    }

    @Override
    public boolean test(Person person) {
        return fieldValues(person).anyMatch(value -> keywords.stream()
                .anyMatch(keyword -> value.toLowerCase(Locale.ROOT).contains(keyword.toLowerCase(Locale.ROOT))));
    }

    private Stream<String> fieldValues(Person person) {
        return switch (criterion) {
            case NAME -> Stream.of(person.getName().fullName);
            case PATIENT_PHONE -> Stream.of(person.getPatientNo().value);
            case FAMILY_PHONE -> Stream.of(person.getFamilyNo().value);
            case ADDRESS -> Stream.of(person.getAddress().value);
            case PATIENT_EMAIL -> person.getPatientEmail().stream().map(email -> email.value);
            case FAMILY_EMAIL -> person.getFamilyEmail().stream().map(email -> email.value);
            case EMAIL -> Stream.concat(person.getPatientEmail().stream(), person.getFamilyEmail().stream())
                    .map(email -> email.value);
            case TAG -> person.getTags().stream().map(tag -> tag.tagName);
            case REMARK -> person.getRemark().getNotes().stream().map(note -> note.trim().replaceAll("\\s+", " "));
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
