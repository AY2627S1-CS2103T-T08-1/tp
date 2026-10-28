package seedu.address.logic;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import seedu.address.model.person.Person;
import seedu.address.testutil.PersonBuilder;

public class MessagesTest {

    @Test
    public void format_personWithCheckupAppointment_showsAppointment() {
        Person person = new PersonBuilder().withCheckupAppointment("01/01/2099 09:05").build();

        assertTrue(Messages.format(person).contains("; Checkup Appointment: 01/01/2099 09:05"));
    }

    @Test
    public void format_personWithoutCheckupAppointment_omitsAppointment() {
        Person person = new PersonBuilder().build();

        assertFalse(Messages.format(person).contains("Checkup Appointment"));
    }

    @Test
    public void format_personWithRemark_showsRemark() {
        Person person = new PersonBuilder().withRemarks("Allergic to penicillin").build();

        assertTrue(Messages.format(person).contains("; Remark: "));
        assertTrue(Messages.format(person).contains("Allergic to penicillin"));
    }
}
