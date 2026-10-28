package seedu.address.model.person;

import java.util.Comparator;
import java.util.Optional;

import seedu.address.model.tag.Priority;

/**
 * Comparators for ordering {@code Person} objects.
 */
public class PersonComparators {

    private static final Comparator<Person> BY_NAME_IGNORING_CASE =
            Comparator.comparing(person -> person.getName().fullName, PersonComparators::compareNaturally);

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
     * Compares two strings ignoring case, treating each run of digits as one number so that
     * "Ethan2" comes before "Ethan10". Strings that are equal under this order but differ in case or
     * leading zeros are ordered by a plain case-insensitive comparison, so the order stays consistent.
     */
    private static int compareNaturally(String first, String second) {
        int i = 0;
        int j = 0;
        while (i < first.length() && j < second.length()) {
            char firstChar = first.charAt(i);
            char secondChar = second.charAt(j);
            if (isAsciiDigit(firstChar) && isAsciiDigit(secondChar)) {
                int firstEnd = endOfDigits(first, i);
                int secondEnd = endOfDigits(second, j);
                int comparison = compareNumbers(first.substring(i, firstEnd), second.substring(j, secondEnd));
                if (comparison != 0) {
                    return comparison;
                }
                i = firstEnd;
                j = secondEnd;
                continue;
            }
            int comparison = Character.compare(foldCase(firstChar), foldCase(secondChar));
            if (comparison != 0) {
                return comparison;
            }
            i++;
            j++;
        }
        int remaining = Boolean.compare(i < first.length(), j < second.length());
        return remaining != 0 ? remaining : String.CASE_INSENSITIVE_ORDER.compare(first, second);
    }

    private static boolean isAsciiDigit(char c) {
        return c >= '0' && c <= '9';
    }

    private static int endOfDigits(String value, int start) {
        int end = start;
        while (end < value.length() && isAsciiDigit(value.charAt(end))) {
            end++;
        }
        return end;
    }

    /** Compares two strings of digits by numeric value, without limit on their length. */
    private static int compareNumbers(String first, String second) {
        String firstNumber = first.replaceFirst("^0+(?=\\d)", "");
        String secondNumber = second.replaceFirst("^0+(?=\\d)", "");
        if (firstNumber.length() != secondNumber.length()) {
            return Integer.compare(firstNumber.length(), secondNumber.length());
        }
        return firstNumber.compareTo(secondNumber);
    }

    /** Folds case the same way as {@link String#CASE_INSENSITIVE_ORDER}. */
    private static char foldCase(char c) {
        return Character.toLowerCase(Character.toUpperCase(c));
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
