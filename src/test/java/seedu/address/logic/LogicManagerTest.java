package seedu.address.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.Messages.MESSAGE_UNKNOWN_COMMAND;
import static seedu.address.logic.commands.CommandTestUtil.ADDRESS_DESC_AMY;
import static seedu.address.logic.commands.CommandTestUtil.FAMILY_PHONE_DESC_AMY;
import static seedu.address.logic.commands.CommandTestUtil.NAME_DESC_AMY;
import static seedu.address.logic.commands.CommandTestUtil.PATIENT_EMAIL_DESC_AMY;
import static seedu.address.logic.commands.CommandTestUtil.PHONE_DESC_AMY;
import static seedu.address.logic.commands.CommandTestUtil.VALID_EMAIL_AMY;
import static seedu.address.logic.commands.CommandTestUtil.VALID_FAMILY_PHONE_AMY;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalPersons.AMY;
import static seedu.address.testutil.TypicalPersons.BOB;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.address.logic.commands.AddCommand;
import seedu.address.logic.commands.ClearCommand;
import seedu.address.logic.commands.CommandResult;
import seedu.address.logic.commands.DeleteCommand;
import seedu.address.logic.commands.ExitCommand;
import seedu.address.logic.commands.ListCommand;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.ReadOnlyAddressBook;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.Person;
import seedu.address.storage.JsonAddressBookStorage;
import seedu.address.storage.JsonUserPrefsStorage;
import seedu.address.storage.StorageManager;
import seedu.address.testutil.PersonBuilder;
import seedu.address.testutil.PersonUtil;

public class LogicManagerTest {
    private static final IOException DUMMY_IO_EXCEPTION = new IOException("dummy IO exception");
    private static final IOException DUMMY_AD_EXCEPTION = new AccessDeniedException("dummy access denied exception");

    @TempDir
    public Path temporaryFolder;

    private Model model = new ModelManager();
    private Logic logic;

    @BeforeEach
    public void setUp() {
        JsonAddressBookStorage addressBookStorage =
                new JsonAddressBookStorage(temporaryFolder.resolve("addressBook.json"));
        JsonUserPrefsStorage userPrefsStorage = new JsonUserPrefsStorage(temporaryFolder.resolve("userPrefs.json"));
        StorageManager storage = new StorageManager(addressBookStorage, userPrefsStorage);
        logic = new LogicManager(model, storage);
    }

    @Test
    public void execute_invalidCommandFormat_throwsParseException() {
        String invalidCommand = "uicfhmowqewca";
        assertParseException(invalidCommand, MESSAGE_UNKNOWN_COMMAND);
    }

    @Test
    public void execute_commandExecutionError_throwsCommandException() {
        String deleteCommand = "delete 9";
        assertCommandException(deleteCommand, DeleteCommand.MESSAGE_EMPTY_LIST);
    }

    @Test
    public void execute_validCommand_success() throws Exception {
        String listCommand = ListCommand.COMMAND_WORD;
        assertCommandSuccess(listCommand, ListCommand.MESSAGE_SUCCESS, model);
    }

    @Test
    public void execute_exitWithArguments_preservesContactsDisplayAndSavedData() throws Exception {
        logic.execute(PersonUtil.getAddCommand(AMY));
        logic.execute(PersonUtil.getAddCommand(BOB));
        logic.execute("find Amy");
        Path dataFile = temporaryFolder.resolve("addressBook.json");
        String savedData = Files.readString(dataFile);
        String expectedMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, ExitCommand.MESSAGE_USAGE);

        for (String input : List.of("exit Alice", "exit 1")) {
            assertThrows(ParseException.class, expectedMessage, () -> logic.execute(input));
            assertEquals(List.of(AMY, BOB), model.getAddressBook().getPersonList());
            assertEquals(List.of(AMY), model.getFilteredPersonList());
            assertEquals(savedData, Files.readString(dataFile));
        }

        // A rejected exit must not prevent the user from continuing to use CareBook.
        logic.execute(ListCommand.COMMAND_WORD);
        assertEquals(List.of(AMY, BOB), model.getFilteredPersonList());
    }

    @Test
    public void execute_exitWithWhitespace_returnsExitResultWithoutSaving() throws Exception {
        logic = new LogicManager(model, createStorageThatFailsToSave());

        CommandResult result = logic.execute("  exit \t ");

        assertTrue(result.isExit());
        assertFalse(result.isShowHelp());
        assertEquals(ExitCommand.MESSAGE_EXIT_ACKNOWLEDGEMENT, result.getFeedbackToUser());
    }

    @Test
    public void execute_storageThrowsIoException_throwsCommandException() throws Exception {
        assertCommandFailureForExceptionFromStorage(DUMMY_IO_EXCEPTION, String.format(
                LogicManager.FILE_OPS_ERROR_FORMAT, DUMMY_IO_EXCEPTION.getMessage()));
    }

    @Test
    public void execute_storageThrowsAdException_throwsCommandException() throws Exception {
        assertCommandFailureForExceptionFromStorage(DUMMY_AD_EXCEPTION, String.format(
                LogicManager.FILE_OPS_PERMISSION_ERROR_FORMAT, DUMMY_AD_EXCEPTION.getMessage()));
    }

    @Test
    public void execute_nonModifyingCommandsWithFailingStorage_doNotSave() throws Exception {
        logic = new LogicManager(model, createStorageThatFailsToSave());

        // none of these commands change the data, so the failing save must never be triggered
        for (String commandText : new String[] {ListCommand.COMMAND_WORD, "find alice", "clear", "help", "exit"}) {
            logic.execute(commandText);
        }
    }

    @Test
    public void execute_modifyingCommand_savesDataToFile() throws Exception {
        Path dataFile = temporaryFolder.resolve("addressBook.json");
        assertFalse(Files.exists(dataFile));

        logic.execute(PersonUtil.getAddCommand(AMY));

        assertTrue(Files.exists(dataFile));
    }

    @Test
    public void execute_modifyingCommandWithFailingStorage_attemptsSave() {
        logic = new LogicManager(model, createStorageThatFailsToSave());

        assertThrows(CommandException.class, () -> logic.execute(PersonUtil.getAddCommand(AMY)));
    }

    @Test
    public void execute_clear_doesNotCreateDataFile() throws Exception {
        model.addPerson(AMY);

        CommandResult result = logic.execute(ClearCommand.COMMAND_WORD);

        assertEquals(ClearCommand.MESSAGE_SUCCESS, result.getFeedbackToUser());
        assertTrue(model.getFilteredPersonList().isEmpty());
        assertEquals(List.of(AMY), model.getAddressBook().getPersonList());
        assertFalse(Files.exists(temporaryFolder.resolve("addressBook.json")));
    }

    @Test
    public void execute_clear_preservesSavedContactsAfterReload() throws Exception {
        logic.execute(PersonUtil.getAddCommand(AMY));
        Path dataFile = temporaryFolder.resolve("addressBook.json");
        String savedData = Files.readString(dataFile);

        logic.execute(ClearCommand.COMMAND_WORD);

        assertEquals(savedData, Files.readString(dataFile));
        ReadOnlyAddressBook reloadedAddressBook = new JsonAddressBookStorage(dataFile).readAddressBook().orElseThrow();
        Model reloadedModel = new ModelManager(reloadedAddressBook, new UserPrefs());
        assertEquals(List.of(AMY), reloadedModel.getFilteredPersonList());
    }

    @Test
    public void execute_clearThenFindAndList_restoresContacts() throws Exception {
        model.addPerson(AMY);
        model.addPerson(BOB);

        logic.execute(ClearCommand.COMMAND_WORD);
        assertTrue(model.getFilteredPersonList().isEmpty());
        logic.execute("find Amy");
        assertEquals(List.of(AMY), model.getFilteredPersonList());

        logic.execute(ClearCommand.COMMAND_WORD);
        logic.execute(ListCommand.COMMAND_WORD);
        assertEquals(List.of(AMY, BOB), model.getFilteredPersonList());
        assertEquals(List.of(AMY, BOB), model.getAddressBook().getPersonList());
    }

    @Test
    public void execute_clearWithArguments_preservesDataAndCurrentFilter() throws Exception {
        model.addPerson(AMY);
        model.addPerson(BOB);
        logic.execute("find Amy");
        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.updateFilteredPersonList(person -> person.equals(AMY));

        assertCommandFailure("clear Amy", ParseException.class,
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, ClearCommand.MESSAGE_USAGE), expectedModel);
        assertFalse(Files.exists(temporaryFolder.resolve("addressBook.json")));
    }

    @Test
    public void getFilteredPersonList_modifyList_throwsUnsupportedOperationException() {
        assertThrows(UnsupportedOperationException.class, () -> logic.getFilteredPersonList().remove(0));
    }

    /**
     * Executes the command and confirms that
     * - no exceptions are thrown <br>
     * - the feedback message is equal to {@code expectedMessage} <br>
     * - the internal model manager state is the same as that in {@code expectedModel} <br>
     * @see #assertCommandFailure(String, Class, String, Model)
     */
    private void assertCommandSuccess(String inputCommand, String expectedMessage,
            Model expectedModel) throws CommandException, ParseException {
        CommandResult result = logic.execute(inputCommand);
        assertEquals(expectedMessage, result.getFeedbackToUser());
        assertEquals(expectedModel, model);
    }

    /**
     * Executes the command, confirms that a ParseException is thrown and that the result message is correct.
     * @see #assertCommandFailure(String, Class, String, Model)
     */
    private void assertParseException(String inputCommand, String expectedMessage) {
        assertCommandFailure(inputCommand, ParseException.class, expectedMessage);
    }

    /**
     * Executes the command, confirms that a CommandException is thrown and that the result message is correct.
     * @see #assertCommandFailure(String, Class, String, Model)
     */
    private void assertCommandException(String inputCommand, String expectedMessage) {
        assertCommandFailure(inputCommand, CommandException.class, expectedMessage);
    }

    /**
     * Executes the command, confirms that the exception is thrown and that the result message is correct.
     * @see #assertCommandFailure(String, Class, String, Model)
     */
    private void assertCommandFailure(String inputCommand, Class<? extends Throwable> expectedException,
            String expectedMessage) {
        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        assertCommandFailure(inputCommand, expectedException, expectedMessage, expectedModel);
    }

    /**
     * Executes the command and confirms that
     * - the {@code expectedException} is thrown <br>
     * - the resulting error message is equal to {@code expectedMessage} <br>
     * - the internal model manager state is the same as that in {@code expectedModel} <br>
     * @see #assertCommandSuccess(String, String, Model)
     */
    private void assertCommandFailure(String inputCommand, Class<? extends Throwable> expectedException,
            String expectedMessage, Model expectedModel) {
        assertThrows(expectedException, expectedMessage, () -> logic.execute(inputCommand));
        assertEquals(expectedModel, model);
    }

    /**
     * Returns a {@code StorageManager} that throws an {@code IOException} whenever it is asked to save.
     */
    private StorageManager createStorageThatFailsToSave() {
        JsonAddressBookStorage addressBookStorage =
                new JsonAddressBookStorage(temporaryFolder.resolve("FailingAddressBook.json")) {
                    @Override
                    public void saveAddressBook(ReadOnlyAddressBook addressBook) throws IOException {
                        throw DUMMY_IO_EXCEPTION;
                    }
                };
        JsonUserPrefsStorage userPrefsStorage =
                new JsonUserPrefsStorage(temporaryFolder.resolve("FailingUserPrefs.json"));
        return new StorageManager(addressBookStorage, userPrefsStorage);
    }

    /**
     * Tests the Logic component's handling of an {@code IOException} thrown by the Storage component.
     *
     * @param e the exception to be thrown by the Storage component
     * @param expectedMessage the message expected inside exception thrown by the Logic component
     */
    private void assertCommandFailureForExceptionFromStorage(IOException e, String expectedMessage)
            throws Exception {
        Path prefPath = temporaryFolder.resolve("ExceptionUserPrefs.json");

        // Inject LogicManager with a JsonAddressBookStorage that throws the IOException e when saving
        JsonAddressBookStorage addressBookStorage = new JsonAddressBookStorage(prefPath) {
            @Override
            public void saveAddressBook(ReadOnlyAddressBook addressBook) throws IOException {
                throw e;
            }
        };

        JsonUserPrefsStorage userPrefsStorage =
                new JsonUserPrefsStorage(temporaryFolder.resolve("ExceptionUserPrefs.json"));
        StorageManager storage = new StorageManager(addressBookStorage, userPrefsStorage);

        logic = new LogicManager(model, storage);

        // Triggers the saveAddressBook method by executing an add command
        String addCommand = AddCommand.COMMAND_WORD + NAME_DESC_AMY + PHONE_DESC_AMY
                + FAMILY_PHONE_DESC_AMY + PATIENT_EMAIL_DESC_AMY + ADDRESS_DESC_AMY;

        // the add is undone, so the shown contacts still match the data file
        ModelManager expectedModel = new ModelManager();
        assertCommandFailure(addCommand, CommandException.class, expectedMessage, expectedModel);

        // a retry is not blocked as a duplicate, and is saved once storage works again
        logic = new LogicManager(model, new StorageManager(new JsonAddressBookStorage(
                temporaryFolder.resolve("RetryAddressBook.json")), userPrefsStorage));
        Person expectedPerson = new PersonBuilder(AMY).withFamilyNo(VALID_FAMILY_PHONE_AMY)
                .withPatientEmail(VALID_EMAIL_AMY).withTags().build();
        expectedModel.addPerson(expectedPerson);
        assertCommandSuccess(addCommand, String.format(AddCommand.MESSAGE_SUCCESS, Messages.format(expectedPerson)),
                expectedModel);
    }
}
