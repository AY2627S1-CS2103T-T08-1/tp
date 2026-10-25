package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;

import java.util.List;

import seedu.address.logic.commands.FindCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.person.NameContainsKeywordsPredicate;
import seedu.address.model.person.PersonContainsKeywordsPredicate;

/**
 * Parses input arguments and creates a new FindCommand object
 */
public class FindCommandParser implements Parser<FindCommand> {

    /**
     * Parses the given {@code String} of arguments in the context of the FindCommand
     * and returns a FindCommand object for execution.
     * @throws ParseException if the user input does not conform to the expected format
     */
    public FindCommand parse(String args) throws ParseException {
        String trimmedArgs = args.trim();
        if (trimmedArgs.isEmpty()) {
            throw new ParseException(
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindCommand.MESSAGE_USAGE));
        }

        String[] tokens = trimmedArgs.split("\\s+", 2);
        if (!tokens[0].startsWith("/")) {
            return new FindCommand(new NameContainsKeywordsPredicate(List.of(trimmedArgs.split("\\s+"))));
        }

        String criterion = PersonContainsKeywordsPredicate.SUPPORTED_CRITERIA.stream()
                .filter(supported -> supported.equalsIgnoreCase(tokens[0].substring(1)))
                .findFirst().orElse(null);
        if (criterion == null || tokens.length < 2) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindCommand.MESSAGE_USAGE));
        }

        String input = tokens[1];
        if ((criterion.equals("patientNo") || criterion.equals("familyNo")) && !input.matches("[0-9]{2,}")) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindCommand.MESSAGE_USAGE));
        }

        List<String> keywords = List.of(input.split("\\s+"));
        if (keywords.stream().anyMatch(keyword -> keyword.startsWith("/"))) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindCommand.MESSAGE_USAGE));
        }
        return new FindCommand(new PersonContainsKeywordsPredicate(criterion, keywords));
    }

}
