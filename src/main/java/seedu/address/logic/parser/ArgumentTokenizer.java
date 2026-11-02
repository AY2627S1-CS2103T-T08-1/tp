package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TAG;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import seedu.address.logic.parser.exceptions.ParseException;

/**
 * Tokenizes arguments string of the form: {@code preamble <prefix>value <prefix>value ...}<br>
 *     e.g. {@code some preamble text t/ 11.00 t/12.00 k/ m/ July}  where prefixes are {@code t/ k/ m/}.<br>
 * 1. An argument's value can be an empty string e.g. the value of {@code k/} in the above example.<br>
 * 2. Leading and trailing whitespaces of an argument value will be discarded.<br>
 * 3. An argument may be repeated and all its values will be accumulated e.g. the value of {@code t/}
 *    in the above example.<br>
 */
public class ArgumentTokenizer {

    public static final String MESSAGE_SLASH_IN_VALUE = "Unexpected \"/\" in \"%1$s\". Put a space before each "
            + "prefix. Inside a value, \"/\" can only join short parts, as in s/o, a/l, c/o or 4/5.";
    public static final String MESSAGE_LONE_SLASH = "Unexpected \"/\" on its own. Put the prefix name straight "
            + "after \"/\", e.g. /name, or remove the spaces around a \"/\" that joins short parts, e.g. s/o.";

    // the shortest prefix name, tag in /tag, has 3 letters, so a slash followed by fewer letters is not a prefix
    private static final int MIN_PREFIX_NAME_LENGTH = 3;

    /**
     * Tokenizes an arguments string and returns an {@code ArgumentMultimap} object that maps prefixes to their
     * respective argument values. Only the given prefixes will be recognized in the arguments string.
     *
     * @param argsString Arguments string of the form: {@code preamble <prefix>value <prefix>value ...}
     * @param prefixes   Prefixes to tokenize the arguments string with
     * @return           ArgumentMultimap object that maps prefixes to their arguments
     */
    public static ArgumentMultimap tokenize(String argsString, Prefix... prefixes) {
        List<PrefixPosition> positions = findAllPrefixPositions(argsString, prefixes);
        return extractArguments(argsString, positions);
    }

    /**
     * Tokenizes contact fields, rejecting every slash that is neither part of a complete recognized prefix
     * nor a slash joining short parts of a value, as in {@code s/o} or {@code 4/5}.
     * Prefixes must be separated from their values by whitespace. The supplied usage is shown on failure.
     */
    public static ArgumentMultimap tokenizeStrict(String argsString, String usage, Prefix... prefixes)
            throws ParseException {
        List<PrefixPosition> positions = findAllPrefixPositions(argsString, prefixes);
        Set<Integer> validSlashPositions = positions.stream().filter(position -> {
            int end = position.getStartPosition() + position.getPrefix().getPrefix().length();
            return end == argsString.length() || Character.isWhitespace(argsString.charAt(end));
        }).map(PrefixPosition::getStartPosition).collect(Collectors.toSet());

        for (int index = argsString.indexOf('/'); index >= 0; index = argsString.indexOf('/', index + 1)) {
            if (!validSlashPositions.contains(index) && !isSlashInValue(argsString, index)) {
                boolean isWordStart = index == 0 || Character.isWhitespace(argsString.charAt(index - 1));
                if (isWordStart && index + 1 < argsString.length() && argsString.charAt(index + 1) != '/'
                        && !Character.isWhitespace(argsString.charAt(index + 1))) {
                    String prefix = argsString.substring(index).split("\\s+", 2)[0];
                    throw new ParseException("Unknown criterion: " + prefix + "\n"
                            + String.format(MESSAGE_INVALID_COMMAND_FORMAT, usage));
                }
                if (!isWordStart) {
                    throw createExceptionWithUsage(
                            String.format(MESSAGE_SLASH_IN_VALUE, getWordAt(argsString, index)), usage);
                }
                throw createExceptionWithUsage(MESSAGE_LONE_SLASH, usage);
            }
        }
        return extractArguments(argsString, positions);
    }

    /**
     * Returns true if the slash at {@code index} joins short parts of a value, as in {@code s/o} or {@code 4/5}.
     * Such a slash follows a non-whitespace character and is followed by a letter or digit, but not by
     * {@link #MIN_PREFIX_NAME_LENGTH} or more letters, so a prefix typed without a space before it is rejected.
     */
    private static boolean isSlashInValue(String argsString, int index) {
        boolean isAfterNonWhitespace = index > 0 && !Character.isWhitespace(argsString.charAt(index - 1));
        boolean isBeforeLetterOrDigit = index + 1 < argsString.length()
                && Character.isLetterOrDigit(argsString.charAt(index + 1));
        if (!isAfterNonWhitespace || !isBeforeLetterOrDigit) {
            return false;
        }
        int lettersAfter = 0;
        while (index + 1 + lettersAfter < argsString.length()
                && Character.isLetter(argsString.charAt(index + 1 + lettersAfter))) {
            lettersAfter++;
        }
        return lettersAfter < MIN_PREFIX_NAME_LENGTH;
    }

    /**
     * Returns an exception with {@code message}, followed by the invalid-format message with {@code usage}.
     */
    private static ParseException createExceptionWithUsage(String message, String usage) {
        return new ParseException(message + "\n" + String.format(MESSAGE_INVALID_COMMAND_FORMAT, usage));
    }

    /**
     * Returns the whitespace-separated word of {@code argsString} that contains the character at {@code index}.
     */
    private static String getWordAt(String argsString, int index) {
        int start = index;
        while (start > 0 && !Character.isWhitespace(argsString.charAt(start - 1))) {
            start--;
        }
        int end = index;
        while (end < argsString.length() && !Character.isWhitespace(argsString.charAt(end))) {
            end++;
        }
        return argsString.substring(start, end);
    }

    /**
     * Finds all zero-based prefix positions in the given arguments string.
     *
     * @param argsString Arguments string of the form: {@code preamble <prefix>value <prefix>value ...}
     * @param prefixes   Prefixes to find in the arguments string
     * @return           List of zero-based prefix positions in the given arguments string
     */
    private static List<PrefixPosition> findAllPrefixPositions(String argsString, Prefix... prefixes) {
        return Arrays.stream(prefixes)
                .flatMap(prefix -> findPrefixPositions(argsString, prefix).stream())
                .collect(Collectors.toList());
    }

    /**
     * @see #findAllPrefixPositions(String, Prefix...)
     */
    private static List<PrefixPosition> findPrefixPositions(String argsString, Prefix prefix) {
        List<PrefixPosition> positions = new ArrayList<>();

        int prefixPosition = findPrefixPosition(argsString, prefix.getPrefix(), 0);
        while (prefixPosition != -1) {
            PrefixPosition extendedPrefix = new PrefixPosition(prefix, prefixPosition);
            positions.add(extendedPrefix);
            prefixPosition = findPrefixPosition(argsString, prefix.getPrefix(), prefixPosition);
        }

        return positions;
    }

    /**
     * Returns the index of the first occurrence of {@code prefix} in
     * {@code argsString} starting from index {@code fromIndex}. An occurrence
     * is valid if there is a whitespace before {@code prefix}. Returns -1 if no
     * such occurrence can be found.
     * The tag prefix is matched ignoring case; other prefixes retain exact matching.
     *
     * E.g if {@code argsString} = "e/hip/900", {@code prefix} = "p/" and
     * {@code fromIndex} = 0, this method returns -1 as there are no valid
     * occurrences of "p/" with whitespace before it. However, if
     * {@code argsString} = "e/hi p/900", {@code prefix} = "p/" and
     * {@code fromIndex} = 0, this method returns 5.
     */
    private static int findPrefixPosition(String argsString, String prefix, int fromIndex) {
        if (prefix.equals(PREFIX_TAG.getPrefix())) {
            for (int i = fromIndex; i < argsString.length(); i++) {
                if (argsString.charAt(i) == ' '
                        && argsString.regionMatches(true, i + 1, prefix, 0, prefix.length())) {
                    return i + 1;
                }
            }
            return -1;
        }
        int prefixIndex = argsString.indexOf(" " + prefix, fromIndex);
        return prefixIndex == -1 ? -1
                : prefixIndex + 1; // +1 as offset for whitespace
    }

    /**
     * Extracts prefixes and their argument values, and returns an {@code ArgumentMultimap} object that maps the
     * extracted prefixes to their respective arguments. Prefixes are extracted based on their zero-based positions in
     * {@code argsString}.
     *
     * @param argsString      Arguments string of the form: {@code preamble <prefix>value <prefix>value ...}
     * @param prefixPositions Zero-based positions of all prefixes in {@code argsString}
     * @return                ArgumentMultimap object that maps prefixes to their arguments
     */
    private static ArgumentMultimap extractArguments(String argsString, List<PrefixPosition> prefixPositions) {

        // Sort by start position
        prefixPositions.sort((prefix1, prefix2) -> prefix1.getStartPosition() - prefix2.getStartPosition());

        // Insert a PrefixPosition to represent the preamble
        PrefixPosition preambleMarker = new PrefixPosition(new Prefix(""), 0);
        prefixPositions.addFirst(preambleMarker);

        // Add a dummy PrefixPosition to represent the end of the string
        PrefixPosition endPositionMarker = new PrefixPosition(new Prefix(""), argsString.length());
        prefixPositions.add(endPositionMarker);

        // Map prefixes to their argument values (if any)
        ArgumentMultimap argMultimap = new ArgumentMultimap();
        for (int i = 0; i < prefixPositions.size() - 1; i++) {
            // Extract and store prefixes and their arguments
            Prefix argPrefix = prefixPositions.get(i).getPrefix();
            String argValue = extractArgumentValue(argsString, prefixPositions.get(i), prefixPositions.get(i + 1));
            argMultimap.put(argPrefix, argValue);
        }

        return argMultimap;
    }

    /**
     * Returns the trimmed value of the argument in the arguments string specified by {@code currentPrefixPosition}.
     * The end position of the value is determined by {@code nextPrefixPosition}.
     */
    private static String extractArgumentValue(String argsString,
                                        PrefixPosition currentPrefixPosition,
                                        PrefixPosition nextPrefixPosition) {
        Prefix prefix = currentPrefixPosition.getPrefix();

        int valueStartPos = currentPrefixPosition.getStartPosition() + prefix.getPrefix().length();
        String value = argsString.substring(valueStartPos, nextPrefixPosition.getStartPosition());

        return value.trim();
    }

    /**
     * Represents a prefix's position in an arguments string.
     */
    private static class PrefixPosition {
        private int startPosition;
        private final Prefix prefix;

        PrefixPosition(Prefix prefix, int startPosition) {
            this.prefix = prefix;
            this.startPosition = startPosition;
        }

        int getStartPosition() {
            return startPosition;
        }

        Prefix getPrefix() {
            return prefix;
        }
    }

}
