package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ADDRESS;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TAG;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import seedu.address.logic.commands.FilterCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.person.PersonMatchesFilterPredicate;

/**
 * Parses input arguments and creates a new FilterCommand object
 */
public class FilterCommandParser implements Parser<FilterCommand> {

    // A filter prefix stuck to the end of a word, e.g. "ali/tag", where the space before the prefix is missing.
    private static final Pattern PREFIX_WITHOUT_SPACE = Pattern.compile(
            "(?i)\\S(" + PREFIX_NAME + "|" + PREFIX_TAG + "|" + PREFIX_ADDRESS + ")(\\s|$)");

    /**
     * Parses the given {@code String} of arguments in the context of the FilterCommand
     * and returns a FilterCommand object for execution.
     * Any run of whitespace (spaces, tabs, newlines) is treated as a single space, so prefixes are recognized
     * after any whitespace. Name keywords are separated by whitespace; name, tag and address prefixes may
     * be repeated. Name and tag prefixes are case-insensitive; the address prefix is lowercase.
     *
     * @throws ParseException if no criterion is given, a value is empty, a tag is not a valid tag,
     *     there is text before the first prefix, or a prefix other than
     *     name, tag and address is given
     */
    public FilterCommand parse(String args) throws ParseException {
        String normalizedArgs = args.trim().replaceAll("\\s+", " ");
        if (hasUnsupportedPrefix(args)) {
            throw invalidFormat();
        }

        normalizedArgs = normalizedArgs.replaceAll("(?i)(?<!\\S)/name(?=\\s|$)", "/name");

        ArgumentMultimap argMultimap =
                ArgumentTokenizer.tokenize(" " + normalizedArgs, PREFIX_NAME, PREFIX_TAG, PREFIX_ADDRESS);

        if (!argMultimap.getPreamble().isEmpty()) {
            throw invalidFormat();
        }

        List<String> nameKeywords = new ArrayList<>();
        for (String value : argMultimap.getAllValues(PREFIX_NAME)) {
            if (value.isEmpty() || hasPrefixWithoutSpace(value)) {
                throw invalidFormat();
            }
            nameKeywords.addAll(Arrays.asList(value.split("\\s+")));
        }

        List<String> tags = argMultimap.getAllValues(PREFIX_TAG);
        for (String tag : tags) {
            if (FilterCommand.VALID_TAGS.stream().noneMatch(tag::equalsIgnoreCase)) {
                throw invalidFormat();
            }
        }

        List<String> addressPhrases = argMultimap.getAllValues(PREFIX_ADDRESS);
        if (addressPhrases.stream().anyMatch(phrase -> phrase.isEmpty() || hasPrefixWithoutSpace(phrase))) {
            throw invalidFormat();
        }

        if (nameKeywords.isEmpty() && tags.isEmpty() && addressPhrases.isEmpty()) {
            throw invalidFormat();
        }

        return new FilterCommand(new PersonMatchesFilterPredicate(nameKeywords, tags, addressPhrases));
    }

    /**
     * Returns true if any whitespace-separated token looks like a prefix (starts with {@code /}) but is not
     * one of the prefixes supported by filter.
     */
    private static boolean hasUnsupportedPrefix(String args) {
        return Arrays.stream(args.trim().split("\\s+"))
                .filter(token -> token.startsWith("/"))
                .anyMatch(token -> !token.equalsIgnoreCase(PREFIX_NAME.getPrefix())
                        && !token.equalsIgnoreCase(PREFIX_TAG.getPrefix())
                        && !token.equals(PREFIX_ADDRESS.getPrefix()));
    }

    private static boolean hasPrefixWithoutSpace(String value) {
        return PREFIX_WITHOUT_SPACE.matcher(value).find();
    }

    private static ParseException invalidFormat() {
        return new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, FilterCommand.MESSAGE_USAGE));
    }

}
