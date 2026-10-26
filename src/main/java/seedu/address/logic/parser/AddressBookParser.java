package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.Messages.MESSAGE_UNKNOWN_COMMAND;

import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import seedu.address.commons.core.LogsCenter;
import seedu.address.logic.commands.AddCommand;
import seedu.address.logic.commands.ArchiveCommand;
import seedu.address.logic.commands.CheckupCommand;
import seedu.address.logic.commands.ClearCommand;
import seedu.address.logic.commands.Command;
import seedu.address.logic.commands.DeleteCommand;
import seedu.address.logic.commands.EditCommand;
import seedu.address.logic.commands.ExitCommand;
import seedu.address.logic.commands.FilterCommand;
import seedu.address.logic.commands.FindCommand;
import seedu.address.logic.commands.HelpCommand;
import seedu.address.logic.commands.ListCommand;
import seedu.address.logic.commands.MarkCommand;
import seedu.address.logic.commands.RemarkCommand;
import seedu.address.logic.commands.SortCommand;
import seedu.address.logic.commands.UnarchiveCommand;
import seedu.address.logic.commands.UnmarkCommand;
import seedu.address.logic.commands.ViewCommand;
import seedu.address.logic.parser.exceptions.ParseException;

/**
 * Parses user input.
 */
public class AddressBookParser {

    /**
     * Used for initial separation of command word and args.
     */
    private static final Pattern BASIC_COMMAND_FORMAT = Pattern.compile("(?<commandWord>\\S+)(?<arguments>.*)");
    private static final Logger logger = LogsCenter.getLogger(AddressBookParser.class);

    /**
     * Parses user input into command for execution.
     *
     * @param userInput full user input string
     * @return the command based on the user input
     * @throws ParseException if the user input does not conform to the expected format
     */
    public Command parseCommand(String userInput) throws ParseException {
        final Matcher matcher = BASIC_COMMAND_FORMAT.matcher(userInput.trim());
        if (!matcher.matches()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, HelpCommand.MESSAGE_USAGE));
        }


        // These command words are case-insensitive.
        String commandWord = matcher.group("commandWord");
        if (commandWord.equalsIgnoreCase(DeleteCommand.COMMAND_WORD)) {
            commandWord = DeleteCommand.COMMAND_WORD;
        } else if (commandWord.equalsIgnoreCase(ArchiveCommand.COMMAND_WORD)) {
            commandWord = ArchiveCommand.COMMAND_WORD;
        } else if (commandWord.equalsIgnoreCase(UnarchiveCommand.COMMAND_WORD)) {
            commandWord = UnarchiveCommand.COMMAND_WORD;
        } else if (commandWord.equalsIgnoreCase(HelpCommand.COMMAND_WORD)) {
            commandWord = HelpCommand.COMMAND_WORD;
        } else if (commandWord.equalsIgnoreCase(MarkCommand.COMMAND_WORD)) {
            commandWord = MarkCommand.COMMAND_WORD;
        } else if (commandWord.equalsIgnoreCase(UnmarkCommand.COMMAND_WORD)) {
            commandWord = UnmarkCommand.COMMAND_WORD;
        }
        final String arguments = matcher.group("arguments");

        // Note to developers: Change LOG_LEVEL in LogsCenter to enable lower level (i.e., FINE, FINER and lower)
        // log messages such as the one below.
        // Lower level log messages are used sparingly to minimize noise in the code.
        logger.fine("Command word: " + commandWord + "; Arguments: " + arguments);

        return switch (commandWord) {
            case AddCommand.COMMAND_WORD -> new AddCommandParser().parse(arguments);
            case ArchiveCommand.COMMAND_WORD -> new ArchiveCommandParser().parse(arguments);
            case UnarchiveCommand.COMMAND_WORD -> new UnarchiveCommandParser().parse(arguments);
            case CheckupCommand.COMMAND_WORD -> new CheckupCommandParser().parse(arguments);
            case EditCommand.COMMAND_WORD -> new EditCommandParser().parse(arguments);
            case DeleteCommand.COMMAND_WORD -> new DeleteCommandParser().parse(arguments);
            case ClearCommand.COMMAND_WORD -> {
                if (!arguments.isBlank()) {
                    throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, ClearCommand.MESSAGE_USAGE));
                }
                yield new ClearCommand();
            }
            case FindCommand.COMMAND_WORD -> new FindCommandParser().parse(arguments);
            case ListCommand.COMMAND_WORD -> {
                String trimmedArguments = arguments.trim();
                if (trimmedArguments.equalsIgnoreCase(ArchiveCommand.COMMAND_WORD)) {
                    yield new ListCommand(true);
                } else if (trimmedArguments.isEmpty()) {
                    yield new ListCommand();
                } else {
                    throw new ParseException(ListCommand.MESSAGE_INVALID_FORMAT);
                }
            }
            case FilterCommand.COMMAND_WORD -> new FilterCommandParser().parse(arguments);
            case SortCommand.COMMAND_WORD -> new SortCommandParser().parse(arguments);
            case ExitCommand.COMMAND_WORD -> {
                if (!arguments.isBlank()) {
                    throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, ExitCommand.MESSAGE_USAGE));
                }
                yield new ExitCommand();
            }
            case HelpCommand.COMMAND_WORD -> {
                if (!arguments.trim().isEmpty()) {
                    throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, HelpCommand.MESSAGE_USAGE));
                }
                yield new HelpCommand();
            }
            case ViewCommand.COMMAND_WORD -> new ViewCommandParser().parse(arguments);
            case RemarkCommand.COMMAND_WORD -> new RemarkCommandParser().parse(arguments);
            case MarkCommand.COMMAND_WORD -> new MarkCommandParser().parse(arguments);
            case UnmarkCommand.COMMAND_WORD -> new UnmarkCommandParser().parse(arguments);
            default -> {
                logger.finer("This user input caused a ParseException: " + userInput);
                throw new ParseException(MESSAGE_UNKNOWN_COMMAND);
            }
        };
    }

}
