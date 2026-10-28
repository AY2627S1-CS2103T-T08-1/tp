package seedu.address.model.person;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;

public class RemarkTest {

    private static final Remark TWO_NOTES = new Remark(List.of("Allergic to penicillin", "Visits after 3pm"));

    @Test
    public void constructor_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new Remark(null));
    }

    @Test
    public void constructor_invalidNotes_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> new Remark(List.of("")));
        assertThrows(IllegalArgumentException.class, () -> new Remark(List.of(" note")));
        assertThrows(IllegalArgumentException.class, () -> new Remark(Arrays.asList("note", null)));
        assertThrows(IllegalArgumentException.class, () -> new Remark(List.of("a".repeat(501))));
    }

    @Test
    public void constructor_copiesNotes() {
        List<String> notes = new ArrayList<>(List.of("Allergic to penicillin"));
        Remark remark = new Remark(notes);
        notes.add("Visits after 3pm");
        assertEquals(List.of("Allergic to penicillin"), remark.getNotes());
    }

    @Test
    public void isValidNote() {
        // invalid notes
        assertFalse(Remark.isValidNote("")); // empty string
        assertFalse(Remark.isValidNote(" ")); // spaces only
        assertFalse(Remark.isValidNote(" leading space"));
        assertFalse(Remark.isValidNote("\u00A0")); // non-breaking space only
        assertFalse(Remark.isValidNote("\u3000note")); // starts with an ideographic space

        // valid notes
        assertTrue(Remark.isValidNote("a"));
        assertTrue(Remark.isValidNote("Takes /med at 9am, 50% dose")); // slashes and symbols
    }

    @Test
    public void isWithinLengthLimit() {
        assertTrue(Remark.isWithinLengthLimit(List.of()));
        assertTrue(Remark.isWithinLengthLimit(List.of("a".repeat(500))));
        assertTrue(Remark.isWithinLengthLimit(List.of("a".repeat(250), "b".repeat(250))));
        assertFalse(Remark.isWithinLengthLimit(List.of("a".repeat(501))));
        assertFalse(Remark.isWithinLengthLimit(List.of("a".repeat(250), "b".repeat(251))));
    }

    @Test
    public void hasNote() {
        assertTrue(TWO_NOTES.hasNote(Index.fromOneBased(2)));
        assertFalse(TWO_NOTES.hasNote(Index.fromOneBased(3)));
        assertFalse(Remark.EMPTY.hasNote(Index.fromOneBased(1)));
    }

    @Test
    public void withNoteChanges_returnUpdatedNotesAndLeaveRemarkUnchanged() {
        assertEquals(List.of("Allergic to penicillin", "Visits after 3pm", "Diabetic"),
                TWO_NOTES.withNoteAdded("Diabetic"));
        assertEquals(List.of("Allergic to penicillin", "Visits after 4pm"),
                TWO_NOTES.withNoteReplaced(Index.fromOneBased(2), "Visits after 4pm"));
        assertEquals(List.of("Visits after 3pm"), TWO_NOTES.withNoteRemoved(Index.fromOneBased(1)));
        assertEquals(List.of("Allergic to penicillin", "Visits after 3pm"), TWO_NOTES.getNotes());
    }

    @Test
    public void getNumberedNotes() {
        assertEquals(List.of("1. Allergic to penicillin", "2. Visits after 3pm"), TWO_NOTES.getNumberedNotes());
        assertEquals(List.of(), Remark.EMPTY.getNumberedNotes());
    }

    @Test
    public void toStringMethod() {
        assertEquals("1. Allergic to penicillin 2. Visits after 3pm", TWO_NOTES.toString());
        assertEquals("", Remark.EMPTY.toString());
    }

    @Test
    public void equals() {
        // same values -> returns true
        assertTrue(TWO_NOTES.equals(new Remark(List.of("Allergic to penicillin", "Visits after 3pm"))));

        // same object -> returns true
        assertTrue(TWO_NOTES.equals(TWO_NOTES));

        // null -> returns false
        assertFalse(TWO_NOTES.equals(null));

        // different types -> returns false
        assertFalse(TWO_NOTES.equals(5.0f));

        // different order -> returns false
        assertFalse(TWO_NOTES.equals(new Remark(List.of("Visits after 3pm", "Allergic to penicillin"))));

        assertEquals(TWO_NOTES.hashCode(),
                new Remark(List.of("Allergic to penicillin", "Visits after 3pm")).hashCode());
    }
}
