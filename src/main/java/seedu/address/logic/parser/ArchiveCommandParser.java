package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.ParserUtil.MESSAGE_INVALID_INDEX;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.ArchiveCommand;
import seedu.address.logic.parser.exceptions.ParseException;

/**
 * Parses input arguments and creates a new ArchiveCommand object
 */
public class ArchiveCommandParser implements Parser<ArchiveCommand> {

    private static final String INTEGER_REGEX = "-?\\d+";
    private static final String ZERO_REGEX = "-?0+";

    /**
     * Parses the given {@code String} of arguments in the context of the ArchiveCommand
     * and returns a ArchiveCommand object for execution.
     * @throws ParseException if the user input does not conform to the expected format
     */
    public ArchiveCommand parse(String args) throws ParseException {
        String trimmedArgs = args.trim();

        // missing index, non-numeric index or extra arguments
        if (!trimmedArgs.matches(INTEGER_REGEX)) {
            throw new ParseException(
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, ArchiveCommand.MESSAGE_USAGE));
        }

        // an integer, but not a positive one
        if (trimmedArgs.startsWith("-") || trimmedArgs.matches(ZERO_REGEX)) {
            throw new ParseException(MESSAGE_INVALID_INDEX);
        }

        return new ArchiveCommand(parsePositiveIndex(trimmedArgs));
    }

    /**
     * Parses {@code digits}, a string of digits representing a positive integer, into an {@code Index}.
     * A value too large to fit in an {@code int} cannot refer to any person in the list, so it is capped at
     * {@code Integer.MAX_VALUE} for the command to report it as out of range.
     */
    private static Index parsePositiveIndex(String digits) {
        try {
            return Index.fromOneBased(Integer.parseInt(digits));
        } catch (NumberFormatException nfe) {
            return Index.fromOneBased(Integer.MAX_VALUE);
        }
    }

}
