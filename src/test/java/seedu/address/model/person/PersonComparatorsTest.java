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
