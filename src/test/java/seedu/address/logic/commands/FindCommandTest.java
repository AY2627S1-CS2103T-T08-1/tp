package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.Messages.MESSAGE_PERSONS_LISTED_OVERVIEW;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.testutil.TypicalPersons.CARL;
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
    public void execute_extraSpacesWithinSearchText_returnsSameMatches() throws Exception {
        Model searchModel = new ModelManager();
        Person match = new PersonBuilder().withName("D Est").withAddress("D Est").build();
        Person containingPhrase = new PersonBuilder().withName("Dr D Est Tan").withAddress("Dr D Est Tan").build();
        searchModel.addPerson(match);
        searchModel.addPerson(containingPhrase);
        for (String name : List.of("Kim Lee", "D Tan", "Est Lee", "D Middle Est", "Est D", "Brandy")) {
            searchModel.addPerson(new PersonBuilder().withName(name).withAddress(name).build());
        }
        AddressBookParser parser = new AddressBookParser();
        for (String command : List.of("find D Est", "find D       Est   ",
                "find /name D       Est   ", "find /address D       Est   ")) {
            parser.parseCommand(command).execute(searchModel);
            assertEquals(List.of(match, containingPhrase), searchModel.getFilteredPersonList(), command);
        }
    }

    @Test
    public void execute_singleSubstring_stillMatchesWithinName() throws Exception {
        Model searchModel = new ModelManager();
        Person brandy = new PersonBuilder().withName("Brandy").build();
        searchModel.addPerson(brandy);
        searchModel.addPerson(new PersonBuilder().withName("Dylan").build());
        searchModel.addPerson(new PersonBuilder().withName("D Y").build());
        for (String command : List.of("find dy", "find /name dy")) {
            new AddressBookParser().parseCommand(command).execute(searchModel);
            assertEquals(List.of(brandy, new PersonBuilder().withName("Dylan").build()),
                    searchModel.getFilteredPersonList());
        }
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
