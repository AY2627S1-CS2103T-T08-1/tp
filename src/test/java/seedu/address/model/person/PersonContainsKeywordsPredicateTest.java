package seedu.address.model.person;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.testutil.PersonBuilder;

public class PersonContainsKeywordsPredicateTest {
    @Test
    public void test_phoneDigitsMustBeConsecutive() {
        PersonContainsKeywordsPredicate patientPredicate = predicate("patientNo", "87");
        assertTrue(patientPredicate.test(new PersonBuilder().withPatientNo("900087000").build()));
        assertTrue(patientPredicate.test(new PersonBuilder().withPatientNo("87000000").build()));
        assertTrue(patientPredicate.test(new PersonBuilder().withPatientNo("90000087").build()));
        assertFalse(patientPredicate.test(new PersonBuilder().withPatientNo("89700000").build()));
        assertFalse(patientPredicate.test(new PersonBuilder().withPatientNo("78000000").build()));
        assertFalse(patientPredicate.test(new PersonBuilder().withPatientNo("99999999")
                .withFamilyNo("900087000").build()));

        PersonContainsKeywordsPredicate familyPredicate = predicate("familyNo", "87");
        assertTrue(familyPredicate.test(new PersonBuilder().withFamilyNo("900087000").build()));
        assertFalse(familyPredicate.test(new PersonBuilder().withFamilyNo("89700000").build()));
        assertFalse(familyPredicate.test(new PersonBuilder().withFamilyNo("99999999")
                .withPatientNo("900087000").build()));
        assertTrue(predicate("patientNo", "00").test(new PersonBuilder().withPatientNo("87000000").build()));
    }

    @Test
    public void test_textFieldsAllowCaseInsensitivePartialMatches() {
        Person person = new PersonBuilder().withName("Alice Bob").withAddress("123 Main Street")
                .withPatientEmail("alice@patient.com").withFamilyEmail("bob@family.com").withTags("HIGH").build();
        assertTrue(predicate("name", "LIC").test(person));
        assertTrue(predicate("address", "AIN").test(person));
        assertTrue(predicate("pEmail", "PAT").test(person));
        assertTrue(predicate("fEmail", "FAM").test(person));
        assertTrue(predicate("email", "PAT").test(person));
        assertTrue(predicate("email", "FAM").test(person));
        assertTrue(predicate("tag", "igh").test(person));
        assertTrue(new PersonContainsKeywordsPredicate("address", List.of("missing", "AIN")).test(person));
        assertFalse(predicate("name", "Main").test(person));
        assertFalse(predicate("address", "Alice").test(person));
        assertFalse(predicate("pEmail", "family").test(person));
        assertFalse(predicate("fEmail", "patient").test(person));
        assertFalse(predicate("tag", "missing").test(person));
        assertFalse(new PersonContainsKeywordsPredicate("name", List.of()).test(person));
    }

    @Test
    public void test_missingOptionalEmailsAndTags_doNotMatch() {
        Person person = new PersonBuilder().withoutEmail().build();
        assertFalse(predicate("pEmail", "com").test(person));
        assertFalse(predicate("fEmail", "com").test(person));
        assertFalse(predicate("email", "com").test(person));
        assertFalse(predicate("tag", "igh").test(person));
    }

    @Test
    public void equals_comparesCriterionAndKeywords() {
        PersonContainsKeywordsPredicate predicate = predicate("patientNo", "87");
        assertEquals(predicate, predicate);
        assertEquals(predicate, predicate("patientNo", "87"));
        assertFalse(predicate.equals(predicate("familyNo", "87")));
        assertFalse(predicate.equals(predicate("patientNo", "88")));
        assertFalse(predicate.equals(null));
        assertFalse(predicate.equals("87"));
    }

    private PersonContainsKeywordsPredicate predicate(String criterion, String keyword) {
        return new PersonContainsKeywordsPredicate(criterion, List.of(keyword));
    }
}
