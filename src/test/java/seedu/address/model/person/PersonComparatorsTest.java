package seedu.address.model.person;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.testutil.PersonBuilder;

public class PersonComparatorsTest {

    private static final Person HIGH_BOB = new PersonBuilder().withName("Bob").withTags("HIGH").build();
    private static final Person HIGH_ALICE = new PersonBuilder().withName("alice").withTags("HIGH").build();
    private static final Person MEDIUM_CARL = new PersonBuilder().withName("Carl").withTags("MEDIUM").build();
    private static final Person LOW_DAN = new PersonBuilder().withName("Dan").withTags("LOW").build();
    private static final Person UNTAGGED_ANNA = new PersonBuilder().withName("Anna").withTags().build();
    private static final Person UNTAGGED_ZOE = new PersonBuilder().withName("Zoe").withTags().build();

    private static List<Person> sorted(List<Person> persons, Comparator<Person> comparator) {
        List<Person> copy = new ArrayList<>(persons);
        copy.sort(comparator);
        return copy;
    }

    @Test
    public void byName_numbersInNamesComparedByValue() {
        Person ethan1 = new PersonBuilder().withName("Ethan1").build();
        Person ethan2 = new PersonBuilder().withName("Ethan2").build();
        Person ethan10 = new PersonBuilder().withName("ethan10").build();
        Person ethan100 = new PersonBuilder().withName("Ethan100").build();
        Person ethanAlone = new PersonBuilder().withName("Ethan").build();
        Person ethanB = new PersonBuilder().withName("Ethan B").build();
        List<Person> persons = List.of(ethan100, ethan10, ethanB, ethan2, ethan1, ethanAlone);

        assertEquals(List.of(ethanAlone, ethanB, ethan1, ethan2, ethan10, ethan100),
                sorted(persons, PersonComparators.byName(true)));
        assertEquals(List.of(ethan100, ethan10, ethan2, ethan1, ethanB, ethanAlone),
                sorted(persons, PersonComparators.byName(false)));
    }

    @Test
    public void byName_numbersInMiddleAndWithLeadingZeros() {
        Person room2 = new PersonBuilder().withName("Room 2 Alice").build();
        Person room02Bob = new PersonBuilder().withName("Room 02 Bob").build();
        Person room10 = new PersonBuilder().withName("Room 10 Alice").build();
        Person huge = new PersonBuilder().withName("Room 99999999999999999999999 Alice").build();
        List<Person> persons = List.of(huge, room10, room02Bob, room2);

        // 2 and 02 are the same number, so the rest of the name decides
        assertEquals(List.of(room2, room02Bob, room10, huge), sorted(persons, PersonComparators.byName(true)));
    public void byCheckup_earliestOrLatestFirstWithoutCheckupLast() {
        Person early = new PersonBuilder().withName("Zed").withCheckupAppointment("01/01/2098 09:00").build();
        Person sameDayLater = new PersonBuilder().withName("Bob").withCheckupAppointment("01/01/2098 15:30").build();
        Person late = new PersonBuilder().withName("Carl").withCheckupAppointment("05/06/2099 08:00").build();
        List<Person> persons = List.of(UNTAGGED_ANNA, late, early, sameDayLater);

        assertEquals(List.of(early, sameDayLater, late, UNTAGGED_ANNA),
                sorted(persons, PersonComparators.byCheckup(true)));

        // persons without a checkup are still last when descending
        assertEquals(List.of(late, sameDayLater, early, UNTAGGED_ANNA),
                sorted(persons, PersonComparators.byCheckup(false)));
    }

    @Test
    public void byCheckup_sameAppointmentOrderedByNameInBothDirections() {
        Person bob = new PersonBuilder().withName("Bob").withCheckupAppointment("01/01/2098 09:00").build();
        Person alice = new PersonBuilder().withName("alice").withCheckupAppointment("01/01/2098 09:00").build();
        List<Person> persons = List.of(UNTAGGED_ZOE, bob, UNTAGGED_ANNA, alice);

        assertEquals(List.of(alice, bob, UNTAGGED_ANNA, UNTAGGED_ZOE),
                sorted(persons, PersonComparators.byCheckup(true)));
        assertEquals(List.of(alice, bob, UNTAGGED_ANNA, UNTAGGED_ZOE),
                sorted(persons, PersonComparators.byCheckup(false)));
    }

    @Test
    public void byName_ignoresCase() {
        List<Person> persons = List.of(LOW_DAN, HIGH_BOB, HIGH_ALICE, MEDIUM_CARL);

        assertEquals(List.of(HIGH_ALICE, HIGH_BOB, MEDIUM_CARL, LOW_DAN),
                sorted(persons, PersonComparators.byName(true)));
        assertEquals(List.of(LOW_DAN, MEDIUM_CARL, HIGH_BOB, HIGH_ALICE),
                sorted(persons, PersonComparators.byName(false)));
    }

    @Test
    public void byPriority_mostUrgentFirst() {
        List<Person> persons = List.of(LOW_DAN, UNTAGGED_ANNA, MEDIUM_CARL, HIGH_BOB);

        assertEquals(List.of(HIGH_BOB, MEDIUM_CARL, LOW_DAN, UNTAGGED_ANNA),
                sorted(persons, PersonComparators.byPriority(true)));
    }

    @Test
    public void byPriority_leastUrgentFirst() {
        List<Person> persons = List.of(HIGH_BOB, UNTAGGED_ANNA, MEDIUM_CARL, LOW_DAN);

        // untagged persons are still last
        assertEquals(List.of(LOW_DAN, MEDIUM_CARL, HIGH_BOB, UNTAGGED_ANNA),
                sorted(persons, PersonComparators.byPriority(false)));
    }

    @Test
    public void byPriority_samePriorityOrderedByNameInBothDirections() {
        List<Person> persons = List.of(UNTAGGED_ZOE, HIGH_BOB, UNTAGGED_ANNA, HIGH_ALICE);

        assertEquals(List.of(HIGH_ALICE, HIGH_BOB, UNTAGGED_ANNA, UNTAGGED_ZOE),
                sorted(persons, PersonComparators.byPriority(true)));
        assertEquals(List.of(HIGH_ALICE, HIGH_BOB, UNTAGGED_ANNA, UNTAGGED_ZOE),
                sorted(persons, PersonComparators.byPriority(false)));
    }
}
