package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ADDRESS;
import static seedu.address.logic.parser.CliSyntax.PREFIX_FAMILY_EMAIL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_FAMILY_PHONE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_LEGACY_EMAIL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PATIENT_EMAIL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PATIENT_PHONE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_REMARK;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TAG;

import java.util.List;
import java.util.Map;

import seedu.address.logic.commands.FindCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.person.FindCriterion;
import seedu.address.model.person.NameContainsKeywordsPredicate;
import seedu.address.model.person.PersonContainsKeywordsPredicate;

/**
 * Parses input arguments and creates a new FindCommand object
 */
public class FindCommandParser implements Parser<FindCommand> {
    private static final Map<Prefix, FindCriterion> CRITERIA = Map.of(
            PREFIX_NAME, FindCriterion.NAME,
            PREFIX_PATIENT_PHONE, FindCriterion.PATIENT_PHONE,
            PREFIX_FAMILY_PHONE, FindCriterion.FAMILY_PHONE,
            PREFIX_ADDRESS, FindCriterion.ADDRESS,
            PREFIX_PATIENT_EMAIL, FindCriterion.PATIENT_EMAIL,
            PREFIX_FAMILY_EMAIL, FindCriterion.FAMILY_EMAIL,
            PREFIX_LEGACY_EMAIL, FindCriterion.EMAIL,
            PREFIX_TAG, FindCriterion.TAG,
            PREFIX_REMARK, FindCriterion.REMARK);

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

        FindCriterion criterion = CRITERIA.entrySet().stream()
                .filter(entry -> entry.getKey().getPrefix().equalsIgnoreCase(tokens[0]))
                .map(Map.Entry::getValue)
                .findFirst().orElse(null);
        if (criterion == null) {
            throw new ParseException("Unknown criterion: " + tokens[0]);
        }
        if (tokens.length < 2) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindCommand.MESSAGE_USAGE));
        }

        String input = tokens[1];
        if ((criterion == FindCriterion.PATIENT_PHONE || criterion == FindCriterion.FAMILY_PHONE)
                && !input.matches("[0-9]{2,}")) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindCommand.MESSAGE_USAGE));
        }

        List<String> keywords = List.of(input.split("\\s+"));
        if (keywords.stream().anyMatch(keyword -> keyword.startsWith("/"))) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindCommand.MESSAGE_USAGE));
        }
        List<String> searchTerms = criterion == FindCriterion.REMARK
                ? List.of(input.trim().replaceAll("\\s+", " ")) : keywords;
        return new FindCommand(new PersonContainsKeywordsPredicate(criterion, searchTerms));
    }

}
