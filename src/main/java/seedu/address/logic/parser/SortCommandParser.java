package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;

import java.util.Locale;

import seedu.address.logic.commands.SortCommand;
import seedu.address.logic.commands.SortCommand.Criterion;
import seedu.address.logic.parser.exceptions.ParseException;

/**
 * Parses input arguments and creates a new SortCommand object
 */
public class SortCommandParser implements Parser<SortCommand> {

    /**
     * Parses the given {@code String} of arguments in the context of the SortCommand
     * and returns a SortCommand object for execution. Criterion and direction are case-insensitive.
     * Priority defaults to descending (HIGH first) and name defaults to ascending (A to Z).
     * @throws ParseException if the criterion or direction is unknown, a direction is given for
     *     {@code recent}, or there are extra arguments
     */
    public SortCommand parse(String args) throws ParseException {
        String trimmedArgs = args.trim();
        if (trimmedArgs.isEmpty()) {
            throw invalidFormat();
        }

        String[] tokens = trimmedArgs.toLowerCase(Locale.ROOT).split("\\s+");
        if (tokens.length > 2) {
            throw invalidFormat();
        }

        boolean hasDirection = tokens.length == 2;
        return switch (tokens[0]) {
            case "priority" -> new SortCommand(Criterion.PRIORITY, hasDirection && parseAscending(tokens[1]));
            case "name" -> new SortCommand(Criterion.NAME, !hasDirection || parseAscending(tokens[1]));
            case "recent" -> {
                if (hasDirection) {
                    throw invalidFormat();
                }
                yield new SortCommand(Criterion.RECENT, false);
            }
            default -> throw invalidFormat();
        };
    }

    /**
     * Returns true for {@code asc} and false for {@code desc}.
     */
    private static boolean parseAscending(String direction) throws ParseException {
        return switch (direction) {
            case "asc" -> true;
            case "desc" -> false;
            default -> throw invalidFormat();
        };
    }

    private static ParseException invalidFormat() {
        return new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, SortCommand.MESSAGE_USAGE));
    }

}
