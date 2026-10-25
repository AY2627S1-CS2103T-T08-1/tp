package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.Messages;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.person.CheckupAppointment;
import seedu.address.model.person.NameContainsKeywordsPredicate;
import seedu.address.model.person.Person;
import seedu.address.model.person.PersonHasTagPredicate;
import seedu.address.testutil.PersonBuilder;

public class CheckupCommandTest {
    private final Model model = new ModelManager();
    private final Person alice = new PersonBuilder().withName("Alice").withTags("LOW").build();
    private final Person bob = new PersonBuilder().withName("Bob").withTags("HIGH").build();
    private final CheckupAppointment appointment = appointmentOn(LocalDate.now().plusDays(1));

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

        CheckupAppointment replacement = appointmentOn(LocalDate.now());
        new CheckupCommand(Index.fromOneBased(2), replacement).execute(model);
        assertEquals(replacement, model.getFilteredPersonList().get(1).getCheckupAppointment().orElseThrow());
    }

    @Test
    public void execute_filteredList_usesDisplayedIndexAndPreservesFilter() throws Exception {
        model.addPerson(alice);
        model.addPerson(bob);
        new FilterCommand(new PersonHasTagPredicate("HIGH")).execute(model);
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
        new CheckupCommand(Index.fromOneBased(1), appointment).execute(model);
        Person updatedBob = new PersonBuilder(bob).withCheckupAppointment(appointment.toString()).build();
        assertEquals(List.of(updatedBob), model.getFilteredPersonList());
        assertEquals(List.of(alice, updatedBob), model.getAddressBook().getPersonList());
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
                model, CheckupAppointment.MESSAGE_PAST_DATE);
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
