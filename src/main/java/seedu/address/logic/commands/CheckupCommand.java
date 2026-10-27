package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.List;

import seedu.address.commons.core.index.Index;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.person.CheckupAppointment;
import seedu.address.model.person.Person;

/**
 * Sets the checkup appointment of the contact at an index in the currently displayed list.
 */
public class CheckupCommand extends Command {
    public static final String COMMAND_WORD = "checkup";
    public static final String MESSAGE_USAGE = "checkup INDEX dd/mm/yyyy HH:mm: Sets a checkup appointment.\n"
            + "INDEX must be a positive displayed index. Use a valid 24-hour time and a date today or later.\n"
            + "Example: checkup 1 25/12/2030 14:30";
    public static final String MESSAGE_SUCCESS = "Checkup appointment for %1$s: %2$s";
    public static final String MESSAGE_BOOKING_CONFLICT =
            "A contact with this booking date and time already exists. "
            + "Only contacts with the same family HP can be booked at the same time.";

    private final Index targetIndex;
    private final CheckupAppointment appointment;

    /**
     * Creates a command to set or replace the selected contact's checkup appointment.
     */
    public CheckupCommand(Index targetIndex, CheckupAppointment appointment) {
        this.targetIndex = requireNonNull(targetIndex);
        this.appointment = requireNonNull(appointment);
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        List<Person> displayedPersons = model.getFilteredPersonList();
        if (targetIndex.getZeroBased() >= displayedPersons.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
        }
        if (appointment.isBeforeToday()) {
            throw new CommandException(CheckupAppointment.MESSAGE_PAST_DATE);
        }
        Person person = displayedPersons.get(targetIndex.getZeroBased());
        boolean hasConflict = model.getAddressBook().getPersonList().stream()
                .anyMatch(existing -> !existing.isArchived() && !existing.equals(person)
                        && existing.getCheckupAppointment().filter(appointment::equals).isPresent()
                        && !existing.getFamilyNo().equals(person.getFamilyNo()));
        if (hasConflict) {
            throw new CommandException(MESSAGE_BOOKING_CONFLICT);
        }
        boolean keepMarked = person.isCheckupMarked()
                && person.getCheckupAppointment().filter(appointment::equals).isPresent();
        Person updatedPerson = new Person(person.getName(), person.getPatientNo(), person.getFamilyNo(),
                person.getPatientEmail().orElse(null), person.getFamilyEmail().orElse(null),
                person.getAddress(), person.getTags(), appointment, person.getRemark(),
                person.isArchived(), keepMarked);
        model.setPerson(person, updatedPerson);
        // A view filter compares all fields, including the appointment being replaced.
        if (!model.getFilteredPersonList().contains(updatedPerson)) {
            model.updateFilteredPersonList(updatedPerson::isSamePerson);
        }
        return new CommandResult(String.format(MESSAGE_SUCCESS, person.getName(), appointment));
    }

    @Override
    public boolean equals(Object other) {
        return other == this || other instanceof CheckupCommand otherCommand
                && targetIndex.equals(otherCommand.targetIndex) && appointment.equals(otherCommand.appointment);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).add("targetIndex", targetIndex).add("appointment", appointment).toString();
    }
}
