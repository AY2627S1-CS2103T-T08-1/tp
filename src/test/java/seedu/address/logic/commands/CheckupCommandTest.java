package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;

import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.LogicManager;
import seedu.address.logic.Messages;
import seedu.address.logic.parser.AddressBookParser;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.person.CheckupAppointment;
import seedu.address.model.person.NameContainsKeywordsPredicate;
import seedu.address.model.person.Person;
import seedu.address.model.person.PersonHasTagPredicate;
import seedu.address.model.person.PersonMatchesFilterPredicate;
import seedu.address.storage.JsonAddressBookStorage;
import seedu.address.storage.JsonUserPrefsStorage;
import seedu.address.storage.StorageManager;
import seedu.address.testutil.PersonBuilder;

public class CheckupCommandTest {
    @TempDir
    public Path testFolder;

    private final Model model = new ModelManager();
    private final Person alice = new PersonBuilder().withName("Alice").withTags("LOW").build();
    private final Person bob = new PersonBuilder().withName("Bob").withTags("HIGH")
            .withPhone("91234567").withEmail("bob@example.com").build();
    private final CheckupAppointment appointment = appointmentOn(LocalDate.now().plusDays(1));

    @Test
    public void execute_delete_removesAppointmentAndMarkPreservingOtherDetailsAndSelection() throws Exception {
        Person original = new PersonBuilder(alice).withRemarks("Needs assistance").build();
        Person scheduled = new PersonBuilder(original).withCheckupAppointment(appointment.toString())
                .build().withMarkedCheckup();
        model.addPerson(scheduled);
        new ViewCommand(Index.fromOneBased(1)).execute(model);

        Command command = new AddressBookParser().parseCommand("checkup 1 /delete");
        assertTrue(command.modifiesData());
        assertEquals("Deleted checkup appointment for Alice.", command.execute(model).getFeedbackToUser());
        assertEquals(List.of(original), model.getFilteredPersonList());
        assertEquals(List.of(original), model.getAddressBook().getPersonList());
        assertEquals(original, model.selectedPersonProperty().get());
    }

    @Test
    public void execute_deleteInFilteredAndSortedLists_usesDisplayedIndex() throws Exception {
        Person scheduledAlice = new PersonBuilder(alice).withCheckupAppointment(appointment.toString()).build();
        Person scheduledBob = new PersonBuilder(bob).withCheckupAppointment(appointment.toString()).build();
        model.addPerson(scheduledAlice);
        model.addPerson(scheduledBob);
        AddressBookParser parser = new AddressBookParser();
        parser.parseCommand("filter /tag HIGH").execute(model);
        parser.parseCommand("checkup 1 /delete").execute(model);
        assertEquals(List.of(bob), model.getFilteredPersonList());
        assertEquals(List.of(scheduledAlice, bob), model.getAddressBook().getPersonList());

        parser.parseCommand("list").execute(model);
        parser.parseCommand("sort name desc").execute(model);
        parser.parseCommand("checkup 2 /delete").execute(model);
        assertEquals(List.of(bob, alice), model.getFilteredPersonList());
    }

    @Test
    public void execute_deleteWithoutAppointmentOrInvalidIndex_leavesModelUnchanged() {
        assertCommandFailure(CheckupCommand.delete(Index.fromOneBased(1)), model,
                Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
        model.addPerson(alice);
        assertCommandFailure(CheckupCommand.delete(Index.fromOneBased(1)), model,
                CheckupCommand.MESSAGE_NO_APPOINTMENT);
    }

    @Test
    public void execute_delete_savesClearedAppointmentAndMark() throws Exception {
        Person scheduled = new PersonBuilder(alice).withCheckupAppointment(appointment.toString())
                .build().withMarkedCheckup();
        model.addPerson(scheduled);
        StorageManager storage = new StorageManager(new JsonAddressBookStorage(testFolder.resolve("contacts.json")),
                new JsonUserPrefsStorage(testFolder.resolve("preferences.json")));
        new LogicManager(model, storage).execute("checkup 1 /delete");
        assertEquals(List.of(alice), storage.readAddressBook().orElseThrow().getPersonList());
        assertEquals(CheckupCommand.delete(Index.fromOneBased(1)), CheckupCommand.delete(Index.fromOneBased(1)));
        assertFalse(CheckupCommand.delete(Index.fromOneBased(1)).equals(
                new CheckupCommand(Index.fromOneBased(1), appointment)));
    }

    @Test
    public void execute_archivedContactWithInvalidDate_prioritizesArchiveError() throws Exception {
        model.addPerson(alice.copyWithArchivedStatus(true));
        new ListCommand(true).execute(model);
        AddressBookParser parser = new AddressBookParser();
        for (String value : List.of("31/02/2030 12:00", "01/01/2030 24:00", "not a date",
                "01/01/2030", appointmentOn(LocalDate.now().minusDays(1)).toString())) {
            assertCommandFailure(parser.parseCommand("checkup 1 " + value), model,
                    CheckupCommand.MESSAGE_ARCHIVED_CONTACT);
        }
    }

    @Test
    public void execute_activeContactWithInvalidDate_preservesDateErrors() throws Exception {
        model.addPerson(alice);
        AddressBookParser parser = new AddressBookParser();
        for (String value : List.of("31/02/2030 12:00", "01/01/2030 24:00", "not a date",
                "01/01/2030", "01/01/2030 12:00 extra", "delete", "/delete extra")) {
            assertCommandFailure(parser.parseCommand("checkup 1 " + value), model,
                    CheckupAppointment.MESSAGE_CONSTRAINTS + "\n" + CheckupCommand.MESSAGE_USAGE);
        }
        assertCommandFailure(parser.parseCommand("checkup 1 " + appointmentOn(LocalDate.now().minusDays(1))),
                model, CheckupAppointment.MESSAGE_PAST_DATE_TIME);
    }

    @Test
    public void execute_archivedContact_rejectsAppointmentWithoutChangingModel() throws Exception {
        model.addPerson(alice.copyWithArchivedStatus(true));
        new ListCommand(true).execute(model);

        assertCommandFailure(new CheckupCommand(Index.fromOneBased(1), appointment), model,
                "Appointments cannot be made with archived contacts. Unarchive the contact to do so.");
    }

    @Test
    public void execute_archivedContactWithAppointment_preservesAppointmentMarkAndSelection() throws Exception {
        Person archived = new PersonBuilder(alice).withCheckupAppointment(appointment.toString())
                .build().withMarkedCheckup().copyWithArchivedStatus(true);
        model.addPerson(archived);
        new ListCommand(true).execute(model);
        new FilterCommand(new PersonMatchesFilterPredicate(List.of("Alice"), List.of(), List.of())).execute(model);
        new ViewCommand(Index.fromOneBased(1)).execute(model);

        CheckupAppointment replacement = appointmentOn(LocalDate.now().plusDays(2));
        assertCommandFailure(new CheckupCommand(Index.fromOneBased(1), replacement), model,
                CheckupCommand.MESSAGE_ARCHIVED_CONTACT);
        assertEquals(archived, model.selectedPersonProperty().get());
    }

    @Test
    public void execute_afterUnarchivingContact_allowsAppointment() throws Exception {
        model.addPerson(alice.copyWithArchivedStatus(true));
        new ListCommand(true).execute(model);
        new UnarchiveCommand(Index.fromOneBased(1)).execute(model);
        new ListCommand().execute(model);

        new CheckupCommand(Index.fromOneBased(1), appointment).execute(model);

        assertEquals(appointment, model.getFilteredPersonList().get(0).getCheckupAppointment().orElseThrow());
    }

    @Test
    public void execute_sameBookingDifferentFamilyPhone_rejectsWithoutChangingContactOrSelection() throws Exception {
        Person bookedAlice = new PersonBuilder(alice).withFamilyNo("91234567")
                .withCheckupAppointment(appointment.toString()).build();
        Person bookedBob = new PersonBuilder(bob).withFamilyNo("92345678")
                .withCheckupAppointment(appointmentOn(LocalDate.now().plusDays(2)).toString())
                .build().withMarkedCheckup();
        model.addPerson(bookedAlice);
        model.addPerson(bookedBob);
        new ViewCommand(Index.fromOneBased(2)).execute(model);

        assertCommandFailure(new CheckupCommand(Index.fromOneBased(2), appointment), model,
                "A contact with this booking date and time already exists. "
                        + "Only contacts with the same family HP can be booked at the same time.");
        assertEquals(bookedBob, model.selectedPersonProperty().get());
    }

    @Test
    public void execute_sameBookingSameFamilyPhone_allowsSharedSlot() throws Exception {
        model.addPerson(new PersonBuilder(alice).withCheckupAppointment(appointment.toString()).build());
        model.addPerson(bob);

        new CheckupCommand(Index.fromOneBased(2), appointment).execute(model);

        assertEquals(appointment, model.getFilteredPersonList().get(1).getCheckupAppointment().orElseThrow());
    }

    @Test
    public void execute_conflictingBookingOutsideDisplayedList_stillRejects() throws Exception {
        Person bookedAlice = new PersonBuilder(alice).withFamilyNo("91234567")
                .withCheckupAppointment(appointment.toString()).build();
        model.addPerson(bookedAlice);
        model.addPerson(bob);
        new FilterCommand(new PersonMatchesFilterPredicate(List.of("Bob"), List.of(), List.of())).execute(model);

        assertCommandFailure(new CheckupCommand(Index.fromOneBased(1), appointment), model,
                CheckupCommand.MESSAGE_BOOKING_CONFLICT);
    }

    @Test
    public void execute_archivedBookingOutsideDisplayedList_doesNotBlockSlot() throws Exception {
        Person archivedAlice = new PersonBuilder(alice).withFamilyNo("91234567")
                .withCheckupAppointment(appointment.toString()).build().copyWithArchivedStatus(true);
        model.addPerson(archivedAlice);
        model.addPerson(bob);

        new CheckupCommand(Index.fromOneBased(1), appointment).execute(model);

        assertEquals(appointment, model.getFilteredPersonList().get(0).getCheckupAppointment().orElseThrow());
        assertEquals(archivedAlice, model.getAddressBook().getPersonList().get(0));
    }

    @Test
    public void execute_parsedExtraSpaces_preservesWhitespaceSupport() throws Exception {
        model.addPerson(alice);
        String spacedValue = appointment.toString().replace(" ", "   ");

        new AddressBookParser().parseCommand("checkup 1   " + spacedValue + "   ").execute(model);

        assertEquals(appointment, model.getFilteredPersonList().get(0).getCheckupAppointment().orElseThrow());
    }

    @Test
    public void execute_sharedSlotWithAnotherDifferentFamilyPhone_stillRejects() {
        model.addPerson(new PersonBuilder(alice).withCheckupAppointment(appointment.toString()).build());
        model.addPerson(new PersonBuilder(bob).withFamilyNo("91234567")
                .withCheckupAppointment(appointment.toString()).build());
        Person charlie = new PersonBuilder(alice).withName("Charlie")
                .withPhone("93456789").withEmail("charlie@example.com").build();
        model.addPerson(charlie);

        assertCommandFailure(new CheckupCommand(Index.fromOneBased(3), appointment), model,
                CheckupCommand.MESSAGE_BOOKING_CONFLICT);
    }

    @Test
    public void execute_differentDateOrTimeDifferentFamilyPhone_allowsBooking() throws Exception {
        String date = LocalDate.now().plusDays(1).format(DateTimeFormatter.ofPattern("dd/MM/uuuu"));
        model.addPerson(new PersonBuilder(alice).withFamilyNo("91234567")
                .withCheckupAppointment(appointment.toString()).build());
        model.addPerson(bob);
        CheckupAppointment differentTime = new CheckupAppointment(date + " 00:01");
        new CheckupCommand(Index.fromOneBased(2), differentTime).execute(model);
        assertEquals(differentTime, model.getFilteredPersonList().get(1).getCheckupAppointment().orElseThrow());

        CheckupAppointment differentDate = appointmentOn(LocalDate.now().plusDays(2));
        new CheckupCommand(Index.fromOneBased(2), differentDate).execute(model);
        assertEquals(differentDate, model.getFilteredPersonList().get(1).getCheckupAppointment().orElseThrow());
    }

    @Test
    public void execute_unfilteredList_updatesSelectedContactAndAllowsReplacement() throws Exception {
        model.addPerson(alice);
        model.addPerson(bob);
        CheckupCommand command = new CheckupCommand(Index.fromOneBased(2), appointment);
        assertTrue(command.modifiesData());
        assertEquals(String.format(CheckupCommand.MESSAGE_SUCCESS, bob.getName(), appointment),
                command.execute(model).getFeedbackToUser());
        assertEquals(alice, model.getAddressBook().getPersonList().get(0));
        assertEquals(new PersonBuilder(bob).withCheckupAppointment(appointment.toString()).build(),
                model.getAddressBook().getPersonList().get(1));

        CheckupAppointment replacement = appointmentOn(LocalDate.now().plusDays(2));
        new CheckupCommand(Index.fromOneBased(2), replacement).execute(model);
        assertEquals(replacement, model.getFilteredPersonList().get(1).getCheckupAppointment().orElseThrow());
    }

    @Test
    public void execute_sameAppointment_preservesMark() throws Exception {
        Person marked = new PersonBuilder(alice).withCheckupAppointment(appointment.toString())
                .build().withMarkedCheckup();
        model.addPerson(marked);

        new CheckupCommand(Index.fromOneBased(1), appointment).execute(model);

        assertEquals(List.of(marked), model.getFilteredPersonList());
    }

    @Test
    public void execute_replacementAppointment_resetsMarkAndAllowsMarkingAgain() throws Exception {
        Person marked = new PersonBuilder(alice).withCheckupAppointment(appointment.toString())
                .withRemarks("Needs assistance").build().withMarkedCheckup();
        model.addPerson(marked);
        CheckupAppointment replacement = appointmentOn(LocalDate.now().plusDays(2));

        new CheckupCommand(Index.fromOneBased(1), replacement).execute(model);

        Person expected = new PersonBuilder(alice).withCheckupAppointment(replacement.toString())
                .withRemarks("Needs assistance").build();
        assertEquals(List.of(expected), model.getFilteredPersonList());
        new MarkCommand(Index.fromOneBased(1)).execute(model);
        assertEquals(List.of(expected.withMarkedCheckup()), model.getFilteredPersonList());
    }

    @Test
    public void execute_personWithRemark_keepsRemark() throws Exception {
        Person bobWithNotes = new PersonBuilder(bob).withRemarks("Allergic to penicillin").build();
        model.addPerson(bobWithNotes);
        new CheckupCommand(Index.fromOneBased(1), appointment).execute(model);
        assertEquals(new PersonBuilder(bobWithNotes).withCheckupAppointment(appointment.toString()).build(),
                model.getAddressBook().getPersonList().get(0));
    }

    @Test
    public void execute_filteredList_usesDisplayedIndexAndPreservesFilter() throws Exception {
        model.addPerson(alice);
        model.addPerson(bob);
        new FilterCommand(new PersonMatchesFilterPredicate(List.of(), List.of("HIGH"), List.of())).execute(model);
        new CheckupCommand(Index.fromOneBased(1), appointment).execute(model);
        Person updatedBob = new PersonBuilder(bob).withCheckupAppointment(appointment.toString()).build();
        assertEquals(List.of(updatedBob), model.getFilteredPersonList());
        assertEquals(List.of(alice, updatedBob), model.getAddressBook().getPersonList());

        new ListCommand().execute(model);
        new CheckupCommand(Index.fromOneBased(1), appointment).execute(model);
        assertEquals(appointment, model.getAddressBook().getPersonList().get(0)
                .getCheckupAppointment().orElseThrow());
        assertEquals(2, model.getFilteredPersonList().size());
    }

    @Test
    public void execute_findResults_usesDisplayedIndex() throws Exception {
        model.addPerson(alice);
        model.addPerson(bob);
        new FindCommand(new NameContainsKeywordsPredicate(List.of("bo"))).execute(model);
        new CheckupCommand(Index.fromOneBased(1), appointment).execute(model);
        assertEquals(appointment, model.getFilteredPersonList().get(0).getCheckupAppointment().orElseThrow());
        assertEquals(alice, model.getAddressBook().getPersonList().get(0));
    }

    @Test
    public void execute_viewedContact_keepsUpdatedCardVisible() throws Exception {
        model.addPerson(alice);
        model.addPerson(bob);
        new ViewCommand(Index.fromOneBased(2)).execute(model);
        new CheckupCommand(Index.fromOneBased(2), appointment).execute(model);
        Person updatedBob = new PersonBuilder(bob).withCheckupAppointment(appointment.toString()).build();
        assertEquals(List.of(alice, updatedBob), model.getFilteredPersonList());
        assertEquals(List.of(alice, updatedBob), model.getAddressBook().getPersonList());
        assertEquals(updatedBob, model.selectedPersonProperty().get());
    }

    @Test
    public void execute_invalidDisplayedIndex_leavesModelUnchanged() {
        model.addPerson(alice);
        model.addPerson(bob);
        model.updateFilteredPersonList(new PersonHasTagPredicate("HIGH"));
        assertCommandFailure(new CheckupCommand(Index.fromOneBased(2), appointment), model,
                Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
        model.updateFilteredPersonList(person -> false);
        assertCommandFailure(new CheckupCommand(Index.fromOneBased(1), appointment), model,
                Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
    }

    @Test
    public void execute_pastDate_leavesModelUnchanged() {
        model.addPerson(alice);
        assertCommandFailure(new CheckupCommand(Index.fromOneBased(1), appointmentOn(LocalDate.now().minusDays(1))),
                model, CheckupAppointment.MESSAGE_PAST_DATE_TIME);
    }

    @Test
    public void execute_pastTime_leavesModelUnchanged() {
        model.addPerson(alice);
        String pastTime = LocalDateTime.now().minusMinutes(5).format(DateTimeFormatter.ofPattern("dd/MM/uuuu HH:mm"));
        assertCommandFailure(new CheckupCommand(Index.fromOneBased(1), new CheckupAppointment(pastTime)),
                model, CheckupAppointment.MESSAGE_PAST_DATE_TIME);
    }

    @Test
    public void equals_comparesIndexAndAppointment() {
        CheckupCommand command = new CheckupCommand(Index.fromOneBased(1), appointment);
        assertEquals(command, command);
        assertEquals(command, new CheckupCommand(Index.fromOneBased(1), appointment));
        assertFalse(command.equals(new CheckupCommand(Index.fromOneBased(2), appointment)));
        assertFalse(command.equals(new CheckupCommand(Index.fromOneBased(1), appointmentOn(LocalDate.now()))));
        assertFalse(command.equals(null));
        assertFalse(command.equals("checkup"));
    }

    private CheckupAppointment appointmentOn(LocalDate date) {
        return new CheckupAppointment(date.format(DateTimeFormatter.ofPattern("dd/MM/uuuu")) + " 00:00");
    }
}
