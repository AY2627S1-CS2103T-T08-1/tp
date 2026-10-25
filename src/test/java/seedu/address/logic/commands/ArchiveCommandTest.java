package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.logic.parser.AddressBookParser;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.CheckupAppointment;
import seedu.address.model.person.Person;
import seedu.address.model.person.Remark;
import seedu.address.testutil.PersonBuilder;

public class ArchiveCommandTest {
    private final AddressBookParser parser = new AddressBookParser();
    private final Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());

    @Test
    public void equals_comparedObjects_returnsExpectedResult() {
        ArchiveCommand command = new ArchiveCommand(Index.fromOneBased(1));
        ArchiveCommand sameIndexCommand = new ArchiveCommand(Index.fromOneBased(1));
        ArchiveCommand differentIndexCommand = new ArchiveCommand(Index.fromOneBased(2));

        assertTrue(command.equals(command));
        assertTrue(command.equals(sameIndexCommand));
        assertTrue(sameIndexCommand.equals(command));
        assertFalse(command.equals(differentIndexCommand));
        assertFalse(command.equals(new DeleteCommand(Index.fromOneBased(1))));
        assertFalse(command.equals(null));
    }

    @Test
    public void toString_containsTargetIndex() {
        Index targetIndex = Index.fromOneBased(3);
        ArchiveCommand command = new ArchiveCommand(targetIndex);
        String expected = ArchiveCommand.class.getCanonicalName() + "{targetIndex=" + targetIndex + "}";

        assertEquals(expected, command.toString());
    }

    @Test
    public void execute_lastDisplayedContact_archivesOnlyTarget() {
        int lastIndex = model.getFilteredPersonList().size();
        Person target = model.getFilteredPersonList().get(lastIndex - 1);
        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.setPerson(target, target.copyWithArchivedStatus(true));

        String expectedMessage = String.format(ArchiveCommand.MESSAGE_ARCHIVE_PERSON_SUCCESS, Messages.format(target));
        assertCommandSuccess(new ArchiveCommand(Index.fromOneBased(lastIndex)), model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_onlyContact_leavesActiveListEmpty() throws Exception {
        Person target = model.getFilteredPersonList().get(0);
        AddressBook addressBook = new AddressBook();
        addressBook.addPerson(target);
        Model singleContactModel = new ModelManager(addressBook, new UserPrefs());

        new ArchiveCommand(Index.fromOneBased(1)).execute(singleContactModel);

        assertTrue(singleContactModel.getFilteredPersonList().isEmpty());
        assertEquals(List.of(target.copyWithArchivedStatus(true)), singleContactModel.getAddressBook().getPersonList());
        new ListCommand(true).execute(singleContactModel);
        assertEquals(List.of(target.copyWithArchivedStatus(true)), singleContactModel.getFilteredPersonList());
    }

    @Test
    public void execute_sortedList_usesDisplayedIndex() throws Exception {
        model.updateSortedPersonList(Comparator.comparing((Person person) -> person.getName().fullName).reversed());
        Person target = model.getFilteredPersonList().get(0);
        List<Person> remainingPersons = new ArrayList<>(model.getFilteredPersonList());
        remainingPersons.remove(0);

        new ArchiveCommand(Index.fromOneBased(1)).execute(model);

        assertEquals(remainingPersons, model.getFilteredPersonList());
        new ListCommand(true).execute(model);
        assertEquals(List.of(target.copyWithArchivedStatus(true)), model.getFilteredPersonList());
    }

    @Test
    public void execute_repeatedFirstIndex_archivesAllContactsWithoutDeleting() throws Exception {
        List<Person> originalPersons = List.copyOf(model.getFilteredPersonList());
        ArchiveCommand command = new ArchiveCommand(Index.fromOneBased(1));
        for (int i = 0; i < originalPersons.size(); i++) {
            assertEquals(originalPersons.get(i), model.getFilteredPersonList().get(0));
            command.execute(model);
            assertEquals(originalPersons.size() - i - 1, model.getFilteredPersonList().size());
        }

        assertCommandFailure(command, model, ArchiveCommand.MESSAGE_EMPTY_LIST);
        new ListCommand(true).execute(model);
        assertEquals(originalPersons.stream().map(person -> person.copyWithArchivedStatus(true)).toList(),
                model.getFilteredPersonList());
    }

    @Test
    public void execute_indexOutsideFilteredList_preservesModel() {
        Person target = model.getFilteredPersonList().get(0);
        model.updateFilteredPersonList(target::equals);

        assertCommandFailure(new ArchiveCommand(Index.fromOneBased(2)), model,
                String.format(ArchiveCommand.MESSAGE_INDEX_OUT_OF_RANGE, 1));
    }

    @Test
    public void execute_emptySearchResults_preservesStoredContacts() {
        model.updateFilteredPersonList(person -> false);

        assertCommandFailure(new ArchiveCommand(Index.fromOneBased(1)), model, ArchiveCommand.MESSAGE_EMPTY_LIST);
    }

    @Test
    public void execute_emptyAddressBook_reportsEmptyList() {
        assertCommandFailure(new ArchiveCommand(Index.fromOneBased(1)), new ModelManager(),
                ArchiveCommand.MESSAGE_EMPTY_LIST);
    }

    @Test
    public void execute_alreadyArchivedContact_preservesModel() throws Exception {
        new ArchiveCommand(Index.fromOneBased(1)).execute(model);
        new ListCommand(true).execute(model);

        assertCommandFailure(new ArchiveCommand(Index.fromOneBased(1)), model, ArchiveCommand.MESSAGE_ALREADY_ARCHIVED);
    }

    @Test
    public void execute_contactWithAppointmentAndRemarks_preservesDetails() throws Exception {
        Person original = model.getFilteredPersonList().get(0);
        Person detailed = new PersonBuilder(original).withCheckupAppointment("01/01/2000 09:05")
                .withRemarks("Needs assistance").build();
        model.setPerson(original, detailed);
        parser.parseCommand("archive 1").execute(model);
        parser.parseCommand("list archive").execute(model);
        Person archived = model.getFilteredPersonList().get(0);
        assertTrue(archived.isArchived());
        assertEquals(detailed.getCheckupAppointment(), archived.getCheckupAppointment());
        assertEquals(detailed.getRemark(), archived.getRemark());
        assertEquals(detailed, archived.copyWithArchivedStatus(false));
    }

    @Test
    public void execute_updateArchivedDetails_preservesArchivedStatus() throws Exception {
        parser.parseCommand("archive 1").execute(model);
        parser.parseCommand("list archive").execute(model);
        Remark remark = new Remark(List.of("Needs assistance"));
        RemarkCommand.add(Index.fromOneBased(1), "Needs assistance").execute(model);
        CheckupAppointment appointment = new CheckupAppointment(
                LocalDate.now().plusDays(1).format(DateTimeFormatter.ofPattern("dd/MM/uuuu")) + " 09:00");
        new CheckupCommand(Index.fromOneBased(1), appointment).execute(model);
        parser.parseCommand("list archive").execute(model);
        assertEquals(1, model.getFilteredPersonList().size());
        Person archived = model.getFilteredPersonList().get(0);
        assertTrue(archived.isArchived());
        assertEquals(remark, archived.getRemark());
        assertEquals(appointment, archived.getCheckupAppointment().orElseThrow());
    }

    @Test
    public void execute_archiveAndSwitchLists_preservesContactDetails() throws Exception {
        Person original = model.getFilteredPersonList().get(2);
        int originalSize = model.getFilteredPersonList().size();
        Command command = parser.parseCommand("archive 3");
        assertTrue(command.modifiesData());
        command.execute(model);
        assertEquals(originalSize - 1, model.getFilteredPersonList().size());
        assertFalse(model.getFilteredPersonList().contains(original));
        assertEquals(originalSize, model.getAddressBook().getPersonList().size());

        Command listArchive = parser.parseCommand("  list archive  ");
        assertFalse(listArchive.modifiesData());
        assertEquals(ListCommand.MESSAGE_ARCHIVE_SUCCESS, listArchive.execute(model).getFeedbackToUser());
        assertEquals(List.of(original.copyWithArchivedStatus(true)), model.getFilteredPersonList());
        listArchive.execute(model);
        assertEquals(1, model.getFilteredPersonList().size());
        parser.parseCommand("list").execute(model);
        assertEquals(originalSize - 1, model.getFilteredPersonList().size());
    }

    @Test
    public void execute_filteredList_usesDisplayedIndex() throws Exception {
        parser.parseCommand("find Meier").execute(model);
        Person target = model.getFilteredPersonList().get(1);
        parser.parseCommand("archive 2").execute(model);
        assertEquals(1, model.getFilteredPersonList().size());
        parser.parseCommand("find Meier").execute(model);
        assertEquals(1, model.getFilteredPersonList().size());
        parser.parseCommand("list archive").execute(model);
        assertEquals(List.of(target.copyWithArchivedStatus(true)), model.getFilteredPersonList());
    }

    @Test
    public void execute_invalidIndexOrAlreadyArchived_preservesData() throws Exception {
        var original = getTypicalAddressBook();
        for (String input : List.of("archive 99", "archive 9999999999999999999999")) {
            assertThrows(CommandException.class, () -> parser.parseCommand(input).execute(model));
            assertEquals(original, model.getAddressBook());
        }
        parser.parseCommand("list archive").execute(model);
        assertThrows(CommandException.class, () -> parser.parseCommand("archive 1").execute(model));
        parser.parseCommand("list").execute(model);
        parser.parseCommand("archive 1").execute(model);
        parser.parseCommand("list archive").execute(model);
        Person archived = model.getFilteredPersonList().get(0);
        CommandException exception =
                assertThrows(CommandException.class, () -> parser.parseCommand("archive 1").execute(model));
        assertEquals(ArchiveCommand.MESSAGE_ALREADY_ARCHIVED, exception.getMessage());
        assertEquals(List.of(archived), model.getFilteredPersonList());
    }

    @Test
    public void parse_invalidArguments_throwsParseException() {
        for (String input : List.of("archive", "archive 0", "archive -1", "archive abc", "archive 1 2",
                "archive 1.5", "list archive extra", "list Archive")) {
            assertThrows(ParseException.class, () -> parser.parseCommand(input));
        }
    }

    @Test
    public void execute_editArchivedContact_preservesArchivedStatus() throws Exception {
        parser.parseCommand("archive 1").execute(model);
        parser.parseCommand("list archive").execute(model);
        parser.parseCommand("edit 1 /name Updated Name").execute(model);
        parser.parseCommand("list archive").execute(model);
        assertEquals(1, model.getFilteredPersonList().size());
        assertEquals("Updated Name", model.getFilteredPersonList().get(0).getName().fullName);
        assertTrue(model.getFilteredPersonList().get(0).isArchived());
    }
}
