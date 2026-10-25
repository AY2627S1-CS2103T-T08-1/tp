package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.SortCommand;
import seedu.address.logic.commands.SortCommand.Criterion;

public class SortCommandParserTest {

    private static final String MESSAGE_INVALID_FORMAT =
            String.format(MESSAGE_INVALID_COMMAND_FORMAT, SortCommand.MESSAGE_USAGE);

    private SortCommandParser parser = new SortCommandParser();

    @Test
    public void parse_emptyArg_throwsParseException() {
        assertParseFailure(parser, "     ", MESSAGE_INVALID_FORMAT);
    }

    @Test
    public void parse_unknownCriterion_throwsParseException() {
        assertParseFailure(parser, "address", MESSAGE_INVALID_FORMAT);
        assertParseFailure(parser, "priorities", MESSAGE_INVALID_FORMAT);
        assertParseFailure(parser, "asc", MESSAGE_INVALID_FORMAT);

        // prefixed input
        assertParseFailure(parser, "/name", MESSAGE_INVALID_FORMAT);
    }

    @Test
    public void parse_unknownDirection_throwsParseException() {
        assertParseFailure(parser, "priority up", MESSAGE_INVALID_FORMAT);
        assertParseFailure(parser, "name ascending", MESSAGE_INVALID_FORMAT);
    }

    @Test
    public void parse_recentWithDirection_throwsParseException() {
        assertParseFailure(parser, "recent asc", MESSAGE_INVALID_FORMAT);
        assertParseFailure(parser, "recent desc", MESSAGE_INVALID_FORMAT);
    }

    @Test
    public void parse_extraArguments_throwsParseException() {
        assertParseFailure(parser, "priority desc name", MESSAGE_INVALID_FORMAT);
        assertParseFailure(parser, "recent now please", MESSAGE_INVALID_FORMAT);
    }

    @Test
    public void parse_priority_defaultsToDescending() {
        assertParseSuccess(parser, "priority", new SortCommand(Criterion.PRIORITY, false));
        assertParseSuccess(parser, " priority desc ", new SortCommand(Criterion.PRIORITY, false));
        assertParseSuccess(parser, "priority asc", new SortCommand(Criterion.PRIORITY, true));
    }

    @Test
    public void parse_name_defaultsToAscending() {
        assertParseSuccess(parser, "name", new SortCommand(Criterion.NAME, true));
        assertParseSuccess(parser, "name asc", new SortCommand(Criterion.NAME, true));
        assertParseSuccess(parser, "name desc", new SortCommand(Criterion.NAME, false));
    }

    @Test
    public void parse_recent_success() {
        assertParseSuccess(parser, "recent", new SortCommand(Criterion.RECENT, false));
        assertParseSuccess(parser, " \n recent \t ", new SortCommand(Criterion.RECENT, false));
    }

    @Test
    public void parse_caseInsensitive_success() {
        assertParseSuccess(parser, "PRIORITY ASC", new SortCommand(Criterion.PRIORITY, true));
        assertParseSuccess(parser, "Name Desc", new SortCommand(Criterion.NAME, false));
        assertParseSuccess(parser, "RECENT", new SortCommand(Criterion.RECENT, false));
    }

}
