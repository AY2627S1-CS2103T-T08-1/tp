package seedu.address.logic.parser;

import seedu.address.logic.commands.MarkCommand;
import seedu.address.logic.parser.exceptions.ParseException;

/**
 * Parses a single positive displayed index for the mark command.
 */
public class MarkCommandParser implements Parser<MarkCommand> {
    @Override
    public MarkCommand parse(String args) throws ParseException {
        try {
            return new MarkCommand(ParserUtil.parseIndex(args));
        } catch (ParseException e) {
            throw new ParseException(MarkCommand.MESSAGE_INVALID_INDEX, e);
        }
    }
}
