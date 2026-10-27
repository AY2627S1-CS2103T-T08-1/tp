package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ADDRESS;
import static seedu.address.logic.parser.CliSyntax.PREFIX_FAMILY_EMAIL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_FAMILY_PHONE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_LEGACY_EMAIL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PATIENT_EMAIL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PATIENT_PHONE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TAG;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.FindCommand;
import seedu.address.model.person.FindCriterion;
import seedu.address.model.person.NameContainsKeywordsPredicate;
import seedu.address.model.person.PersonContainsKeywordsPredicate;

public class FindCommandParserTest {

    private static final Map<Prefix, FindCriterion> CRITERIA = Map.of(
            PREFIX_NAME, FindCriterion.NAME,
            PREFIX_PATIENT_PHONE, FindCriterion.PATIENT_PHONE,
            PREFIX_FAMILY_PHONE, FindCriterion.FAMILY_PHONE,
            PREFIX_ADDRESS, FindCriterion.ADDRESS,
            PREFIX_PATIENT_EMAIL, FindCriterion.PATIENT_EMAIL,
            PREFIX_FAMILY_EMAIL, FindCriterion.FAMILY_EMAIL,
            PREFIX_LEGACY_EMAIL, FindCriterion.EMAIL,
            PREFIX_TAG, FindCriterion.TAG);

    private FindCommandParser parser = new FindCommandParser();

    @Test
    public void parse_emptyArg_throwsParseException() {
        assertParseFailure(parser, "     ", String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_validArgs_returnsFindCommand() {
        // no leading and trailing whitespaces
        FindCommand expectedFindCommand =
                new FindCommand(new NameContainsKeywordsPredicate(List.of("Alice", "Bob")));
        assertParseSuccess(parser, "Alice Bob", expectedFindCommand);

        // multiple whitespaces between keywords
        assertParseSuccess(parser, " \n Alice \n \t Bob  \t", expectedFindCommand);
    }

    @Test
    public void parse_keywordsMixedWithCriterion_usesSameFormatErrorAsEmptyInput() {
        String expectedMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindCommand.MESSAGE_USAGE);
        for (String input : List.of("", " ", "Alice /address A", "Alice Bob /name Kim", "Alice /ADDRESS A",
                "Alice \t /patientNo 87", "Alice /tag HIGH", "Alice /unknown input", "Alice /address")) {
            assertParseFailure(parser, input, expectedMessage);
        }
    }

    @Test
    public void parse_fieldCriteria_returnsFindCommand() {
        for (Map.Entry<Prefix, FindCriterion> entry : CRITERIA.entrySet()) {
            assertParseSuccess(parser, " " + entry.getKey() + " 87 ",
                    new FindCommand(new PersonContainsKeywordsPredicate(entry.getValue(), List.of("87"))));
        }
        assertParseSuccess(parser, "/address \n Main \t Street",
                new FindCommand(new PersonContainsKeywordsPredicate(FindCriterion.ADDRESS, List.of("Main", "Street"))));
    }

    @Test
    public void parse_fieldCriteriaIgnoreCase_returnsCanonicalFindCommand() {
        for (Map.Entry<Prefix, FindCriterion> entry : CRITERIA.entrySet()) {
            FindCommand expected =
                    new FindCommand(new PersonContainsKeywordsPredicate(entry.getValue(), List.of("87")));
            assertParseSuccess(parser, entry.getKey().getPrefix().toUpperCase(Locale.ROOT) + " 87", expected);
            assertParseSuccess(parser, entry.getKey().getPrefix().toLowerCase(Locale.ROOT) + " 87", expected);
        }
        FindCommand expectedTag =
                new FindCommand(new PersonContainsKeywordsPredicate(FindCriterion.TAG, List.of("HIGH")));
        assertParseSuccess(parser, "/TAG HIGH", expectedTag);
        assertParseSuccess(parser, "/TaG HIGH", expectedTag);
        assertParseSuccess(parser, "/PaTiEnTnO 87",
                new FindCommand(new PersonContainsKeywordsPredicate(FindCriterion.PATIENT_PHONE, List.of("87"))));
    }

    @Test
    public void parse_invalidCriteriaOrInput_throwsParseException() {
        for (String input : List.of("/patientNo", "/name   ", "/address", "/email",
                "/patientNo 8", "/familyNo 8", "/patientNo 8a", "/familyNo 8 7", "/patientNo +87",
                "/patientNo 87.0", "/patientNo ８７", "/name Alice /address Main",
                "/TAG", "/PATIENTNO 8", "/FaMiLyNo 8 7", "/PATIENTNO ab")) {
            assertParseFailure(parser, input,
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindCommand.MESSAGE_USAGE));
        }
    }

    @Test
    public void parse_unknownCriterion_reportsEnteredCriterion() {
        for (String criterion : List.of("/unknown", "/UNKNOWN", "/patientNumber", "/")) {
            assertParseFailure(parser, criterion + " abc", "Unknown criterion: " + criterion);
            assertParseFailure(parser, criterion, "Unknown criterion: " + criterion);
        }
    }

}
