package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.Messages.MESSAGE_LEGACY_EMAIL_PREFIX;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ADDRESS;
import static seedu.address.logic.parser.CliSyntax.PREFIX_FAMILY_EMAIL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_FAMILY_PHONE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_LEGACY_EMAIL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PATIENT_EMAIL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PATIENT_PHONE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TAG;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.EditCommand;
import seedu.address.logic.commands.EditCommand.EditPersonDescriptor;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.person.Email;
import seedu.address.model.tag.Tag;

/**
 * Parses input arguments and creates a new EditCommand object
 */
public class EditCommandParser implements Parser<EditCommand> {

    public static final String MESSAGE_TEXT_AFTER_INDEX = "Unexpected text after the index: %2$s. "
            + "Put each new value after its prefix, e.g. edit %1$s /name John Doe";

    private static final String INTEGER_REGEX = "-?\\d+";

    /**
     * Parses the given {@code String} of arguments in the context of the EditCommand
     * and returns an EditCommand object for execution.
     * @throws ParseException if the user input does not conform to the expected format
     */
    public EditCommand parse(String args) throws ParseException {
        requireNonNull(args);
        ArgumentMultimap argMultimap =
                ArgumentTokenizer.tokenizeStrict(args, EditCommand.MESSAGE_USAGE,
                        PREFIX_NAME, PREFIX_PATIENT_PHONE, PREFIX_FAMILY_PHONE,
                        PREFIX_PATIENT_EMAIL, PREFIX_FAMILY_EMAIL, PREFIX_LEGACY_EMAIL, PREFIX_ADDRESS,
                        PREFIX_TAG);

        Index index = parseIndexForEdit(argMultimap.getPreamble());

        if (argMultimap.getValue(PREFIX_LEGACY_EMAIL).isPresent()) {
            throw new ParseException(MESSAGE_LEGACY_EMAIL_PREFIX);
        }

        argMultimap.verifyNoDuplicatePrefixesFor(PREFIX_NAME, PREFIX_PATIENT_PHONE, PREFIX_FAMILY_PHONE,
                PREFIX_PATIENT_EMAIL, PREFIX_FAMILY_EMAIL, PREFIX_ADDRESS);

        EditPersonDescriptor editPersonDescriptor = parseFieldsForEdit(argMultimap);
        if (!editPersonDescriptor.isAnyFieldEdited()) {
            throw new ParseException(EditCommand.MESSAGE_NOT_EDITED);
        }

        return new EditCommand(index, editPersonDescriptor);
    }

    /**
     * Returns a descriptor holding the field values given in {@code argMultimap}.
     *
     * @throws ParseException if a given field value is invalid.
     */
    private EditPersonDescriptor parseFieldsForEdit(ArgumentMultimap argMultimap) throws ParseException {
        EditPersonDescriptor editPersonDescriptor = new EditPersonDescriptor();

        if (argMultimap.getValue(PREFIX_NAME).isPresent()) {
            editPersonDescriptor.setName(ParserUtil.parseName(argMultimap.getValue(PREFIX_NAME).get()));
        }
        if (argMultimap.getValue(PREFIX_PATIENT_PHONE).isPresent()) {
            editPersonDescriptor.setPatientNo(ParserUtil.parsePhone(argMultimap.getValue(PREFIX_PATIENT_PHONE).get()));
        }
        if (argMultimap.getValue(PREFIX_FAMILY_PHONE).isPresent()) {
            editPersonDescriptor.setFamilyNo(ParserUtil.parsePhone(argMultimap.getValue(PREFIX_FAMILY_PHONE).get()));
        }
        if (argMultimap.getValue(PREFIX_PATIENT_EMAIL).isPresent()) {
            editPersonDescriptor.setPatientEmail(
                    parseEmailForEdit(argMultimap.getValue(PREFIX_PATIENT_EMAIL).get()));
        }
        if (argMultimap.getValue(PREFIX_FAMILY_EMAIL).isPresent()) {
            editPersonDescriptor.setFamilyEmail(
                    parseEmailForEdit(argMultimap.getValue(PREFIX_FAMILY_EMAIL).get()));
        }
        if (argMultimap.getValue(PREFIX_ADDRESS).isPresent()) {
            editPersonDescriptor.setAddress(ParserUtil.parseAddress(argMultimap.getValue(PREFIX_ADDRESS).get()));
        }
        parseTagsForEdit(argMultimap.getAllValues(PREFIX_TAG)).ifPresent(editPersonDescriptor::setTags);
        return editPersonDescriptor;
    }

    /**
     * Parses {@code preamble} into the index of the person to edit.
     *
     * @throws ParseException if {@code preamble} is not a positive integer, is not a number at all,
     *     or is a number followed by other text.
     */
    private static Index parseIndexForEdit(String preamble) throws ParseException {
        try {
            return ParserUtil.parseDisplayedIndex(preamble);
        } catch (ParseException pe) {
            // a number that is not a valid index, e.g. 0, is reported as such, as in remark, delete and archive
            if (preamble.trim().matches(INTEGER_REGEX)) {
                throw pe;
            }
            // a value typed without its prefix, e.g. edit 1 John Doe, is left in the preamble after the index
            String[] indexAndRest = preamble.trim().split("\\s+", 2);
            if (indexAndRest.length == 2 && indexAndRest[0].matches(INTEGER_REGEX)) {
                ParserUtil.parseDisplayedIndex(indexAndRest[0]); // an invalid index, e.g. 0, is reported first
                throw new ParseException(String.format(MESSAGE_TEXT_AFTER_INDEX, indexAndRest[0], indexAndRest[1]),
                        pe);
            }
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, EditCommand.MESSAGE_USAGE), pe);
        }
    }

    /**
     * Parses {@code Collection<String> tags} into a {@code Set<Tag>} if {@code tags} is non-empty.
     * If {@code tags} contains only one element which is an empty string, it will be parsed into a
     * {@code Set<Tag>} containing zero tags.
     */
    private Optional<Set<Tag>> parseTagsForEdit(Collection<String> tags) throws ParseException {
        assert tags != null;

        if (tags.isEmpty()) {
            return Optional.empty();
        }
        Collection<String> tagSet = tags.size() == 1 && tags.contains("") ? Collections.emptySet() : tags;
        return Optional.of(ParserUtil.parseTags(tagSet));
    }

    /**
     * Parses {@code email} into an {@code Email}, or returns null if {@code email} is empty.
     * A null email tells {@code EditCommand} to remove that email from the person.
     *
     * @throws ParseException if {@code email} is not empty and not a valid email.
     */
    private Email parseEmailForEdit(String email) throws ParseException {
        assert email != null;

        return email.isEmpty() ? null : ParserUtil.parseEmail(email);
    }

}
