package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import seedu.address.model.person.Person;
import seedu.address.testutil.PersonBuilder;

public class PersonDetailsFormatterTest {
    @Test
    public void format_noSelection_returnsEmptyBody() {
        assertEquals("", PersonDetailsFormatter.format(null));
    }

    @Test
    public void format_completeContact_includesEveryField() {
        Person person = new PersonBuilder().withPatientNo("91234567").withFamilyNo("81234567")
                .withPatientEmail("patient@example.com").withFamilyEmail("family@example.com")
                .withAddress("123 Long Road").withTags("HIGH").build();
        String details = PersonDetailsFormatter.format(person);
        assertEquals("Patient phone: 91234567\nFamily phone: 81234567"
                + "\nPatient email: patient@example.com\nFamily email: family@example.com"
                + "\nAddress: 123 Long Road\nPriority: HIGH", details);
    }

    @Test
    public void format_absentOptionalFields_showsExplicitPlaceholders() {
        String details = PersonDetailsFormatter.format(new PersonBuilder().withoutEmail().withTags().build());
        assertTrue(details.contains("Patient email: Not provided"));
        assertTrue(details.contains("Family email: Not provided"));
        assertTrue(details.contains("Priority: Not assigned"));
    }

    @Test
    public void format_longAddress_doesNotTruncateValue() {
        String address = "Long Road ".repeat(100);
        String details = PersonDetailsFormatter.format(new PersonBuilder().withAddress(address).build());
        assertTrue(details.contains(address));
    }
}
