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
    public void parse_delete_returnsDeleteCommand() {
        for (String keyword : List.of("delete", "DELETE", "DeLeTe")) {
            assertParseSuccess(parser, " 2 " + keyword + " ", CheckupCommand.delete(Index.fromOneBased(2)));
        }
    }

    @Test
    public void parse_deleteWithInvalidIndexOrExtraArguments_failure() {
        for (String args : List.of("0 delete", "-1 delete", "abc delete", "2147483648 delete")) {
            assertParseFailure(parser, args,
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, CheckupCommand.MESSAGE_USAGE));
        }
        assertParseFailure(parser, "1 delete extra",
                CheckupAppointment.MESSAGE_CONSTRAINTS + "\n" + CheckupCommand.MESSAGE_USAGE);
    }

    @Test
    public void parse_todayOrFuture_returnsCommand() {
        for (LocalDate date : List.of(LocalDate.now(), LocalDate.now().plusDays(1))) {
            String value = date.format(DateTimeFormatter.ofPattern("dd/MM/uuuu")) + " 00:00";
    public void parse_futureDateTime_returnsCommand() {
        for (LocalDateTime dateTime : List.of(LocalDateTime.now().plusMinutes(5),
                LocalDateTime.now().plusDays(1))) {
            String value = dateTime.format(DateTimeFormatter.ofPattern("dd/MM/uuuu HH:mm"));
            assertParseSuccess(parser, " 2 " + value + " ",
                    new CheckupCommand(Index.fromOneBased(2), new CheckupAppointment(value)));
        }
    }

    @Test
    public void parse_extraWhitespaceBetweenFields_returnsCommand() {
        String date = LocalDate.now().plusDays(1).format(DateTimeFormatter.ofPattern("dd/MM/uuuu"));
        CheckupCommand expected = new CheckupCommand(Index.fromOneBased(1),
                new CheckupAppointment(date + " 10:00"));
        for (String whitespace : List.of(" ", "   ", "\t", " \t ")) {
            assertParseSuccess(parser, whitespace + "1" + whitespace + date + whitespace + "10:00" + whitespace,
                    expected);
            assertParseSuccess(parser, whitespace + "1" + whitespace + "delete" + whitespace,
                    CheckupCommand.delete(Index.fromOneBased(1)));
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
    }

    @Test
    public void parse_pastTime_defersValidationUntilExecution() {
        String value = LocalDateTime.now().minusMinutes(5).format(DateTimeFormatter.ofPattern("dd/MM/uuuu HH:mm"));
        assertParseSuccess(parser, "1 " + value, new CheckupCommand(Index.fromOneBased(1), value));
    }
}
