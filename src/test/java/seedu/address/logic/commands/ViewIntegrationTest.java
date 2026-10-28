package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import seedu.address.logic.parser.AddressBookParser;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.person.Person;
import seedu.address.testutil.PersonBuilder;

/** Checks view selection against the real parsers and commands merged from other feature branches. */
public class ViewIntegrationTest {
    private final Model model = new ModelManager();
    private final AddressBookParser parser = new AddressBookParser();
    private final Person alpha = new PersonBuilder().withName("Alpha").withTags("LOW").build();
    private final Person beta = new PersonBuilder().withName("Beta").withTags("HIGH")
            .withPhone("91234567").withEmail("beta@example.com").build();
    private final Person gamma = new PersonBuilder().withName("Gamma").withTags("HIGH")
            .withPhone("92345678").withEmail("gamma@example.com").build();

    @BeforeEach
    public void setUp() {
        model.addPerson(alpha);
        model.addPerson(beta);
        model.addPerson(gamma);
    }

    @Test
    public void view_afterNameSort_usesDisplayedOrderAndKeepsSelectionWhenResorted() throws Exception {
        execute("sort name desc");
        execute("view 1");
        assertEquals(gamma, model.selectedPersonProperty().get());
        assertEquals(List.of(gamma, beta, alpha), model.getFilteredPersonList());
        execute("sort name asc");
        assertEquals(gamma, model.selectedPersonProperty().get());
        assertEquals(List.of(alpha, beta, gamma), model.getFilteredPersonList());
        assertEquals(List.of(alpha, beta, gamma), model.getAddressBook().getPersonList());
    }

    @Test
    public void view_afterRecentAndPrioritySort_usesEachDisplayedOrder() throws Exception {
        execute("sort recent");
        execute("view 1");
        assertEquals(gamma, model.selectedPersonProperty().get());
        execute("sort priority");
        execute("view 3");
        assertEquals(alpha, model.selectedPersonProperty().get());
        assertEquals(List.of(beta, gamma, alpha), model.getFilteredPersonList());
    }

    @Test
    public void view_afterMultiFieldFilterAndSort_preservesBoth() throws Exception {
        execute("filter /name Beta Gamma /tag HIGH");
        execute("sort name desc");
        execute("view 2");
        assertEquals(beta, model.selectedPersonProperty().get());
        assertEquals(List.of(gamma, beta), model.getFilteredPersonList());
        assertEquals(List.of(alpha, beta, gamma), model.getAddressBook().getPersonList());
        execute("find /name Alpha");
        assertNull(model.selectedPersonProperty().get());
        execute("view 1");
        assertEquals(alpha, model.selectedPersonProperty().get());
    }

    @Test
    public void remarkOnViewedSortedContact_refreshesSelectionWithoutChangingOrder() throws Exception {
        execute("sort name desc");
        execute("view 2");
        execute("remark 2 Allergic /tag HIGH is literal note text");
        Person selected = model.selectedPersonProperty().get();
        assertEquals("Beta", selected.getName().fullName);
        assertEquals(List.of("Allergic /tag HIGH is literal note text"), selected.getRemark().getNotes());
        assertEquals(selected, model.getFilteredPersonList().get(1));
        assertEquals(List.of("Gamma", "Beta", "Alpha"), displayedNames());
        execute("remark 2 /edit 1 Updated care note");
        assertEquals(List.of("Updated care note"), model.selectedPersonProperty().get().getRemark().getNotes());
        execute("remark 2 /delete 1");
        assertTrue(model.selectedPersonProperty().get().getRemark().isEmpty());
        assertEquals(List.of("Gamma", "Beta", "Alpha"), displayedNames());
    }

    @Test
    public void checkupAndRemarkOnFilteredContact_preserveEachOtherAndSelection() throws Exception {
        execute("filter /tag HIGH");
        execute("sort name desc");
        execute("view 2");
        execute("remark 2 Bring medication list");
        String appointment = LocalDate.now().plusDays(1).format(DateTimeFormatter.ofPattern("dd/MM/uuuu"))
                + " 09:30";
        execute("checkup 2 " + appointment);
        Person selected = model.selectedPersonProperty().get();
        assertEquals("Beta", selected.getName().fullName);
        assertEquals(appointment, selected.getCheckupAppointment().orElseThrow().toString());
        assertEquals(List.of("Bring medication list"), selected.getRemark().getNotes());
        execute("remark 2 /clear");
        assertEquals(appointment,
                model.selectedPersonProperty().get().getCheckupAppointment().orElseThrow().toString());
        assertTrue(model.selectedPersonProperty().get().getRemark().isEmpty());
        assertEquals(List.of("Gamma", "Beta"), displayedNames());
    }

    @Test
    public void listAfterClear_restoresContactsWithoutRestoringSelection() throws Exception {
        execute("sort recent");
        execute("view 1");
        execute("clear");
        execute("sort name");
        assertTrue(model.getFilteredPersonList().isEmpty());
        assertNull(model.selectedPersonProperty().get());
        execute("list");
        assertEquals(List.of(alpha, beta, gamma), model.getFilteredPersonList());
        assertNull(model.selectedPersonProperty().get());
    }

    private void execute(String input) throws Exception {
        parser.parseCommand(input).execute(model);
    }

    private List<String> displayedNames() {
        return model.getFilteredPersonList().stream().map(person -> person.getName().fullName).toList();
    }
}
