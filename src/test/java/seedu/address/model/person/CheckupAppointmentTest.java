package seedu.address.model.person;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.junit.jupiter.api.Test;

public class CheckupAppointmentTest {
    @Test
    public void constructor_invalidDateTime_rejectsWithoutNormalizing() {
        for (String value : List.of("", "29/02/2025 12:00", "31/04/2030 12:00", "01/13/2030 12:00",
                "00/01/2030 12:00", "01/01/0000 12:00", "01/01/2030 24:00", "01/01/2030 23:60",
                "1/01/2030 12:00", "01/01/2030 1:00", "2030-01-01 12:00", "01/01/2030 12:00 PM",
                "01/01/2030  12:00", "01/01/2030 12:00:00")) {
            assertThrows(IllegalArgumentException.class, CheckupAppointment.MESSAGE_CONSTRAINTS, () ->
                    new CheckupAppointment(value));
        }
        assertThrows(NullPointerException.class, () -> new CheckupAppointment(null));
    }

    @Test
    public void constructor_validDateTimes_preservesExactFormat() {
        for (String value : List.of("29/02/2028 00:00", "31/12/2030 23:59", "01/01/2000 09:05")) {
            assertEquals(value, new CheckupAppointment(value).toString());
        }
    }

    @Test
    public void isBeforeToday_comparesDateOnly() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/uuuu");
        LocalDate today = LocalDate.now();
        assertFalse(new CheckupAppointment(today.format(formatter) + " 00:00").isBeforeToday());
        assertFalse(new CheckupAppointment(today.plusDays(1).format(formatter) + " 00:00").isBeforeToday());
        assertTrue(new CheckupAppointment(today.minusDays(1).format(formatter) + " 23:59").isBeforeToday());
    }

    @Test
    public void equals_comparesDateTime() {
        CheckupAppointment appointment = new CheckupAppointment("01/01/2030 12:00");
        assertEquals(appointment, appointment);
        assertEquals(appointment, new CheckupAppointment("01/01/2030 12:00"));
        assertEquals(appointment.hashCode(), new CheckupAppointment("01/01/2030 12:00").hashCode());
        assertFalse(appointment.equals(new CheckupAppointment("01/01/2030 12:01")));
        assertFalse(appointment.equals(null));
        assertFalse(appointment.equals("01/01/2030 12:00"));
    }
}
