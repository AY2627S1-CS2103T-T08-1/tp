package seedu.address.model.person;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.testutil.PersonBuilder;

public class PersonContainsKeywordsPredicateTest {
    @Test
    public void test_remark_matchesWithinOneNoteIgnoringCaseAndSpacing() {
        Person person = new PersonBuilder().withRemarks("Bring medication", "Needs    Assistance   ").build();
        assertTrue(predicate(FindCriterion.REMARK, "needs assistance").test(person));
        assertTrue(predicate(FindCriterion.REMARK, "ASSIST").test(person));
        assertFalse(predicate(FindCriterion.REMARK, "medication Needs").test(person));
        assertFalse(predicate(FindCriterion.REMARK, "1.").test(person));
        assertFalse(predicate(FindCriterion.REMARK, "assistance").test(new PersonBuilder().build()));
        assertFalse(predicate(FindCriterion.REMARK, "assistance")
                .test(new PersonBuilder().withName("Needs assistance").build()));
    }

    @Test
    public void test_phoneDigitsMustBeConsecutive() {
        PersonContainsKeywordsPredicate patientPredicate = predicate(FindCriterion.PATIENT_PHONE, "87");
        assertTrue(patientPredicate.test(new PersonBuilder().withPatientNo("900087000").build()));
        assertTrue(patientPredicate.test(new PersonBuilder().withPatientNo("87000000").build()));
        assertTrue(patientPredicate.test(new PersonBuilder().withPatientNo("90000087").build()));
        assertFalse(patientPredicate.test(new PersonBuilder().withPatientNo("89700000").build()));
        assertFalse(patientPredicate.test(new PersonBuilder().withPatientNo("78000000").build()));
        assertFalse(patientPredicate.test(new PersonBuilder().withPatientNo("99999999")
                .withFamilyNo("900087000").build()));

        PersonContainsKeywordsPredicate familyPredicate = predicate(FindCriterion.FAMILY_PHONE, "87");
        assertTrue(familyPredicate.test(new PersonBuilder().withFamilyNo("900087000").build()));
        assertFalse(familyPredicate.test(new PersonBuilder().withFamilyNo("89700000").build()));
        assertFalse(familyPredicate.test(new PersonBuilder().withFamilyNo("99999999")
                .withPatientNo("900087000").build()));
        assertTrue(predicate(FindCriterion.PATIENT_PHONE, "00")
                .test(new PersonBuilder().withPatientNo("87000000").build()));
    }

    @Test
    public void test_textFieldsAllowCaseInsensitivePartialMatches() {
        Person person = new PersonBuilder().withName("Alice Bob").withAddress("123 Main Street")
                .withPatientEmail("alice@patient.com").withFamilyEmail("bob@family.com").withTags("HIGH").build();
        assertTrue(predicate(FindCriterion.NAME, "LIC").test(person));
        assertTrue(predicate(FindCriterion.ADDRESS, "AIN").test(person));
        assertTrue(predicate(FindCriterion.PATIENT_EMAIL, "PAT").test(person));
        assertTrue(predicate(FindCriterion.FAMILY_EMAIL, "FAM").test(person));
        assertTrue(predicate(FindCriterion.EMAIL, "PAT").test(person));
        assertTrue(predicate(FindCriterion.EMAIL, "FAM").test(person));
        assertTrue(predicate(FindCriterion.TAG, "igh").test(person));
        assertTrue(new PersonContainsKeywordsPredicate(FindCriterion.ADDRESS, List.of("missing", "AIN")).test(person));
        assertFalse(predicate(FindCriterion.NAME, "Main").test(person));
        assertFalse(predicate(FindCriterion.ADDRESS, "Alice").test(person));
        assertFalse(predicate(FindCriterion.PATIENT_EMAIL, "family").test(person));
        assertFalse(predicate(FindCriterion.FAMILY_EMAIL, "patient").test(person));
        assertFalse(predicate(FindCriterion.TAG, "missing").test(person));
        assertFalse(new PersonContainsKeywordsPredicate(FindCriterion.NAME, List.of()).test(person));
    }

    @Test
    public void test_missingOptionalEmailsAndTags_doNotMatch() {
        Person person = new PersonBuilder().withoutEmail().build();
        assertFalse(predicate(FindCriterion.PATIENT_EMAIL, "com").test(person));
        assertFalse(predicate(FindCriterion.FAMILY_EMAIL, "com").test(person));
        assertFalse(predicate(FindCriterion.EMAIL, "com").test(person));
        assertFalse(predicate(FindCriterion.TAG, "igh").test(person));
    }

    @Test
    public void equals_comparesCriterionAndKeywords() {
        PersonContainsKeywordsPredicate predicate = predicate(FindCriterion.PATIENT_PHONE, "87");
        assertEquals(predicate, predicate);
        assertEquals(predicate, predicate(FindCriterion.PATIENT_PHONE, "87"));
        assertFalse(predicate.equals(predicate(FindCriterion.FAMILY_PHONE, "87")));
        assertFalse(predicate.equals(predicate(FindCriterion.PATIENT_PHONE, "88")));
        assertFalse(predicate.equals(null));
        assertFalse(predicate.equals("87"));
    }

    private PersonContainsKeywordsPredicate predicate(FindCriterion criterion, String keyword) {
        return new PersonContainsKeywordsPredicate(criterion, List.of(keyword));
    }
}
