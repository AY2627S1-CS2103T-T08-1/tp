package seedu.address;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.UserPrefs;
import seedu.address.storage.JsonAddressBookStorage;
import seedu.address.storage.JsonUserPrefsStorage;
import seedu.address.storage.Storage;
import seedu.address.storage.StorageManager;

public class MainAppTest {

    private static final Path TEST_DATA_FOLDER = Paths.get("src", "test", "data", "JsonAddressBookStorageTest");

    @TempDir
    public Path testFolder;

    private MainApp mainApp;

    @BeforeEach
    public void setUp() {
        mainApp = new MainApp();
    }

    @Test
    public void initModelManager_missingDataFile_startsEmptyWithoutMessage() {
        Model model = mainApp.initModelManager(createStorage(testFolder.resolve("missing.json")), new UserPrefs());

        assertEquals(new AddressBook(), model.getAddressBook());
        assertEquals("", mainApp.getStartupMessage());
    }

    @Test
    public void initModelManager_validDataFile_loadsContactsWithoutMessage() throws Exception {
        Path dataFile = testFolder.resolve("valid.json");
        Storage storage = createStorage(dataFile);
        storage.saveAddressBook(getTypicalAddressBook());

        Model model = mainApp.initModelManager(storage, new UserPrefs());

        assertEquals(getTypicalAddressBook(), new AddressBook(model.getAddressBook()));
        assertEquals("", mainApp.getStartupMessage());
    }

    @Test
    public void initModelManager_notJsonDataFile_startsEmptyWithMessage() {
        Path dataFile = TEST_DATA_FOLDER.resolve("notJsonFormatAddressBook.json");

        Model model = mainApp.initModelManager(createStorage(dataFile), new UserPrefs());

        assertEquals(new AddressBook(), model.getAddressBook());
        assertEquals(MainApp.MESSAGE_DATA_LOADING_FAILED, mainApp.getStartupMessage());
    }

    @Test
    public void initModelManager_invalidContactInDataFile_startsEmptyWithMessage() {
        Path dataFile = TEST_DATA_FOLDER.resolve("invalidAndValidPersonAddressBook.json");

        Model model = mainApp.initModelManager(createStorage(dataFile), new UserPrefs());

        assertEquals(new AddressBook(), model.getAddressBook());
        assertEquals(MainApp.MESSAGE_DATA_LOADING_FAILED, mainApp.getStartupMessage());
    }

    private Storage createStorage(Path addressBookPath) {
        return new StorageManager(new JsonAddressBookStorage(addressBookPath),
                new JsonUserPrefsStorage(testFolder.resolve("prefs.json")));
    }
}
