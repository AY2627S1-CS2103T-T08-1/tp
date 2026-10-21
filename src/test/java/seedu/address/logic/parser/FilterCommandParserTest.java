package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.FilterCommand;
import seedu.address.model.person.PersonHasTagPredicate;

public class FilterCommandParserTest {

    private static final String MESSAGE_INVALID_FORMAT =
            String.format(MESSAGE_INVALID_COMMAND_FORMAT, FilterCommand.MESSAGE_USAGE);

    private FilterCommandParser parser = new FilterCommandParser();

    @Test
    public void parse_emptyArg_throwsParseException() {
        assertParseFailure(parser, "     ", MESSAGE_INVALID_FORMAT);
    }

    @Test
    public void parse_invalidTag_throwsParseException() {
        // tag outside the enumerated values
        assertParseFailure(parser, "URGENT", MESSAGE_INVALID_FORMAT);

        // tag that only partially matches a valid tag
        assertParseFailure(parser, "MEDIUMS", MESSAGE_INVALID_FORMAT);

        // prefixed input
        assertParseFailure(parser, "t/MEDIUM", MESSAGE_INVALID_FORMAT);
    }

    @Test
    public void parse_multipleTags_throwsParseException() {
        assertParseFailure(parser, "HIGH LOW", MESSAGE_INVALID_FORMAT);
    }

    @Test
    public void parse_validArgs_returnsFilterCommand() {
        FilterCommand expectedFilterCommand = new FilterCommand(new PersonHasTagPredicate("MEDIUM"));

        // no leading and trailing whitespaces
        assertParseSuccess(parser, "MEDIUM", expectedFilterCommand);

        // leading and trailing whitespaces
        assertParseSuccess(parser, " \n MEDIUM \t ", expectedFilterCommand);

        // lower case
        assertParseSuccess(parser, "medium", expectedFilterCommand);

        // the other valid tags
        assertParseSuccess(parser, "HIGH", new FilterCommand(new PersonHasTagPredicate("HIGH")));
        assertParseSuccess(parser, "LOW", new FilterCommand(new PersonHasTagPredicate("LOW")));
    }

}
