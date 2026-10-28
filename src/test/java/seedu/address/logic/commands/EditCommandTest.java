package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.DESC_AMY;
import static seedu.address.logic.commands.CommandTestUtil.DESC_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_ADDRESS_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_EMAIL_AMY;
import static seedu.address.logic.commands.CommandTestUtil.VALID_EMAIL_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_FAMILY_EMAIL_AMY;
import static seedu.address.logic.commands.CommandTestUtil.VALID_FAMILY_EMAIL_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_NAME_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_PHONE_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_TAG_LOW;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.logic.commands.CommandTestUtil.showPersonAtIndex;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;
import static seedu.address.testutil.TypicalIndexes.INDEX_SECOND_PERSON;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.EditCommand.EditPersonDescriptor;
import seedu.address.logic.parser.EditCommandParser;
import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.Person;
import seedu.address.testutil.EditPersonDescriptorBuilder;
import seedu.address.testutil.PersonBuilder;

/**
 * Contains integration tests (interaction with the Model) and unit tests for EditCommand.
 */
public class EditCommandTest {

    private Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());

    @Test
    public void execute_unchangedTag_reportsNoChange() throws Exception {
        for (String priority : new String[] {"", "HIGH", "MEDIUM", "LOW"}) {
            Person person = priority.isEmpty() ? new PersonBuilder().withTags().build()
                    : new PersonBuilder().withTags(priority).build();
            Model tagModel = new ModelManager();
            tagModel.addPerson(person);
            Model expectedModel = new ModelManager(tagModel.getAddressBook(), new UserPrefs());
            EditCommand command = new EditCommandParser().parse("1 /tag " + priority.toLowerCase());

            assertCommandSuccess(command, tagModel, "No change to tag", expectedModel);
        }
    }

    @Test
    public void execute_changedTag_reportsEditedPerson() throws Exception {
        for (String priority : new String[] {"", "HIGH", "LOW"}) {
            Person person = new PersonBuilder().withTags("MEDIUM").build();
            Person editedPerson = priority.isEmpty() ? new PersonBuilder(person).withTags().build()
                    : new PersonBuilder(person).withTags(priority).build();
            Model tagModel = new ModelManager();
            tagModel.addPerson(person);
            Model expectedModel = new ModelManager();
            expectedModel.addPerson(editedPerson);
            EditCommand command = new EditCommandParser().parse("1 /tag " + priority);

            assertCommandSuccess(command, tagModel,
                    String.format(EditCommand.MESSAGE_EDIT_PERSON_SUCCESS, Messages.format(editedPerson)),
                    expectedModel);
        }
    }

    @Test
    public void execute_unchangedTagWithChangedName_editsName() throws Exception {
        Person person = new PersonBuilder().withTags("MEDIUM").build();
        Person editedPerson = new PersonBuilder(person).withName(VALID_NAME_BOB).build();
        Model tagModel = new ModelManager();
        tagModel.addPerson(person);
        Model expectedModel = new ModelManager();
        expectedModel.addPerson(editedPerson);
        EditCommand command = new EditCommandParser().parse("1 /tag MEDIUM /name " + VALID_NAME_BOB);

        assertCommandSuccess(command, tagModel,
                String.format(EditCommand.MESSAGE_EDIT_PERSON_SUCCESS, Messages.format(editedPerson)), expectedModel);
    }

    @Test
    public void execute_duplicatePatientPhoneWithUnchangedName_failure() {
        Person first = model.getFilteredPersonList().get(0);
        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder()
                .withPhone(first.getPatientNo().value).build();
        assertCommandFailure(new EditCommand(INDEX_SECOND_PERSON, descriptor), model,
                EditCommand.MESSAGE_DUPLICATE_PERSON);
    }

    @Test
    public void execute_sameNameWithDifferentAddress_success() throws Exception {
        Person first = model.getFilteredPersonList().get(0);
        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder()
                .withName(first.getName().fullName).build();
        new EditCommand(INDEX_SECOND_PERSON, descriptor).execute(model);
        assertEquals(first.getName(), model.getAddressBook().getPersonList().get(1).getName());
    }

    @Test
    public void execute_sameNameAndNormalizedAddress_failure() {
        Person first = model.getFilteredPersonList().get(0);
        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder()
                .withName(first.getName().fullName)
                .withAddress(first.getAddress().value.toUpperCase().replace(" ", "")).build();
        assertCommandFailure(new EditCommand(INDEX_SECOND_PERSON, descriptor), model,
                Person.MESSAGE_NAME_ADDRESS_CONFLICT);
    }

    @Test
    public void execute_duplicatePatientEmailWithUnchangedPhone_failure() {
        Person first = model.getFilteredPersonList().get(0);
        model.setPerson(first, new PersonBuilder(first).withEmail("alice@example.com").build());
        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder()
                .withPatientEmail("ALICE@example.com").build();
        assertCommandFailure(new EditCommand(INDEX_SECOND_PERSON, descriptor), model,
                EditCommand.MESSAGE_DUPLICATE_PERSON);
    }

    @Test
    public void execute_otherFields_preservesCheckupAppointment() throws Exception {
        Model checkupModel = new ModelManager();
        Person scheduled = new PersonBuilder().withCheckupAppointment("01/01/2030 12:00").build();
        checkupModel.addPerson(scheduled);
        new EditCommand(INDEX_FIRST_PERSON, new EditPersonDescriptorBuilder().withName(VALID_NAME_BOB).build())
                .execute(checkupModel);
        assertEquals(new PersonBuilder(scheduled).withName(VALID_NAME_BOB).build(),
                checkupModel.getAddressBook().getPersonList().get(0));
    }

    @Test
    public void execute_archivedPerson_keepsArchiveListShown() throws Exception {
        Person active = new PersonBuilder().withName(VALID_NAME_BOB).withPhone(VALID_PHONE_BOB)
                .withPatientEmail(VALID_EMAIL_BOB).build();
        Person archived = new PersonBuilder().build().copyWithArchivedStatus(true);
        Model archiveModel = new ModelManager();
        archiveModel.addPerson(active);
        archiveModel.addPerson(archived);
        archiveModel.setArchiveScope(true);

        new EditCommand(INDEX_FIRST_PERSON, new EditPersonDescriptorBuilder().withAddress(VALID_ADDRESS_BOB).build())
                .execute(archiveModel);

        Person editedArchived = new PersonBuilder(archived).withAddress(VALID_ADDRESS_BOB).build();
        assertTrue(editedArchived.isArchived());
        assertEquals(List.of(editedArchived), archiveModel.getFilteredPersonList());
    }

    @Test
    public void execute_activePerson_showsActiveList() throws Exception {
        Person active = new PersonBuilder().withName(VALID_NAME_BOB).withPhone(VALID_PHONE_BOB)
                .withPatientEmail(VALID_EMAIL_BOB).build();
        Person archived = new PersonBuilder().build().copyWithArchivedStatus(true);
        Model archiveModel = new ModelManager();
        archiveModel.addPerson(active);
        archiveModel.addPerson(archived);
        archiveModel.setArchiveScope(false);

        new EditCommand(INDEX_FIRST_PERSON, new EditPersonDescriptorBuilder().withAddress(VALID_ADDRESS_BOB).build())
                .execute(archiveModel);

        Person editedActive = new PersonBuilder(active).withAddress(VALID_ADDRESS_BOB).build();
        assertEquals(List.of(editedActive), archiveModel.getFilteredPersonList());
    }

    @Test
    public void execute_samePatientPhoneAsArchivedContact_mentionsArchive() {
        Person archived = model.getFilteredPersonList().get(INDEX_SECOND_PERSON.getZeroBased());
        model.setPerson(archived, archived.copyWithArchivedStatus(true));
        EditCommand editCommand = new EditCommand(INDEX_FIRST_PERSON,
                new EditPersonDescriptorBuilder().withPhone(archived.getPatientNo().value).build());

        assertCommandFailure(editCommand, model,
                EditCommand.MESSAGE_DUPLICATE_PERSON + " " + Messages.MESSAGE_CLASHING_PERSON_ARCHIVED);
    }

    @Test
    public void execute_allFieldsSpecifiedUnfilteredList_success() {
        Person editedPerson = new PersonBuilder().build();
        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder(editedPerson).build();
        EditCommand editCommand = new EditCommand(INDEX_FIRST_PERSON, descriptor);

        String expectedMessage = String.format(EditCommand.MESSAGE_EDIT_PERSON_SUCCESS, Messages.format(editedPerson));

        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        expectedModel.setPerson(model.getFilteredPersonList().get(0), editedPerson);

        assertCommandSuccess(editCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_personWithRemark_keepsRemark() {
        Person firstPerson = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        Person withRemark = new PersonBuilder(firstPerson).withRemarks("Allergic to penicillin").build();
        model.setPerson(firstPerson, withRemark);

        Person editedPerson = new PersonBuilder(withRemark).withPhone(VALID_PHONE_BOB).build();
        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder().withPhone(VALID_PHONE_BOB).build();
        EditCommand editCommand = new EditCommand(INDEX_FIRST_PERSON, descriptor);

        String expectedMessage = String.format(EditCommand.MESSAGE_EDIT_PERSON_SUCCESS, Messages.format(editedPerson));

        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        expectedModel.setPerson(withRemark, editedPerson);

        assertCommandSuccess(editCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_someFieldsSpecifiedUnfilteredList_success() {
        Index indexLastPerson = Index.fromOneBased(model.getFilteredPersonList().size());
        Person lastPerson = model.getFilteredPersonList().get(indexLastPerson.getZeroBased());

        PersonBuilder personInList = new PersonBuilder(lastPerson);
        Person editedPerson = personInList.withName(VALID_NAME_BOB).withPhone(VALID_PHONE_BOB)
                .withTags(VALID_TAG_LOW).build();

        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder().withName(VALID_NAME_BOB)
                .withPhone(VALID_PHONE_BOB).withTags(VALID_TAG_LOW).build();
        EditCommand editCommand = new EditCommand(indexLastPerson, descriptor);

        String expectedMessage = String.format(EditCommand.MESSAGE_EDIT_PERSON_SUCCESS, Messages.format(editedPerson));

        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        expectedModel.setPerson(lastPerson, editedPerson);

        assertCommandSuccess(editCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_nameOnly_keepsBothEmails() {
        Person personWithEmails = new PersonBuilder().withPatientEmail(VALID_EMAIL_AMY)
                .withFamilyEmail(VALID_FAMILY_EMAIL_AMY).build();
        Model modelWithEmails = new ModelManager(new AddressBook(), new UserPrefs());
        modelWithEmails.addPerson(personWithEmails);

        Person editedPerson = new PersonBuilder(personWithEmails).withName(VALID_NAME_BOB).build();
        EditCommand editCommand = new EditCommand(INDEX_FIRST_PERSON,
                new EditPersonDescriptorBuilder().withName(VALID_NAME_BOB).build());

        String expectedMessage = String.format(EditCommand.MESSAGE_EDIT_PERSON_SUCCESS, Messages.format(editedPerson));

        Model expectedModel = new ModelManager(new AddressBook(), new UserPrefs());
        expectedModel.addPerson(editedPerson);

        assertCommandSuccess(editCommand, modelWithEmails, expectedMessage, expectedModel);
    }

    @Test
    public void execute_removePatientEmail_keepsFamilyEmail() {
        Person personWithEmails = new PersonBuilder().withPatientEmail(VALID_EMAIL_AMY)
                .withFamilyEmail(VALID_FAMILY_EMAIL_AMY).build();
        Model modelWithEmails = new ModelManager(new AddressBook(), new UserPrefs());
        modelWithEmails.addPerson(personWithEmails);

        Person editedPerson = new PersonBuilder(personWithEmails).withoutEmail()
                .withFamilyEmail(VALID_FAMILY_EMAIL_AMY).build();
        EditCommand editCommand = new EditCommand(INDEX_FIRST_PERSON,
                new EditPersonDescriptorBuilder().withoutPatientEmail().build());

        String expectedMessage = String.format(EditCommand.MESSAGE_EDIT_PERSON_SUCCESS, Messages.format(editedPerson));

        Model expectedModel = new ModelManager(new AddressBook(), new UserPrefs());
        expectedModel.addPerson(editedPerson);

        assertCommandSuccess(editCommand, modelWithEmails, expectedMessage, expectedModel);
    }

    @Test
    public void execute_familyEmailOnly_success() {
        Person firstPerson = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        Person editedPerson = new PersonBuilder(firstPerson).withFamilyEmail(VALID_FAMILY_EMAIL_BOB).build();
        EditCommand editCommand = new EditCommand(INDEX_FIRST_PERSON,
                new EditPersonDescriptorBuilder().withFamilyEmail(VALID_FAMILY_EMAIL_BOB).build());

        String expectedMessage = String.format(EditCommand.MESSAGE_EDIT_PERSON_SUCCESS, Messages.format(editedPerson));

        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        expectedModel.setPerson(firstPerson, editedPerson);

        assertCommandSuccess(editCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_noFieldSpecifiedUnfilteredList_success() {
        EditCommand editCommand = new EditCommand(INDEX_FIRST_PERSON, new EditPersonDescriptor());
        Person editedPerson = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());

        String expectedMessage = String.format(EditCommand.MESSAGE_EDIT_PERSON_SUCCESS, Messages.format(editedPerson));

        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());

        assertCommandSuccess(editCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_filteredList_success() {
        showPersonAtIndex(model, INDEX_FIRST_PERSON);

        Person personInFilteredList = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        Person editedPerson = new PersonBuilder(personInFilteredList).withName(VALID_NAME_BOB).build();
        EditCommand editCommand = new EditCommand(INDEX_FIRST_PERSON,
                new EditPersonDescriptorBuilder().withName(VALID_NAME_BOB).build());

        String expectedMessage = String.format(EditCommand.MESSAGE_EDIT_PERSON_SUCCESS, Messages.format(editedPerson));

        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        expectedModel.setPerson(model.getFilteredPersonList().get(0), editedPerson);
        // the search results stay on screen, and the edited person stays in them though the name changed
        expectedModel.updateFilteredPersonList(editedPerson::equals);

        assertCommandSuccess(editCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_filteredList_keepsOtherResultsAndOrder() throws Exception {
        model.updateFilteredPersonList(person -> person.getName().fullName.contains("Meier"));
        List<Person> resultsBefore = List.copyOf(model.getFilteredPersonList());

        new EditCommand(INDEX_FIRST_PERSON, new EditPersonDescriptorBuilder().withAddress(VALID_ADDRESS_BOB).build())
                .execute(model);

        Person editedPerson = new PersonBuilder(resultsBefore.get(0)).withAddress(VALID_ADDRESS_BOB).build();
        assertEquals(List.of(editedPerson, resultsBefore.get(1)), model.getFilteredPersonList());
    }

    @Test
    public void execute_duplicatePersonUnfilteredList_failure() {
        Person firstPerson = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder(firstPerson).build();
        EditCommand editCommand = new EditCommand(INDEX_SECOND_PERSON, descriptor);

        assertCommandFailure(editCommand, model, EditCommand.MESSAGE_DUPLICATE_PERSON);
    }

    @Test
    public void execute_duplicatePersonFilteredList_failure() {
        showPersonAtIndex(model, INDEX_FIRST_PERSON);

        // edit person in filtered list into a duplicate in address book
        Person personInList = model.getAddressBook().getPersonList().get(INDEX_SECOND_PERSON.getZeroBased());
        EditCommand editCommand = new EditCommand(INDEX_FIRST_PERSON,
                new EditPersonDescriptorBuilder(personInList).build());

        assertCommandFailure(editCommand, model, EditCommand.MESSAGE_DUPLICATE_PERSON);
    }

    @Test
    public void execute_invalidPersonIndexUnfilteredList_failure() {
        Index outOfBoundIndex = Index.fromOneBased(model.getFilteredPersonList().size() + 1);
        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder().withName(VALID_NAME_BOB).build();
        EditCommand editCommand = new EditCommand(outOfBoundIndex, descriptor);

        assertCommandFailure(editCommand, model, Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
    }

    /**
     * Edit filtered list where index is larger than size of filtered list,
     * but smaller than size of address book
     */
    @Test
    public void execute_invalidPersonIndexFilteredList_failure() {
        showPersonAtIndex(model, INDEX_FIRST_PERSON);
        Index outOfBoundIndex = INDEX_SECOND_PERSON;
        // ensures that outOfBoundIndex is still in bounds of address book list
        assertTrue(outOfBoundIndex.getZeroBased() < model.getAddressBook().getPersonList().size());

        EditCommand editCommand = new EditCommand(outOfBoundIndex,
                new EditPersonDescriptorBuilder().withName(VALID_NAME_BOB).build());

        assertCommandFailure(editCommand, model, Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
    }

    @Test
    public void equals() {
        final EditCommand standardCommand = new EditCommand(INDEX_FIRST_PERSON, DESC_AMY);

        // same values -> returns true
        EditPersonDescriptor copyDescriptor = new EditPersonDescriptor(DESC_AMY);
        EditCommand commandWithSameValues = new EditCommand(INDEX_FIRST_PERSON, copyDescriptor);
        assertTrue(standardCommand.equals(commandWithSameValues));

        // same object -> returns true
        assertTrue(standardCommand.equals(standardCommand));

        // null -> returns false
        assertFalse(standardCommand.equals(null));

        // different types -> returns false
        assertFalse(standardCommand.equals(new ClearCommand()));

        // different index -> returns false
        assertFalse(standardCommand.equals(new EditCommand(INDEX_SECOND_PERSON, DESC_AMY)));

        // different descriptor -> returns false
        assertFalse(standardCommand.equals(new EditCommand(INDEX_FIRST_PERSON, DESC_BOB)));
    }

    @Test
    public void toStringMethod() {
        Index index = Index.fromOneBased(1);
        EditPersonDescriptor editPersonDescriptor = new EditPersonDescriptor();
        EditCommand editCommand = new EditCommand(index, editPersonDescriptor);
        String expected = EditCommand.class.getCanonicalName() + "{index=" + index + ", editPersonDescriptor="
                + editPersonDescriptor + "}";
        assertEquals(expected, editCommand.toString());
    }

}
