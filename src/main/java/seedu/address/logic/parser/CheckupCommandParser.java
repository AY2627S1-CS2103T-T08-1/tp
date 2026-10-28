package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.CheckupCommand;
import seedu.address.logic.parser.exceptions.ParseException;

/**
 * Parses a displayed index followed by a checkup date and time.
 */
public class CheckupCommandParser implements Parser<CheckupCommand> {
    @Override
    public CheckupCommand parse(String args) throws ParseException {
        String[] parts = args.trim().replaceAll("\\s+", " ").split(" ", 2);
        if (parts.length != 2) {
            throw invalidFormat();
        }
        final Index index;
        try {
            index = ParserUtil.parseIndex(parts[0]);
        } catch (ParseException e) {
            throw invalidFormat();
        }
        return new CheckupCommand(index, parts[1]);
    }

    private ParseException invalidFormat() {
        return new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, CheckupCommand.MESSAGE_USAGE));
    }
}
