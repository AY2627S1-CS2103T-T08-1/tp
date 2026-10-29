package seedu.address.model.person;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import seedu.address.commons.core.index.Index;

/**
 * Represents the numbered care notes kept on a Person in the address book.
 * Guarantees: immutable; every note is valid as declared in {@link #isValidNote(String)}
 * and the notes fit within the limit declared in {@link #isWithinLengthLimit(List)}.
 */
public class Remark {

    public static final int MAX_TOTAL_LENGTH = 500;

    public static final String MESSAGE_CONSTRAINTS = "Notes should not be blank or start with a space";
    public static final String MESSAGE_TOO_LONG = "A contact's notes can be at most " + MAX_TOTAL_LENGTH
            + " characters in total.";

    /*
     * The first character of a note must not be a whitespace or another space character such as a
     * non-breaking space, otherwise " " (a blank string) becomes a valid input.
     */
    public static final String VALIDATION_REGEX = "[^\\s\\p{Z}].*";

    /** A remark with no notes. */
    public static final Remark EMPTY = new Remark(List.of());

    private final List<String> notes;

    /**
     * Constructs a {@code Remark} holding {@code notes} in order.
     *
     * @param notes Valid notes whose total length is within the limit.
     */
    public Remark(List<String> notes) {
        requireNonNull(notes);
        notes.forEach(note -> checkArgument(note != null && isValidNote(note), MESSAGE_CONSTRAINTS));
        checkArgument(isWithinLengthLimit(notes), MESSAGE_TOO_LONG);
        this.notes = List.copyOf(notes);
    }

    /**
     * Returns true if a given string is a valid note.
     */
    public static boolean isValidNote(String test) {
        return test.matches(VALIDATION_REGEX);
    }

    /**
     * Returns true if the total length of {@code notes} is at most {@link #MAX_TOTAL_LENGTH}.
     */
    public static boolean isWithinLengthLimit(List<String> notes) {
        return getTotalLength(notes) <= MAX_TOTAL_LENGTH;
    }

    private static int getTotalLength(List<String> notes) {
        return notes.stream().mapToInt(String::length).sum();
    }

    /**
     * Returns the notes in order, as an unmodifiable list.
     */
    public List<String> getNotes() {
        return notes;
    }

    /**
     * Returns true if there are no notes.
     */
    public boolean isEmpty() {
        return notes.isEmpty();
    }

    /**
     * Returns true if {@code noteIndex} refers to one of the notes.
     */
    public boolean hasNote(Index noteIndex) {
        requireNonNull(noteIndex);
        return noteIndex.getZeroBased() < notes.size();
    }

    /**
     * Returns the index of the first note that is the same as {@code note} ignoring letter case, if any.
     */
    public Optional<Index> findNoteIgnoringCase(String note) {
        requireNonNull(note);
        return IntStream.range(0, notes.size())
                .filter(i -> notes.get(i).equalsIgnoreCase(note))
                .mapToObj(Index::fromZeroBased)
                .findFirst();
    }

    /**
     * Returns the notes that would result from adding {@code note} after the last one.
     */
    public List<String> withNoteAdded(String note) {
        requireNonNull(note);
        List<String> updatedNotes = new ArrayList<>(notes);
        updatedNotes.add(note);
        return updatedNotes;
    }

    /**
     * Returns the notes that would result from replacing the note at {@code noteIndex} with {@code note}.
     */
    public List<String> withNoteReplaced(Index noteIndex, String note) {
        requireNonNull(note);
        assert hasNote(noteIndex);
        List<String> updatedNotes = new ArrayList<>(notes);
        updatedNotes.set(noteIndex.getZeroBased(), note);
        return updatedNotes;
    }

    /**
     * Returns the notes that would result from removing the note at {@code noteIndex}.
     */
    public List<String> withNoteRemoved(Index noteIndex) {
        assert hasNote(noteIndex);
        List<String> updatedNotes = new ArrayList<>(notes);
        updatedNotes.remove(noteIndex.getZeroBased());
        return updatedNotes;
    }

    /**
     * Returns the notes numbered from 1, such as {@code ["1. Allergic to penicillin", "2. Visits after 3pm"]}.
     */
    public List<String> getNumberedNotes() {
        return IntStream.range(0, notes.size())
                .mapToObj(i -> (i + 1) + ". " + notes.get(i))
                .toList();
    }

    @Override
    public String toString() {
        return String.join(" ", getNumberedNotes());
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof Remark otherRemark)) {
            return false;
        }

        return notes.equals(otherRemark.notes);
    }

    @Override
    public int hashCode() {
        return notes.hashCode();
    }

}
