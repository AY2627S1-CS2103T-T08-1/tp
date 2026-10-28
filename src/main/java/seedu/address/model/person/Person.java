package seedu.address.model.person;

import static seedu.address.commons.util.AppUtil.checkArgument;
import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import seedu.address.commons.util.ToStringBuilder;
import seedu.address.model.tag.Tag;

/**
 * Represents a Person in the address book.
 * Guarantees: details are present and not null, field values are validated, immutable.
 */
public class Person {

    public static final String MESSAGE_TAG_COUNT_CONSTRAINTS = "A contact can have at most one priority tag.";
    public static final String MESSAGE_MARK_WITHOUT_CHECKUP =
            "Contacts with no checkup appointments cannot be marked.";
    public static final String MESSAGE_NAME_ADDRESS_CONFLICT =
            "Another contact with this name already has this Address. "
            + "Include a differentiator such as a number (e.g. Alice 2) or surname.";

    // Identity fields
    private final Name name;
    private final Phone patientNo;
    private final Phone familyNo;

    // Data fields
    private final Email patientEmail;
    private final Email familyEmail;
    private final Address address;
    private final boolean archived;
    private final CheckupAppointment checkupAppointment;
    private final boolean checkupMarked;
    private final Set<Tag> tags = new HashSet<>();
    private final Remark remark;

    /**
     * Every field must be present and not null.
     * The tag set must contain at most one priority and may be empty.
     */
    public Person(Name name, Phone patientNo, Phone familyNo, Address address, Set<Tag> tags) {
        this(name, patientNo, familyNo, null, null, address, tags);
    }

    /**
     * Every field except emails must be present and not null.
     * The tag set must contain at most one priority and may be empty.
     */
    public Person(Name name, Phone patientNo, Phone familyNo, Email patientEmail, Email familyEmail,
            Address address, Set<Tag> tags) {
        this(name, patientNo, familyNo, patientEmail, familyEmail, address, tags, null, Remark.EMPTY);
    }

    /**
     * Creates a contact with optional emails, an optional checkup appointment and a remark.
     * The tag set must contain at most one priority and may be empty, and the remark may have no notes.
     */
    public Person(Name name, Phone patientNo, Phone familyNo, Email patientEmail, Email familyEmail,
            Address address, Set<Tag> tags, CheckupAppointment checkupAppointment, Remark remark) {
        this(name, patientNo, familyNo, patientEmail, familyEmail, address, tags, checkupAppointment,
                remark, false);
    }

    /**
     * Creates a person with the given details and archived status.
     */
    public Person(Name name, Phone patientNo, Phone familyNo, Email patientEmail, Email familyEmail,
            Address address, Set<Tag> tags, CheckupAppointment checkupAppointment, Remark remark,
            boolean archived) {
        this(name, patientNo, familyNo, patientEmail, familyEmail, address, tags, checkupAppointment,
                remark, archived, false);
    }

    /**
     * Creates a contact with its archived and checkup marking states.
     * Only a contact with a checkup appointment may be marked.
     */
    public Person(Name name, Phone patientNo, Phone familyNo, Email patientEmail, Email familyEmail,
            Address address, Set<Tag> tags, CheckupAppointment checkupAppointment, Remark remark,
            boolean archived, boolean checkupMarked) {
        requireAllNonNull(name, patientNo, familyNo, address, tags, remark);
        checkArgument(tags.size() <= 1, MESSAGE_TAG_COUNT_CONSTRAINTS);
        checkArgument(!checkupMarked || checkupAppointment != null, MESSAGE_MARK_WITHOUT_CHECKUP);

        this.name = name;
        this.patientNo = patientNo;
        this.familyNo = familyNo;
        this.patientEmail = patientEmail;
        this.familyEmail = familyEmail;
        this.address = address;
        this.archived = archived;
        this.checkupAppointment = checkupAppointment;
        this.checkupMarked = checkupMarked;
        this.tags.addAll(tags);
        this.remark = remark;
    }

    /**
     * Returns whether this person is archived.
     */
    public boolean isArchived() {
        return archived;
    }

    /**
     * Returns a copy of this person with the given archived status.
     */
    public Person copyWithArchivedStatus(boolean archived) {
        return new Person(name, patientNo, familyNo, patientEmail, familyEmail, address, tags, checkupAppointment,
                remark, archived, checkupMarked);
    }

    public Name getName() {
        return name;
    }

    public Phone getPatientNo() {
        return patientNo;
    }

    public Phone getFamilyNo() {
        return familyNo;
    }

    public Optional<Email> getPatientEmail() {
        return Optional.ofNullable(patientEmail);
    }

    public Optional<Email> getFamilyEmail() {
        return Optional.ofNullable(familyEmail);
    }

    public Address getAddress() {
        return address;
    }

    public Optional<CheckupAppointment> getCheckupAppointment() {
        return Optional.ofNullable(checkupAppointment);
    }

    public boolean isCheckupMarked() {
        return checkupMarked;
    }

    /**
     * Returns a marked copy, preserving all other contact details.
     */
    public Person withMarkedCheckup() {
        return new Person(name, patientNo, familyNo, patientEmail, familyEmail, address, tags, checkupAppointment,
                remark, archived, true);
    }

    /**
     * Returns an unmarked copy, preserving all other contact details.
     */
    public Person withUnmarkedCheckup() {
        return new Person(name, patientNo, familyNo, patientEmail, familyEmail, address, tags, checkupAppointment,
                remark, archived, false);
    }

    /**
     * Returns an immutable tag set, which throws {@code UnsupportedOperationException}
     * if modification is attempted.
     * Returns an empty set when the person has no assigned tags.
     */
    public Set<Tag> getTags() {
        return Collections.unmodifiableSet(tags);
    }

    public Remark getRemark() {
        return remark;
    }

    /**
     * Returns a copy of this person with {@code remark} in place of the current remark.
     */
    public Person withRemark(Remark remark) {
        return new Person(name, patientNo, familyNo, patientEmail, familyEmail, address, tags, checkupAppointment,
                remark, archived, checkupMarked);
    }

    /**
     * Returns true if the persons share patient contact details, or share a name and address.
     * This duplicate check is weaker than full data equality.
     */
    public boolean isSamePerson(Person otherPerson) {
        if (otherPerson == this) {
            return true;
        }

        return otherPerson != null
                && (hasSamePatientContactDetails(otherPerson) || hasSameNameAndAddress(otherPerson));
    }

    /**
     * Returns whether either patient phone or a present patient email matches.
     */
    public boolean hasSamePatientContactDetails(Person otherPerson) {
        return patientNo.equals(otherPerson.patientNo)
                || patientEmail != null && otherPerson.patientEmail != null
                && patientEmail.value.equalsIgnoreCase(otherPerson.patientEmail.value);
    }

    /**
     * Returns whether names match ignoring case and surrounding spaces, and addresses match
     * ignoring case and all whitespace.
     */
    public boolean hasSameNameAndAddress(Person otherPerson) {
        return name.fullName.strip().equalsIgnoreCase(otherPerson.name.fullName.strip())
                && address.value.replaceAll("\\s+", "")
                .equalsIgnoreCase(otherPerson.address.value.replaceAll("\\s+", ""));
    }

    /**
     * Returns true if both persons have the same identity and data fields.
     * This defines a stronger notion of equality between two persons.
     */
    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof Person otherPerson)) {
            return false;
        }

        return name.equals(otherPerson.name)
                && patientNo.equals(otherPerson.patientNo)
                && familyNo.equals(otherPerson.familyNo)
                && Objects.equals(patientEmail, otherPerson.patientEmail)
                && Objects.equals(familyEmail, otherPerson.familyEmail)
                && address.equals(otherPerson.address)
                && tags.equals(otherPerson.tags)
                && archived == otherPerson.archived
                && checkupMarked == otherPerson.checkupMarked
                && Objects.equals(checkupAppointment, otherPerson.checkupAppointment)
                && remark.equals(otherPerson.remark);
    }

    @Override
    public int hashCode() {
        // use this method for custom fields hashing instead of implementing your own
        return Objects.hash(name, patientNo, familyNo, patientEmail, familyEmail, address, tags, checkupAppointment,
                remark, archived, checkupMarked);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("name", name)
                .add("patientNo", patientNo)
                .add("familyNo", familyNo)
                .add("patientEmail", patientEmail)
                .add("familyEmail", familyEmail)
                .add("address", address)
                .add("tags", tags)
                .add("checkupAppointment", checkupAppointment)
                .add("remark", remark)
                .add("archived", archived)
                .add("checkupMarked", checkupMarked)
                .toString();
    }

}
