package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.CheckupCommand;
import seedu.address.model.person.CheckupAppointment;

public class CheckupCommandParserTest {
    private final CheckupCommandParser parser = new CheckupCommandParser();

    @Test
    public void parse_futureDateTime_returnsCommand() {
        for (LocalDateTime dateTime : List.of(LocalDateTime.now().plusMinutes(5),
                LocalDateTime.now().plusDays(1))) {
            String value = dateTime.format(DateTimeFormatter.ofPattern("dd/MM/uuuu HH:mm"));
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
    public void parse_invalidDateTime_defersValidationUntilExecution() {
        for (String value : List.of("31/02/2030 12:00", "01/01/2030 24:00", "01/01/2030",
                "2030-01-01 12:00", "01/01/2030 12:00 extra")) {
            assertParseSuccess(parser, "1 " + value, new CheckupCommand(Index.fromOneBased(1), value));
        }
    }

    @Test
    public void parse_yesterday_defersValidationUntilExecution() {
        String value = LocalDate.now().minusDays(1).format(DateTimeFormatter.ofPattern("dd/MM/uuuu")) + " 23:59";
        assertParseSuccess(parser, "1 " + value, new CheckupCommand(Index.fromOneBased(1), value));
        assertParseFailure(parser, "1 " + value, CheckupAppointment.MESSAGE_PAST_DATE_TIME);
    }

    @Test
    public void parse_pastTime_throwsParseException() {
        String value = LocalDateTime.now().minusMinutes(5).format(DateTimeFormatter.ofPattern("dd/MM/uuuu HH:mm"));
        assertParseFailure(parser, "1 " + value, CheckupAppointment.MESSAGE_PAST_DATE_TIME);
    }
}
