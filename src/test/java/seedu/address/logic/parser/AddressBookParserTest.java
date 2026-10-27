package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.Messages.MESSAGE_UNKNOWN_COMMAND;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.AddCommand;
import seedu.address.logic.commands.CheckupCommand;
import seedu.address.logic.commands.ClearCommand;
import seedu.address.logic.commands.DeleteCommand;
import seedu.address.logic.commands.EditCommand;
import seedu.address.logic.commands.EditCommand.EditPersonDescriptor;
import seedu.address.logic.commands.ExitCommand;
import seedu.address.logic.commands.FilterCommand;
import seedu.address.logic.commands.FindCommand;
import seedu.address.logic.commands.HelpCommand;
import seedu.address.logic.commands.ListCommand;
import seedu.address.logic.commands.MarkCommand;
import seedu.address.logic.commands.RemarkCommand;
import seedu.address.logic.commands.UnmarkCommand;
import seedu.address.logic.commands.ViewCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.person.CheckupAppointment;
import seedu.address.model.person.FindCriterion;
import seedu.address.model.person.NameContainsKeywordsPredicate;
import seedu.address.model.person.Person;
import seedu.address.model.person.PersonContainsKeywordsPredicate;
import seedu.address.model.person.PersonMatchesFilterPredicate;
import seedu.address.testutil.EditPersonDescriptorBuilder;
import seedu.address.testutil.PersonBuilder;
import seedu.address.testutil.PersonUtil;

public class AddressBookParserTest {

    private final AddressBookParser parser = new AddressBookParser();

    @Test
    public void parseCommand_mark() throws Exception {
        for (String word : List.of("mark", "MARK", "Mark", "mArK")) {
            assertEquals(new MarkCommand(INDEX_FIRST_PERSON), parser.parseCommand(word + " 1"));
        }
    }

    @Test
    public void parseCommand_unmark() throws Exception {
        for (String word : List.of("unmark", "UNMARK", "Unmark", "uNmArK")) {
            assertEquals(new UnmarkCommand(INDEX_FIRST_PERSON), parser.parseCommand(word + " 1"));
        }
    }

    @Test
    public void parseCommand_markAndUnmarkInvalidIndex_showsExactError() {
        for (String word : List.of("MARK", "mArK", "UNMARK", "uNmArK")) {
            for (String args : List.of("", "0", "-1", "abc", "1.5", "2147483648", "1 2", "1 extra")) {
                assertThrows(ParseException.class, "Invalid index.", () -> parser.parseCommand(word + " " + args));
            }
        }
    }

    @Test
    public void parseCommand_checkup() throws Exception {
        String value = LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/uuuu")) + " 00:00";
        assertEquals(new CheckupCommand(INDEX_FIRST_PERSON, new CheckupAppointment(value)),
                new AddressBookParser().parseCommand("checkup 1 " + value));
    }


    @Test
    public void parseCommand_add() throws Exception {
        Person person = new PersonBuilder().build();
        AddCommand command = (AddCommand) parser.parseCommand(PersonUtil.getAddCommand(person));
        assertEquals(new AddCommand(person), command);
    }

    @Test
    public void parseCommand_clear() throws Exception {
        for (String input : List.of("clear", "  clear  ", "\tclear\t", "\nclear\r\n")) {
            assertTrue(parser.parseCommand(input) instanceof ClearCommand, input);
        }
    }

    @Test
    public void parseCommand_clearWithArguments_throwsParseException() {
        String expectedMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, ClearCommand.MESSAGE_USAGE);
        for (String input : List.of("clear 1", "clear Alice", "clear /name Alice", "clear\tAlice", "clear !")) {
            assertThrows(ParseException.class, expectedMessage, () -> parser.parseCommand(input));
        }
    }

    @Test
    public void parseCommand_delete() throws Exception {
        DeleteCommand command = (DeleteCommand) parser.parseCommand(
                DeleteCommand.COMMAND_WORD + " " + INDEX_FIRST_PERSON.getOneBased());
        assertEquals(new DeleteCommand(INDEX_FIRST_PERSON), command);
    }

    @Test
    public void parseCommand_deleteInDifferentCase_success() throws Exception {
        assertEquals(new DeleteCommand(INDEX_FIRST_PERSON), parser.parseCommand("Delete 1"));
        assertEquals(new DeleteCommand(INDEX_FIRST_PERSON), parser.parseCommand("DELETE 1"));
    }

    @Test
    public void parseCommand_edit() throws Exception {
        Person person = new PersonBuilder().build();
        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder(person).build();
        EditCommand command = (EditCommand) parser.parseCommand(EditCommand.COMMAND_WORD + " "
                + INDEX_FIRST_PERSON.getOneBased() + " " + PersonUtil.getEditPersonDescriptorDetails(descriptor));
        assertEquals(new EditCommand(INDEX_FIRST_PERSON, descriptor), command);
    }

    @Test
    public void parseCommand_exit() throws Exception {
        for (String input : List.of("exit", "exit   ", "  exit  ", "\texit\t", "\nexit\r\n")) {
            assertTrue(parser.parseCommand(input) instanceof ExitCommand, input);
        }
    }

    @Test
    public void parseCommand_exitWithArguments_throwsParseException() {
        String expectedMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, ExitCommand.MESSAGE_USAGE);
        for (String input : List.of("exit 1", "exit Alice", "exit /name Alice", "exit --help",
                "exit\tAlice", "  exit Alice  ", "exit Alice Bob", "exit !")) {
            assertThrows(ParseException.class, expectedMessage, () -> parser.parseCommand(input));
        }
    }

    @Test
    public void parseCommand_filter() throws Exception {
        FilterCommand command = (FilterCommand) parser.parseCommand(
                FilterCommand.COMMAND_WORD + " /tag medium");
        assertEquals(new FilterCommand(
                new PersonMatchesFilterPredicate(List.of(), List.of("MEDIUM"), List.of())), command);
    }

    @Test
    public void parseCommand_find() throws Exception {
        List<String> keywords = List.of("foo", "bar", "baz");
        for (String word : List.of("find", "finD", "fiNd", "fiND", "fInd", "fInD", "fINd", "fIND",
                "Find", "FinD", "FiNd", "FiND", "FInd", "FInD", "FINd", "FIND")) {
            FindCommand command = (FindCommand) parser.parseCommand(
                    word + " " + keywords.stream().collect(Collectors.joining(" ")));
            assertEquals(new FindCommand(new NameContainsKeywordsPredicate(keywords)), command);
        }
    }

    @Test
    public void parseCommand_findWithCriterion() throws Exception {
        FindCommand expected =
                new FindCommand(new PersonContainsKeywordsPredicate(FindCriterion.PATIENT_PHONE, List.of("87")));
        assertEquals(expected, parser.parseCommand("find /patientNo 87"));
        assertEquals(expected, parser.parseCommand("FiNd /patientNo 87"));
    }

    @Test
    public void parseCommand_help_success() throws Exception {
        for (String input : List.of("help", "   help", "help   ", "   help   ", "\thelp\t",
                " \t help \t ", "\nhelp\r\n", "HELP", "Help", "hElP", " \t HeLp \r\n")) {
            assertTrue(parser.parseCommand(input) instanceof HelpCommand, input);
        }
    }

    @Test
    public void parseCommand_helpWithArguments_throwsParseException() {
        String expectedMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, HelpCommand.MESSAGE_USAGE);
        for (String input : List.of("help 3", "help abc", "   help abc   ", "help\tabc",
                "help --help", "help abc def", "help !", "help\nabc", "HELP 3", "HeLp\tabc")) {
            assertThrows(ParseException.class, expectedMessage, () -> parser.parseCommand(input));
        }
    }

    @Test
    public void parseCommand_helpWithExtraText_throwsUnknownCommand() {
        for (String input : List.of("abc help", "   abc help   ", "abchelp", "helpabc", "help3",
                "abc HELP", "HELPabc", "abcHeLp")) {
            assertThrows(ParseException.class, MESSAGE_UNKNOWN_COMMAND, () -> parser.parseCommand(input));
        }
    }

    @Test
    public void parseCommand_list_success() throws Exception {
        for (String input : List.of("list", "   list", "list   ", "   list   ", "\tlist\t",
                " \t list \t ", "\nlist\r\n")) {
            assertTrue(parser.parseCommand(input) instanceof ListCommand, input);
        }
    }

    @Test
    public void parseCommand_listWithArguments_throwsParseException() {
        for (String input : List.of("list 3", "list abc", "   list abc", "list\tabc",
                "list n/Alice", "list --help", "list abc def", "list 0", "list -1", "list !")) {
            assertThrows(ParseException.class, ListCommand.MESSAGE_INVALID_FORMAT, () -> parser.parseCommand(input));
        }
    }

    @Test
    public void parseCommand_listWithWrongCaseOrSpelling_throwsUnknownCommand() {
        for (String input : List.of("LIST", "List", "lIsT", "list3", "listabc", "lists")) {
            assertThrows(ParseException.class, MESSAGE_UNKNOWN_COMMAND, () -> parser.parseCommand(input));
        }
    }

    @Test
    public void parseCommand_view() throws Exception {
        ViewCommand command = (ViewCommand) parser.parseCommand(
                ViewCommand.COMMAND_WORD + " " + INDEX_FIRST_PERSON.getOneBased());
        assertEquals(new ViewCommand(INDEX_FIRST_PERSON), command);
    }

    @Test
    public void parseCommand_remark() throws Exception {
        assertEquals(RemarkCommand.add(INDEX_FIRST_PERSON, "Allergic to penicillin"),
                parser.parseCommand(RemarkCommand.COMMAND_WORD + " 1 Allergic to penicillin"));
        assertEquals(RemarkCommand.clear(INDEX_FIRST_PERSON),
                parser.parseCommand(RemarkCommand.COMMAND_WORD + " 1 /clear"));
    }

    @Test
    public void parseCommand_unrecognisedInput_throwsParseException() {
        assertThrows(ParseException.class, String.format(MESSAGE_INVALID_COMMAND_FORMAT, HelpCommand.MESSAGE_USAGE), ()
            -> parser.parseCommand(""));
    }

    @Test
    public void parseCommand_unknownCommand_throwsParseException() {
        assertThrows(ParseException.class, MESSAGE_UNKNOWN_COMMAND, () -> parser.parseCommand("unknownCommand"));
    }
}
