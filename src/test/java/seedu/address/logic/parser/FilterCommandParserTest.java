package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.FilterCommand;
import seedu.address.model.person.PersonMatchesFilterPredicate;

public class FilterCommandParserTest {

    private static final String MESSAGE_INVALID_FORMAT =
            String.format(MESSAGE_INVALID_COMMAND_FORMAT, FilterCommand.MESSAGE_USAGE);

    private FilterCommandParser parser = new FilterCommandParser();

    private static FilterCommand command(List<String> names, List<String> tags, List<String> addresses) {
        return new FilterCommand(new PersonMatchesFilterPredicate(names, tags, addresses));
    }

    @Test
    public void parse_emptyArg_throwsParseException() {
        assertParseFailure(parser, "     ", MESSAGE_INVALID_FORMAT);
    }

    @Test
    public void parse_noPrefix_throwsParseException() {
        // bare tag is no longer accepted
        assertParseFailure(parser, "MEDIUM", MESSAGE_INVALID_FORMAT);

        // text before the first prefix
        assertParseFailure(parser, "ali /tag HIGH", MESSAGE_INVALID_FORMAT);
    }

    @Test
    public void parse_unsupportedPrefix_throwsParseException() {
        assertParseFailure(parser, " /patientNo 91234567", MESSAGE_INVALID_FORMAT);
        assertParseFailure(parser, " /name ali /patientNo 91234567", MESSAGE_INVALID_FORMAT);
        assertParseFailure(parser, " /tag HIGH /familyNo 91234567", MESSAGE_INVALID_FORMAT);
        assertParseFailure(parser, " /address clementi /pEmail a@b.com", MESSAGE_INVALID_FORMAT);
        assertParseFailure(parser, " /name ali /unknown x", MESSAGE_INVALID_FORMAT);
    }

    @Test
    public void parse_emptyValue_throwsParseException() {
        assertParseFailure(parser, " /name", MESSAGE_INVALID_FORMAT);
        assertParseFailure(parser, " /name   ", MESSAGE_INVALID_FORMAT);
        assertParseFailure(parser, " /tag", MESSAGE_INVALID_FORMAT);
        assertParseFailure(parser, " /address", MESSAGE_INVALID_FORMAT);

        // one empty value among valid ones
        assertParseFailure(parser, " /tag HIGH /tag", MESSAGE_INVALID_FORMAT);
    }

    @Test
    public void parse_invalidTag_throwsParseException() {
        // tag outside the enumerated values
        assertParseFailure(parser, " /tag URGENT", MESSAGE_INVALID_FORMAT);

        // tag that only partially matches a valid tag
        assertParseFailure(parser, " /tag MEDIUMS", MESSAGE_INVALID_FORMAT);

        // several tags must be given by repeating the prefix
        assertParseFailure(parser, " /tag HIGH LOW", MESSAGE_INVALID_FORMAT);

        // one invalid tag among valid ones
        assertParseFailure(parser, " /tag HIGH /tag URGENT", MESSAGE_INVALID_FORMAT);
    }

    @Test
    public void parse_validArgs_returnsFilterCommand() {
        // name only, keywords split on whitespace
        assertParseSuccess(parser, " /name ali bob",
                command(List.of("ali", "bob"), List.of(), List.of()));

        // tag only, case-insensitive, surrounding whitespace ignored
        assertParseSuccess(parser, " \n /tag medium \t ",
                command(List.of(), List.of("MEDIUM"), List.of()));

        // address only, kept as a phrase
        assertParseSuccess(parser, " /address clementi ave",
                command(List.of(), List.of(), List.of("clementi ave")));
    }

    @Test
    public void parse_repeatedPrefixes_accumulatesValues() {
        assertParseSuccess(parser, " /tag HIGH /tag MEDIUM",
                command(List.of(), List.of("HIGH", "MEDIUM"), List.of()));

        assertParseSuccess(parser, " /address clementi /address jurong west",
                command(List.of(), List.of(), List.of("clementi", "jurong west")));

        assertParseSuccess(parser, " /name ali /name bob",
                command(List.of("ali", "bob"), List.of(), List.of()));
    }

    @Test
    public void parse_multipleCriteria_returnsFilterCommand() {
        assertParseSuccess(parser, " /name ali bob /tag HIGH /tag MEDIUM /address clementi",
                command(List.of("ali", "bob"), List.of("HIGH", "MEDIUM"), List.of("clementi")));

        // prefix order does not matter
        assertParseSuccess(parser, " /address clementi /tag HIGH /name ali",
                command(List.of("ali"), List.of("HIGH"), List.of("clementi")));
    }

}
