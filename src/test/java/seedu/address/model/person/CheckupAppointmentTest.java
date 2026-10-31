package seedu.address.model.person;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
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
    public void isBeforeNow_comparesDateAndTimeInLocalTimezone() {
        Clock clock = Clock.fixed(Instant.parse("2026-10-27T07:30:00Z"), ZoneId.of("Asia/Singapore"));
        assertTrue(new CheckupAppointment("27/10/2026 15:29").isBeforeNow(clock));
        assertFalse(new CheckupAppointment("27/10/2026 15:30").isBeforeNow(clock));
        assertFalse(new CheckupAppointment("27/10/2026 15:31").isBeforeNow(clock));
        assertTrue(new CheckupAppointment("26/10/2026 23:59").isBeforeNow(clock));
        assertFalse(new CheckupAppointment("28/10/2026 00:00").isBeforeNow(clock));
    }

    @Test
    public void isBeforeNow_currentMinuteAfterItsStart_isInThePast() {
        Clock clock = Clock.fixed(Instant.parse("2026-10-27T07:30:30Z"), ZoneId.of("Asia/Singapore"));
        assertTrue(new CheckupAppointment("27/10/2026 15:30").isBeforeNow(clock));
    }

    @Test
    public void isAfterToday_comparesCalendarDateInLocalTimezone() {
        Clock clock = Clock.fixed(Instant.parse("2026-10-27T17:30:00Z"), ZoneId.of("Asia/Singapore"));
        assertFalse(new CheckupAppointment("27/10/2026 23:59").isAfterToday(clock));
        assertFalse(new CheckupAppointment("28/10/2026 00:00").isAfterToday(clock));
        assertFalse(new CheckupAppointment("28/10/2026 23:59").isAfterToday(clock));
        assertTrue(new CheckupAppointment("29/10/2026 00:00").isAfterToday(clock));
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
