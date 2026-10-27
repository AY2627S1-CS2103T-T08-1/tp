package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.UnarchiveCommand;

public class UnarchiveCommandParserTest {
    private final UnarchiveCommandParser parser = new UnarchiveCommandParser();

    @Test
    public void parse_positiveIndexWithWhitespace_success() {
        for (String input : List.of("1", "001", "  1  ", "\t1\t", "\n1\r\n")) {
            assertParseSuccess(parser, input, new UnarchiveCommand(Index.fromOneBased(1)));
        }
        assertParseSuccess(parser, "2147483647", new UnarchiveCommand(Index.fromOneBased(Integer.MAX_VALUE)));
    }

    @Test
    public void parse_invalidArguments_rejectsEntireInput() {
        String message = String.format(MESSAGE_INVALID_COMMAND_FORMAT, UnarchiveCommand.MESSAGE_USAGE);
        for (String input : List.of("", " \t ", "0", "000", "-0", "-1", "+1", "1.0", "1e2", "one",
                "1 2", "1\t2", "1\n2", "1 extra", "1 /name Alice", "1abc", "2147483648",
                "99999999999999999999999999")) {
            assertParseFailure(parser, input, message);
        }
    }
}
