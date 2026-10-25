package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.CheckupCommand;
import seedu.address.model.person.CheckupAppointment;

public class CheckupCommandParserTest {
    private final CheckupCommandParser parser = new CheckupCommandParser();

    @Test
    public void parse_todayOrFuture_returnsCommand() {
        for (LocalDate date : List.of(LocalDate.now(), LocalDate.now().plusDays(1))) {
            String value = date.format(DateTimeFormatter.ofPattern("dd/MM/uuuu")) + " 00:00";
            assertParseSuccess(parser, " 2 " + value + " ",
                    new CheckupCommand(Index.fromOneBased(2), new CheckupAppointment(value)));
        }
    }

    @Test
    public void parse_missingFieldsOrInvalidIndex_throwsParseException() {
        for (String args : List.of("", "1", "0 01/01/2030 12:00", "-1 01/01/2030 12:00",
                "abc 01/01/2030 12:00", "2147483648 01/01/2030 12:00")) {
            assertParseFailure(parser, args,
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, CheckupCommand.MESSAGE_USAGE));
        }
    }

    @Test
    public void parse_invalidDateTime_throwsParseException() {
        for (String value : List.of("31/02/2030 12:00", "01/01/2030 24:00", "01/01/2030",
                "2030-01-01 12:00", "01/01/2030 12:00 extra")) {
            assertParseFailure(parser, "1 " + value,
                    CheckupAppointment.MESSAGE_CONSTRAINTS + "\n" + CheckupCommand.MESSAGE_USAGE);
        }
    }

    @Test
    public void parse_yesterday_throwsParseException() {
        String value = LocalDate.now().minusDays(1).format(DateTimeFormatter.ofPattern("dd/MM/uuuu")) + " 23:59";
        assertParseFailure(parser, "1 " + value, CheckupAppointment.MESSAGE_PAST_DATE);
    }
}
