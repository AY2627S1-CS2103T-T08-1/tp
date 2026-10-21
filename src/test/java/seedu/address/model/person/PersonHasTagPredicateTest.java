package seedu.address.model.person;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import seedu.address.testutil.PersonBuilder;

public class PersonHasTagPredicateTest {

    @Test
    public void constructor_nullTagName_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new PersonHasTagPredicate(null));
    }

    @Test
    public void equals() {
        PersonHasTagPredicate firstPredicate = new PersonHasTagPredicate("HIGH");
        PersonHasTagPredicate secondPredicate = new PersonHasTagPredicate("LOW");

        // same object -> returns true
        assertTrue(firstPredicate.equals(firstPredicate));

        // same values -> returns true
        assertTrue(firstPredicate.equals(new PersonHasTagPredicate("HIGH")));

        // same values, different case -> returns true
        assertTrue(firstPredicate.equals(new PersonHasTagPredicate("high")));

        // different types -> returns false
        assertFalse(firstPredicate.equals(1));

        // null -> returns false
        assertFalse(firstPredicate.equals(null));

        // different tag -> returns false
        assertFalse(firstPredicate.equals(secondPredicate));
    }

    @Test
    public void hashCode_consistentWithEquals() {
        assertEquals(new PersonHasTagPredicate("HIGH").hashCode(), new PersonHasTagPredicate("high").hashCode());
        assertNotEquals(new PersonHasTagPredicate("HIGH").hashCode(), new PersonHasTagPredicate("LOW").hashCode());
    }

    @Test
    public void test_personHasTag_returnsTrue() {
        PersonHasTagPredicate predicate = new PersonHasTagPredicate("MEDIUM");

        // only tag
        assertTrue(predicate.test(new PersonBuilder().withTags("MEDIUM").build()));
    }

    @Test
    public void test_lowerCaseTagName_returnsTrue() {
        PersonHasTagPredicate predicate = new PersonHasTagPredicate("medium");
        assertTrue(predicate.test(new PersonBuilder().withTags("MEDIUM").build()));
    }

    @Test
    public void test_personLacksTag_returnsFalse() {
        PersonHasTagPredicate predicate = new PersonHasTagPredicate("MEDIUM");

        // no tags
        assertFalse(predicate.test(new PersonBuilder().withTags().build()));

        // different tag
        assertFalse(predicate.test(new PersonBuilder().withTags("HIGH").build()));
    }

    @Test
    public void toStringMethod() {
        PersonHasTagPredicate predicate = new PersonHasTagPredicate("HIGH");
        String expected = PersonHasTagPredicate.class.getCanonicalName() + "{tagName=HIGH}";
        assertEquals(expected, predicate.toString());
    }
}
