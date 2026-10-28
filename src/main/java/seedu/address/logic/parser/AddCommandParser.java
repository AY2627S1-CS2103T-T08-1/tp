package seedu.address.logic.parser;

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

import java.util.Set;
import java.util.stream.Stream;

import seedu.address.logic.commands.AddCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.person.Address;
import seedu.address.model.person.Email;
import seedu.address.model.person.Name;
import seedu.address.model.person.Person;
import seedu.address.model.person.Phone;
import seedu.address.model.tag.Tag;

/**
 * Parses input arguments and creates a new AddCommand object
 */
public class AddCommandParser implements Parser<AddCommand> {

    /**
     * Parses the given {@code String} of arguments in the context of the AddCommand
     * and returns an AddCommand object for execution.
     * @throws ParseException if the user input does not conform to the expected format
     */
    public AddCommand parse(String args) throws ParseException {
        ArgumentMultimap argMultimap =
                ArgumentTokenizer.tokenizeStrict(args, AddCommand.MESSAGE_USAGE,
                        PREFIX_NAME, PREFIX_PATIENT_PHONE, PREFIX_FAMILY_PHONE,
                        PREFIX_PATIENT_EMAIL, PREFIX_FAMILY_EMAIL, PREFIX_LEGACY_EMAIL, PREFIX_ADDRESS, PREFIX_TAG);

        if (argMultimap.getValue(PREFIX_LEGACY_EMAIL).isPresent()) {
            throw new ParseException(MESSAGE_LEGACY_EMAIL_PREFIX);
        }

        if (!arePrefixesPresent(argMultimap, PREFIX_NAME, PREFIX_PATIENT_PHONE, PREFIX_FAMILY_PHONE, PREFIX_ADDRESS)
                || !argMultimap.getPreamble().isEmpty()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, AddCommand.MESSAGE_USAGE));
        }

        argMultimap.verifyNoDuplicatePrefixesFor(PREFIX_NAME, PREFIX_PATIENT_PHONE, PREFIX_FAMILY_PHONE,
                PREFIX_PATIENT_EMAIL, PREFIX_FAMILY_EMAIL, PREFIX_LEGACY_EMAIL, PREFIX_ADDRESS);
        Name name = ParserUtil.parseName(argMultimap.getValue(PREFIX_NAME).get());
        Phone patientNo = ParserUtil.parsePhone(argMultimap.getValue(PREFIX_PATIENT_PHONE).get());
        Phone familyNo = ParserUtil.parsePhone(argMultimap.getValue(PREFIX_FAMILY_PHONE).get());
        Email patientEmail = null;
        if (argMultimap.getValue(PREFIX_PATIENT_EMAIL).isPresent()) {
            patientEmail = ParserUtil.parseEmail(argMultimap.getValue(PREFIX_PATIENT_EMAIL).get());
        }
        Email familyEmail = null;
        if (argMultimap.getValue(PREFIX_FAMILY_EMAIL).isPresent()) {
            familyEmail = ParserUtil.parseEmail(argMultimap.getValue(PREFIX_FAMILY_EMAIL).get());
        }
        Address address = ParserUtil.parseAddress(argMultimap.getValue(PREFIX_ADDRESS).get());
        Set<Tag> tagList = ParserUtil.parseTags(argMultimap.getAllValues(PREFIX_TAG));

        Person person = new Person(name, patientNo, familyNo, patientEmail, familyEmail, address, tagList);

        return new AddCommand(person);
    }

    /**
     * Returns true if none of the prefixes contains empty {@code Optional} values in the given
     * {@code ArgumentMultimap}.
     */
    private static boolean arePrefixesPresent(ArgumentMultimap argumentMultimap, Prefix... prefixes) {
        return Stream.of(prefixes).allMatch(prefix -> argumentMultimap.getValue(prefix).isPresent());
    }

}
