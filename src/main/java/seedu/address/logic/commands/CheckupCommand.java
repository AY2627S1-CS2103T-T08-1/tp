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
            + "INDEX must be a positive displayed index. Use a valid 24-hour time. "
            + "The appointment must not be in the past.\n"
            + "Example: checkup 1 25/12/2030 14:30";
    public static final String MESSAGE_SUCCESS = "Checkup appointment for %1$s: %2$s";
    public static final String MESSAGE_ARCHIVED_CONTACT =
            "Appointments cannot be made with archived contacts. Unarchive the contact to do so.";
    public static final String MESSAGE_BOOKING_CONFLICT =
            "A contact with this booking date and time already exists. "
            + "Only contacts with the same family HP can be booked at the same time.";

    private final Index targetIndex;
    private final String appointmentValue;

    /**
     * Creates a command to set or replace the selected contact's checkup appointment.
     */
    public CheckupCommand(Index targetIndex, CheckupAppointment appointment) {
        this(targetIndex, requireNonNull(appointment).toString());
    }

    /**
     * Creates a command whose date and time are validated after checking the selected contact.
     */
    public CheckupCommand(Index targetIndex, String appointmentValue) {
        this.targetIndex = requireNonNull(targetIndex);
        this.appointmentValue = requireNonNull(appointmentValue);
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        List<Person> displayedPersons = model.getFilteredPersonList();
        if (targetIndex.getZeroBased() >= displayedPersons.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
        }
        Person person = displayedPersons.get(targetIndex.getZeroBased());
        if (person.isArchived()) {
            throw new CommandException(MESSAGE_ARCHIVED_CONTACT);
        }
        final CheckupAppointment appointment;
        try {
            appointment = new CheckupAppointment(appointmentValue);
        } catch (IllegalArgumentException e) {
            throw new CommandException(CheckupAppointment.MESSAGE_CONSTRAINTS + "\n" + MESSAGE_USAGE, e);
        }
        if (appointment.isBeforeNow()) {
            throw new CommandException(CheckupAppointment.MESSAGE_PAST_DATE_TIME);
        }
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
                && targetIndex.equals(otherCommand.targetIndex)
                && appointmentValue.equals(otherCommand.appointmentValue);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).add("targetIndex", targetIndex)
                .add("appointment", appointmentValue).toString();
    }
}
