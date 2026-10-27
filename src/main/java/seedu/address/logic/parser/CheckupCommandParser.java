package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.CheckupCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.person.CheckupAppointment;

/**
 * Parses a displayed index followed by a checkup date and time or the delete keyword.
 */
public class CheckupCommandParser implements Parser<CheckupCommand> {
    @Override
    public CheckupCommand parse(String args) throws ParseException {
        String[] parts = args.trim().split("\\s+", 2);
        if (parts.length != 2) {
            throw invalidFormat();
        }
        final Index index;
        try {
            index = ParserUtil.parseIndex(parts[0]);
        } catch (ParseException e) {
            throw invalidFormat();
        }
        if (parts[1].equalsIgnoreCase("delete")) {
            return CheckupCommand.delete(index);
        }
        final CheckupAppointment appointment;
        try {
            appointment = new CheckupAppointment(parts[1]);
        } catch (IllegalArgumentException e) {
            throw new ParseException(CheckupAppointment.MESSAGE_CONSTRAINTS + "\n" + CheckupCommand.MESSAGE_USAGE, e);
        }
        if (appointment.isBeforeToday()) {
            throw new ParseException(CheckupAppointment.MESSAGE_PAST_DATE);
        }
        return new CheckupCommand(index, appointment);
    }

    private ParseException invalidFormat() {
        return new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, CheckupCommand.MESSAGE_USAGE));
    }
}
