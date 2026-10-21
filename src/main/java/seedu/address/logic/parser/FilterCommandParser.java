package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;

import seedu.address.logic.commands.FilterCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.person.PersonHasTagPredicate;

/**
 * Parses input arguments and creates a new FilterCommand object
 */
public class FilterCommandParser implements Parser<FilterCommand> {

    /**
     * Parses the given {@code String} of arguments in the context of the FilterCommand
     * and returns a FilterCommand object for execution.
     * @throws ParseException if the user input is not exactly one of the valid tags
     */
    public FilterCommand parse(String args) throws ParseException {
        String trimmedArgs = args.trim();
        boolean isValidTag = FilterCommand.VALID_TAGS.stream().anyMatch(trimmedArgs::equalsIgnoreCase);
        if (!isValidTag) {
            throw new ParseException(
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, FilterCommand.MESSAGE_USAGE));
        }

        return new FilterCommand(new PersonHasTagPredicate(trimmedArgs));
    }

}
