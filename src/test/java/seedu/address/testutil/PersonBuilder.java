package seedu.address.testutil;

import java.util.HashSet;
import java.util.Set;

import seedu.address.model.person.Address;
import seedu.address.model.person.Email;
import seedu.address.model.person.Name;
import seedu.address.model.person.Person;
import seedu.address.model.person.Phone;
import seedu.address.model.tag.Tag;
import seedu.address.model.util.SampleDataUtil;

/**
 * A utility class to help with building Person objects.
 */
public class PersonBuilder {

    public static final String DEFAULT_NAME = "Amy Bee";
    public static final String DEFAULT_PHONE = "85355255";
    public static final String DEFAULT_EMAIL = "amy@gmail.com";
    public static final String DEFAULT_ADDRESS = "123, Jurong West Ave 6, #08-111";

    private Name name;
    private Phone patientNo;
    private Phone familyNo;
    private Email patientEmail;
    private Email familyEmail;
    private Address address;
    private Set<Tag> tags;

    /**
     * Creates a {@code PersonBuilder} with the default details.
     */
    public PersonBuilder() {
        name = new Name(DEFAULT_NAME);
        patientNo = new Phone(DEFAULT_PHONE);
        familyNo = new Phone("87654321");
        patientEmail = new Email(DEFAULT_EMAIL);
        familyEmail = null;
        address = new Address(DEFAULT_ADDRESS);
        tags = new HashSet<>();
    }

    /**
     * Initializes the PersonBuilder with the data of {@code personToCopy}.
     */
    public PersonBuilder(Person personToCopy) {
        name = personToCopy.getName();
        patientNo = personToCopy.getPatientNo();
        familyNo = personToCopy.getFamilyNo();
        patientEmail = personToCopy.getPatientEmail().orElse(null);
        familyEmail = personToCopy.getFamilyEmail().orElse(null);
        address = personToCopy.getAddress();
        tags = new HashSet<>(personToCopy.getTags());
    }

    /**
     * Sets the {@code Name} of the {@code Person} that we are building.
     */
    public PersonBuilder withName(String name) {
        this.name = new Name(name);
        return this;
    }

    /**
     * Parses the {@code tags} into a {@code Set<Tag>} and sets it to the {@code Person} that we are building.
     */
    public PersonBuilder withTags(String ... tags) {
        this.tags = SampleDataUtil.getTagSet(tags);
        return this;
    }

    /**
     * Sets the {@code Address} of the {@code Person} that we are building.
     */
    public PersonBuilder withAddress(String address) {
        this.address = new Address(address);
        return this;
    }

    /**
     * Sets the {@code Phone} of the {@code Person} that we are building.
     */
    public PersonBuilder withPhone(String phone) {
        this.patientNo = new Phone(phone);
        return this;
    }

    /**
     * Sets the patient phone number of the {@code Person} that we are building.
     */
    public PersonBuilder withPatientNo(String patientNo) {
        this.patientNo = new Phone(patientNo);
        return this;
    }

    /**
     * Sets the family phone number of the {@code Person} that we are building.
     */
    public PersonBuilder withFamilyNo(String familyNo) {
        this.familyNo = new Phone(familyNo);
        return this;
    }

    /**
     * Sets the {@code Email} of the {@code Person} that we are building.
     */
    public PersonBuilder withEmail(String email) {
        this.patientEmail = new Email(email);
        return this;
    }

    /**
     * Sets the patient email of the {@code Person} that we are building.
     */
    public PersonBuilder withPatientEmail(String patientEmail) {
        this.patientEmail = new Email(patientEmail);
        return this;
    }

    /**
     * Sets the family email of the {@code Person} that we are building.
     */
    public PersonBuilder withFamilyEmail(String familyEmail) {
        this.familyEmail = new Email(familyEmail);
        return this;
    }

    /**
     * Removes the optional email from the {@code Person} that we are building.
     */
    public PersonBuilder withoutEmail() {
        this.patientEmail = null;
        this.familyEmail = null;
        return this;
    }

    public Person build() {
        return new Person(name, patientNo, familyNo, patientEmail, familyEmail, address, tags);
    }

}
