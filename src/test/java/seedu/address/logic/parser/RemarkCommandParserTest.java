package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static seedu.address.logic.parser.ParserUtil.MESSAGE_INVALID_INDEX;
import static seedu.address.logic.parser.RemarkCommandParser.MESSAGE_INVALID_NOTE_NUMBER;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;
import static seedu.address.testutil.TypicalIndexes.INDEX_SECOND_PERSON;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.RemarkCommand;

public class RemarkCommandParserTest {

    private static final String MESSAGE_INVALID_FORMAT =
            String.format(MESSAGE_INVALID_COMMAND_FORMAT, RemarkCommand.MESSAGE_USAGE);
    private static final Index NOTE_SECOND = Index.fromOneBased(2);

    private final RemarkCommandParser parser = new RemarkCommandParser();

    @Test
    public void parse_note_returnsAddCommand() {
        assertParseSuccess(parser, " 1 Allergic to penicillin",
                RemarkCommand.add(INDEX_FIRST_PERSON, "Allergic to penicillin"));

        // surrounding spaces are trimmed, but spaces inside the note are kept
        assertParseSuccess(parser, "   2    Visits  after 3pm   ",
                RemarkCommand.add(INDEX_SECOND_PERSON, "Visits  after 3pm"));
    }

    @Test
    public void parse_noteWithSlashes_keepsNoteAsTyped() {
        assertParseSuccess(parser, " 1 Takes /tag and /name pills s/o",
                RemarkCommand.add(INDEX_FIRST_PERSON, "Takes /tag and /name pills s/o"));

        // a keyword later in the note is part of the note
        assertParseSuccess(parser, " 1 Do not /clear the tray",
                RemarkCommand.add(INDEX_FIRST_PERSON, "Do not /clear the tray"));

        // a word that only starts with a keyword is a note
        assertParseSuccess(parser, " 1 /clearance needed",
                RemarkCommand.add(INDEX_FIRST_PERSON, "/clearance needed"));
    }

    @Test
    public void parse_edit_returnsEditCommand() {
        assertParseSuccess(parser, " 1 /edit 2 Visits after 4pm",
                RemarkCommand.edit(INDEX_FIRST_PERSON, NOTE_SECOND, "Visits after 4pm"));

        // keywords are case-insensitive
        assertParseSuccess(parser, " 1 /EDIT 2 Visits after 4pm",
                RemarkCommand.edit(INDEX_FIRST_PERSON, NOTE_SECOND, "Visits after 4pm"));
    }

    @Test
    public void parse_delete_returnsDeleteCommand() {
        assertParseSuccess(parser, " 1 /delete 2", RemarkCommand.delete(INDEX_FIRST_PERSON, NOTE_SECOND));
        assertParseSuccess(parser, " 1   /Delete   2  ", RemarkCommand.delete(INDEX_FIRST_PERSON, NOTE_SECOND));
    }

    @Test
    public void parse_clear_returnsClearCommand() {
        assertParseSuccess(parser, " 1 /clear", RemarkCommand.clear(INDEX_FIRST_PERSON));
        assertParseSuccess(parser, " 1 /CLEAR ", RemarkCommand.clear(INDEX_FIRST_PERSON));
    }

    @Test
    public void parse_missingParts_throwsParseException() {
        // no index and no note
        assertParseFailure(parser, "", MESSAGE_INVALID_FORMAT);
        assertParseFailure(parser, "   ", MESSAGE_INVALID_FORMAT);

        // no note, which must not clear the notes
        assertParseFailure(parser, " 1", MESSAGE_INVALID_FORMAT);
        assertParseFailure(parser, " 1   ", MESSAGE_INVALID_FORMAT);

        // no note number
        assertParseFailure(parser, " 1 /delete", MESSAGE_INVALID_FORMAT);
        assertParseFailure(parser, " 1 /edit", MESSAGE_INVALID_FORMAT);

        // no new note text
        assertParseFailure(parser, " 1 /edit 2", MESSAGE_INVALID_FORMAT);
        assertParseFailure(parser, " 1 /edit 2   ", MESSAGE_INVALID_FORMAT);
    }

    @Test
    public void parse_extraArguments_throwsParseException() {
        assertParseFailure(parser, " 1 /clear all", MESSAGE_INVALID_FORMAT);
        assertParseFailure(parser, " 1 /delete 2 3", MESSAGE_INVALID_FORMAT);
    }

    @Test
    public void parse_invalidPersonIndex_throwsParseException() {
        assertParseFailure(parser, " 0 note", MESSAGE_INVALID_INDEX);
        assertParseFailure(parser, " -1 note", MESSAGE_INVALID_INDEX);
        assertParseFailure(parser, " abc note", MESSAGE_INVALID_INDEX);
        assertParseFailure(parser, " /clear", MESSAGE_INVALID_INDEX);
    }

    @Test
    public void parse_invalidNoteNumber_throwsParseException() {
        assertParseFailure(parser, " 1 /delete 0", MESSAGE_INVALID_NOTE_NUMBER);
        assertParseFailure(parser, " 1 /delete -1", MESSAGE_INVALID_NOTE_NUMBER);
        assertParseFailure(parser, " 1 /delete abc", MESSAGE_INVALID_NOTE_NUMBER);
        assertParseFailure(parser, " 1 /edit +2 text", MESSAGE_INVALID_NOTE_NUMBER);
        assertParseFailure(parser, " 1 /edit new text", MESSAGE_INVALID_NOTE_NUMBER);
    }
}
