package seedu.address.logic.parser;

import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.MarkCommand;

public class MarkCommandParserTest {
    private final MarkCommandParser parser = new MarkCommandParser();

    @Test
    public void parse_positiveDisplayedIndex_returnsMarkCommand() {
        assertParseSuccess(parser, " 2 ", new MarkCommand(Index.fromOneBased(2)));
    }

    @Test
    public void parse_invalidIndex_showsExactError() {
        for (String input : List.of("", " ", "0", "-1", "abc", "1.5", "2147483648", "1 2", "1 extra")) {
            assertParseFailure(parser, input, "Invalid index.");
        }
    }
}
