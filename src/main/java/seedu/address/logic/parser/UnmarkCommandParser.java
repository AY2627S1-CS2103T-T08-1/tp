package seedu.address.logic.parser;

import seedu.address.logic.commands.UnmarkCommand;
import seedu.address.logic.parser.exceptions.ParseException;

/**
 * Parses a single positive displayed index for the unmark command.
 */
public class UnmarkCommandParser implements Parser<UnmarkCommand> {
    @Override
    public UnmarkCommand parse(String args) throws ParseException {
        try {
            return new UnmarkCommand(ParserUtil.parseIndex(args));
        } catch (ParseException e) {
            throw new ParseException(UnmarkCommand.MESSAGE_INVALID_INDEX, e);
        }
    }
}
