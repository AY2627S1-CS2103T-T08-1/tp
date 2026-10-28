package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import seedu.address.commons.core.index.Index;
import seedu.address.commons.util.StringUtil;
import seedu.address.logic.commands.RemarkCommand;
import seedu.address.logic.parser.exceptions.ParseException;

/**
 * Parses input arguments and creates a new RemarkCommand object.
 * Everything after the index is kept as typed, so a note may contain {@code /} without being split.
 */
public class RemarkCommandParser implements Parser<RemarkCommand> {

    public static final String MESSAGE_INVALID_NOTE_NUMBER = "Note number must be a positive integer.";

    /** Matches a whole number with an optional minus sign, which is what an attempted index looks like. */
    private static final String INTEGER_REGEX = "-?\\d+";

    /** Splits text into its first word and the rest, which may be empty. */
    private static final Pattern FIRST_WORD_FORMAT = Pattern.compile("(?<first>\\S+)\\s*(?<rest>.*)");

    /**
     * Parses the given {@code String} of arguments in the context of the RemarkCommand
     * and returns a RemarkCommand object for execution.
     * @throws ParseException if the user input does not conform to the expected format
     */
    public RemarkCommand parse(String args) throws ParseException {
        requireNonNull(args);
        Matcher indexAndDetails = splitFirstWord(args.trim());
        String firstWord = indexAndDetails.group("first");
        // a first word that is not a number means the index was left out, not that it was mistyped
        if (!firstWord.matches(INTEGER_REGEX)) {
            throw invalidFormat();
        }
        Index personIndex = ParserUtil.parseIndex(firstWord);
        String details = indexAndDetails.group("rest");

        // a missing note is an error rather than a way to clear, so notes are never lost by accident
        Matcher keywordAndRest = splitFirstWord(details);
        String keyword = keywordAndRest.group("first").toLowerCase(Locale.ROOT);
        String rest = keywordAndRest.group("rest");

        return switch (keyword) {
            case RemarkCommand.KEYWORD_CLEAR -> {
                requireNothingAfter(rest);
                yield RemarkCommand.clear(personIndex);
            }
            case RemarkCommand.KEYWORD_DELETE -> {
                Matcher noteNumberAndRest = splitFirstWord(rest);
                requireNothingAfter(noteNumberAndRest.group("rest"));
                yield RemarkCommand.delete(personIndex, parseNoteIndex(noteNumberAndRest.group("first")));
            }
            case RemarkCommand.KEYWORD_EDIT -> {
                Matcher noteNumberAndNote = splitFirstWord(rest);
                Index noteIndex = parseNoteIndex(noteNumberAndNote.group("first"));
                String note = noteNumberAndNote.group("rest");
                if (note.isEmpty()) {
                    throw invalidFormat();
                }
                yield RemarkCommand.edit(personIndex, noteIndex, note);
            }
            default -> RemarkCommand.add(personIndex, details);
        };
    }

    /**
     * Returns a matcher holding the first word of {@code text} and the rest of it.
     * @throws ParseException if {@code text} is empty
     */
    private static Matcher splitFirstWord(String text) throws ParseException {
        Matcher matcher = FIRST_WORD_FORMAT.matcher(text);
        if (!matcher.matches()) {
            throw invalidFormat();
        }
        return matcher;
    }

    private static void requireNothingAfter(String rest) throws ParseException {
        if (!rest.isEmpty()) {
            throw invalidFormat();
        }
    }

    private static Index parseNoteIndex(String noteNumber) throws ParseException {
        if (!StringUtil.isNonZeroUnsignedInteger(noteNumber)) {
            throw new ParseException(MESSAGE_INVALID_NOTE_NUMBER);
        }
        return Index.fromOneBased(Integer.parseInt(noteNumber));
    }

    private static ParseException invalidFormat() {
        return new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, RemarkCommand.MESSAGE_USAGE));
    }

}
