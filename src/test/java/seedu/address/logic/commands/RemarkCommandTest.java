package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.logic.commands.CommandTestUtil.showPersonAtIndex;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;
import static seedu.address.testutil.TypicalIndexes.INDEX_SECOND_PERSON;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.Messages;
import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.Person;
import seedu.address.model.person.Remark;
import seedu.address.testutil.PersonBuilder;

public class RemarkCommandTest {

    private static final Index NOTE_FIRST = Index.fromOneBased(1);
    private static final Index NOTE_SECOND = Index.fromOneBased(2);
    private static final String NOTE_ALLERGY = "Allergic to penicillin";
    private static final String NOTE_VISITS = "Visits after 3pm";

    private final Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());

    /**
     * Gives the first person in {@code model} the notes {@code NOTE_ALLERGY} and {@code NOTE_VISITS}.
     */
    private static Person giveFirstPersonTwoNotes(Model model) {
        Person firstPerson = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        Person withNotes = new PersonBuilder(firstPerson).withRemarks(NOTE_ALLERGY, NOTE_VISITS).build();
        model.setPerson(firstPerson, withNotes);
        return withNotes;
    }

    @Test
    public void execute_addExistingNote_throwsCommandException() {
        Person withNotes = giveFirstPersonTwoNotes(model);
        String expectedMessage = String.format(RemarkCommand.MESSAGE_DUPLICATE_NOTE, withNotes.getName(), 2);

        assertCommandFailure(RemarkCommand.add(INDEX_FIRST_PERSON, NOTE_VISITS), model, expectedMessage);
        assertCommandFailure(RemarkCommand.add(INDEX_FIRST_PERSON, NOTE_VISITS.toUpperCase()), model,
                expectedMessage);
    }

    @Test
    public void execute_editNoteToAnotherExistingNote_throwsCommandException() {
        Person withNotes = giveFirstPersonTwoNotes(model);
        String expectedMessage = String.format(RemarkCommand.MESSAGE_DUPLICATE_NOTE, withNotes.getName(), 1);

        assertCommandFailure(RemarkCommand.edit(INDEX_FIRST_PERSON, NOTE_SECOND, NOTE_ALLERGY), model,
                expectedMessage);
    }

    @Test
    public void execute_editNoteToItselfInOtherCase_success() throws Exception {
        Person withNotes = giveFirstPersonTwoNotes(model);
        String upperAllergy = NOTE_ALLERGY.toUpperCase();

        RemarkCommand.edit(INDEX_FIRST_PERSON, NOTE_FIRST, upperAllergy).execute(model);

        assertEquals(List.of(upperAllergy, NOTE_VISITS),
                model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased()).getRemark().getNotes());
    }

    private static Model expectedModelWith(Model model, Person original, Person changed) {
        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        expectedModel.setPerson(original, changed);
        return expectedModel;
    }

    @Test
    public void execute_addNoteUnfilteredList_success() {
        Person firstPerson = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        Person changedPerson = new PersonBuilder(firstPerson).withRemarks(NOTE_ALLERGY).build();
        String expectedMessage = String.format(RemarkCommand.MESSAGE_ADD_SUCCESS, 1, firstPerson.getName(),
                NOTE_ALLERGY);

        assertCommandSuccess(RemarkCommand.add(INDEX_FIRST_PERSON, NOTE_ALLERGY), model, expectedMessage,
                expectedModelWith(model, firstPerson, changedPerson));
    }

    @Test
    public void execute_addNoteToExistingNotes_addsAfterLastNote() {
        Person withNotes = giveFirstPersonTwoNotes(model);
        Person changedPerson = new PersonBuilder(withNotes).withRemarks(NOTE_ALLERGY, NOTE_VISITS, "Diabetic")
                .build();
        String expectedMessage = String.format(RemarkCommand.MESSAGE_ADD_SUCCESS, 3, withNotes.getName(),
                "Diabetic");

        assertCommandSuccess(RemarkCommand.add(INDEX_FIRST_PERSON, "Diabetic"), model, expectedMessage,
                expectedModelWith(model, withNotes, changedPerson));
    }

    @Test
    public void execute_editNote_success() {
        Person withNotes = giveFirstPersonTwoNotes(model);
        Person changedPerson = new PersonBuilder(withNotes).withRemarks(NOTE_ALLERGY, "Visits after 4pm").build();
        String expectedMessage = String.format(RemarkCommand.MESSAGE_EDIT_SUCCESS, 2, withNotes.getName(),
                "Visits after 4pm");

        assertCommandSuccess(RemarkCommand.edit(INDEX_FIRST_PERSON, NOTE_SECOND, "Visits after 4pm"), model,
                expectedMessage, expectedModelWith(model, withNotes, changedPerson));
    }

    @Test
    public void execute_deleteNote_success() {
        Person withNotes = giveFirstPersonTwoNotes(model);
        Person changedPerson = new PersonBuilder(withNotes).withRemarks(NOTE_VISITS).build();
        String expectedMessage = String.format(RemarkCommand.MESSAGE_DELETE_SUCCESS, 1, withNotes.getName(),
                NOTE_ALLERGY);

        assertCommandSuccess(RemarkCommand.delete(INDEX_FIRST_PERSON, NOTE_FIRST), model, expectedMessage,
                expectedModelWith(model, withNotes, changedPerson));
    }

    @Test
    public void execute_clearNotes_success() {
        Person withNotes = giveFirstPersonTwoNotes(model);
        Person changedPerson = withNotes.withRemark(Remark.EMPTY);
        String expectedMessage = String.format(RemarkCommand.MESSAGE_CLEAR_SUCCESS, withNotes.getName());

        assertCommandSuccess(RemarkCommand.clear(INDEX_FIRST_PERSON), model, expectedMessage,
                expectedModelWith(model, withNotes, changedPerson));
    }

    @Test
    public void execute_missingNote_throwsCommandException() {
        Person withNotes = giveFirstPersonTwoNotes(model);
        Index noteThree = Index.fromOneBased(3);
        String expectedMessage = String.format(RemarkCommand.MESSAGE_NOTE_NOT_FOUND, 3, withNotes.getName(), 2);

        assertCommandFailure(RemarkCommand.edit(INDEX_FIRST_PERSON, noteThree, "x"), model, expectedMessage);
        assertCommandFailure(RemarkCommand.delete(INDEX_FIRST_PERSON, noteThree), model, expectedMessage);
    }

    @Test
    public void execute_deleteNoteWithoutNotes_throwsCommandException() {
        Person firstPerson = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        String expectedMessage = String.format(RemarkCommand.MESSAGE_NOTE_NOT_FOUND, 1, firstPerson.getName(), 0);

        assertCommandFailure(RemarkCommand.delete(INDEX_FIRST_PERSON, NOTE_FIRST), model, expectedMessage);
    }

    @Test
    public void execute_notesTooLong_throwsCommandException() {
        giveFirstPersonTwoNotes(model);
        int roomLeft = Remark.MAX_TOTAL_LENGTH - NOTE_ALLERGY.length() - NOTE_VISITS.length();

        assertCommandFailure(RemarkCommand.add(INDEX_FIRST_PERSON, "a".repeat(roomLeft + 1)), model,
                Remark.MESSAGE_TOO_LONG);
        assertCommandFailure(RemarkCommand.edit(INDEX_FIRST_PERSON, NOTE_SECOND,
                "a".repeat(roomLeft + NOTE_VISITS.length() + 1)), model, Remark.MESSAGE_TOO_LONG);
    }

    @Test
    public void execute_notesExactlyAtLimit_success() {
        Person withNotes = giveFirstPersonTwoNotes(model);
        String longNote = "a".repeat(Remark.MAX_TOTAL_LENGTH - NOTE_ALLERGY.length() - NOTE_VISITS.length());
        Person changedPerson = new PersonBuilder(withNotes).withRemarks(NOTE_ALLERGY, NOTE_VISITS, longNote).build();
        String expectedMessage = String.format(RemarkCommand.MESSAGE_ADD_SUCCESS, 3, withNotes.getName(),
                longNote);

        assertCommandSuccess(RemarkCommand.add(INDEX_FIRST_PERSON, longNote), model, expectedMessage,
                expectedModelWith(model, withNotes, changedPerson));
    }

    @Test
    public void execute_invalidPersonIndexUnfilteredList_throwsCommandException() {
        Index outOfBoundIndex = Index.fromOneBased(model.getFilteredPersonList().size() + 1);

        String expectedMessage = Messages.getMessageForIndexNotDisplayed(outOfBoundIndex.getOneBased(),
                model.getFilteredPersonList().size());

        assertCommandFailure(RemarkCommand.add(outOfBoundIndex, NOTE_ALLERGY), model, expectedMessage);
        assertCommandFailure(RemarkCommand.clear(outOfBoundIndex), model, expectedMessage);
    }

    @Test
    public void execute_emptyDisplayedList_throwsCommandException() {
        model.updateFilteredPersonList(Model.PREDICATE_SHOW_NO_PERSONS);

        assertCommandFailure(RemarkCommand.add(INDEX_FIRST_PERSON, NOTE_ALLERGY), model,
                Messages.MESSAGE_EMPTY_DISPLAYED_LIST);
    }

    @Test
    public void execute_invalidPersonIndexFilteredList_throwsCommandException() {
        showPersonAtIndex(model, INDEX_FIRST_PERSON);

        assertCommandFailure(RemarkCommand.add(INDEX_SECOND_PERSON, NOTE_ALLERGY), model,
                Messages.getMessageForIndexNotDisplayed(2, 1));
    }

    @Test
    public void execute_changedNoteNoLongerMatchesSearch_keepsPersonDisplayed() throws Exception {
        Person firstPerson = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        Person secondPerson = model.getFilteredPersonList().get(INDEX_SECOND_PERSON.getZeroBased());
        RemarkCommand.add(INDEX_FIRST_PERSON, NOTE_ALLERGY).execute(model);
        RemarkCommand.add(INDEX_SECOND_PERSON, NOTE_ALLERGY).execute(model);
        model.updateFilteredPersonList(person -> person.getRemark().getNotes().contains(NOTE_ALLERGY));

        RemarkCommand.edit(INDEX_FIRST_PERSON, NOTE_FIRST, NOTE_VISITS).execute(model);

        assertEquals(List.of(new PersonBuilder(firstPerson).withRemarks(NOTE_VISITS).build(),
                new PersonBuilder(secondPerson).withRemarks(NOTE_ALLERGY).build()), model.getFilteredPersonList());
    }

    @Test
    public void execute_filteredList_keepsFilter() {
        showPersonAtIndex(model, INDEX_SECOND_PERSON);
        Person secondPerson = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        Person changedPerson = new PersonBuilder(secondPerson).withRemarks(NOTE_ALLERGY).build();
        String expectedMessage = String.format(RemarkCommand.MESSAGE_ADD_SUCCESS, 1, secondPerson.getName(),
                NOTE_ALLERGY);

        Model expectedModel = expectedModelWith(model, secondPerson, changedPerson);
        showPersonAtIndex(expectedModel, INDEX_SECOND_PERSON);

        assertCommandSuccess(RemarkCommand.add(INDEX_FIRST_PERSON, NOTE_ALLERGY), model, expectedMessage,
                expectedModel);
    }

    @Test
    public void execute_afterView_preservesListAndRefreshesSelectedPerson() throws Exception {
        List<Person> expectedList = new ArrayList<>(model.getFilteredPersonList());
        new ViewCommand(INDEX_SECOND_PERSON).execute(model);
        Person secondPerson = model.getFilteredPersonList().get(INDEX_SECOND_PERSON.getZeroBased());

        RemarkCommand.add(INDEX_SECOND_PERSON, NOTE_ALLERGY).execute(model);
        RemarkCommand.add(INDEX_SECOND_PERSON, NOTE_VISITS).execute(model);

        Person updatedPerson = new PersonBuilder(secondPerson).withRemarks(NOTE_ALLERGY, NOTE_VISITS).build();
        expectedList.set(INDEX_SECOND_PERSON.getZeroBased(), updatedPerson);
        assertEquals(expectedList, model.getFilteredPersonList());
        assertEquals(updatedPerson, model.selectedPersonProperty().get());
    }

    @Test
    public void equals() {
        RemarkCommand addCommand = RemarkCommand.add(INDEX_FIRST_PERSON, NOTE_ALLERGY);

        // same values -> returns true
        assertTrue(addCommand.equals(RemarkCommand.add(INDEX_FIRST_PERSON, NOTE_ALLERGY)));
        assertTrue(RemarkCommand.clear(INDEX_FIRST_PERSON).equals(RemarkCommand.clear(INDEX_FIRST_PERSON)));

        // same object -> returns true
        assertTrue(addCommand.equals(addCommand));

        // null -> returns false
        assertFalse(addCommand.equals(null));

        // different types -> returns false
        assertFalse(addCommand.equals(new ClearCommand()));

        // different person index -> returns false
        assertFalse(addCommand.equals(RemarkCommand.add(INDEX_SECOND_PERSON, NOTE_ALLERGY)));

        // different note -> returns false
        assertFalse(addCommand.equals(RemarkCommand.add(INDEX_FIRST_PERSON, NOTE_VISITS)));

        // different action -> returns false
        assertFalse(addCommand.equals(RemarkCommand.edit(INDEX_FIRST_PERSON, NOTE_FIRST, NOTE_ALLERGY)));
        assertFalse(RemarkCommand.delete(INDEX_FIRST_PERSON, NOTE_FIRST)
                .equals(RemarkCommand.clear(INDEX_FIRST_PERSON)));

        // different note index -> returns false
        assertFalse(RemarkCommand.delete(INDEX_FIRST_PERSON, NOTE_FIRST)
                .equals(RemarkCommand.delete(INDEX_FIRST_PERSON, NOTE_SECOND)));
    }

    @Test
    public void toStringMethod() {
        RemarkCommand command = RemarkCommand.edit(INDEX_FIRST_PERSON, NOTE_SECOND, NOTE_VISITS);
        String expected = RemarkCommand.class.getCanonicalName() + "{personIndex=" + INDEX_FIRST_PERSON
                + ", action=EDIT, noteIndex=" + NOTE_SECOND + ", note=" + NOTE_VISITS + "}";
        assertEquals(expected, command.toString());
    }
}
