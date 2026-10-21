package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.Arrays;
import java.util.List;

import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.Messages;
import seedu.address.model.Model;
import seedu.address.model.person.PersonHasTagPredicate;
import seedu.address.model.tag.Priority;

/**
 * Filters and lists all persons in the address book who are tagged with the given tag.
 * Tag matching is case insensitive.
 */
public class FilterCommand extends Command {

    public static final String COMMAND_WORD = "filter";

    public static final List<String> VALID_TAGS = Arrays.stream(Priority.values()).map(Enum::name).toList();

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Lists all persons tagged with the specified tag "
            + "(case-insensitive).\n"
            + "Parameters: TAG (must be one of " + String.join(", ", VALID_TAGS) + ")\n"
            + "Example: " + COMMAND_WORD + " MEDIUM";

    private final PersonHasTagPredicate predicate;

    public FilterCommand(PersonHasTagPredicate predicate) {
        this.predicate = predicate;
    }

    @Override
    public boolean modifiesData() {
        return false;
    }

    @Override
    public CommandResult execute(Model model) {
        requireNonNull(model);
        model.updateFilteredPersonList(predicate);
        return new CommandResult(
                String.format(Messages.MESSAGE_PERSONS_LISTED_OVERVIEW, model.getFilteredPersonList().size()));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof FilterCommand otherFilterCommand)) {
            return false;
        }

        return predicate.equals(otherFilterCommand.predicate);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("predicate", predicate)
                .toString();
    }
}
