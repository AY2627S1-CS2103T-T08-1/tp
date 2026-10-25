package seedu.address.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.storage.JsonAdaptedPerson.MISSING_FIELD_MESSAGE_FORMAT;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalPersons.BENSON;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.commons.util.JsonUtil;
import seedu.address.model.person.Address;
import seedu.address.model.person.CheckupAppointment;
import seedu.address.model.person.Email;
import seedu.address.model.person.Name;
import seedu.address.model.person.Person;
import seedu.address.model.person.Phone;
import seedu.address.model.person.Remark;
import seedu.address.testutil.PersonBuilder;

public class JsonAdaptedPersonTest {
    private static final String INVALID_NAME = "R@chel";
    private static final String INVALID_PHONE = "+651234";
    private static final String INVALID_ADDRESS = " ";
    private static final String INVALID_EMAIL = "example.com";
    private static final String INVALID_TAG = "#friend";

    private static final String VALID_NAME = BENSON.getName().toString();
    private static final String VALID_PATIENT_PHONE = BENSON.getPatientNo().toString();
    private static final String VALID_FAMILY_PHONE = BENSON.getFamilyNo().toString();
    private static final String VALID_EMAIL = "benson@example.com";
    private static final String VALID_ADDRESS = BENSON.getAddress().toString();
    private static final List<JsonAdaptedTag> VALID_TAGS = BENSON.getTags().stream()
            .map(JsonAdaptedTag::new)
            .collect(Collectors.toList());

    @Test
    public void jsonRoundTrip_preservesCheckupIncludingPastAppointments() throws Exception {
        Person scheduled = new PersonBuilder(BENSON).withCheckupAppointment("01/01/2000 09:05").build();
        String json = JsonUtil.toJsonString(new JsonAdaptedPerson(scheduled));
        assertEquals(scheduled, JsonUtil.fromJsonString(json, JsonAdaptedPerson.class).toModelType());
    }

    @Test
    public void jsonWithoutCheckup_loadsExistingContacts() throws Exception {
        String json = JsonUtil.toJsonString(new JsonAdaptedPerson(BENSON));
        json = json.replaceAll(",\\s*\"checkupAppointment\"\\s*:\\s*null", "");
        assertEquals(BENSON, JsonUtil.fromJsonString(json, JsonAdaptedPerson.class).toModelType());
    }

    @Test
    public void toModelType_invalidCheckup_rejectsMalformedSavedDate() {
        JsonAdaptedPerson adapted = new JsonAdaptedPerson(VALID_NAME, VALID_PATIENT_PHONE, VALID_FAMILY_PHONE,
                VALID_EMAIL, null, null, VALID_ADDRESS, VALID_TAGS, "31/02/2030 12:00", null);
        assertThrows(IllegalValueException.class, CheckupAppointment.MESSAGE_CONSTRAINTS, adapted::toModelType);
    }

    @Test
    public void toModelType_validPersonDetails_returnsPerson() throws Exception {
        JsonAdaptedPerson person = new JsonAdaptedPerson(BENSON);
        assertEquals(BENSON, person.toModelType());
    }

    @Test
    public void toModelType_personWithRemarks_returnsPerson() throws Exception {
        Person withRemarks = new PersonBuilder(BENSON).withRemarks("Allergic to penicillin", "Visits after 3pm")
                .build();
        assertEquals(withRemarks, new JsonAdaptedPerson(withRemarks).toModelType());
    }

    @Test
    public void toModelType_noRemarksField_returnsPersonWithoutNotes() throws Exception {
        // data files from v1.4 and earlier have no remarks field
        JsonAdaptedPerson person = new JsonAdaptedPerson(VALID_NAME, VALID_PATIENT_PHONE, VALID_FAMILY_PHONE,
                VALID_EMAIL, VALID_ADDRESS, VALID_TAGS);
        assertEquals(Remark.EMPTY, person.toModelType().getRemark());
    }

    @Test
    public void toModelType_invalidRemarks_throwsIllegalValueException() {
        for (List<String> invalidNotes : List.of(List.of(" "), Arrays.asList("note", null))) {
            JsonAdaptedPerson person = new JsonAdaptedPerson(VALID_NAME, VALID_PATIENT_PHONE, VALID_FAMILY_PHONE,
                    VALID_EMAIL, null, null, VALID_ADDRESS, VALID_TAGS, null, invalidNotes);
            assertThrows(IllegalValueException.class, Remark.MESSAGE_CONSTRAINTS, person::toModelType);
        }
    }

    @Test
    public void toModelType_remarksTooLong_throwsIllegalValueException() {
        JsonAdaptedPerson person = new JsonAdaptedPerson(VALID_NAME, VALID_PATIENT_PHONE, VALID_FAMILY_PHONE,
                VALID_EMAIL, null, null, VALID_ADDRESS, VALID_TAGS, null,
                List.of("a".repeat(300), "b".repeat(201)));
        assertThrows(IllegalValueException.class, Remark.MESSAGE_TOO_LONG, person::toModelType);
    }

    @Test
    public void toModelType_invalidName_throwsIllegalValueException() {
        JsonAdaptedPerson person =
                new JsonAdaptedPerson(INVALID_NAME, VALID_PATIENT_PHONE, VALID_FAMILY_PHONE, VALID_EMAIL,
                        VALID_ADDRESS, VALID_TAGS);
        String expectedMessage = Name.MESSAGE_CONSTRAINTS;
        assertThrows(IllegalValueException.class, expectedMessage, person::toModelType);
    }

    @Test
    public void toModelType_nullName_throwsIllegalValueException() {
        JsonAdaptedPerson person = new JsonAdaptedPerson(null, VALID_PATIENT_PHONE, VALID_FAMILY_PHONE, VALID_EMAIL,
                VALID_ADDRESS, VALID_TAGS);
        String expectedMessage = String.format(MISSING_FIELD_MESSAGE_FORMAT, Name.class.getSimpleName());
        assertThrows(IllegalValueException.class, expectedMessage, person::toModelType);
    }

    @Test
    public void toModelType_invalidPhone_throwsIllegalValueException() {
        JsonAdaptedPerson person =
                new JsonAdaptedPerson(VALID_NAME, INVALID_PHONE, VALID_FAMILY_PHONE, VALID_EMAIL,
                        VALID_ADDRESS, VALID_TAGS);
        String expectedMessage = "Patient HP is invalid. " + Phone.MESSAGE_CONSTRAINTS;
        assertThrows(IllegalValueException.class, expectedMessage, person::toModelType);
    }

    @Test
    public void toModelType_nullPhone_throwsIllegalValueException() {
        JsonAdaptedPerson person = new JsonAdaptedPerson(VALID_NAME, null, VALID_FAMILY_PHONE, VALID_EMAIL,
                VALID_ADDRESS, VALID_TAGS);
        String expectedMessage = String.format(MISSING_FIELD_MESSAGE_FORMAT, Phone.class.getSimpleName());
        assertThrows(IllegalValueException.class, expectedMessage, person::toModelType);
    }

    @Test
    public void toModelType_invalidFamilyPhone_throwsIllegalValueException() {
        JsonAdaptedPerson person =
                new JsonAdaptedPerson(VALID_NAME, VALID_PATIENT_PHONE, INVALID_PHONE, VALID_EMAIL,
                        VALID_ADDRESS, VALID_TAGS);
        String expectedMessage = "Family HP is invalid. " + Phone.MESSAGE_CONSTRAINTS;
        assertThrows(IllegalValueException.class, expectedMessage, person::toModelType);
    }

    @Test
    public void toModelType_nullFamilyPhone_throwsIllegalValueException() {
        JsonAdaptedPerson person = new JsonAdaptedPerson(VALID_NAME, VALID_PATIENT_PHONE, null, VALID_EMAIL,
                VALID_ADDRESS, VALID_TAGS);
        String expectedMessage = String.format(MISSING_FIELD_MESSAGE_FORMAT, Phone.class.getSimpleName());
        assertThrows(IllegalValueException.class, expectedMessage, person::toModelType);
    }

    @Test
    public void toModelType_invalidEmail_throwsIllegalValueException() {
        JsonAdaptedPerson person =
                new JsonAdaptedPerson(VALID_NAME, VALID_PATIENT_PHONE, VALID_FAMILY_PHONE, INVALID_EMAIL,
                        VALID_ADDRESS, VALID_TAGS);
        String expectedMessage = Email.MESSAGE_CONSTRAINTS;
        assertThrows(IllegalValueException.class, expectedMessage, person::toModelType);
    }

    @Test
    public void toModelType_invalidAddress_throwsIllegalValueException() {
        JsonAdaptedPerson person =
                new JsonAdaptedPerson(VALID_NAME, VALID_PATIENT_PHONE, VALID_FAMILY_PHONE, VALID_EMAIL,
                        INVALID_ADDRESS, VALID_TAGS);
        String expectedMessage = Address.MESSAGE_CONSTRAINTS;
        assertThrows(IllegalValueException.class, expectedMessage, person::toModelType);
    }

    @Test
    public void toModelType_nullAddress_throwsIllegalValueException() {
        JsonAdaptedPerson person = new JsonAdaptedPerson(VALID_NAME, VALID_PATIENT_PHONE, VALID_FAMILY_PHONE,
                VALID_EMAIL, null, VALID_TAGS);
        String expectedMessage = String.format(MISSING_FIELD_MESSAGE_FORMAT, Address.class.getSimpleName());
        assertThrows(IllegalValueException.class, expectedMessage, person::toModelType);
    }

    @Test
    public void toModelType_invalidTags_throwsIllegalValueException() {
        List<JsonAdaptedTag> invalidTags = new ArrayList<>(VALID_TAGS);
        invalidTags.add(new JsonAdaptedTag(INVALID_TAG));
        JsonAdaptedPerson person =
                new JsonAdaptedPerson(VALID_NAME, VALID_PATIENT_PHONE, VALID_FAMILY_PHONE, VALID_EMAIL,
                        VALID_ADDRESS, invalidTags);
        assertThrows(IllegalValueException.class, person::toModelType);
    }


    @Test
    public void toModelType_multiplePriorities_throwsIllegalValueException() {
        JsonAdaptedPerson person = new JsonAdaptedPerson(VALID_NAME, VALID_PATIENT_PHONE, VALID_FAMILY_PHONE,
                VALID_EMAIL, VALID_ADDRESS, List.of(new JsonAdaptedTag("HIGH"), new JsonAdaptedTag("LOW")));
        assertThrows(IllegalValueException.class, Person.MESSAGE_TAG_COUNT_CONSTRAINTS, person::toModelType);
    }

    @Test
    public void toModelType_duplicatePriority_returnsSingleTag() throws Exception {
        JsonAdaptedPerson person = new JsonAdaptedPerson(VALID_NAME, VALID_PATIENT_PHONE, VALID_FAMILY_PHONE,
                VALID_EMAIL, VALID_ADDRESS, List.of(new JsonAdaptedTag("HIGH"), new JsonAdaptedTag("HIGH")));
        assertEquals(1, person.toModelType().getTags().size());
    }

    @Test
    public void toModelType_noPriority_returnsEmptyTagSet() throws Exception {
        JsonAdaptedPerson person = new JsonAdaptedPerson(VALID_NAME, VALID_PATIENT_PHONE, VALID_FAMILY_PHONE,
                VALID_EMAIL, VALID_ADDRESS, List.of());
        assertEquals(0, person.toModelType().getTags().size());
    }
}
