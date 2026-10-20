package seedu.address.testutil;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import seedu.address.logic.commands.EditCommand.EditPersonDescriptor;
import seedu.address.model.person.Address;
import seedu.address.model.person.Email;
import seedu.address.model.person.Name;
import seedu.address.model.person.Person;
import seedu.address.model.person.Phone;
import seedu.address.model.tag.Tag;

/**
 * A utility class to help with building EditPersonDescriptor objects.
 */
public class EditPersonDescriptorBuilder {

    private EditPersonDescriptor descriptor;

    public EditPersonDescriptorBuilder() {
        descriptor = new EditPersonDescriptor();
    }

    public EditPersonDescriptorBuilder(EditPersonDescriptor descriptor) {
        this.descriptor = new EditPersonDescriptor(descriptor);
    }

    /**
     * Returns an {@code EditPersonDescriptor} with fields containing {@code person}'s details
     */
    public EditPersonDescriptorBuilder(Person person) {
        descriptor = new EditPersonDescriptor();
        descriptor.setName(person.getName());
        descriptor.setPatientNo(person.getPatientNo());
        descriptor.setFamilyNo(person.getFamilyNo());
        person.getPatientEmail().ifPresent(descriptor::setPatientEmail);
        person.getFamilyEmail().ifPresent(descriptor::setFamilyEmail);
        descriptor.setAddress(person.getAddress());
        descriptor.setTags(person.getTags());
    }

    /**
     * Sets the {@code Name} of the {@code EditPersonDescriptor} that we are building.
     */
    public EditPersonDescriptorBuilder withName(String name) {
        descriptor.setName(new Name(name));
        return this;
    }

    /**
     * Sets the {@code Phone} of the {@code EditPersonDescriptor} that we are building.
     */
    public EditPersonDescriptorBuilder withPhone(String phone) {
        descriptor.setPhone(new Phone(phone));
        return this;
    }

    /**
     * Sets the family phone number of the {@code EditPersonDescriptor} that we are building.
     */
    public EditPersonDescriptorBuilder withFamilyNo(String familyNo) {
        descriptor.setFamilyNo(new Phone(familyNo));
        return this;
    }

    /**
     * Sets the patient email of the {@code EditPersonDescriptor} that we are building.
     */
    public EditPersonDescriptorBuilder withPatientEmail(String patientEmail) {
        descriptor.setPatientEmail(new Email(patientEmail));
        return this;
    }

    /**
     * Sets the family email of the {@code EditPersonDescriptor} that we are building.
     */
    public EditPersonDescriptorBuilder withFamilyEmail(String familyEmail) {
        descriptor.setFamilyEmail(new Email(familyEmail));
        return this;
    }

    /**
     * Marks the patient email for removal in the {@code EditPersonDescriptor} that we are building.
     */
    public EditPersonDescriptorBuilder withoutPatientEmail() {
        descriptor.setPatientEmail(null);
        return this;
    }

    /**
     * Marks the family email for removal in the {@code EditPersonDescriptor} that we are building.
     */
    public EditPersonDescriptorBuilder withoutFamilyEmail() {
        descriptor.setFamilyEmail(null);
        return this;
    }

    /**
     * Sets the {@code Address} of the {@code EditPersonDescriptor} that we are building.
     */
    public EditPersonDescriptorBuilder withAddress(String address) {
        descriptor.setAddress(new Address(address));
        return this;
    }

    /**
     * Parses the {@code tags} into a {@code Set<Tag>} and sets it to the {@code EditPersonDescriptor}
     * that we are building.
     */
    public EditPersonDescriptorBuilder withTags(String... tags) {
        Set<Tag> tagSet = Stream.of(tags).map(Tag::new).collect(Collectors.toSet());
        descriptor.setTags(tagSet);
        return this;
    }

    public EditPersonDescriptor build() {
        return descriptor;
    }
}
