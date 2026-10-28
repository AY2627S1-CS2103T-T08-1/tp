package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ADDRESS;
import static seedu.address.logic.parser.CliSyntax.PREFIX_FAMILY_EMAIL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_FAMILY_PHONE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_LEGACY_EMAIL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PATIENT_EMAIL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PATIENT_PHONE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_REMARK;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TAG;

import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.Messages;
import seedu.address.model.Model;
import seedu.address.model.person.Person;

/**
 * Finds and lists persons whose selected field contains the search phrase.
 * Matching is case insensitive and allows partial matches.
 */
public class FindCommand extends Command {

    public static final String COMMAND_WORD = "find";
    public static final String MESSAGE_NO_MATCHES_HINT = "Use 'list archive' to search through archived contacts";

    public static final String MESSAGE_CRITERIA = "Criteria: "
            + Stream.of(PREFIX_NAME, PREFIX_PATIENT_PHONE, PREFIX_FAMILY_PHONE, PREFIX_ADDRESS,
                    PREFIX_PATIENT_EMAIL, PREFIX_FAMILY_EMAIL, PREFIX_LEGACY_EMAIL, PREFIX_TAG)
                    .map(Object::toString).collect(Collectors.joining(", ")) + ".";

    public static final String MESSAGE_USAGE = "Use either find KEYWORD or find /CRITERION INPUT.\n"
            + "find /CRITERION INPUT: Finds contacts by a field "
            + "using case-insensitive partial matches.\n"
            + MESSAGE_CRITERIA + "\n"
            + "Criteria: " + Stream.of(PREFIX_NAME, PREFIX_PATIENT_PHONE, PREFIX_FAMILY_PHONE, PREFIX_ADDRESS,
                    PREFIX_PATIENT_EMAIL, PREFIX_FAMILY_EMAIL, PREFIX_LEGACY_EMAIL, PREFIX_TAG, PREFIX_REMARK)
                    .map(Object::toString).collect(Collectors.joining(", ")) + ".\n"
            + "Phone input must be one sequence of at least 2 digits; digits must occur consecutively.\n"
            + "Remark input matches a case-insensitive phrase within any one care note.\n"
            + "Example: " + COMMAND_WORD + " " + PREFIX_PATIENT_PHONE + " 87\n"
            + "Text input is matched as one phrase, with repeated spaces treated as a single space.";

    private final Predicate<Person> predicate;

    /**
     * Creates a command that filters people using {@code predicate}.
     */
    public FindCommand(Predicate<Person> predicate) {
        this.predicate = requireNonNull(predicate);
    }

    /**
     * Returns false because finding persons only changes the displayed list.
     */
    @Override
    public boolean modifiesData() {
        return false;
    }

    /**
     * Searches all contacts within the current active or archive scope, replacing previous search criteria.
     * Returns the number of matches.
     */
    @Override
    public CommandResult execute(Model model) {
        requireNonNull(model);
        model.filterPersonsInCurrentList(predicate);
        int matchCount = model.getFilteredPersonList().size();
        String feedback = String.format(Messages.MESSAGE_PERSONS_LISTED_OVERVIEW, matchCount);
        if (matchCount == 0) {
            feedback += "\n" + MESSAGE_NO_MATCHES_HINT;
        }
        return new CommandResult(feedback);
    }

    /**
     * Returns whether {@code other} is a find command with the same predicate.
     */
    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof FindCommand otherFindCommand)) {
            return false;
        }

        return predicate.equals(otherFindCommand.predicate);
    }

    /**
     * Returns a string representation of this command and its predicate.
     */
    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("predicate", predicate)
                .toString();
    }
}
