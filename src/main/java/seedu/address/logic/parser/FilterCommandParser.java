package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ADDRESS;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TAG;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import seedu.address.logic.commands.FilterCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.person.PersonMatchesFilterPredicate;

/**
 * Parses input arguments and creates a new FilterCommand object
 */
public class FilterCommandParser implements Parser<FilterCommand> {

    /**
     * Parses the given {@code String} of arguments in the context of the FilterCommand
     * and returns a FilterCommand object for execution.
     * Any run of whitespace (spaces, tabs, newlines) is treated as a single space, so prefixes are recognized
     * after any whitespace. Name keywords are separated by whitespace and {@code /name} may be given only once;
     * tags and addresses are given by repeating their prefix.
     *
     * @throws ParseException if no criterion is given, a value is empty, a tag is not a valid tag,
     *     there is text before the first prefix, {@code /name} is repeated, or a prefix other than
     *     name, tag and address is given
     */
    public FilterCommand parse(String args) throws ParseException {
        String normalizedArgs = args.trim().replaceAll("\\s+", " ");
        if (hasUnsupportedPrefix(args)) {
            throw invalidFormat();
        }

        ArgumentMultimap argMultimap =
                ArgumentTokenizer.tokenize(" " + normalizedArgs, PREFIX_NAME, PREFIX_TAG, PREFIX_ADDRESS);

        if (!argMultimap.getPreamble().isEmpty()) {
            throw invalidFormat();
        }

        argMultimap.verifyNoDuplicatePrefixesFor(PREFIX_NAME);

        List<String> nameKeywords = new ArrayList<>();
        for (String value : argMultimap.getAllValues(PREFIX_NAME)) {
            if (value.isEmpty()) {
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
        if (addressPhrases.stream().anyMatch(String::isEmpty)) {
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
                .anyMatch(token -> Stream.of(PREFIX_NAME, PREFIX_TAG, PREFIX_ADDRESS)
                        .noneMatch(prefix -> token.regionMatches(true, 0, prefix.getPrefix(), 0,
                                prefix.getPrefix().length())));
    }

    private static ParseException invalidFormat() {
        return new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, FilterCommand.MESSAGE_USAGE));
    }

}
