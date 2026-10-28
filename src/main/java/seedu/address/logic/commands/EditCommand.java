package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ADDRESS;
import static seedu.address.logic.parser.CliSyntax.PREFIX_FAMILY_EMAIL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_FAMILY_PHONE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PATIENT_EMAIL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PATIENT_PHONE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TAG;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Logger;

import seedu.address.commons.core.LogsCenter;
import seedu.address.commons.core.index.Index;
import seedu.address.commons.util.CollectionUtil;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.person.Address;
import seedu.address.model.person.Email;
import seedu.address.model.person.Name;
import seedu.address.model.person.Person;
import seedu.address.model.person.Phone;
import seedu.address.model.tag.Tag;

/**
 * Edits the details of an existing person in the address book.
 */
public class EditCommand extends Command {

    public static final String COMMAND_WORD = "edit";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Edits the details of the person identified "
            + "by the index number used in the displayed person list. "
            + "Existing values will be overwritten by the input values.\n"
            + "Parameters: INDEX (must be a positive integer) "
            + "[" + PREFIX_NAME + " NAME] "
            + "[" + PREFIX_PATIENT_PHONE + " PATIENT_PHONE] "
            + "[" + PREFIX_FAMILY_PHONE + " FAMILY_PHONE] "
            + "[" + PREFIX_PATIENT_EMAIL + " PATIENT_EMAIL] "
            + "[" + PREFIX_FAMILY_EMAIL + " FAMILY_EMAIL] "
            + "[" + PREFIX_ADDRESS + " ADDRESS] "
            + "[" + PREFIX_TAG + " PRIORITY]\n"
            + "The '/' character is reserved for valid prefixes and is not allowed in any field value. "
            + "Separate each prefix from its value with a space.\n"
            + "Example: " + COMMAND_WORD + " 1 "
            + PREFIX_PATIENT_PHONE + " 91234567 "
            + PREFIX_FAMILY_EMAIL + " family@example.com "
            + PREFIX_TAG + " HIGH";

    public static final String MESSAGE_EDIT_PERSON_SUCCESS = "Edited person: %1$s";
    public static final String MESSAGE_NO_TAG_CHANGE = "No change to tag";
    public static final String MESSAGE_NOT_EDITED = "At least one field to edit must be provided.";
    public static final String MESSAGE_DUPLICATE_PERSON =
            "Someone with this patient HP or email already exists in CareBook.";

    private static final Logger logger = LogsCenter.getLogger(EditCommand.class);

    private final Index index;
    private final EditPersonDescriptor editPersonDescriptor;

    /**
     * @param index of the person in the filtered person list to edit
     * @param editPersonDescriptor details to edit the person with
     */
    public EditCommand(Index index, EditPersonDescriptor editPersonDescriptor) {
        requireNonNull(index);
        requireNonNull(editPersonDescriptor);

        this.index = index;
        this.editPersonDescriptor = new EditPersonDescriptor(editPersonDescriptor);
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        List<Person> lastShownList = model.getFilteredPersonList();

        if (index.getZeroBased() >= lastShownList.size()) {
            logger.fine("Edit rejected: index " + index.getOneBased() + " is not in the displayed list");
            throw new CommandException(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
        }

        Person personToEdit = lastShownList.get(index.getZeroBased());
        Person editedPerson = createEditedPerson(personToEdit, editPersonDescriptor);

        if (editPersonDescriptor.getTags().isPresent()
                && personToEdit.getTags().equals(editedPerson.getTags())
                && personToEdit.equals(editedPerson)) {
            return new CommandResult(MESSAGE_NO_TAG_CHANGE);
        }

        List<Person> otherPersons = model.getAddressBook().getPersonList().stream()
                .filter(person -> !person.equals(personToEdit)).toList();

        if (otherPersons.stream().anyMatch(editedPerson::hasSamePatientContactDetails)) {
            logger.fine("Edit rejected: patient contact details already exist");
            throw new CommandException(MESSAGE_DUPLICATE_PERSON);
        }

        if (otherPersons.stream().anyMatch(editedPerson::hasSameNameAndAddress)) {
            throw new CommandException(Person.MESSAGE_NAME_ADDRESS_CONFLICT);
        }

        model.setPersonKeepingDisplayed(personToEdit, editedPerson);
        logger.info("Edited contact at index " + index.getOneBased());
        return new CommandResult(String.format(MESSAGE_EDIT_PERSON_SUCCESS, Messages.format(editedPerson)));
    }

    /**
     * Creates and returns a {@code Person} with the details of {@code personToEdit}
     * edited with {@code editPersonDescriptor}.
     */
    private static Person createEditedPerson(Person personToEdit, EditPersonDescriptor editPersonDescriptor) {
        assert personToEdit != null;

        Name updatedName = editPersonDescriptor.getName().orElse(personToEdit.getName());
        Phone updatedPatientNo = editPersonDescriptor.getPatientNo().orElse(personToEdit.getPatientNo());
        Phone updatedFamilyNo = editPersonDescriptor.getFamilyNo().orElse(personToEdit.getFamilyNo());
        Email updatedPatientEmail = editPersonDescriptor.isPatientEmailEdited()
                ? editPersonDescriptor.getPatientEmail().orElse(null)
                : personToEdit.getPatientEmail().orElse(null);
        Email updatedFamilyEmail = editPersonDescriptor.isFamilyEmailEdited()
                ? editPersonDescriptor.getFamilyEmail().orElse(null)
                : personToEdit.getFamilyEmail().orElse(null);
        Address updatedAddress = editPersonDescriptor.getAddress().orElse(personToEdit.getAddress());
        Set<Tag> updatedTags = editPersonDescriptor.getTags().orElse(personToEdit.getTags());

        return new Person(updatedName, updatedPatientNo, updatedFamilyNo, updatedPatientEmail,
                updatedFamilyEmail, updatedAddress, updatedTags, personToEdit.getCheckupAppointment().orElse(null),
                personToEdit.getRemark(), personToEdit.isArchived(), personToEdit.isCheckupMarked());
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof EditCommand otherEditCommand)) {
            return false;
        }

        return index.equals(otherEditCommand.index)
                && editPersonDescriptor.equals(otherEditCommand.editPersonDescriptor);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("index", index)
                .add("editPersonDescriptor", editPersonDescriptor)
                .toString();
    }

    /**
     * Stores the details to edit the person with. Each non-empty field value will replace the
     * corresponding field value of the person.
     */
    public static class EditPersonDescriptor {
        private Name name;
        private Phone patientNo;
        private Phone familyNo;
        private Email patientEmail;
        private Email familyEmail;
        private boolean isPatientEmailEdited;
        private boolean isFamilyEmailEdited;
        private Address address;
        private Set<Tag> tags;

        public EditPersonDescriptor() {}

        /**
         * Copy constructor.
         * A defensive copy of {@code tags} is used internally.
         */
        public EditPersonDescriptor(EditPersonDescriptor toCopy) {
            setName(toCopy.name);
            setPatientNo(toCopy.patientNo);
            setFamilyNo(toCopy.familyNo);
            patientEmail = toCopy.patientEmail;
            familyEmail = toCopy.familyEmail;
            isPatientEmailEdited = toCopy.isPatientEmailEdited;
            isFamilyEmailEdited = toCopy.isFamilyEmailEdited;
            setAddress(toCopy.address);
            setTags(toCopy.tags);
        }

        /**
         * Returns true if at least one field is edited.
         */
        public boolean isAnyFieldEdited() {
            return CollectionUtil.isAnyNonNull(name, patientNo, familyNo, address, tags)
                    || isPatientEmailEdited || isFamilyEmailEdited;
        }

        public void setName(Name name) {
            this.name = name;
        }

        public Optional<Name> getName() {
            return Optional.ofNullable(name);
        }

        public void setPatientNo(Phone patientNo) {
            this.patientNo = patientNo;
        }

        public Optional<Phone> getPatientNo() {
            return Optional.ofNullable(patientNo);
        }

        public void setFamilyNo(Phone familyNo) {
            this.familyNo = familyNo;
        }

        public Optional<Phone> getFamilyNo() {
            return Optional.ofNullable(familyNo);
        }

        /**
         * Sets the patient email to {@code patientEmail}, or removes it if {@code patientEmail} is null.
         */
        public void setPatientEmail(Email patientEmail) {
            this.patientEmail = patientEmail;
            isPatientEmailEdited = true;
        }

        public Optional<Email> getPatientEmail() {
            return Optional.ofNullable(patientEmail);
        }

        /**
         * Sets the family email to {@code familyEmail}, or removes it if {@code familyEmail} is null.
         */
        public void setFamilyEmail(Email familyEmail) {
            this.familyEmail = familyEmail;
            isFamilyEmailEdited = true;
        }

        public Optional<Email> getFamilyEmail() {
            return Optional.ofNullable(familyEmail);
        }

        /**
         * Returns true if the patient email is to be changed or removed.
         */
        public boolean isPatientEmailEdited() {
            return isPatientEmailEdited;
        }

        /**
         * Returns true if the family email is to be changed or removed.
         */
        public boolean isFamilyEmailEdited() {
            return isFamilyEmailEdited;
        }

        public void setAddress(Address address) {
            this.address = address;
        }

        public Optional<Address> getAddress() {
            return Optional.ofNullable(address);
        }

        /**
         * Sets {@code tags} to this object's {@code tags}.
         * A defensive copy of {@code tags} is used internally.
         */
        public void setTags(Set<Tag> tags) {
            this.tags = (tags != null) ? new HashSet<>(tags) : null;
        }

        /**
         * Returns an unmodifiable tag set, which throws {@code UnsupportedOperationException}
         * if modification is attempted.
         * Returns {@code Optional#empty()} if {@code tags} is null.
         */
        public Optional<Set<Tag>> getTags() {
            return (tags != null) ? Optional.of(Collections.unmodifiableSet(tags)) : Optional.empty();
        }

        @Override
        public boolean equals(Object other) {
            if (other == this) {
                return true;
            }

            // instanceof handles nulls
            if (!(other instanceof EditPersonDescriptor otherEditPersonDescriptor)) {
                return false;
            }

            return Objects.equals(name, otherEditPersonDescriptor.name)
                    && Objects.equals(patientNo, otherEditPersonDescriptor.patientNo)
                    && Objects.equals(familyNo, otherEditPersonDescriptor.familyNo)
                    && Objects.equals(patientEmail, otherEditPersonDescriptor.patientEmail)
                    && Objects.equals(familyEmail, otherEditPersonDescriptor.familyEmail)
                    && isPatientEmailEdited == otherEditPersonDescriptor.isPatientEmailEdited
                    && isFamilyEmailEdited == otherEditPersonDescriptor.isFamilyEmailEdited
                    && Objects.equals(address, otherEditPersonDescriptor.address)
                    && Objects.equals(tags, otherEditPersonDescriptor.tags);
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
                    .toString();
        }
    }
}
