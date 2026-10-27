package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.Messages.MESSAGE_PERSONS_LISTED_OVERVIEW;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.testutil.TypicalPersons.BENSON;
import static seedu.address.testutil.TypicalPersons.CARL;
import static seedu.address.testutil.TypicalPersons.DANIEL;
import static seedu.address.testutil.TypicalPersons.ELLE;
import static seedu.address.testutil.TypicalPersons.FIONA;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.logic.parser.AddressBookParser;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.FindCriterion;
import seedu.address.model.person.NameContainsKeywordsPredicate;
import seedu.address.model.person.Person;
import seedu.address.model.person.PersonContainsKeywordsPredicate;
import seedu.address.testutil.PersonBuilder;

/**
 * Contains integration tests (interaction with the Model) for {@code FindCommand}.
 */
public class FindCommandTest {
    private Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());
    private Model expectedModel = new ModelManager(getTypicalAddressBook(), new UserPrefs());

    @Test
    public void execute_archiveList_searchesArchivedContactsOnly() throws Exception {
        Person archivedDaniel = DANIEL.copyWithArchivedStatus(true);
        model.setPerson(DANIEL, archivedDaniel);
        AddressBookParser parser = new AddressBookParser();
        parser.parseCommand("list archive").execute(model);

        CommandResult result = parser.parseCommand("find Meier").execute(model);

        assertEquals(List.of(archivedDaniel), model.getFilteredPersonList());
        assertEquals(String.format(MESSAGE_PERSONS_LISTED_OVERVIEW, 1), result.getFeedbackToUser());
    }

    @Test
    public void execute_emptyArchiveList_doesNotSearchActiveContacts() throws Exception {
        new ListCommand(true).execute(model);
        new FindCommand(preparePredicate("Meier")).execute(model);
        assertTrue(model.getFilteredPersonList().isEmpty());
    }

    @Test
    public void execute_archiveListSupportsOtherCriteriaAndNoMatches() throws Exception {
        Person archivedDaniel = DANIEL.copyWithArchivedStatus(true);
        model.setPerson(DANIEL, archivedDaniel);
        AddressBookParser parser = new AddressBookParser();
        parser.parseCommand("list archive").execute(model);
        parser.parseCommand("find /patientNo 87").execute(model);
        assertEquals(List.of(archivedDaniel), model.getFilteredPersonList());

        parser.parseCommand("list archive").execute(model);
        parser.parseCommand("find Benson").execute(model);
        assertTrue(model.getFilteredPersonList().isEmpty());
    }

    @Test
    public void execute_otherViews_searchesAllActiveContacts() throws Exception {
        model.setPerson(DANIEL, DANIEL.copyWithArchivedStatus(true));
        AddressBookParser parser = new AddressBookParser();
        for (String command : List.of("list", "filter /name Carl", "sort name desc", "sort recent")) {
            parser.parseCommand("list").execute(model);
            parser.parseCommand(command).execute(model);
            parser.parseCommand("find Meier").execute(model);
            assertEquals(List.of(BENSON), model.getFilteredPersonList(), command);
        }
    }

    @Test
    public void execute_filteredOrSortedArchiveView_searchesArchivedContactsOnly() throws Exception {
        Person archivedDaniel = DANIEL.copyWithArchivedStatus(true);
        model.setPerson(DANIEL, archivedDaniel);
        AddressBookParser parser = new AddressBookParser();
        for (String command : List.of("filter /name Daniel", "sort name desc", "sort recent")) {
            parser.parseCommand("list archive").execute(model);
            parser.parseCommand(command).execute(model);
            parser.parseCommand("find Meier").execute(model);
            assertEquals(List.of(archivedDaniel), model.getFilteredPersonList(), command);
        }
    }

    @Test
    public void execute_repeatedArchiveSearches_neverReturnsActiveContacts() throws Exception {
        Person archivedDaniel = DANIEL.copyWithArchivedStatus(true);
        model.setPerson(DANIEL, archivedDaniel);
        AddressBookParser parser = new AddressBookParser();
        parser.parseCommand("list archive").execute(model);
        parser.parseCommand("find Meier").execute(model);
        assertEquals(List.of(archivedDaniel), model.getFilteredPersonList());
        parser.parseCommand("find Benson").execute(model);
        assertTrue(model.getFilteredPersonList().isEmpty());
        parser.parseCommand("find Meier").execute(model);
        assertEquals(List.of(archivedDaniel), model.getFilteredPersonList());

        parser.parseCommand("list").execute(model);
        parser.parseCommand("find Meier").execute(model);
        assertEquals(List.of(BENSON), model.getFilteredPersonList());
    }

    @Test
    public void execute_phoneCriterion_filtersConsecutiveDigitsWithoutChangingStoredContacts() {
        Model phoneModel = new ModelManager();
        Person match = new PersonBuilder().withName("Alice").withPatientNo("900087000").build();
        Person nonMatch = new PersonBuilder().withName("Bob").withPatientNo("89700000").build();
        phoneModel.addPerson(match);
        phoneModel.addPerson(nonMatch);
        phoneModel.updateFilteredPersonList(person -> false);
        FindCommand command =
                new FindCommand(new PersonContainsKeywordsPredicate(FindCriterion.PATIENT_PHONE, List.of("87")));
        assertEquals(String.format(MESSAGE_PERSONS_LISTED_OVERVIEW, 1),
                command.execute(phoneModel).getFeedbackToUser());
        assertEquals(List.of(match), phoneModel.getFilteredPersonList());
        assertEquals(List.of(match, nonMatch), phoneModel.getAddressBook().getPersonList());
        assertFalse(command.modifiesData());
    }

    /**
     * Checks that command equality depends on the search predicate.
     */
    @Test
    public void equals() {
        NameContainsKeywordsPredicate firstPredicate =
                new NameContainsKeywordsPredicate(List.of("first"));
        NameContainsKeywordsPredicate secondPredicate =
                new NameContainsKeywordsPredicate(List.of("second"));

        FindCommand findFirstCommand = new FindCommand(firstPredicate);
        FindCommand findSecondCommand = new FindCommand(secondPredicate);

        // same object -> returns true
        assertTrue(findFirstCommand.equals(findFirstCommand));

        // same values -> returns true
        FindCommand findFirstCommandCopy = new FindCommand(firstPredicate);
        assertTrue(findFirstCommand.equals(findFirstCommandCopy));

        // different types -> returns false
        assertFalse(findFirstCommand.equals(1));

        // null -> returns false
        assertFalse(findFirstCommand.equals(null));

        // different person -> returns false
        assertFalse(findFirstCommand.equals(findSecondCommand));
    }

    /**
     * Checks that an empty keyword list produces no matches.
     */
    @Test
    public void execute_zeroKeywords_noPersonFound() {
        String expectedMessage = String.format(MESSAGE_PERSONS_LISTED_OVERVIEW, 0);
        NameContainsKeywordsPredicate predicate = preparePredicate(" ");
        FindCommand command = new FindCommand(predicate);
        expectedModel.updateFilteredPersonList(predicate);
        assertCommandSuccess(command, model, expectedMessage, expectedModel);
        assertEquals(List.of(), model.getFilteredPersonList());
    }

    /**
     * Checks that persons matching any supplied keyword are displayed.
     */
    @Test
    public void execute_multipleKeywords_multiplePersonsFound() {
        String expectedMessage = String.format(MESSAGE_PERSONS_LISTED_OVERVIEW, 3);
        NameContainsKeywordsPredicate predicate = preparePredicate("Kurz Elle Kunz");
        FindCommand command = new FindCommand(predicate);
        expectedModel.updateFilteredPersonList(predicate);
        assertCommandSuccess(command, model, expectedMessage, expectedModel);
        assertEquals(List.of(CARL, ELLE, FIONA), model.getFilteredPersonList());
    }

    /**
     * Checks that partial keywords match names regardless of case.
     */
    @Test
    public void execute_partialKeywords_multiplePersonsFound() {
        String expectedMessage = String.format(MESSAGE_PERSONS_LISTED_OVERVIEW, 3);
        NameContainsKeywordsPredicate predicate = preparePredicate("URZ eLl UNZ");
        FindCommand command = new FindCommand(predicate);
        expectedModel.updateFilteredPersonList(predicate);
        assertCommandSuccess(command, model, expectedMessage, expectedModel);
        assertEquals(List.of(CARL, ELLE, FIONA), model.getFilteredPersonList());
    }

    /**
     * Checks that the command's string representation includes its predicate.
     */
    @Test
    public void toStringMethod() {
        NameContainsKeywordsPredicate predicate = new NameContainsKeywordsPredicate(List.of("keyword"));
        FindCommand findCommand = new FindCommand(predicate);
        String expected = FindCommand.class.getCanonicalName() + "{predicate=" + predicate + "}";
        assertEquals(expected, findCommand.toString());
    }

    /**
     * Parses {@code userInput} into a {@code NameContainsKeywordsPredicate}.
     */
    private NameContainsKeywordsPredicate preparePredicate(String userInput) {
        return new NameContainsKeywordsPredicate(List.of(userInput.split("\\s+")));
    }
}
