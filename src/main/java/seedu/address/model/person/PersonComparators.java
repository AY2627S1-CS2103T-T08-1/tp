package seedu.address.model.person;

import java.util.Comparator;
import java.util.Optional;

import seedu.address.model.tag.Priority;

/**
 * Comparators for ordering {@code Person} objects.
 */
public class PersonComparators {

    private static final Comparator<Person> BY_NAME_IGNORING_CASE =
            Comparator.comparing(person -> person.getName().fullName, String.CASE_INSENSITIVE_ORDER);

    private PersonComparators() {
        // Prevent instantiation.
    }

    /**
     * Returns a comparator ordering persons by name, ignoring case.
     *
     * @param ascending True for A to Z, false for Z to A.
     */
    public static Comparator<Person> byName(boolean ascending) {
        return ascending ? BY_NAME_IGNORING_CASE : BY_NAME_IGNORING_CASE.reversed();
    }

    /**
     * Returns a comparator ordering persons by priority tag. Persons without a priority tag always come last.
     * Persons with the same priority are ordered by name, A to Z.
     *
     * @param mostUrgentFirst True for HIGH, MEDIUM, LOW; false for LOW, MEDIUM, HIGH.
     */
    public static Comparator<Person> byPriority(boolean mostUrgentFirst) {
        Comparator<Person> byPriority = (first, second) -> {
            Optional<Priority> firstPriority = getPriority(first);
            Optional<Priority> secondPriority = getPriority(second);
            if (firstPriority.isEmpty() || secondPriority.isEmpty()) {
                // untagged persons go last regardless of direction
                return Boolean.compare(firstPriority.isEmpty(), secondPriority.isEmpty());
            }
            // HIGH is declared before LOW, so the natural enum order is most urgent first
            int comparison = firstPriority.get().compareTo(secondPriority.get());
            return mostUrgentFirst ? comparison : -comparison;
        };
        return byPriority.thenComparing(BY_NAME_IGNORING_CASE);
    }

    /**
     * Returns a comparator ordering persons by checkup appointment date and time. Persons without a checkup
     * appointment always come last. Persons with the same appointment (or none) are ordered by name, A to Z.
     *
     * @param ascending True for the earliest appointment first, false for the latest appointment first.
     */
    public static Comparator<Person> byCheckup(boolean ascending) {
        Comparator<Person> byCheckup = (first, second) -> {
            Optional<CheckupAppointment> firstCheckup = first.getCheckupAppointment();
            Optional<CheckupAppointment> secondCheckup = second.getCheckupAppointment();
            if (firstCheckup.isEmpty() || secondCheckup.isEmpty()) {
                // persons without a checkup go last regardless of direction
                return Boolean.compare(firstCheckup.isEmpty(), secondCheckup.isEmpty());
            }
            int comparison = firstCheckup.get().compareTo(secondCheckup.get());
            return ascending ? comparison : -comparison;
        };
        return byCheckup.thenComparing(BY_NAME_IGNORING_CASE);
    }

    private static Optional<Priority> getPriority(Person person) {
        return person.getTags().stream()
                .map(tag -> Priority.valueOf(tag.tagName))
                .findFirst();
    }
}
