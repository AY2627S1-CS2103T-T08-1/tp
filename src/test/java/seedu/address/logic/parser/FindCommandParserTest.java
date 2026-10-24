package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.FindCommand;
import seedu.address.model.person.NameContainsKeywordsPredicate;
import seedu.address.model.person.PersonContainsKeywordsPredicate;

public class FindCommandParserTest {

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
    public void parse_fieldCriteria_returnsFindCommand() {
        for (String criterion : PersonContainsKeywordsPredicate.SUPPORTED_CRITERIA) {
            assertParseSuccess(parser, " /" + criterion + " 87 ",
                    new FindCommand(new PersonContainsKeywordsPredicate(criterion, List.of("87"))));
        }
        assertParseSuccess(parser, "/address \n Main \t Street",
                new FindCommand(new PersonContainsKeywordsPredicate("address", List.of("Main", "Street"))));
    }

    @Test
    public void parse_invalidCriteriaOrInput_throwsParseException() {
        for (String input : List.of("/unknown abc", "/patientNo", "/name   ", "/address", "/email",
                "/patientNo 8", "/familyNo 8", "/patientNo 8a", "/familyNo 8 7", "/patientNo +87",
                "/patientNo 87.0", "/patientNo ８７", "/name Alice /address Main")) {
            assertParseFailure(parser, input,
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindCommand.MESSAGE_USAGE));
        }
    }

}
