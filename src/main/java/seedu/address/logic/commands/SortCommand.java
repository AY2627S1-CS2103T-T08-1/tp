package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import seedu.address.commons.util.ToStringBuilder;
import seedu.address.model.Model;
import seedu.address.model.person.PersonComparators;

/**
 * Changes the order in which persons are displayed. The stored data is not reordered, and the
 * change applies on top of any filter. Use {@code list} to restore the original order.
 */
public class SortCommand extends Command {

    public static final String COMMAND_WORD = "sort";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Sorts the displayed persons.\n"
            + "Parameters: priority [asc|desc] | name [asc|desc] | recent\n"
            + "priority: desc (default) shows HIGH, MEDIUM, LOW; asc shows LOW, MEDIUM, HIGH. "
            + "Persons without a priority tag are always last.\n"
            + "name: asc (default) shows A to Z; desc shows Z to A.\n"
            + "recent: shows the most recently added person first.\n"
            + "Use `list` to restore the original order.\n"
            + "Examples: " + COMMAND_WORD + " priority, " + COMMAND_WORD + " name desc, " + COMMAND_WORD + " recent";

    public static final String MESSAGE_SORTED_PRIORITY_DESC = "Sorted by priority: HIGH to LOW.";
    public static final String MESSAGE_SORTED_PRIORITY_ASC = "Sorted by priority: LOW to HIGH.";
    public static final String MESSAGE_SORTED_NAME_ASC = "Sorted by name: A to Z.";
    public static final String MESSAGE_SORTED_NAME_DESC = "Sorted by name: Z to A.";
    public static final String MESSAGE_SORTED_RECENT = "Sorted by most recently added.";

    /** What the displayed persons are ordered by. */
    public enum Criterion {
        PRIORITY,
        NAME,
        RECENT
    }

    private final Criterion criterion;
    private final boolean ascending;

    /**
     * Creates a command that sorts by {@code criterion}. {@code ascending} is ignored for
     * {@code Criterion.RECENT}, which always shows the newest person first.
     */
    public SortCommand(Criterion criterion, boolean ascending) {
        requireNonNull(criterion);
        this.criterion = criterion;
        this.ascending = criterion != Criterion.RECENT && ascending;
    }

    @Override
    public boolean modifiesData() {
        return false;
    }

    @Override
    public CommandResult execute(Model model) {
        requireNonNull(model);
        return switch (criterion) {
            case PRIORITY -> {
                // descending priority means the most urgent (HIGH) first
                model.updateSortedPersonList(PersonComparators.byPriority(!ascending));
                yield new CommandResult(ascending ? MESSAGE_SORTED_PRIORITY_ASC : MESSAGE_SORTED_PRIORITY_DESC);
            }
            case NAME -> {
                model.updateSortedPersonList(PersonComparators.byName(ascending));
                yield new CommandResult(ascending ? MESSAGE_SORTED_NAME_ASC : MESSAGE_SORTED_NAME_DESC);
            }
            case RECENT -> {
                model.sortPersonsByMostRecent();
                yield new CommandResult(MESSAGE_SORTED_RECENT);
            }
        };
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof SortCommand otherSortCommand)) {
            return false;
        }

        return criterion == otherSortCommand.criterion && ascending == otherSortCommand.ascending;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("criterion", criterion)
                .add("ascending", ascending)
                .toString();
    }
}
