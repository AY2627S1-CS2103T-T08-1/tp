package seedu.address.model.person;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.Locale;

/**
 * An immutable checkup appointment. Past appointments remain valid when loading saved contacts.
 */
public class CheckupAppointment {
    public static final String MESSAGE_CONSTRAINTS =
            "Checkup must be a valid date and time in dd/mm/yyyy HH:mm format (24-hour clock).";
    public static final String MESSAGE_PAST_DATE_TIME = "Checkup date and time must not be in the past.";

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/uuuu HH:mm", Locale.ROOT)
            .withResolverStyle(ResolverStyle.STRICT);

    private final LocalDateTime dateTime;

    /**
     * Parses an exact date and time, rejecting invalid dates and times without rounding.
     */
    public CheckupAppointment(String value) {
        requireNonNull(value);
        checkArgument(value.matches("[0-9]{2}/[0-9]{2}/[0-9]{4} [0-9]{2}:[0-9]{2}"), MESSAGE_CONSTRAINTS);
        try {
            dateTime = LocalDateTime.parse(value, FORMATTER);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException(MESSAGE_CONSTRAINTS, e);
        }
        checkArgument(dateTime.getYear() > 0, MESSAGE_CONSTRAINTS);
    }

    /**
     * Returns whether the appointment precedes the current time in the system's local timezone.
     */
    public boolean isBeforeNow() {
        return isBeforeNow(Clock.systemDefaultZone());
    }

    /** Checks against a supplied clock so time boundaries can be tested deterministically. */
    boolean isBeforeNow(Clock clock) {
        return dateTime.isBefore(LocalDateTime.now(clock));
    }

    @Override
    public boolean equals(Object other) {
        return other == this || other instanceof CheckupAppointment otherAppointment
                && dateTime.equals(otherAppointment.dateTime);
    }

    @Override
    public int hashCode() {
        return dateTime.hashCode();
    }

    @Override
    public String toString() {
        return dateTime.format(FORMATTER);
    }
}
