package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.function.Predicate;

import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.Messages;
import seedu.address.model.Model;
import seedu.address.model.person.Person;

/**
 * Finds and lists persons whose selected field contains any of the argument keywords.
 * Keyword matching is case insensitive and allows partial matches.
 */
public class FindCommand extends Command {

    public static final String COMMAND_WORD = "find";

    public static final String MESSAGE_USAGE = "find /CRITERION INPUT: Finds contacts by a field "
            + "using case-insensitive partial matches.\n"
            + "Criteria: /name, /patientNo, /familyNo, /address, /pEmail, /fEmail, /email, /tag.\n"
            + "Phone input must be one sequence of at least 2 digits; digits must occur consecutively.\n"
            + "Example: find /patientNo 87\n"
            + "Name searches also support: find KEYWORD [MORE_KEYWORDS]...";

    private final Predicate<Person> predicate;

    /**
     * Creates a command that filters persons using {@code predicate}.
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
     * Filters the displayed persons and returns the number of matches.
     */
    @Override
    public CommandResult execute(Model model) {
        requireNonNull(model);
        model.updateFilteredPersonList(predicate);
        return new CommandResult(
                String.format(Messages.MESSAGE_PERSONS_LISTED_OVERVIEW, model.getFilteredPersonList().size()));
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
