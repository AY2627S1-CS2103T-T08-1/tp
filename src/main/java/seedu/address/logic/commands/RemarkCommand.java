package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.logging.Logger;

import seedu.address.commons.core.LogsCenter;
import seedu.address.commons.core.index.Index;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.person.Person;
import seedu.address.model.person.Remark;

/**
 * Adds, edits or deletes the numbered notes in the remark of a person identified using their displayed index.
 */
public class RemarkCommand extends Command {

    public static final String COMMAND_WORD = "remark";

    public static final String KEYWORD_EDIT = "/edit";
    public static final String KEYWORD_DELETE = "/delete";
    public static final String KEYWORD_CLEAR = "/clear";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Adds, edits or deletes the notes of the person identified by the index number used in the "
            + "displayed person list.\n"
            + "Parameters: INDEX NOTE, or INDEX " + KEYWORD_EDIT + " NOTE_NUMBER NOTE, or INDEX " + KEYWORD_DELETE
            + " NOTE_NUMBER, or INDEX " + KEYWORD_CLEAR + " (INDEX and NOTE_NUMBER must be positive integers)\n"
            + "Example: " + COMMAND_WORD + " 1 Allergic to penicillin";

    public static final String MESSAGE_ADD_SUCCESS = "Added note %1$d to %2$s: %3$s";
    public static final String MESSAGE_EDIT_SUCCESS = "Edited note %1$d of %2$s: %3$s";
    public static final String MESSAGE_DELETE_SUCCESS = "Deleted note %1$d of %2$s: %3$s";
    public static final String MESSAGE_CLEAR_SUCCESS = "Cleared all notes of %1$s.";
    public static final String MESSAGE_NOTE_NOT_FOUND = "There is no note %1$d. %2$s has %3$d note(s).";
    public static final String MESSAGE_DUPLICATE_NOTE = "%1$s already has this note (note %2$d).";

    private static final Logger logger = LogsCenter.getLogger(RemarkCommand.class);

    /** The ways a remark can be changed. */
    private enum Action { ADD, EDIT, DELETE, CLEAR }

    private final Index personIndex;
    private final Action action;
    private final Index noteIndex;
    private final String note;

    private RemarkCommand(Index personIndex, Action action, Index noteIndex, String note) {
        requireNonNull(personIndex);
        this.personIndex = personIndex;
        this.action = action;
        this.noteIndex = noteIndex;
        this.note = note;
    }

    /**
     * Creates a command that adds {@code note} after the last note of the person at {@code personIndex}.
     */
    public static RemarkCommand add(Index personIndex, String note) {
        requireNonNull(note);
        return new RemarkCommand(personIndex, Action.ADD, null, note);
    }

    /**
     * Creates a command that replaces note {@code noteIndex} of the person at {@code personIndex} with {@code note}.
     */
    public static RemarkCommand edit(Index personIndex, Index noteIndex, String note) {
        requireNonNull(noteIndex);
        requireNonNull(note);
        return new RemarkCommand(personIndex, Action.EDIT, noteIndex, note);
    }

    /**
     * Creates a command that deletes note {@code noteIndex} of the person at {@code personIndex}.
     */
    public static RemarkCommand delete(Index personIndex, Index noteIndex) {
        requireNonNull(noteIndex);
        return new RemarkCommand(personIndex, Action.DELETE, noteIndex, null);
    }

    /**
     * Creates a command that deletes all notes of the person at {@code personIndex}.
     */
    public static RemarkCommand clear(Index personIndex) {
        return new RemarkCommand(personIndex, Action.CLEAR, null, null);
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        List<Person> lastShownList = model.getFilteredPersonList();

        if (personIndex.getZeroBased() >= lastShownList.size()) {
            logger.fine("Remark rejected: index " + personIndex.getOneBased() + " is not in the displayed list");
            throw new CommandException(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
        }

        Person personToChange = lastShownList.get(personIndex.getZeroBased());
        Remark remark = personToChange.getRemark();

        if (noteIndex != null && !remark.hasNote(noteIndex)) {
            logger.fine("Remark rejected: note " + noteIndex.getOneBased() + " does not exist");
            throw new CommandException(String.format(MESSAGE_NOTE_NOT_FOUND, noteIndex.getOneBased(),
                    personToChange.getName(), remark.getNotes().size()));
        }

        requireNoteNotAlreadyThere(personToChange, remark);

        List<String> updatedNotes = switch (action) {
            case ADD -> remark.withNoteAdded(note);
            case EDIT -> remark.withNoteReplaced(noteIndex, note);
            case DELETE -> remark.withNoteRemoved(noteIndex);
            case CLEAR -> List.of();
        };

        if (!Remark.isWithinLengthLimit(updatedNotes)) {
            logger.fine("Remark rejected: the notes would be too long");
            throw new CommandException(Remark.MESSAGE_TOO_LONG);
        }

        Person changedPerson = personToChange.withRemark(new Remark(updatedNotes));
        model.setPersonKeepingDisplayed(personToChange, changedPerson);
        logger.info("Changed the remark of the contact at index " + personIndex.getOneBased() + " (" + action + ")");
        return new CommandResult(getSuccessMessage(personToChange, updatedNotes));
    }

    /**
     * Throws a {@code CommandException} if the person already has the note being added or edited in,
     * ignoring letter case. Editing a note to itself, e.g. to change its letter case, is allowed.
     */
    private void requireNoteNotAlreadyThere(Person personToChange, Remark remark) throws CommandException {
        if (note == null) {
            return;
        }
        Optional<Index> sameNote = remark.findNoteIgnoringCase(note);
        if (sameNote.isPresent() && !sameNote.get().equals(noteIndex)) {
            logger.fine("Remark rejected: the person already has this note");
            throw new CommandException(String.format(MESSAGE_DUPLICATE_NOTE, personToChange.getName(),
                    sameNote.get().getOneBased()));
        }
    }

    private String getSuccessMessage(Person personToChange, List<String> updatedNotes) {
        return switch (action) {
            case ADD -> String.format(MESSAGE_ADD_SUCCESS, updatedNotes.size(), personToChange.getName(), note);
            case EDIT -> String.format(MESSAGE_EDIT_SUCCESS, noteIndex.getOneBased(), personToChange.getName(), note);
            case DELETE -> String.format(MESSAGE_DELETE_SUCCESS, noteIndex.getOneBased(), personToChange.getName(),
                    personToChange.getRemark().getNotes().get(noteIndex.getZeroBased()));
            case CLEAR -> String.format(MESSAGE_CLEAR_SUCCESS, personToChange.getName());
        };
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof RemarkCommand otherRemarkCommand)) {
            return false;
        }

        return personIndex.equals(otherRemarkCommand.personIndex)
                && action == otherRemarkCommand.action
                && Objects.equals(noteIndex, otherRemarkCommand.noteIndex)
                && Objects.equals(note, otherRemarkCommand.note);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("personIndex", personIndex)
                .add("action", action)
                .add("noteIndex", noteIndex)
                .add("note", note)
                .toString();
    }
}
