package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.logic.parser.AddressBookParser;
import seedu.address.logic.parser.exceptions.ParseException;
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
