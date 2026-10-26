package seedu.address.model.person;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.testutil.PersonBuilder;

public class PersonMatchesFilterPredicateTest {

    private static PersonMatchesFilterPredicate predicate(List<String> names, List<String> tags,
            List<String> addresses) {
        return new PersonMatchesFilterPredicate(names, tags, addresses);
    }

    @Test
    public void constructor_nullList_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> predicate(null, List.of(), List.of()));
        assertThrows(NullPointerException.class, () -> predicate(List.of(), null, List.of()));
        assertThrows(NullPointerException.class, () -> predicate(List.of(), List.of(), null));
    }

    @Test
    public void equals() {
        PersonMatchesFilterPredicate first = predicate(List.of("ali"), List.of("HIGH"), List.of("clementi"));

        // same object -> returns true
        assertTrue(first.equals(first));

        // same values -> returns true
        assertTrue(first.equals(predicate(List.of("ali"), List.of("HIGH"), List.of("clementi"))));

        // tags differing only in case -> returns true
        assertTrue(first.equals(predicate(List.of("ali"), List.of("high"), List.of("clementi"))));

        // different types -> returns false
        assertFalse(first.equals(1));

        // null -> returns false
        assertFalse(first.equals(null));

        // different name, tag or address -> returns false
        assertFalse(first.equals(predicate(List.of("bob"), List.of("HIGH"), List.of("clementi"))));
        assertFalse(first.equals(predicate(List.of("ali"), List.of("LOW"), List.of("clementi"))));
        assertFalse(first.equals(predicate(List.of("ali"), List.of("HIGH"), List.of("jurong"))));
    }

    @Test
    public void hashCode_consistentWithEquals() {
        assertEquals(predicate(List.of("ali"), List.of("HIGH"), List.of()).hashCode(),
                predicate(List.of("ali"), List.of("high"), List.of()).hashCode());
        assertNotEquals(predicate(List.of("ali"), List.of("HIGH"), List.of()).hashCode(),
                predicate(List.of("ali"), List.of("LOW"), List.of()).hashCode());
    }

    @Test
    public void test_nameCriterion() {
        PersonMatchesFilterPredicate predicate = predicate(List.of("ali", "bob"), List.of(), List.of());

        // partial, any keyword, case-insensitive
        assertTrue(predicate.test(new PersonBuilder().withName("Alice Pauline").build()));
        assertTrue(predicate.test(new PersonBuilder().withName("BOBBY Tan").build()));

        // no keyword matches
        assertFalse(predicate.test(new PersonBuilder().withName("Carl Kurz").build()));
    }

    @Test
    public void test_tagCriterion() {
        PersonMatchesFilterPredicate predicate = predicate(List.of(), List.of("high", "MEDIUM"), List.of());

        // any listed tag, case-insensitive
        assertTrue(predicate.test(new PersonBuilder().withTags("HIGH").build()));
        assertTrue(predicate.test(new PersonBuilder().withTags("MEDIUM").build()));

        // other tag or no tag
        assertFalse(predicate.test(new PersonBuilder().withTags("LOW").build()));
        assertFalse(predicate.test(new PersonBuilder().withTags().build()));
    }

    @Test
    public void test_addressCriterion() {
        PersonMatchesFilterPredicate predicate = predicate(List.of(), List.of(), List.of("clementi ave", "JURONG"));

        // partial, any phrase, case-insensitive
        assertTrue(predicate.test(new PersonBuilder().withAddress("311, Clementi Ave 2, #02-25").build()));
        assertTrue(predicate.test(new PersonBuilder().withAddress("123, Jurong West Ave 6").build()));

        // phrase must match as a whole
        assertFalse(predicate.test(new PersonBuilder().withAddress("Clementi Road, Ave 2").build()));
        assertFalse(predicate.test(new PersonBuilder().withAddress("wall street").build()));
    }

    @Test
    public void test_multipleCriteria_allMustMatch() {
        PersonMatchesFilterPredicate predicate =
                predicate(List.of("meier"), List.of("HIGH"), List.of("street"));

        assertTrue(predicate.test(new PersonBuilder().withName("Daniel Meier").withTags("HIGH")
                .withAddress("10th street").build()));

        // each criterion failing on its own
        assertFalse(predicate.test(new PersonBuilder().withName("Daniel Tan").withTags("HIGH")
                .withAddress("10th street").build()));
        assertFalse(predicate.test(new PersonBuilder().withName("Daniel Meier").withTags("LOW")
                .withAddress("10th street").build()));
        assertFalse(predicate.test(new PersonBuilder().withName("Daniel Meier").withTags("HIGH")
                .withAddress("little india").build()));
    }

    @Test
    public void toStringMethod() {
        PersonMatchesFilterPredicate predicate = predicate(List.of("ali"), List.of("HIGH"), List.of("clementi"));
        String expected = PersonMatchesFilterPredicate.class.getCanonicalName()
                + "{nameKeywords=[ali], tags=[HIGH], addressPhrases=[clementi]}";
        assertEquals(expected, predicate.toString());
    }
}
