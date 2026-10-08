package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static seedu.address.logic.parser.ParserUtil.MESSAGE_INVALID_INDEX;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.DeleteCommand;

/**
 * Contains unit tests for {@code DeleteCommandParser}, which validates the index itself
 * so that it can tell apart a wrongly formatted command, a non-positive index and
 * an index that is too large.
 */
public class DeleteCommandParserTest {

    private DeleteCommandParser parser = new DeleteCommandParser();

    @Test
    public void parse_validArgs_returnsDeleteCommand() {
        assertParseSuccess(parser, "1", new DeleteCommand(INDEX_FIRST_PERSON));
    }

    @Test
    public void parse_validArgsWithWhitespaceOrLeadingZeros_returnsDeleteCommand() {
        assertParseSuccess(parser, "  1  ", new DeleteCommand(INDEX_FIRST_PERSON));
        assertParseSuccess(parser, "001", new DeleteCommand(INDEX_FIRST_PERSON));
    }

    @Test
    public void parse_invalidArgs_throwsParseException() {
        String expectedMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, DeleteCommand.MESSAGE_USAGE);

        // missing index
        assertParseFailure(parser, "", expectedMessage);
        assertParseFailure(parser, "   ", expectedMessage);

        // non-numeric index
        assertParseFailure(parser, "a", expectedMessage);
        assertParseFailure(parser, "1a", expectedMessage);
        assertParseFailure(parser, "+1", expectedMessage);
        assertParseFailure(parser, "1.5", expectedMessage);

        // extra arguments
        assertParseFailure(parser, "1 2", expectedMessage);
    }

    @Test
    public void parse_nonPositiveIndex_throwsParseException() {
        assertParseFailure(parser, "0", MESSAGE_INVALID_INDEX);
        assertParseFailure(parser, "000", MESSAGE_INVALID_INDEX);
        assertParseFailure(parser, "-0", MESSAGE_INVALID_INDEX);
        assertParseFailure(parser, "-1", MESSAGE_INVALID_INDEX);
        assertParseFailure(parser, "-99999999999", MESSAGE_INVALID_INDEX);
    }

    @Test
    public void parse_indexLargerThanMaxInt_returnsDeleteCommandWithCappedIndex() {
        DeleteCommand expectedCommand = new DeleteCommand(Index.fromOneBased(Integer.MAX_VALUE));
        assertParseSuccess(parser, "2147483647", expectedCommand);
        assertParseSuccess(parser, "2147483648", expectedCommand);
        assertParseSuccess(parser, "99999999999999999999", expectedCommand);
    }
}
