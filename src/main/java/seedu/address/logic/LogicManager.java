package seedu.address.logic;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.nio.file.FileAlreadyExistsException;
import java.util.logging.Logger;

import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.collections.ObservableList;
import seedu.address.commons.core.GuiSettings;
import seedu.address.commons.core.LogsCenter;
import seedu.address.logic.commands.Command;
import seedu.address.logic.commands.CommandResult;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.logic.parser.AddressBookParser;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ReadOnlyAddressBook;
import seedu.address.model.person.Person;
import seedu.address.storage.Storage;

/**
 * The main LogicManager of the app.
 */
public class LogicManager implements Logic {
    private static final String MESSAGE_CHANGE_UNDONE = "\nThe command was undone, so your contacts are unchanged.";

    public static final String FILE_OPS_ERROR_FORMAT = "Could not save data to file: %s" + MESSAGE_CHANGE_UNDONE;

    public static final String FILE_OPS_PERMISSION_ERROR_FORMAT =
            "Could not save data to file %s due to insufficient permissions to write to the file or the folder."
            + MESSAGE_CHANGE_UNDONE;

    public static final String FILE_OPS_FOLDER_BLOCKED_ERROR_FORMAT =
            "Could not save data, because %s is a file, so the data folder cannot be created there. "
            + "Move or rename that file, then try again." + MESSAGE_CHANGE_UNDONE;

    private final Logger logger = LogsCenter.getLogger(LogicManager.class);

    private final Model model;
    private final Storage storage;
    private final AddressBookParser addressBookParser;

    /**
     * Constructs a {@code LogicManager} with the given {@code Model} and {@code Storage}.
     */
    public LogicManager(Model model, Storage storage) {
        this.model = model;
        this.storage = storage;
        addressBookParser = new AddressBookParser();
    }

    @Override
    public CommandResult execute(String commandText) throws CommandException, ParseException {
        logger.info("----------------[USER COMMAND][" + commandText + "]");

        CommandResult commandResult;
        Command command = addressBookParser.parseCommand(commandText);
        ReadOnlyAddressBook dataBeforeCommand = new AddressBook(model.getAddressBook());
        commandResult = command.execute(model);

        if (command.modifiesData()) {
            saveData(dataBeforeCommand);
        } else {
            logger.info("Command does not modify data, skipping save.");
        }

        if (!command.preservesPersonSelection()) {
            model.selectPerson(null);
        }

        return commandResult;
    }

    /**
     * Saves the address book to storage. If the data cannot be written, restores the address book to
     * {@code dataBeforeCommand}, so the contacts shown always match the data file.
     *
     * @param dataBeforeCommand the address book as it was before the command was executed.
     * @throws CommandException if the data could not be written.
     */
    private void saveData(ReadOnlyAddressBook dataBeforeCommand) throws CommandException {
        try {
            storage.saveAddressBook(model.getAddressBook());
        } catch (IOException ioe) {
            logger.warning("Could not save data, so the command is undone: " + ioe);
            model.setAddressBook(dataBeforeCommand);
            throw toSaveFailure(ioe);
        }
    }

    private static CommandException toSaveFailure(IOException ioe) {
        String errorFormat;
        if (ioe instanceof AccessDeniedException) {
            errorFormat = FILE_OPS_PERMISSION_ERROR_FORMAT;
        } else if (ioe instanceof FileAlreadyExistsException) {
            // creating the data folder fails this way when a file already has the folder's name
            errorFormat = FILE_OPS_FOLDER_BLOCKED_ERROR_FORMAT;
        } else {
            errorFormat = FILE_OPS_ERROR_FORMAT;
        }
        return new CommandException(String.format(errorFormat, ioe.getMessage()), ioe);
    }

    @Override
    public ObservableList<Person> getFilteredPersonList() {
        return model.getFilteredPersonList();
    }

    @Override
    public ReadOnlyObjectProperty<Person> selectedPersonProperty() {
        return model.selectedPersonProperty();
    }

    @Override
    public GuiSettings getGuiSettings() {
        return model.getGuiSettings();
    }

    @Override
    public void setGuiSettings(GuiSettings guiSettings) {
        model.setGuiSettings(guiSettings);
    }
}
