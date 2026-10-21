package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.commands.CommandTestUtil.ADDRESS_DESC_AMY;
import static seedu.address.logic.commands.CommandTestUtil.ADDRESS_DESC_BOB;
import static seedu.address.logic.commands.CommandTestUtil.EMAIL_DESC_AMY;
import static seedu.address.logic.commands.CommandTestUtil.FAMILY_EMAIL_DESC_AMY;
import static seedu.address.logic.commands.CommandTestUtil.FAMILY_EMAIL_DESC_BOB;
import static seedu.address.logic.commands.CommandTestUtil.FAMILY_PHONE_DESC_AMY;
import static seedu.address.logic.commands.CommandTestUtil.FAMILY_PHONE_DESC_BOB;
import static seedu.address.logic.commands.CommandTestUtil.INVALID_ADDRESS_DESC;
import static seedu.address.logic.commands.CommandTestUtil.INVALID_FAMILY_EMAIL_DESC;
import static seedu.address.logic.commands.CommandTestUtil.INVALID_NAME_DESC;
import static seedu.address.logic.commands.CommandTestUtil.INVALID_PATIENT_EMAIL_DESC;
import static seedu.address.logic.commands.CommandTestUtil.INVALID_PHONE_DESC;
import static seedu.address.logic.commands.CommandTestUtil.INVALID_TAG_DESC;
import static seedu.address.logic.commands.CommandTestUtil.NAME_DESC_AMY;
import static seedu.address.logic.commands.CommandTestUtil.NAME_DESC_BOB;
import static seedu.address.logic.commands.CommandTestUtil.PATIENT_EMAIL_DESC_AMY;
import static seedu.address.logic.commands.CommandTestUtil.PATIENT_EMAIL_DESC_BOB;
import static seedu.address.logic.commands.CommandTestUtil.PHONE_DESC_AMY;
import static seedu.address.logic.commands.CommandTestUtil.PHONE_DESC_BOB;
import static seedu.address.logic.commands.CommandTestUtil.PREAMBLE_NON_EMPTY;
import static seedu.address.logic.commands.CommandTestUtil.PREAMBLE_WHITESPACE;
import static seedu.address.logic.commands.CommandTestUtil.PREFIX_FAMILY_EMAIL;
import static seedu.address.logic.commands.CommandTestUtil.PREFIX_PATIENT_EMAIL;
import static seedu.address.logic.commands.CommandTestUtil.TAG_DESC_HIGH;
import static seedu.address.logic.commands.CommandTestUtil.TAG_DESC_LOW;
import static seedu.address.logic.commands.CommandTestUtil.VALID_ADDRESS_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_EMAIL_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_FAMILY_EMAIL_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_FAMILY_PHONE_AMY;
import static seedu.address.logic.commands.CommandTestUtil.VALID_FAMILY_PHONE_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_NAME_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_PHONE_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_TAG_HIGH;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ADDRESS;
import static seedu.address.logic.parser.CliSyntax.PREFIX_FAMILY_PHONE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PHONE;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static seedu.address.testutil.TypicalPersons.AMY;
import static seedu.address.testutil.TypicalPersons.BOB;

import org.junit.jupiter.api.Test;

import seedu.address.logic.Messages;
import seedu.address.logic.commands.AddCommand;
import seedu.address.model.person.Address;
import seedu.address.model.person.Email;
import seedu.address.model.person.Name;
import seedu.address.model.person.Person;
import seedu.address.model.person.Phone;
import seedu.address.model.tag.Tag;
import seedu.address.testutil.PersonBuilder;

public class AddCommandParserTest {
    private AddCommandParser parser = new AddCommandParser();

    @Test
    public void parse_allFieldsPresent_success() {
        Person expectedPerson = new PersonBuilder(BOB).withFamilyNo(VALID_FAMILY_PHONE_BOB)
                .withPatientEmail(VALID_EMAIL_BOB).withFamilyEmail(VALID_FAMILY_EMAIL_BOB)
                .withTags(VALID_TAG_HIGH).build();

        // whitespace only preamble
        assertParseSuccess(parser, PREAMBLE_WHITESPACE + NAME_DESC_BOB + PHONE_DESC_BOB + FAMILY_PHONE_DESC_BOB
                + PATIENT_EMAIL_DESC_BOB + FAMILY_EMAIL_DESC_BOB + ADDRESS_DESC_BOB + TAG_DESC_HIGH,
                new AddCommand(expectedPerson));


        // repeated identical tags still represent one priority
        assertParseSuccess(parser, NAME_DESC_BOB + PHONE_DESC_BOB + FAMILY_PHONE_DESC_BOB
                + PATIENT_EMAIL_DESC_BOB + FAMILY_EMAIL_DESC_BOB + ADDRESS_DESC_BOB + TAG_DESC_HIGH + TAG_DESC_HIGH,
                new AddCommand(expectedPerson));

        // different priorities are rejected
        assertParseFailure(parser, NAME_DESC_BOB + PHONE_DESC_BOB + FAMILY_PHONE_DESC_BOB
                + ADDRESS_DESC_BOB + TAG_DESC_LOW + TAG_DESC_HIGH, Person.MESSAGE_TAG_COUNT_CONSTRAINTS);
    }

    @Test
    public void parse_repeatedNonTagValue_failure() {
        String validExpectedPersonString = NAME_DESC_BOB + PHONE_DESC_BOB + FAMILY_PHONE_DESC_BOB
                + PATIENT_EMAIL_DESC_BOB + FAMILY_EMAIL_DESC_BOB + ADDRESS_DESC_BOB + TAG_DESC_HIGH;

        // multiple names
        assertParseFailure(parser, NAME_DESC_AMY + validExpectedPersonString,
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_NAME));

        // multiple phones
        assertParseFailure(parser, PHONE_DESC_AMY + validExpectedPersonString,
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_PHONE));

        // multiple family phones
        assertParseFailure(parser, FAMILY_PHONE_DESC_AMY + validExpectedPersonString,
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_FAMILY_PHONE));

        // multiple patient emails
        assertParseFailure(parser, PATIENT_EMAIL_DESC_AMY + validExpectedPersonString,
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_PATIENT_EMAIL));

        // multiple family emails
        assertParseFailure(parser, FAMILY_EMAIL_DESC_AMY + validExpectedPersonString,
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_FAMILY_EMAIL));

        // multiple addresses
        assertParseFailure(parser, ADDRESS_DESC_AMY + validExpectedPersonString,
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_ADDRESS));

        // multiple fields repeated
        assertParseFailure(parser,
                validExpectedPersonString + PHONE_DESC_AMY + FAMILY_PHONE_DESC_AMY + PATIENT_EMAIL_DESC_AMY
                        + FAMILY_EMAIL_DESC_AMY + NAME_DESC_AMY + ADDRESS_DESC_AMY + validExpectedPersonString,
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_NAME, PREFIX_PHONE, PREFIX_FAMILY_PHONE,
                        PREFIX_PATIENT_EMAIL, PREFIX_FAMILY_EMAIL, PREFIX_ADDRESS));

        // invalid value followed by valid value

        // invalid name
        assertParseFailure(parser, INVALID_NAME_DESC + validExpectedPersonString,
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_NAME));

        // invalid patient email
        assertParseFailure(parser, INVALID_PATIENT_EMAIL_DESC + validExpectedPersonString,
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_PATIENT_EMAIL));

        // invalid family email
        assertParseFailure(parser, INVALID_FAMILY_EMAIL_DESC + validExpectedPersonString,
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_FAMILY_EMAIL));

        // invalid phone
        assertParseFailure(parser, INVALID_PHONE_DESC + validExpectedPersonString,
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_PHONE));

        // invalid address
        assertParseFailure(parser, INVALID_ADDRESS_DESC + validExpectedPersonString,
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_ADDRESS));

        // valid value followed by invalid value

        // invalid name
        assertParseFailure(parser, validExpectedPersonString + INVALID_NAME_DESC,
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_NAME));

        // invalid patient email
        assertParseFailure(parser, validExpectedPersonString + INVALID_PATIENT_EMAIL_DESC,
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_PATIENT_EMAIL));

        // invalid family email
        assertParseFailure(parser, validExpectedPersonString + INVALID_FAMILY_EMAIL_DESC,
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_FAMILY_EMAIL));

        // invalid phone
        assertParseFailure(parser, validExpectedPersonString + INVALID_PHONE_DESC,
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_PHONE));

        // invalid address
        assertParseFailure(parser, validExpectedPersonString + INVALID_ADDRESS_DESC,
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_ADDRESS));
    }

    @Test
    public void parse_optionalFieldsMissing_success() {
        // zero tags
        Person expectedPerson = new PersonBuilder(AMY).withFamilyNo(VALID_FAMILY_PHONE_AMY)
                .withoutEmail().withTags().build();
        assertParseSuccess(parser, NAME_DESC_AMY + PHONE_DESC_AMY + FAMILY_PHONE_DESC_AMY + ADDRESS_DESC_AMY,
                new AddCommand(expectedPerson));
    }

    @Test
    public void parse_compulsoryFieldMissing_failure() {
        String expectedMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, AddCommand.MESSAGE_USAGE);

        // missing name prefix
        assertParseFailure(parser, VALID_NAME_BOB + PHONE_DESC_BOB + FAMILY_PHONE_DESC_BOB
                        + PATIENT_EMAIL_DESC_BOB + FAMILY_EMAIL_DESC_BOB + ADDRESS_DESC_BOB,
                expectedMessage);

        // missing phone prefix
        assertParseFailure(parser, NAME_DESC_BOB + VALID_PHONE_BOB + FAMILY_PHONE_DESC_BOB
                        + PATIENT_EMAIL_DESC_BOB + FAMILY_EMAIL_DESC_BOB + ADDRESS_DESC_BOB,
                expectedMessage);

        // missing family phone prefix
        assertParseFailure(parser, NAME_DESC_BOB + PHONE_DESC_BOB + VALID_FAMILY_PHONE_BOB
                        + PATIENT_EMAIL_DESC_BOB + FAMILY_EMAIL_DESC_BOB + ADDRESS_DESC_BOB,
                expectedMessage);

        // missing address prefix
        assertParseFailure(parser, NAME_DESC_BOB + PHONE_DESC_BOB + FAMILY_PHONE_DESC_BOB
                        + PATIENT_EMAIL_DESC_BOB + FAMILY_EMAIL_DESC_BOB + VALID_ADDRESS_BOB,
                expectedMessage);

        // old email prefix is not accepted by add command
        assertParseFailure(parser, NAME_DESC_BOB + PHONE_DESC_BOB + FAMILY_PHONE_DESC_BOB
                        + EMAIL_DESC_AMY + ADDRESS_DESC_BOB,
                expectedMessage);

        // all prefixes missing
        assertParseFailure(parser, VALID_NAME_BOB + VALID_PHONE_BOB + VALID_FAMILY_PHONE_BOB
                        + VALID_EMAIL_BOB + VALID_ADDRESS_BOB,
                expectedMessage);
    }

    @Test
    public void parse_invalidValue_failure() {
        // invalid name
        assertParseFailure(parser, INVALID_NAME_DESC + PHONE_DESC_BOB + FAMILY_PHONE_DESC_BOB
                + PATIENT_EMAIL_DESC_BOB + FAMILY_EMAIL_DESC_BOB + ADDRESS_DESC_BOB + TAG_DESC_LOW
                + TAG_DESC_HIGH, Name.MESSAGE_CONSTRAINTS);

        // invalid phone
        assertParseFailure(parser, NAME_DESC_BOB + INVALID_PHONE_DESC + FAMILY_PHONE_DESC_BOB
                + PATIENT_EMAIL_DESC_BOB + FAMILY_EMAIL_DESC_BOB + ADDRESS_DESC_BOB + TAG_DESC_LOW
                + TAG_DESC_HIGH, Phone.MESSAGE_CONSTRAINTS);

        // invalid patient email
        assertParseFailure(parser, NAME_DESC_BOB + PHONE_DESC_BOB + FAMILY_PHONE_DESC_BOB
                + INVALID_PATIENT_EMAIL_DESC + FAMILY_EMAIL_DESC_BOB + ADDRESS_DESC_BOB + TAG_DESC_LOW
                + TAG_DESC_HIGH,
                Email.MESSAGE_CONSTRAINTS);

        // invalid family email
        assertParseFailure(parser, NAME_DESC_BOB + PHONE_DESC_BOB + FAMILY_PHONE_DESC_BOB
                + PATIENT_EMAIL_DESC_BOB + INVALID_FAMILY_EMAIL_DESC + ADDRESS_DESC_BOB + TAG_DESC_LOW
                + TAG_DESC_HIGH,
                Email.MESSAGE_CONSTRAINTS);

        // invalid address
        assertParseFailure(parser, NAME_DESC_BOB + PHONE_DESC_BOB + FAMILY_PHONE_DESC_BOB
                + PATIENT_EMAIL_DESC_BOB + FAMILY_EMAIL_DESC_BOB + INVALID_ADDRESS_DESC + TAG_DESC_LOW
                + TAG_DESC_HIGH,
                Address.MESSAGE_CONSTRAINTS);

        // invalid tag
        assertParseFailure(parser, NAME_DESC_BOB + PHONE_DESC_BOB + FAMILY_PHONE_DESC_BOB
                + PATIENT_EMAIL_DESC_BOB + FAMILY_EMAIL_DESC_BOB + ADDRESS_DESC_BOB + INVALID_TAG_DESC
                + VALID_TAG_HIGH, Tag.MESSAGE_CONSTRAINTS);

        // two invalid values, only first invalid value reported
        assertParseFailure(parser, INVALID_NAME_DESC + PHONE_DESC_BOB + FAMILY_PHONE_DESC_BOB
                + PATIENT_EMAIL_DESC_BOB + FAMILY_EMAIL_DESC_BOB + INVALID_ADDRESS_DESC, Name.MESSAGE_CONSTRAINTS);

        // non-empty preamble
        assertParseFailure(parser, PREAMBLE_NON_EMPTY + NAME_DESC_BOB + PHONE_DESC_BOB + FAMILY_PHONE_DESC_BOB
                + PATIENT_EMAIL_DESC_BOB + FAMILY_EMAIL_DESC_BOB + ADDRESS_DESC_BOB + TAG_DESC_LOW
                + TAG_DESC_HIGH,
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, AddCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_mixedCaseTag_success() {
        Person expectedPerson = new PersonBuilder(AMY).withFamilyNo(VALID_FAMILY_PHONE_AMY)
                .withoutEmail().withTags(VALID_TAG_HIGH).build();
        String fields = NAME_DESC_AMY + PHONE_DESC_AMY + FAMILY_PHONE_DESC_AMY + ADDRESS_DESC_AMY;
        for (String prefix : new String[] {"/tag", "/Tag", "/TAG", "/tAg"}) {
            for (String value : new String[] {"high", "High", "HIGH", "hIgH"}) {
                assertParseSuccess(parser, fields + " " + prefix + " " + value, new AddCommand(expectedPerson));
            }
        }
        assertParseSuccess(parser, fields + " /TAG High /tag high", new AddCommand(expectedPerson));
        assertParseFailure(parser, fields + " /TAG High /tAg low", Person.MESSAGE_TAG_COUNT_CONSTRAINTS);
        assertParseFailure(parser, fields + " /TAG urgent", Tag.MESSAGE_CONSTRAINTS);
    }
}
