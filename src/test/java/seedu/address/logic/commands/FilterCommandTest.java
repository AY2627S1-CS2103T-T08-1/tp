package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.Messages.MESSAGE_PERSONS_LISTED_OVERVIEW;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.testutil.TypicalPersons.ALICE;
import static seedu.address.testutil.TypicalPersons.BENSON;
import static seedu.address.testutil.TypicalPersons.DANIEL;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.Person;
import seedu.address.model.person.PersonMatchesFilterPredicate;

/**
 * Contains integration tests (interaction with the Model) for {@code FilterCommand}.
 */
public class FilterCommandTest {
    private Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());
    private Model expectedModel = new ModelManager(getTypicalAddressBook(), new UserPrefs());

    private static PersonMatchesFilterPredicate tags(String... tags) {
        return new PersonMatchesFilterPredicate(List.of(), List.of(tags), List.of());
    }

    @Test
    public void equals() {
        PersonMatchesFilterPredicate firstPredicate = tags("HIGH");
        PersonMatchesFilterPredicate secondPredicate = tags("LOW");

        FilterCommand filterFirstCommand = new FilterCommand(firstPredicate);
        FilterCommand filterSecondCommand = new FilterCommand(secondPredicate);

        // same object -> returns true
        assertTrue(filterFirstCommand.equals(filterFirstCommand));

        // same values -> returns true
        assertTrue(filterFirstCommand.equals(new FilterCommand(tags("HIGH"))));

        // different types -> returns false
        assertFalse(filterFirstCommand.equals(1));

        // null -> returns false
        assertFalse(filterFirstCommand.equals(null));

        // different criteria -> returns false
        assertFalse(filterFirstCommand.equals(filterSecondCommand));
    }

    @Test
    public void execute_noPersonMatches_noPersonFound() {
        assertFilterResult(tags("MEDIUM"), List.of());
    }

    @Test
    public void execute_tagShared_multiplePersonsFound() {
        assertFilterResult(tags("HIGH"), List.of(ALICE, DANIEL));
    }

    @Test
    public void execute_tagInDifferentCase_personsFound() {
        assertFilterResult(tags("low"), List.of(BENSON));
    }

    @Test
    public void execute_multipleTags_matchesAnyTag() {
        assertFilterResult(tags("HIGH", "LOW"), List.of(ALICE, BENSON, DANIEL));
    }

    @Test
    public void execute_nameKeywords_matchesAnyKeywordPartially() {
        assertFilterResult(
                new PersonMatchesFilterPredicate(List.of("meier", "ALI"), List.of(), List.of()),
                List.of(ALICE, BENSON, DANIEL));
    }

    @Test
    public void execute_addressPhrases_matchesAnyPhrase() {
        assertFilterResult(
                new PersonMatchesFilterPredicate(List.of(), List.of(), List.of("clementi", "jurong west")),
                List.of(ALICE, BENSON));
    }

    @Test
    public void execute_multipleCriteria_matchesAllCriteria() {
        assertFilterResult(
                new PersonMatchesFilterPredicate(List.of("meier"), List.of("HIGH"), List.of()),
                List.of(DANIEL));
        assertFilterResult(
                new PersonMatchesFilterPredicate(List.of("meier"), List.of("HIGH", "LOW"), List.of("clementi")),
                List.of(BENSON));
    }

    @Test
    public void modifiesData_returnsFalse() {
        assertFalse(new FilterCommand(tags("HIGH")).modifiesData());
    }

    @Test
    public void toStringMethod() {
        PersonMatchesFilterPredicate predicate = tags("HIGH");
        FilterCommand filterCommand = new FilterCommand(predicate);
        String expected = FilterCommand.class.getCanonicalName() + "{predicate=" + predicate + "}";
        assertEquals(expected, filterCommand.toString());
    }

    private void assertFilterResult(PersonMatchesFilterPredicate predicate,
            List<Person> expectedPersons) {
        String expectedMessage = String.format(MESSAGE_PERSONS_LISTED_OVERVIEW, expectedPersons.size());
        FilterCommand command = new FilterCommand(predicate);
        expectedModel.updateFilteredPersonList(predicate);
        assertCommandSuccess(command, model, expectedMessage, expectedModel);
        assertEquals(expectedPersons, model.getFilteredPersonList());
    }
}
