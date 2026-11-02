package seedu.address.model.person;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

public class NameTest {

    @Test
    public void constructor_extraSpaces_normalizesName() {
        Name normalized = new Name("Kim Lee");
        Name spaced = new Name("  Kim    Lee   ");
        assertEquals("Kim Lee", spaced.fullName);
        assertEquals(normalized, spaced);
        assertEquals(normalized.hashCode(), spaced.hashCode());
    }

    @Test
    public void constructor_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new Name(null));
    }

    @Test
    public void constructor_invalidName_throwsIllegalArgumentException() {
        String invalidName = "";
        assertThrows(IllegalArgumentException.class, () -> new Name(invalidName));
    }

    @Test
    public void isValidName() {
        // null name
        assertThrows(NullPointerException.class, () -> Name.isValidName(null));

        // invalid name
        assertFalse(Name.isValidName("")); // empty string
        assertFalse(Name.isValidName(" ")); // spaces only
        assertFalse(Name.isValidName("^")); // only non-alphanumeric characters
        assertFalse(Name.isValidName("peter*")); // contains non-alphanumeric characters
        assertFalse(Name.isValidName("-Ann")); // starts with a hyphen
        assertFalse(Name.isValidName("'Brien")); // starts with an apostrophe
        assertFalse(Name.isValidName("/Ravi")); // starts with a slash
        assertFalse(Name.isValidName("Ravi/")); // ends with a slash
        assertFalse(Name.isValidName("Ravi s/ o Kumar")); // space after a slash
        assertFalse(Name.isValidName("Ravi s//o Kumar")); // two slashes
        assertFalse(Name.isValidName("Kim/Lee")); // 3 or more letters after a slash

        // valid name
        assertTrue(Name.isValidName("peter jack")); // alphabets only
        assertTrue(Name.isValidName("12345")); // numbers only
        assertTrue(Name.isValidName("peter the 2nd")); // alphanumeric characters
        assertTrue(Name.isValidName("Capital Tan")); // with capital letters
        assertTrue(Name.isValidName("David Roger Jackson Ray Jr 2nd")); // long names
        assertTrue(Name.isValidName("Marie-Ann Tan")); // hyphen
        assertTrue(Name.isValidName("Siobhan O'Brien")); // apostrophe
        assertTrue(Name.isValidName("Siobhan O\u2019Brien")); // typographic apostrophe
        assertTrue(Name.isValidName("Ravi S. Kumar")); // full stop
        assertTrue(Name.isValidName("Tan, Mei Ling")); // comma
        assertTrue(Name.isValidName("Jos\u00e9 N\u00fa\u00f1ez")); // accented letters
        assertTrue(Name.isValidName("Ravi s/o Kumar")); // slash joining short parts
        assertTrue(Name.isValidName("Devi A/P Rajan")); // in any letter case
    }

    @Test
    public void equals() {
        Name name = new Name("Valid Name");

        // same values -> returns true
        assertTrue(name.equals(new Name("Valid Name")));

        // same object -> returns true
        assertTrue(name.equals(name));

        // null -> returns false
        assertFalse(name.equals(null));

        // different types -> returns false
        assertFalse(name.equals(5.0f));

        // different values -> returns false
        assertFalse(name.equals(new Name("Other Valid Name")));
    }
}
