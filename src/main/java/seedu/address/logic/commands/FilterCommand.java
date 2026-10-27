package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ADDRESS;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TAG;

import java.util.Arrays;
import java.util.List;

import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.Messages;
import seedu.address.model.Model;
import seedu.address.model.person.PersonMatchesFilterPredicate;
import seedu.address.model.tag.Priority;

/**
 * Filters persons in the current active or archived list by name, tag, and address.
 * Within one criterion, matching any one of the values is enough. Matching is case insensitive.
 */
public class FilterCommand extends Command {

    public static final String COMMAND_WORD = "filter";

    public static final List<String> VALID_TAGS = Arrays.stream(Priority.values()).map(Enum::name).toList();

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Lists all persons matching ALL of the given "
            + "criteria (case-insensitive).\n"
            + "Parameters: [" + PREFIX_NAME + "NAME_KEYWORDS] [" + PREFIX_TAG + "TAG]... ["
            + PREFIX_ADDRESS + "ADDRESS]...\n"
            + "At least one criterion is required.\n"
            + PREFIX_NAME + " takes space-separated keywords; a name containing any of them matches (partial match "
            + "allowed).\n"
            + PREFIX_TAG + " must be one of " + String.join(", ", VALID_TAGS) + "; repeat it to match any of "
            + "several tags.\n"
            + PREFIX_ADDRESS + " takes a phrase; repeat it to match any of several phrases (partial match "
            + "allowed).\n"
            + "Example: " + COMMAND_WORD + " " + PREFIX_NAME + "ali bob " + PREFIX_TAG + "HIGH " + PREFIX_TAG
            + "MEDIUM " + PREFIX_ADDRESS + "clementi";

    private final PersonMatchesFilterPredicate predicate;

    /**
     * Creates a command that filters persons using {@code predicate}.
     */
    public FilterCommand(PersonMatchesFilterPredicate predicate) {
        requireNonNull(predicate);
        this.predicate = predicate;
    }

    @Override
    public boolean modifiesData() {
        return false;
    }

    @Override
    public CommandResult execute(Model model) {
        requireNonNull(model);
        model.filterPersonsInCurrentList(predicate);
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
