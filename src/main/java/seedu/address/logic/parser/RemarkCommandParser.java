package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;

import java.util.Locale;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.RemarkCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.person.Remark;

/**
 * Parses input arguments and creates a new RemarkCommand object.
 * Everything after the index is kept as typed, so a note may contain {@code /} without being split.
 */
public class RemarkCommandParser implements Parser<RemarkCommand> {

    public static final String MESSAGE_INVALID_NOTE_NUMBER = "Note number must be a positive integer.";

    /** Matches a whole number with an optional minus sign, which is what an attempted index looks like. */
    private static final String INTEGER_REGEX = "-?\\d+";

    /** The first word of some text and the rest of it, which may be empty. */
    private record FirstWordAndRest(String firstWord, String rest) {}

    /**
     * Parses the given {@code String} of arguments in the context of the RemarkCommand
     * and returns a RemarkCommand object for execution.
     * @throws ParseException if the user input does not conform to the expected format
     */
    public RemarkCommand parse(String args) throws ParseException {
        requireNonNull(args);
        FirstWordAndRest indexAndDetails = splitFirstWord(args.trim());
        Index personIndex = parsePersonIndex(indexAndDetails.firstWord());
        String details = indexAndDetails.rest();

        // a missing note is an error rather than a way to clear, so notes are never lost by accident
        FirstWordAndRest keywordAndRest = splitFirstWord(details);
        return switch (keywordAndRest.firstWord().toLowerCase(Locale.ROOT)) {
            case RemarkCommand.KEYWORD_CLEAR -> parseClear(personIndex, keywordAndRest.rest());
            case RemarkCommand.KEYWORD_DELETE -> parseDelete(personIndex, keywordAndRest.rest());
            case RemarkCommand.KEYWORD_EDIT -> parseEdit(personIndex, keywordAndRest.rest());
            default -> RemarkCommand.add(personIndex, parseNote(details));
        };
    }

    private static Index parsePersonIndex(String firstWord) throws ParseException {
        // a first word that is not a number means the index was left out, not that it was mistyped
        if (!firstWord.matches(INTEGER_REGEX)) {
            throw invalidFormat();
        }
        return ParserUtil.parseDisplayedIndex(firstWord);
    }

    private static RemarkCommand parseClear(Index personIndex, String rest) throws ParseException {
        requireNothingAfter(rest);
        return RemarkCommand.clear(personIndex);
    }

    private static RemarkCommand parseDelete(Index personIndex, String rest) throws ParseException {
        FirstWordAndRest noteNumberAndRest = splitFirstWord(rest);
        requireNothingAfter(noteNumberAndRest.rest());
        return RemarkCommand.delete(personIndex, parseNoteIndex(noteNumberAndRest.firstWord()));
    }

    private static RemarkCommand parseEdit(Index personIndex, String rest) throws ParseException {
        FirstWordAndRest noteNumberAndNote = splitFirstWord(rest);
        Index noteIndex = parseNoteIndex(noteNumberAndNote.firstWord());
        if (noteNumberAndNote.rest().isEmpty()) {
            throw invalidFormat();
        }
        return RemarkCommand.edit(personIndex, noteIndex, parseNote(noteNumberAndNote.rest()));
    }

    /**
     * Returns {@code text} split into its first word and the rest.
     * @throws ParseException if {@code text} has no words
     */
    private static FirstWordAndRest splitFirstWord(String text) throws ParseException {
        String[] parts = text.strip().split("\\s+", 2);
        if (parts[0].isEmpty()) {
            throw invalidFormat();
        }
        return new FirstWordAndRest(parts[0], parts.length > 1 ? parts[1] : "");
    }

    private static void requireNothingAfter(String rest) throws ParseException {
        if (!rest.isEmpty()) {
            throw invalidFormat();
        }
    }

    private static Index parseNoteIndex(String noteNumber) throws ParseException {
        try {
            return ParserUtil.parseIndex(noteNumber);
        } catch (ParseException pe) {
            throw new ParseException(MESSAGE_INVALID_NOTE_NUMBER, pe);
        }
    }

    /**
     * Returns {@code note} if it is a valid note.
     * @throws ParseException if {@code note} is blank, e.g. made only of non-breaking spaces
     */
    private static String parseNote(String note) throws ParseException {
        if (!Remark.isValidNote(note)) {
            throw new ParseException(Remark.MESSAGE_CONSTRAINTS);
        }
        return note;
    }

    private static ParseException invalidFormat() {
        return new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, RemarkCommand.MESSAGE_USAGE));
    }

}
