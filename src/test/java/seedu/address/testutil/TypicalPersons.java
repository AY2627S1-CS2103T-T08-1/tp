package seedu.address.testutil;

import static seedu.address.logic.commands.CommandTestUtil.VALID_ADDRESS_AMY;
import static seedu.address.logic.commands.CommandTestUtil.VALID_ADDRESS_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_NAME_AMY;
import static seedu.address.logic.commands.CommandTestUtil.VALID_NAME_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_PHONE_AMY;
import static seedu.address.logic.commands.CommandTestUtil.VALID_PHONE_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_TAG_FRIEND;
import static seedu.address.logic.commands.CommandTestUtil.VALID_TAG_HUSBAND;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import seedu.address.model.AddressBook;
import seedu.address.model.person.Address;
import seedu.address.model.person.Name;
import seedu.address.model.person.Person;
import seedu.address.model.person.Phone;
import seedu.address.model.util.SampleDataUtil;

/**
 * A utility class containing a list of {@code Person} objects to be used in tests.
 */
public class TypicalPersons {

    public static final Person ALICE = createPerson("Alice Pauline", "94351253", "81234567",
            "123, Jurong West Ave 6, #08-111", "friends");
    public static final Person BENSON = createPerson("Benson Meier", "98765432", "82345678",
            "311, Clementi Ave 2, #02-25", "owesMoney", "friends");
    public static final Person CARL = createPerson("Carl Kurz", "95352563", "83456789",
            "wall street");
    public static final Person DANIEL = createPerson("Daniel Meier", "87652533", "84567890",
            "10th street", "friends");
    public static final Person ELLE = createPerson("Elle Meyer", "9482224", "85678901",
            "michegan ave");
    public static final Person FIONA = createPerson("Fiona Kunz", "9482427", "86789012",
            "little tokyo");
    public static final Person GEORGE = createPerson("George Best", "9482442", "87890123",
            "4th street");

    // Manually added
    public static final Person HOON = createPerson("Hoon Meier", "8482424", "88901234",
            "little india");
    public static final Person IDA = createPerson("Ida Mueller", "8482131", "89012345",
            "chicago ave");

    // Manually added - Person's details found in {@code CommandTestUtil}
    public static final Person AMY = createPerson(VALID_NAME_AMY, VALID_PHONE_AMY, "89123456",
            VALID_ADDRESS_AMY, VALID_TAG_FRIEND);
    public static final Person BOB = createPerson(VALID_NAME_BOB, VALID_PHONE_BOB, "81234569",
            VALID_ADDRESS_BOB, VALID_TAG_HUSBAND, VALID_TAG_FRIEND);

    public static final String KEYWORD_MATCHING_MEIER = "Meier";

    private TypicalPersons() {
        // Prevent instantiation.
    }

    private static Person createPerson(String name, String patientNo, String familyNo,
                                       String address, String... tags) {
        return new Person(
                new Name(name),
                new Phone(patientNo),
                new Phone(familyNo),
                new Address(address),
                SampleDataUtil.getTagSet(tags));
    }

    /**
     * Returns an {@code AddressBook} with all the typical persons.
     */
    public static AddressBook getTypicalAddressBook() {
        AddressBook addressBook = new AddressBook();
        for (Person person : getTypicalPersons()) {
            addressBook.addPerson(person);
        }
        return addressBook;
    }

    public static List<Person> getTypicalPersons() {
        return new ArrayList<>(Arrays.asList(ALICE, BENSON, CARL, DANIEL, ELLE, FIONA, GEORGE));
    }
}
