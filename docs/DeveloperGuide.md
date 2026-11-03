---
  layout: default.md
  title: "Developer Guide"
  pageNav: 3
---

# CareBook Developer Guide

<!-- * Table of Contents -->
<page-nav-print />

--------------------------------------------------------------------------------------------------------------------

## **Acknowledgements**

* CareBook is developed from [AddressBook-Level3](https://github.com/se-edu/addressbook-level3).
* Libraries used: [JavaFX](https://openjfx.io/) for the GUI, [Jackson](https://github.com/FasterXML/jackson) for
  reading and writing the JSON data file, and [JUnit 5](https://junit.org/junit5/) for testing.
* The documentation website is built with [MarkBind](https://markbind.org/), and its diagrams are drawn with
  [PlantUML](https://plantuml.com/).
* Notable uses of AI:
  * [OpenAI Codex](https://developers.openai.com/codex/) and [Claude Code](https://www.anthropic.com/claude-code)
    were used to help implement features and their supporting UI, generate and refine code, write regression tests,
    debug, refactor, and do preliminary reviews of pull requests.
  * The same tools were used to draft and revise parts of the User Guide, the Developer Guide and the PlantUML
    diagrams, and to check the guides against the product.
  * All AI-assisted work was checked by team members and by the project's automated tests before it was merged.

--------------------------------------------------------------------------------------------------------------------

## **Setting up, getting started**

Refer to the guide [_Setting up and getting started_](SettingUp.md).

--------------------------------------------------------------------------------------------------------------------

## **Design**

### Architecture

<puml src="diagrams/ArchitectureDiagram.puml" width="280" />

The ***Architecture Diagram*** given above explains the high-level design of the App.

The following provides a quick overview of the main components and their interactions.

**Main components of the architecture**

**`Main`** (consisting of classes [`Main`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/Main.java) and [`MainApp`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/MainApp.java)) is in charge of the app launch and shut down.
* At app launch, it initializes the other components in the correct sequence, and connects them up with each other.
* At shut down, it shuts down the other components and invokes cleanup methods where necessary.

The bulk of the app's work is done by the following four components:

* [**`UI`**](#ui-component): The UI of the App.
* [**`Logic`**](#logic-component): The command executor.
* [**`Model`**](#model-component): Holds the data of the App in memory.
* [**`Storage`**](#storage-component): Reads data from, and writes data to, the hard disk.

[**`Commons`**](#common-classes) represents a collection of classes used by multiple other components.

**How the architecture components interact with each other**

The *Sequence Diagram* below shows how the components interact with each other for the scenario where the user issues the command `delete 1`.

<puml src="diagrams/ArchitectureSequenceDiagram.puml" width="574" />

Each of the four main components (also shown in the diagram above),

* defines its *API* in an `interface` with the same name as the Component.
* provides its functionality through a concrete `{Component Name}Manager` class that implements the corresponding API interface.

For example, the `Logic` component defines its API in `Logic.java` and implements it in `LogicManager.java`. Other components interact with a component through its interface rather than its concrete class, preventing them from coupling to that component's implementation, as illustrated in the following partial class diagram.

<puml src="diagrams/ComponentManagers.puml" width="300" />

The sections below give more details of each component.

### UI component

The **API** of this component is specified in [`Ui.java`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/ui/Ui.java)

<puml src="diagrams/UiClassDiagram.puml" alt="Structure of the UI Component"/>

The UI consists of a `MainWindow` and its parts, such as `CommandBox`, `ResultDisplay`, `PersonListPanel`, and `StatusBarFooter`. All of these, including `MainWindow`, inherit from the abstract `UiPart` class, which captures common behavior among classes that represent visible GUI parts.

The `UI` component uses the JavaFX UI framework. The layouts of these UI parts are defined in matching `.fxml` files in `src/main/resources/view`. For example, [`MainWindow.fxml`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/resources/view/MainWindow.fxml) specifies the layout of [`MainWindow`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/ui/MainWindow.java).

The `UI` component,

* executes user commands using the `Logic` component.
* listens for changes to `Model` data so that the UI can be updated with the modified data.
* keeps a reference to the `Logic` component, because the `UI` relies on the `Logic` to execute commands.
* depends on some classes in the `Model` component because it displays `Person` objects from the model.

### Logic component

**API** : [`Logic.java`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/logic/Logic.java)

Here's a (partial) class diagram of the `Logic` component:

<puml src="diagrams/LogicClassDiagram.puml" width="550"/>

When `Logic` is called upon to execute a command, the command is passed to an `AddressBookParser` object, which in turn creates a parser that matches the command (e.g., `DeleteCommandParser`) and uses it to parse the command. The resulting `Command` object is executed by the `LogicManager`, which may call methods of `Model`, and the outcome is returned as a `CommandResult`. The sequence diagrams of individual commands are in the [Implementation](#implementation) section, for example [`delete`](#delete-command), [`filter`](#filter-command), [`sort`](#sort-command) and [`find`](#find-command).

Here are the other classes in `Logic` (omitted from the class diagram above) that are used for parsing a user command:

<puml src="diagrams/ParserClasses.puml" width="600"/>

Every command is recognised in any letter case. Command words are case-insensitive; for example, `list`, `LIST` and `LiSt` are equivalent. Prefixes and arguments follow the case rules of their respective command parsers.

How the parsing works:
* `AddressBookParser#parseCommand()` separates the command word from its arguments and normalizes the word with `toLowerCase(Locale.ROOT)` before dispatching it. All 17 command words are case-insensitive: `add`, `archive`, `unarchive`, `checkup`, `edit`, `delete`, `clear`, `find`, `list`, `filter`, `sort`, `exit`, `help`, `view`, `remark`, `mark` and `unmark`. For example, `view 1`, `VIEW 1` and `vIeW 1` follow the same parsing and execution path.
* `Locale.ROOT` makes command recognition independent of the system locale. Normalization applies only to the command word; arguments retain their original text and follow each command parser's own case and validation rules. New commands with lowercase `COMMAND_WORD` constants inherit this behavior when added to the dispatch switch.
* When called upon to parse a user command, the `AddressBookParser` class creates an `XYZCommandParser` (`XYZ` is a placeholder for the specific command name, e.g., `AddCommandParser`). The parser uses the other classes shown above to parse the user command and create an `XYZCommand` object (e.g., `AddCommand`). The `AddressBookParser` returns that object as a `Command` object.
* All `XYZCommandParser` classes, such as `AddCommandParser` and `DeleteCommandParser`, implement the `Parser` interface so they can be treated similarly where appropriate, for example during testing.

### Model component
**API** : [`Model.java`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/model/Model.java)

<puml src="diagrams/ModelClassDiagram.puml" width="450" />


The `Model` component,

* stores the address book data i.e., all `Person` objects (which are contained in a `UniquePersonList` object).
* stores each person's care notes as a `Remark`, which may have no notes.
* stores each person's priority tags as a `Set<Tag>`. Valid names are `HIGH`, `MEDIUM`, and `LOW`; the set must be empty or contain one priority.
* stores the `Person` objects selected by the current filter, such as search results, in a separate _filtered_ list. It exposes this list as an unmodifiable `ObservableList<Person>` that the UI can observe and bind to, so the UI updates when the list changes.
* stores a `UserPrefs` object that represents the user’s preferences (currently, just the GUI settings). This is exposed to the outside as a `ReadOnlyUserPrefs` object.
* does not depend on any of the other three components (as the `Model` represents data entities of the domain, they should make sense on their own without depending on other components)


<box type="info" seamless>

**Note:** The alternative, arguably more object-oriented, design below keeps a unique list of tags in `AddressBook`, and each `Person` references tags from that list. This lets `AddressBook` maintain one `Tag` object per unique tag instead of each `Person` holding its own `Tag` objects.<br>

<puml src="diagrams/BetterModelClassDiagram.puml" width="450" />
</box>


### Storage component

**API** : [`Storage.java`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/storage/Storage.java)

<puml src="diagrams/StorageClassDiagram.puml" width="550" />

The `Storage` component,
* can save both address book data and user preference data in JSON format, and read them back into corresponding objects.
* is implemented by `StorageManager`, which delegates the actual JSON file access to `JsonAddressBookStorage` and `JsonUserPrefsStorage` (one class per data file).
* stores each contact as a `JsonAdaptedPerson` with at most one `JsonAdaptedTag`, because a contact can have at most one priority.
* depends on some classes in the `Model` component (because the `Storage` component's job is to save/retrieve objects that belong to the `Model`)

### Common classes

Classes used by multiple components are in the `seedu.address.commons` package.

--------------------------------------------------------------------------------------------------------------------

## **Implementation**

This section describes some noteworthy details on how certain features are implemented.

### Saving data

CareBook saves the contact list to `data/contacts.json` only after a command that changes it. Each `Command` has a `modifiesData()` method that returns `true` by default. Commands that only change what is displayed or show information (`list`, `find`, `filter`, `sort`, `view`, `clear`, `help` and `exit`) override it to return `false`. `delete INDEX` without `/confirm` only shows a warning, so it returns `false` too. Every other command, including `archive`, `checkup`, `edit` and `remark`, saves.

The sequence diagram below shows how `LogicManager` decides whether to save after executing a command.

<puml src="diagrams/SaveSequenceDiagram.puml" alt="How LogicManager decides whether to save after a command" />

1. `LogicManager` parses the command text into a `Command`, calls `Model#createRestorePoint()`, and executes the command. The restore point is a `Runnable` that puts the address book and the displayed list back as they were before the command.
1. It then calls `modifiesData()` on the command.
1. If the command changes data, `LogicManager` gets the address book from the `Model` and passes it to `Storage#saveAddressBook`. `FileUtil#writeToFile` writes the data to a temporary file in the same folder and then replaces the data file with it in one step, so a write that fails partway leaves the old file unchanged. If the data file cannot be replaced, it is written directly instead, as explained in the design consideration below. If the save fails, `LogicManager` runs the restore point and throws a `CommandException`. The user sees `Could not save data to file ...`, or a more specific message when the cause is insufficient permissions or a file in the place of the data folder (`... is a file, so the data folder cannot be created there`), followed by `The command was undone, so your contacts are unchanged.`, and CareBook keeps running.
1. Otherwise, `LogicManager` only logs that the save was skipped, and the data file is left as it is.

**Design consideration:** if a failed save left the change in memory, the app would show a change that is not in the file. It would be lost on exit, or saved later by an unrelated command, and retrying a failed `add` would be rejected as a duplicate. Undoing the command keeps the app and the file the same. The restore point also puts back the displayed list, including the active or archive list, contacts kept shown after `find` or `filter`, and the sort order, so each index still points to the same contact. It clears the details panel, which is acceptable after an error.

**Design consideration:** saving after every command would be simpler, but it would rewrite the data file even when nothing changed. Skipping the save for commands that only show information means a command such as `list` never overwrites a data file that the user is editing by hand.

**Design consideration:** writing straight to the data file would be simpler, but if CareBook stopped or the disk filled up partway through, the file would be left half written and could not be loaded. Writing a temporary file first and then replacing the data file with it in one step means the file is either the old version or the new one. If the file cannot be replaced, for example because another program has it open on Windows, `FileUtil` writes to the data file directly instead, so the save still succeeds. This fallback gives up the guarantee: if the direct write fails partway, for example because the disk is full, the data file can be left incomplete, even though the command is undone in the app. This needs both the replacement and the direct write to fail, so it is rare, and it is the same risk that every save had before the temporary file was added. Failing the save instead would always keep the old file, but saving would then fail whenever another program has the file open.

**Design consideration:** when `data/contacts.json` cannot be loaded, `MainApp` starts with an empty contact list and shows `Data file could not be loaded. Starting with an empty contact list.` The invalid file stays on disk until the next command that changes data, so the user can close CareBook and fix the file first. Refusing to start would leave a user with a broken file unable to use CareBook at all. Backing up the invalid file before it is overwritten would be safer, but it adds more file operations that can fail, so CareBook does not do it. Instead, the UG warns users to back up the file before editing it.

### Add command

The `add` command creates an active contact with a name, patient phone number, family phone number and
address. Patient email, family email and a priority tag are optional. The command word is case-insensitive:
`add`, `Add` and `ADD` follow the same parsing and execution path.

#### Parsing and validation

`AddressBookParser#parseCommand()` recognises the command word and delegates to `AddCommandParser#parse()`.
The parser tokenizes the supported prefixes, checks required fields and rejects repeated non-tag prefixes
before constructing the field objects and a `Person`. Supported prefixes are `/name`, `/patientNo`,
`/familyNo`, `/pEmail`, `/fEmail`, `/address` and `/tag`. Only the `/tag` prefix is case-insensitive.
The legacy `/email` prefix is rejected with instructions to use `/pEmail` or `/fEmail`.

Every `/` must start a complete recognised prefix, separated from its value by whitespace, unless it joins
short parts of a value, as in `s/o`, `a/l` or `4/5`. `ArgumentTokenizer#isSlashInValue()` accepts a `/` that
comes straight after a non-whitespace character and is followed by a letter or digit, but not by 3 or more
letters. Every prefix name has at least 3 letters, so a prefix typed without a space before it, as in
`/address 4 St/patientNo 123`, is rejected instead of being stored as part of the address. Strict
tokenization also rejects `/address 4 St /phone 123` and `/addressExtra 4 St`. `Name` applies the same slash
rule, so a data file cannot hold a name that could not be typed.
The invalid-format response includes `AddCommand.MESSAGE_USAGE`, which also supplies the Add entry in
the Help window and explains the slash rule.
An unknown slash-prefixed token produces `Unknown criterion: PREFIX` before the invalid-format response
and usage. A rejected `/` inside a word produces `ArgumentTokenizer.MESSAGE_SLASH_IN_VALUE`, naming the
word, before the same response. This validation is shared with `EditCommandParser`.

**Design consideration:** rejecting every `/` inside a value would be simpler, but real names such as
`Ravi s/o Kumar` and addresses such as `Blk 4/5` or `#01/02` need it. Accepting every `/` that does not start a
recognised prefix would allow these, but a mistyped prefix, as in `/address 4 St/phone 123`, would then be saved
quietly as part of the address. An escape character such as `\/` would allow any text, but users would have to
learn it and could easily forget it. The current rule accepts the short forms that real names and addresses use,
and still catches a prefix typed in the wrong place, because no prefix name is shorter than 3 letters.

`ParserUtil` validates individual fields and trims surrounding whitespace. `Name` also collapses repeated
spaces, so `Kim    Lee   ` is stored as `Kim Lee` and cannot bypass duplicate detection through spacing.
Priority normalization and validation follow the rules in [Priority tags](#priority-tags).

#### Duplicate detection and execution

`AddCommand#execute()` checks the candidate against the full address book, including archived contacts;
the current display filter and sort order do not limit duplicate checks. `Person#isSamePerson()` and
`UniquePersonList` enforce the contact identity rules:

* A matching patient phone number or a matching non-empty patient email is a duplicate, even if the names
  differ. Patient email comparison ignores case; two omitted patient emails are not a match.
* A shared name alone is allowed when the patient contact details and address distinguish the contacts.
* The same name and address require a name differentiator. Names are compared ignoring case after
  normalization; addresses are compared ignoring case and all whitespace. This comparison does not remove
  internal spaces from the stored address. Different names may share an address.

The sequence diagram below shows a successful add after input and duplicate validation. Error branches
stop before the model is changed; their messages are described below.

<puml src="diagrams/AddSequenceDiagram.puml" alt="Sequence of validating, adding and saving a new contact" />

1. `AddCommandParser` creates the validated `Person` and an `AddCommand` containing it.
1. `AddCommand` calls `requireNoDuplicate()` against the stored contacts to check for a duplicate.
   A duplicate patient phone or email
   produces `AddCommand.MESSAGE_DUPLICATE_PERSON`: `Someone with this patient HP or email already exists in CareBook.`
1. A same-name, same-address conflict without a duplicate patient phone or email instead produces
   `Another contact with this name already has this Address. Include a differentiator such as a number (e.g. Alice 2) or surname.`
1. Otherwise, `Model#addPerson()` adds the contact and restores the active list. The command returns the
   new contact's details in a `CommandResult`.
1. `AddCommand#modifiesData()` returns `true`, so `LogicManager` saves the updated address book through
   `Storage#saveAddressBook()`.

**Error handling:** Invalid prefixes, missing required fields, duplicate prefixes and invalid values cause a
`ParseException`. Duplicate contacts cause a `CommandException`. Neither path adds a contact or saves a
partial entry. If the conflicting contact is archived, the duplicate error appends
`The matching contact is archived. Use list archive to see it.` Shared family contact details are allowed.

**Design consideration:** Normalizing names when constructing `Name` gives parsing, storage loading and
duplicate checks the same representation. Checking the full address book prevents a hidden or archived
contact from being added again, while allowing different patients to share a name or family contact.

### Viewing a contact's full details

The `view INDEX` command displays one contact from the currently displayed list and returns their full details
in a dedicated scrollable panel. It preserves the current display filter without modifying stored contacts.

The sequence diagram below shows a successful `view 1` command, assuming at least one contact is displayed.
It focuses on command parsing and execution; helper calls and UI updates are omitted for clarity.

<puml src="diagrams/ViewSequenceDiagram.puml" alt="Sequence of parsing and executing a successful view 1 command" />

1. `LogicManager#execute()` passes the input to `AddressBookParser#parseCommand()`, which recognises `view`
   and creates a `ViewCommandParser`.
1. `ViewCommandParser#parse()` uses `ParserUtil#parseIndex()` to validate the argument and creates a
   `ViewCommand` containing an `Index`. The user-facing index is one-based; `Index#getZeroBased()` converts it
   to the corresponding Java list index during execution.
1. `ViewCommand#execute()` obtains `Model#getFilteredPersonList()` and checks that the index is in range.
   The index refers to the displayed list, so after `find Alice`, `view 1` selects the first search result.
1. The command retrieves `personToView`, then calls `Model#selectPerson(personToView)` without changing the filter.
   The UI observes the read-only `selectedPersonProperty()` through `Logic` to refresh `PersonDetailsPanel`
   and highlight the selected contact in `PersonListPanel`.
1. A new `CommandResult` confirms the selected contact's name; full details appear in the dedicated panel.
   `LogicManager` checks `ViewCommand#modifiesData()`, which returns `false`, and skips saving.
   `MainWindow` displays the returned feedback in `ResultDisplay`.

Every successful view result also sets `CommandResult#isViewRequested()` to `true`. `MainWindow`
uses this request to refresh highlighting and scroll the selected contact into view. Unlike a selection
change listener, this also runs when the user repeats `view` for the same contact after scrolling away.
No temporary deselection or stored-data change is needed to trigger the UI action.

**Error handling:** Missing, non-numeric or non-positive indexes cause the parser to throw a `ParseException`
with the command usage. During execution, `ViewCommand#validateTargetIndex()` distinguishes an empty
displayed list from an index outside a non-empty list. An empty display produces recovery instructions
to use `list` or `find KEYWORD`; an out-of-range index in a non-empty list reports `Invalid index.`
Both cases throw a `CommandException` and never select from the full stored list as a fallback.
These errors leave stored contacts, selection and the display filter unchanged.

**Selection lifecycle:** `ModelManager` stores a transient contact reference, not its displayed index.
After a command and its required save succeed, `LogicManager` clears selection unless
`Command#preservesPersonSelection()` returns `true`; `ViewCommand` overrides that method to preserve it.
This closes the details panel after other successful commands without resetting their filter or sort.
Parse errors and command-validation failures never reach this cleanup, so they preserve selection.
The following model-level rules also protect callers that update contacts directly:
`setPerson` replaces that reference when the selected contact is edited, including a name change.
Deleting that contact or replacing the address book clears selection. Editing or deleting a different
contact leaves selection unchanged. Updating a filter clears selection only if it hides the contact;
therefore `clear` also hides the details panel. With no selection, the panel and its placeholder are
invisible and unmanaged, so they reserve no layout space. Selection is never serialized.

`setPerson` clears selection instead of retaining the replacement if it leaves the displayed list.
This also handles archiving an active selected contact and restoring one from the archive list.

**Cross-feature integration:** `getFilteredPersonList()` exposes the `SortedList` over the filtered
contacts, so `view`, `remark` and `checkup` use the same displayed indices. Sorting keeps the selected
contact at model level rather than selecting whichever contact moves into its former row; the command
processing policy then clears selection after a successful sort. `PersonDetailsFormatter`
includes the optional checkup appointment, its `[X]` (marked) or `[ ]` (unmarked) status on a separate line, and
every numbered remark. An absent appointment is shown as `Not scheduled` without a status line for an active
contact; an archived contact without an appointment omits the appointment field entirely.
Replacing the selected immutable `Person` through `setPerson()` refreshes these details without
changing the current filter or sort.

**Design rationale:** a separate details panel lets caregivers read complete contact information while
retaining their position in a search or filtered list. Content-sized panels grow up to height caps, with
vertical scrolling and bounded bottom-edge resize handles for longer content. The feedback panel uses
the same sizing behaviour for every command. A coloured card border and a `Selected contact` heading link the
details to the list without treating a changing list index as a permanent contact identifier.

### Clearing the display and exiting

`AddressBookParser#parseCommand()` recognises `clear` and `exit` in any letter case and
rejects non-blank arguments before creating their command objects. For example, `CLEAR` clears the
display and `Exit` closes the application, while `CLEAR 1` and `Exit 1` are rejected.
A parse error leaves the display and stored contacts unchanged.

`ClearCommand#execute()` calls `Model#updateFilteredPersonList(PREDICATE_SHOW_NO_PERSONS)`.
The predicate hides every contact in the current display but does not remove any from the address
book. When the selected contact is no longer in the filtered list, `ModelManager` sets its transient
selection to `null`; the details panel and highlight disappear. `list` restores the active list,
`list archive` restores the archive list, and `find` searches within the current scope.
`ClearCommand#modifiesData()` returns `false`, so `LogicManager` does not save the display change.

`ExitCommand#execute()` returns a `CommandResult` with the exit flag. `MainWindow` checks that flag
after showing the feedback and calls its exit handler. `ExitCommand#modifiesData()` also returns
`false`; neither an invalid `exit` command nor a successful one changes contact data.

### Editing a contact

`edit INDEX [/name NAME] [/patientNo PATIENT_PHONE] ... [/tag PRIORITY]` changes the given fields of a displayed
contact and keeps everything else, including the contact's notes, checkup appointment, checkup mark and archived
status.

<puml src="diagrams/EditSequenceDiagram.puml" alt="Sequence of parsing and executing a successful edit 1 /pEmail command" />

1. `EditCommandParser` splits the input with `ArgumentTokenizer#tokenizeStrict()`, which rejects unknown prefixes
   and slashes that do not satisfy the short-part rule described under Add command. The preamble is the index.
1. Each given field is parsed with `ParserUtil` and stored in an `EditPersonDescriptor`. A field that is not given
   stays empty in the descriptor, which means "keep the current value".
1. `EditCommand#execute()` checks the index against the displayed list, then builds the edited contact in
   `createEditedPerson()` from the descriptor and the current contact.
1. If the edited contact is the same as the current one, `EditCommand` returns `No changes made: ...` and leaves
   the contact as it is.
1. The edited contact is rejected if another contact has the same patient phone number or patient email
   (`Person#hasSamePatientContactDetails()`), or the same name and address (`Person#hasSameNameAndAddress()`).
   Archived contacts count too.
1. If the family HP changed, the edited contact is also rejected when an active contact with a different family HP
   has a checkup at the same date and time (`EditCommand#hasBookingClash()`). `checkup` lets contacts share a
   booking only if their family HP numbers match, so `edit` keeps that rule.
1. `Model#setPersonKeepingDisplayed()` replaces the contact. The displayed list (search results, archive list and
   sort order) stays as it is, and the edited contact stays in it even if it no longer matches the search, so the
   indices on screen do not change. `ModelManager` keeps a set of such contacts, and the filtered list shows a
   contact if it is in the set or matches the search. A later `Model#setPerson()` on a kept contact (e.g. by
   `checkup` or `mark`) keeps the new contact in the set, unless the change moves it out of the current list, as
   `archive` does. The next `list`, `find` or `filter` empties the set. `LogicManager` then saves the data.

**Keeping, setting and removing an email:** emails are optional, so `edit` has to tell three cases apart. A missing
`/pEmail` keeps the email, `/pEmail new@example.com` sets it, and an empty `/pEmail` removes it. An empty
`Optional<Email>` alone cannot separate "keep" from "remove", so `EditPersonDescriptor` also records
`isPatientEmailEdited` (and `isFamilyEmailEdited`). `EditCommand#chooseEmail()` uses the new email only when the
flag is set. Tags need no flag: a missing `/tag` leaves the tags in the descriptor as `null`, while a single
empty `/tag` gives an empty set, which clears the priority.

#### Design considerations

**Aspect: how to remove an optional field**

* **Alternative 1 (current choice):** an empty value, e.g. `edit 1 /pEmail`.
  * Pros: One command, using the prefix the user already knows.
  * Cons: Needs the extra "edited" flag in `EditPersonDescriptor`.
* **Alternative 2:** a separate command or keyword, e.g. `unset 1 /pEmail` or `/pEmail none`.
  * Pros: Removing data is more explicit.
  * Cons: Another command or keyword to learn, and `none` could not be used as a real value.

**Aspect: what the list shows after an edit**

* **Alternative 1 (current choice):** the same list, with the edited contact kept in it.
  * Pros: The indices the user just saw stay valid, so a follow-up command changes the intended contact.
  * Cons: The list can contain a contact that no longer matches the search until the next `list` or `find`.
* **Alternative 2:** the full active list (CareBook's behaviour before v1.5).
  * Pros: Always shows every contact.
  * Cons: The user loses their search, and indices change under them.

### Priority tags

Priority tags use the existing `/tag` prefix and `Tag` model, with names restricted to `HIGH`, `MEDIUM`, and `LOW`.

#### Model and validation

`Tag` is immutable and stores its priority as a `String` in `tagName`. `Tag.isValidTagName` uses `Priority.valueOf` to check whether the input names a `Priority` enum constant. The enum is the source of accepted values (`HIGH`, `MEDIUM`, and `LOW`); there is no separate validation regex. Matching is exact and case-sensitive. Unknown names return `false` by catching `IllegalArgumentException`, while null input throws `NullPointerException`. The constructor does not trim or change case and rejects invalid names with `IllegalArgumentException`.

`Person` copies tags into a `HashSet` and exposes an unmodifiable set through `getTags()`. Equality and hashing of `Tag` use `tagName`, so duplicates collapse into one tag. The `Person` constructor rejects sets with more than one tag using `Person.MESSAGE_TAG_COUNT_CONSTRAINTS`: `A contact can have at most one priority tag.` This keeps the model valid even when constructed outside the command parsers.

#### Command parsing and editing

`AddCommandParser` and `EditCommandParser` collect all `/tag` values. `ArgumentTokenizer` recognizes `/tag` ignoring case and maps variants such as `/TAG` and `/TaG` to the canonical `PREFIX_TAG`. It preserves argument values and the matching rules for other prefixes. `ParserUtil.parseTag` trims each value and converts it to uppercase using `Locale.ROOT` before enum validation. This accepts mixed-case command input independently of the system locale while keeping model and stored values uppercase. Invalid values cause a `ParseException` with `Tag.MESSAGE_CONSTRAINTS`: `Tag names must be HIGH, MEDIUM or LOW`. `ParserUtil.parseTags` removes duplicates after case normalization and rejects more than one distinct priority with a `ParseException` using `Person.MESSAGE_TAG_COUNT_CONSTRAINTS`. It returns an empty set or a singleton set; invalid input prevents the command from changing the model.

* For `add`, omitting `/tag` produces an empty set. An empty `/tag` value is invalid.
* For `edit`, omitting `/tag` leaves tags unchanged. Providing values replaces the whole set through `EditPersonDescriptor` and `EditCommand`.
* `EditCommandParser.parseTagsForEdit` treats a single empty `/tag` as an empty set, clearing all tags. An empty value combined with other `/tag` occurrences is invalid.

For example, `edit 1 /tag MEDIUM` replaces all existing tags with `MEDIUM`, `edit 1 /tag HIGH /tag LOW` is rejected, and `edit 1 /tag` clears them.

### Checkup appointments

The `checkup INDEX dd/mm/yyyy HH:mm` command sets or replaces an appointment for a displayed contact.
`checkup INDEX /delete` removes it. The command word and `/delete` option are case-insensitive.

#### Parsing and validation

`AddressBookParser` delegates to `CheckupCommandParser`, which trims surrounding whitespace, collapses
repeated whitespace and parses the displayed `Index`. A delete request creates a deletion command;
otherwise the command retains the date/time input until the target contact can be checked during execution.
Missing arguments and invalid index syntax cause a `ParseException` with the command usage.

The deletion factory `CheckupCommand.delete(index)` sets `appointmentValue` to `null`. Only `/delete`,
ignoring case, selects this path; bare `delete` is treated as invalid date/time input. Deletion requires an
existing appointment and also works for archived contacts. Archive, date/time and booking-conflict
checks apply only when setting an appointment.

`CheckupCommand#execute()` validates the displayed index before retrieving the contact. For scheduling,
the archived-contact check runs before date/time validation, so an archived contact produces
`Appointments cannot be made with archived contacts. Unarchive the contact to do so.` even if the supplied
date or time is invalid. This ordering requires date/time validation to be deferred until execution.

`CheckupAppointment` uses the fixed `dd/MM/uuuu HH:mm` formatter with `Locale.ROOT` and
`ResolverStyle.STRICT`. Impossible dates and times are rejected without rounding. The appointment is a
`LocalDateTime`; comparison with the current local date and time rejects earlier times today as well as
past dates. The error is `Checkup date and time must not be in the past.` Regional date-display and
12/24-hour settings do not affect parsing or comparison, but the system clock and timezone determine now.

Before changing the model, the command checks all active contacts for an equal `CheckupAppointment`,
excluding the target contact. A shared slot is allowed only when every contact already booked at that slot
has the same `familyNo` as the target. Hidden active contacts participate; archived contacts are excluded. A conflict
throws a `CommandException` with `A contact with this booking date and time already exists. Only contacts
with the same family HP can be booked at the same time.` No appointment, mark, selection or stored contact
is changed by a rejected booking.

The sequence diagram below shows scheduling or deleting a checkup using the displayed contact index.
Validation failures stop before the model is changed or saved. Field-construction helpers and individual
UI observer calls are omitted for clarity.

<puml src="diagrams/CheckupSequenceDiagram.puml" alt="Sequence of parsing, validating, updating and saving a checkup appointment" />

#### Model, UI and storage

The command creates an immutable replacement `Person` and calls `Model#setPerson()`, preserving the
other contact fields. Replacing an appointment clears `checkupMarked`; assigning the same appointment
preserves its mark. A deletion clears both the optional appointment and its mark, and fails with
`This contact has no checkup appointment to delete.` if no appointment exists.

`PersonCard` shows the appointment row with `[X]` or `[ ]` only while an appointment is present.
The row wraps independently of the name, so name truncation cannot hide the appointment's status.
After deletion, the row is invisible and unmanaged, so it reserves no layout space. The details panel
shows status on its own line only for a scheduled appointment. Both displays read `Person#isCheckupMarked()`;
formatting does not modify the contact. A successful checkup command closes the details panel.
The command uses the displayed list index and preserves sorting and filtering; if a view predicate excludes
the replacement because its fields changed, the display is refreshed to keep the updated contact visible.

`CheckupCommand` modifies data, so `LogicManager` saves successful changes through `Storage`.
`JsonAdaptedPerson` stores `checkupAppointment` and `checkupMarked`; absent appointments are represented
by `null` and cannot be marked. Stored past appointments remain loadable because the constructor validates
the date/time format, while the command enforces the not-in-the-past rule for new bookings.

**Design consideration:** Checking archive status and booking conflicts during execution uses the current
model state and ensures the displayed index refers to the selected contact. Keeping parsing and storage
formatting independent of regional settings makes saved appointments portable between computers.

### Checkup retention when archiving

`ArchiveCommand` removes a checkup only when it is unmarked and its calendar date is after today in the
system's local timezone at execution. It uses `Person#withoutCheckup()` on the archived copy to clear
the appointment and its mark while preserving all other contact details. The success message formats
the updated contact and explicitly reports when a checkup was removed.

Today's appointments are retained, including those later in the day, to avoid losing the contact's
same-day schedule when they are archived. Past appointments retain their marked or unmarked status,
and marked appointments are retained even when dated in the future to preserve the recorded completion
status. Retention preserves a record; it does not reserve the appointment slot. Booking-conflict checks
exclude archived contacts, so an active contact may still be booked at the same time as a retained
archived appointment. This is a deliberate tradeoff between preserving the schedule record and allowing
active contacts to use slots previously assigned to archived contacts.

### Marking and unmarking checkups

`mark INDEX` and `unmark INDEX` change the `checkupMarked` flag of a contact with an appointment.
The command words are case-insensitive. Both commands use the current displayed index, including
filtered, sorted and archived lists; neither changes the appointment date/time or the archive status.

#### Parsing and validation

`AddressBookParser` delegates to `MarkCommandParser` or `UnmarkCommandParser`. Both parsers use
`ParserUtil#parseIndex()` to parse one positive index and convert parsing failures to a `ParseException`
with `Invalid index.` During execution, an index outside the displayed list produces a `CommandException`
with the same message. Each command then checks for an appointment before checking its mark.

| Validation failure | `mark` message | `unmark` message |
| --- | --- | --- |
| No appointment | `Contacts with no checkup appointments cannot be marked.` | `Contacts with no checkup appointments cannot be unmarked.` |
| Mark already has the requested state | `Marked contact cannot be marked again.` | `Unmarked contact cannot be unmarked again.` |

Validation failures occur before the model is changed or saved. Neither command validates the appointment
against the current time or performs booking-conflict checks, since the booking itself is unchanged.

#### Execution, UI and storage

1. The command obtains `Model#getFilteredPersonList()` and retrieves the contact at the displayed index.
1. After validation, `MarkCommand` calls `Person#withMarkedCheckup()`; `UnmarkCommand` calls
   `Person#withUnmarkedCheckup()`. These methods create immutable replacements while preserving other fields.
1. The command calls `Model#setPerson(person, updatedPerson)`. `PersonCard` shows `[X]` for a marked
   appointment and `[ ]` for an unmarked appointment; contacts without appointments have no indicator.
1. The command returns `Marked contact: NAME` or `Unmarked contact: NAME` in a `CommandResult`.
1. Both commands inherit `modifiesData() == true`, so `LogicManager` saves the contact list through `Storage`.
   `JsonAdaptedPerson` stores `checkupMarked` alongside `checkupAppointment`. After a successful command,
   `LogicManager` clears selection and closes the details panel, as it does for other non-view commands.

The following diagrams show the successful mark and unmark paths. Validation errors stop before
`Model#setPerson()`. Restore-point creation and individual UI observer calls are omitted for clarity.

<puml src="diagrams/MarkSequenceDiagram.puml" alt="Sequence of parsing, validating, marking and saving a checkup" />

<puml src="diagrams/UnmarkSequenceDiagram.puml" alt="Sequence of parsing, validating, unmarking and saving a checkup" />

### Remarks

The `remark` command keeps numbered care notes on a contact, such as allergies or visiting rules. One command
word accepts four formats:

* `remark INDEX NOTE` adds a note after the contact's last note.
* `remark INDEX /edit NOTE_NUMBER NOTE` replaces the note with that number.
* `remark INDEX /delete NOTE_NUMBER` deletes the note with that number.
* `remark INDEX /clear` deletes all of the contact's notes.

#### Model and storage

`Remark` is an immutable list of notes held by every `Person`. A person with no notes holds an empty `Remark`
(equal to `Remark.EMPTY`), so `Person#getRemark()` never returns `null`. Each note must not be blank or start with
a space, a person cannot have the same note twice, and the notes can have at most
`Remark.MAX_TOTAL_LENGTH` (500) characters in total.
The `Remark` constructor enforces the blank-note and length rules, so a remark that breaks them cannot exist in the
model. `RemarkCommand` rejects duplicate notes.

The notes count towards `Person#equals()` but not `Person#isSamePerson()`, so notes never decide whether two
contacts are the same person. `EditCommand` copies the remark unchanged into the edited person.
`JsonAdaptedPerson` stores the notes as a `remarks` list of strings. A missing `remarks` field loads as no notes,
so data files saved by v1.4 and earlier still load.

#### Parsing and execution

The sequence diagram below shows a successful `remark 1 Diabetic` command.

<puml src="diagrams/RemarkSequenceDiagram.puml" alt="Sequence of parsing and executing a successful remark 1 Diabetic command" />

1. `RemarkCommandParser#parse()` takes the first word as the index. It then looks at the next word: `/edit`,
   `/delete` and `/clear`, in any letter case, select those actions. Anything else means the whole rest of the
   input is a new note.
1. The parser does not use `ArgumentTokenizer`, so a note is kept exactly as typed even when it contains text
   such as `/tag`. Prefixes would split such a note into fields.
1. The parser creates the command through a factory method: `RemarkCommand#add()`, `#edit()`, `#delete()` or
   `#clear()`. Each stores the action and only the values that action needs.
1. `RemarkCommand#execute()` checks the index against the displayed list and that the note number exists.
   It asks the current `Remark` for the updated notes (`withNoteAdded()`, `withNoteReplaced()` or
   `withNoteRemoved()`), checks the total length, and creates the changed person with `Person#withRemark()`.
1. `RemarkCommand` replaces the person with `Model#setPersonKeepingDisplayed()` instead of `Model#setPerson()`.
   `find /remark` searches notes, so a changed note can stop the person from matching the current search.
   This method keeps the person in the displayed list anyway, so the search results, the indices on screen and
   the sort order stay the same until the next `list`, `find` or `filter`. `EditCommand` does the same.

**Error handling:** `remark INDEX` without a note is an error rather than a way to clear the notes, so notes
are never lost because of a missing argument. A missing or invalid index or note number causes a
`ParseException`. An index outside the displayed list, a note number the person does not have, a note the
person already has, `/clear` for a person with no notes, or notes that would be too long cause a
`CommandException`. In each case the person is unchanged.

#### Design considerations

**Aspect: how a note is entered**

* **Alternative 1 (current choice):** Everything after the index is the note, with keywords only straight after
  the index.
  * Pros: Fast to type, and a note can contain `/` without being split.
  * Cons: A note cannot start with `/edit`, `/delete` or `/clear` as its own word.
* **Alternative 2:** A prefix such as `/note NOTE`, parsed by `ArgumentTokenizer` like other commands.
  * Pros: Consistent with `add` and `edit`.
  * Cons: Longer to type, and a note containing a prefix such as `/tag` would be split or rejected.

**Aspect: how notes are stored**

* **Alternative 1 (current choice):** A list of separate notes.
  * Pros: Each note can be edited or deleted by its number, without retyping the others.
  * Cons: Needs a note number for `/edit` and `/delete`.
* **Alternative 2:** One block of free text that `remark` replaces.
  * Pros: Simpler model.
  * Cons: Changing one detail means retyping the whole remark.

**Aspect: how the four formats are implemented**

* **Alternative 1 (current choice):** one `RemarkCommand` class with an `Action` enum, created through the factory
  methods `add()`, `edit()`, `delete()` and `clear()`. `execute()` switches on the action once to compute the notes,
  and `getSuccessMessage()` switches again for the message.
  * Pros: The shared steps (finding the person, the length check, saving) are written once, and all four formats
    stay in one place that matches the single `remark` command word.
  * Cons: Two switches on the same action, and `noteIndex` and `note` are `null` for actions that do not use them.
* **Alternative 2:** one command class per format, e.g. `AddRemarkCommand` and `DeleteRemarkCommand`.
  * Pros: Each class holds only the fields it needs, with no switches.
  * Cons: Four classes repeat the shared steps, or need a common superclass for them.

### Find command

The `find` command searches names using `find INPUT`, or one selected field using `find /CRITERION INPUT`.
The command word and criterion are case-insensitive. Supported criteria are `/name`, `/patientNo`,
`/familyNo`, `/address`, `/pEmail`, `/fEmail`, `/email` (either email), `/tag` and `/remark`.
It changes the displayed contacts and returns the match count without modifying stored contacts.

#### Parsing and phrase matching

`FindCommandParser#parse()` trims surrounding whitespace and collapses repeated whitespace within the
input. Plain input creates a `NameContainsKeywordsPredicate`; criterion input creates a
`PersonContainsKeywordsPredicate`. In both cases, the normalized input is passed as one search phrase,
not as separate alternative keywords. Text matching uses case-insensitive substrings.

* `find D       Est` searches for the continuous phrase `D Est`. It matches `D Est` and `Dr D Est Tan`,
  but not `D Tan`, `Est Lee` or `D Middle Est`.
* `find alex david` does not return `Alex Yeoh` and `David Li` separately; the name must contain
  `alex david` together in that order.
* Single substrings still work: `find dy` matches `Brandy`. `/name` uses the same phrase semantics.
* `/remark` matches a phrase within any one care note, ignoring case and repeated whitespace. Separate
  notes are not joined, and displayed note numbers are not searched.
* Phone searches require one sequence of at least two digits and match consecutive digits:
  `87` matches `900087000`, but not `89700000`. Spaces within a phone number are rejected.

`PersonContainsKeywordsPredicate#test()` selects the requested field before matching. Missing optional
emails, tags or remarks do not match. Leading and trailing whitespace never creates an additional search term.

#### Search scope and execution

Search scope is determined by the active or archive list selected by the user, not by which contacts happen
to be visible. The model retains this scope separately from the search predicate and sort comparator.

* In the active scope, `find` searches all active contacts, replacing any previous search or filter criteria.
* After `list archive`, `find` searches archived contacts only. Repeated searches, filters, sorting and
  zero-match results retain the archive scope and cannot expose active contacts.
* `list` restores the active scope; `list archive` restores the archive scope. A search does not accumulate
  the previous search terms, and it preserves the current sort order.

The sequence diagram below shows parsing and executing `find /name D       Est`, including the choice
between active and archive scope. Helper calls and UI updates are omitted for clarity.

<puml src="diagrams/FindSequenceDiagram.puml" alt="Sequence of a normalized phrase search within active or archived contacts" />

1. `AddressBookParser#parseCommand()` recognises `find` and delegates to `FindCommandParser`.
1. The parser validates one syntax, normalizes the search phrase and creates the corresponding predicate
   and `FindCommand`.
1. `FindCommand#execute()` calls `Model#filterPersonsInCurrentList()` with the search predicate.
1. `ModelManager` uses its `archiveScope` boolean to combine the scope and search predicates, then updates
   the observable list. List switches call `Model#setArchiveScope(boolean)` explicitly; scope is never
   inferred from predicate identity. The UI updates the contact cards.
1. The command uses `Model#getFilteredPersonList().size()` to format
   `Messages.MESSAGE_PERSONS_LISTED_OVERVIEW` in a `CommandResult`.
1. `FindCommand#modifiesData()` returns `false`, so `LogicManager` skips saving and clears the selected
   contact through `Model#selectPerson(null)`, closing the details panel.

**Error handling:** Empty input, missing criterion input, multiple criteria, invalid phone input and mixed
syntax such as `find Alice /address A` cause a `ParseException`. Mixed syntax uses the same invalid-format
response as bare `find`, including `Use either find KEYWORD or find /CRITERION INPUT.` and the supported
criteria. An unknown criterion reports `Unknown criterion: /...`, followed by the invalid-format message
and `FindCommand.MESSAGE_USAGE`. Parsing errors leave stored contacts,
selection and the current display unchanged. A valid search with no matches instead succeeds, displays
an empty list and reports `0 person(s) listed!` without losing its active or archive scope.
The command appends `Use 'list archive' to search through archived contacts` in active scope, or
`Use 'list' to search through active contacts` in archive scope, using `Model#isShowingArchiveList()`.

**Design consideration:** Passing one normalized phrase to the existing predicate classes preserves partial
matching while preventing multiword input from matching unrelated contacts one word at a time. Retaining
scope in the model keeps archive searches reliable even when the current results are empty.

### Delete command

The `delete INDEX` command removes one contact from the displayed list, but only after a confirmation step.
`delete INDEX` on its own shows a warning and leaves the contact untouched. The contact is deleted only when the user
repeats the command as `delete INDEX /confirm`. The index refers to the displayed list, so it is affected by `filter`,
`find` and `sort`.

The sequence diagram below shows a `delete 1` command. The `alt` fragment shows both outcomes: the confirmed
`delete 1 /confirm` deletes the contact, and the unconfirmed `delete 1` only returns the warning. It focuses on
parsing and execution; helper calls and UI updates are omitted for clarity.

<puml src="diagrams/DeleteSequenceDiagram.puml" alt="Sequence of parsing and executing a delete 1 command" />

<box type="info" seamless>

**Note:** The lifeline for `DeleteCommandParser` should end at the destroy marker (X), but due to a limitation of PlantUML, the lifeline continues till the end of diagram.
</box>

1. `LogicManager#execute()` passes the input to `AddressBookParser#parseCommand()`, which recognises `delete`
   and creates a `DeleteCommandParser`.
1. `DeleteCommandParser#parse()` checks that the argument is a positive integer, optionally followed by the
   case-insensitive `/confirm` flag, and throws a `ParseException` otherwise. If valid, it creates a `DeleteCommand`
   holding the `targetIndex` and whether the command was confirmed.
1. `DeleteCommand#execute()` calls `Model#getFilteredPersonList()` to get the list currently shown to the user. If this
   list is empty, or `targetIndex` is beyond its size, a `CommandException` is thrown.
1. Otherwise, the command retrieves the `Person` at `targetIndex`. If the command was not confirmed, it returns a
   `CommandResult` containing a warning (and the `delete INDEX /confirm` command to enter) without changing the model.
1. If the command was confirmed, it calls `Model#deletePerson(personToDelete)` and returns a `CommandResult` with the
   details of the deleted contact.
1. `LogicManager` checks `DeleteCommand#modifiesData()`, which returns whether the command was confirmed. Only a
   confirmed delete is saved to storage.

**Error handling:** A missing or non-numeric index, or any other extra argument, causes
`DeleteCommandParser#parse()` to throw a `ParseException` with the command usage. A zero or negative index causes it
to throw a `ParseException` with the message `Index must be a positive integer.` An empty displayed list, or an index
larger than the displayed list, causes `DeleteCommand#execute()` to throw a `CommandException`. All of these leave the
stored contacts unchanged.


### Filter command

The `filter` command narrows the displayed list to contacts matching all of the given criteria: `/name`, `/tag` and
`/address`. Within one criterion, matching any one of the values is enough, and a contact must satisfy every criterion
that was given. Matching ignores case. A filter keeps the active or archived scope currently shown, and it does not
modify the stored contacts. It searches the whole scope, not only the contacts currently displayed, so it replaces an
earlier `filter` or `find` instead of narrowing it.

The sequence diagram below shows a successful `filter /tag HIGH` command. It focuses on parsing and execution; helper
calls and UI updates are omitted for clarity.

<puml src="diagrams/FilterSequenceDiagram.puml" alt="Sequence of parsing and executing a successful filter /tag HIGH command" />

1. `LogicManager#execute()` passes the input to `AddressBookParser#parseCommand()`, which recognises `filter`
   and creates a `FilterCommandParser`. It then calls `parse(" /tag HIGH")`.
1. `FilterCommandParser#parse()` first replaces every run of whitespace (spaces, tabs and newlines) with a single
   space, so a prefix is recognised after any whitespace and not only after a literal space. It validates complete
   prefix tokens, accepting `/name` and `/tag` in any letter case and `/address` in lowercase. Extended tokens such
   as `/nameExtra` are rejected. It normalizes name prefixes to `/name`, then uses `ArgumentTokenizer` with the
   three supported prefixes. All prefixes may be repeated. Name values are split on whitespace and accumulated
   into one list of alternative keywords; each `/tag` and `/address` value is kept whole. It creates a
   `PersonMatchesFilterPredicate` from the three lists, and a `FilterCommand` that holds this predicate.
1. `PersonMatchesFilterPredicate#test()` treats an empty list as "criterion not applied". Within a criterion, matching
   any one value is enough (OR); a person must satisfy every criterion that was given (AND). Name, tag and address
   matching ignore case, and address matching also collapses each run of whitespace into a single space in both the
   stored address and the phrase.
1. `FilterCommand#execute()` calls `Model#filterPersonsInCurrentList(predicate)`. This combines the predicate with the
   scope of the list currently shown (active contacts after `list`, archived contacts after `list archive`), so the
   filtered list only contains persons in that scope who match the criteria. The scope is kept even when nothing
   matches, and `find` keeps the same active or archive scope.
1. The command calls `Model#getFilteredPersonList()` to count the persons now shown, and returns a `CommandResult`
   with the "N person(s) listed!" message.
1. `LogicManager` checks `FilterCommand#modifiesData()`, which returns `false`, and skips saving.

**Error handling:** `FilterCommandParser#parse()` throws a `ParseException` with the command usage if there is text
before the first prefix, if no criterion is given, if any prefix has an empty value, if any other token starts with `/`
but is not one of the three prefixes (for example `/patientNo`), or if any `/tag` value is not one of `HIGH`, `MEDIUM`
or `LOW` (case-insensitive). A prefix must be a complete whitespace-separated token, so `/nameExtra Alice`
is invalid. Repeated name prefixes, including `/name alice /NAME bob`, are valid and match either name.
A filter with no matches succeeds, displays an empty list,
and reports `0 person(s) listed!`. Use `list` to display all active contacts again.


### Sort command

The `sort` command changes the order in which contacts are displayed. It supports four criteria:
`sort priority [asc|desc]`, `sort name [asc|desc]`, `sort checkup [asc|desc]` and `sort recent`.

**Design.** `ModelManager` keeps a `SortedList` that wraps the `FilteredList`, and
`Model#getFilteredPersonList()` returns the sorted list. This means:

* A sort is applied on top of the current filter, so `filter` and `sort` combine in either order.
* The stored contact order is never changed. `recent` depends on this: the address book keeps contacts in the
  order they were added, so `recent` is the reverse of that order and needs no timestamp.
* Commands that take an `INDEX` (`delete`, `edit`, `view`) read the displayed list, so the index shown on screen
  always refers to the contact that is acted on.
* `list` calls `Model#resetSortedPersonList()`, which restores the order contacts were added.
* Changing a contact does not move it in a sorted list. Otherwise `edit 1 /name Zed` under `sort name` would move
  contact 1 to the end, and the next `remark 1` would change someone else. When a contact is first replaced after a
  sort, `ModelManager` records the position of every displayed contact and switches the `SortedList` to a comparator
  that orders by those positions; the replacing contact takes the position of the one it replaces. The next `sort`,
  `list`, `find` or `filter` drops the positions and applies the chosen sort order again.

`PersonComparators` provides the orderings:

* `byName(ascending)` compares names ignoring case and compares each run of digits by its numeric value, so `Ethan2`
  comes before `Ethan10`. It is also the tie-breaker of `byPriority`.
* `byCheckup(ascending)` orders by checkup appointment date and time, earliest first when ascending. Contacts without a
  checkup appointment are always last in both directions, and ties are ordered by name. `CheckupAppointment` implements
  `Comparable` for this.
* `byPriority(mostUrgentFirst)` orders `HIGH`, `MEDIUM`, `LOW` (or the reverse). Contacts without a priority tag
  are always last in both directions, and ties are ordered by name. In the user-facing command, `desc` means most
  urgent first, so `SortCommand` passes `!ascending` as `mostUrgentFirst`.

The sequence diagram below shows a successful `sort priority desc` command. It focuses on parsing and execution;
creating the comparator and updating the UI are omitted for clarity. The other criteria follow the same flow, and
`sort recent` calls `Model#sortPersonsByMostRecent()` instead of `Model#updateSortedPersonList(comparator)`.

<puml src="diagrams/SortSequenceDiagram.puml" alt="Sequence of parsing and executing a successful sort priority desc command" />

How the `sort` command works:

1. `AddressBookParser` recognises `sort` and creates a `SortCommandParser`.
2. `SortCommandParser#parse()` lowercases and splits the arguments. It throws a `ParseException` if there is no
   criterion, the criterion or direction is unknown, `recent` is given a direction, or there are extra arguments.
   Priority defaults to descending; name and checkup default to ascending. It creates a `SortCommand`.
3. `SortCommand#execute()` calls `Model#updateSortedPersonList(comparator)` (or `Model#sortPersonsByMostRecent()`
   for `recent`) and returns a `CommandResult` describing the order.
4. `SortCommand#modifiesData()` returns `false`, so `LogicManager` does not save. The UI observes the sorted list
   and reorders the contact cards.

#### Design considerations

**Aspect: How sorting is applied**

* **Alternative 1 (current choice):** Wrap the filtered list in a `SortedList` and change only its comparator.
  * Pros: The stored order and the saved data never change, and sorting combines with `find` and `filter` in either order.
  * Cons: Indices refer to the displayed order, so every command that takes an `INDEX` must read the displayed list (they do).
* **Alternative 2:** Reorder the contacts in the address book itself.
  * Pros: No second list to maintain.
  * Cons: Sorting would change and save the stored data, `list` could not restore the order contacts were added, and `recent` would need a timestamp.

**Aspect: How `recent` is determined**

* **Alternative 1 (current choice):** Reverse the order in which contacts are stored, since new contacts are appended.
  * Pros: No new field in the model or the saved file.
  * Cons: Relies on the stored order never being changed, which is why sorting must not reorder the address book.
* **Alternative 2:** Store a creation timestamp for each contact.
  * Pros: Does not depend on the stored order.
  * Cons: Needs a new field, and the saved file format and existing data files must be updated.

### \[Proposed\] Undo/redo feature

#### Proposed Implementation

The proposed undo/redo mechanism is facilitated by `VersionedAddressBook`. It extends `AddressBook` with an undo/redo history, stored internally as an `addressBookStateList` and `currentStatePointer`. Additionally, it implements the following operations:

* `VersionedAddressBook#commit()` -- Saves the current address book state in its history.
* `VersionedAddressBook#undo()` -- Restores the previous address book state from its history.
* `VersionedAddressBook#redo()` -- Restores a previously undone address book state from its history.

These operations are exposed in the `Model` interface as `Model#commitAddressBook()`, `Model#undoAddressBook()` and `Model#redoAddressBook()` respectively.

Given below is an example usage scenario and how the undo/redo mechanism behaves at each step.

Step 1. The user launches the application for the first time. The `VersionedAddressBook` will be initialized with the initial address book state, and the `currentStatePointer` pointing to that single address book state.

<puml src="diagrams/UndoRedoState0.puml" alt="UndoRedoState0" />

Step 2. The user executes `delete 5` command to delete the 5th person in the address book. The `delete` command calls `Model#commitAddressBook()`, causing the modified state of the address book after the `delete 5` command executes to be saved in the `addressBookStateList`, and the `currentStatePointer` is shifted to the newly inserted address book state.

<puml src="diagrams/UndoRedoState1.puml" alt="UndoRedoState1" />

Step 3. The user executes `add n/David …​` to add a new person. The `add` command also calls `Model#commitAddressBook()`, causing another modified address book state to be saved into the `addressBookStateList`.

<puml src="diagrams/UndoRedoState2.puml" alt="UndoRedoState2" />

<box type="info" seamless>

**Note:** If a command fails its execution, it will not call `Model#commitAddressBook()`, so the address book state will not be saved into the `addressBookStateList`.
</box>

Step 4. The user now decides that adding the person was a mistake, and decides to undo that action by executing the `undo` command. The `undo` command will call `Model#undoAddressBook()`, which will shift the `currentStatePointer` once to the left, pointing it to the previous address book state, and restores the address book to that state.

<puml src="diagrams/UndoRedoState3.puml" alt="UndoRedoState3" />


<box type="info" seamless>

**Note:** If the `currentStatePointer` is at index 0, pointing to the initial AddressBook state, then there are no previous AddressBook states to restore. The `undo` command uses `Model#canUndoAddressBook()` to check if this is the case. If so, it will return an error to the user rather
than attempting to perform the undo.
</box>

The following sequence diagram shows how an undo operation goes through the `Logic` component:

<puml src="diagrams/UndoSequenceDiagram-Logic.puml" alt="UndoSequenceDiagram-Logic" />

<box type="info" seamless>

**Note:** The lifeline for `UndoCommand` should end at the destroy marker (X), but due to a limitation of PlantUML, it continues to the end of the diagram.
</box>

Similarly, how an undo operation goes through the `Model` component is shown below:

<puml src="diagrams/UndoSequenceDiagram-Model.puml" alt="UndoSequenceDiagram-Model" />

The `redo` command does the opposite — it calls `Model#redoAddressBook()`, which shifts the `currentStatePointer` once to the right, pointing to the previously undone state, and restores the address book to that state.

<box type="info" seamless>

**Note:** If the `currentStatePointer` is at index `addressBookStateList.size() - 1`, pointing to the latest address book state, then there are no undone AddressBook states to restore. The `redo` command uses `Model#canRedoAddressBook()` to check if this is the case. If so, it will return an error to the user rather than attempting to perform the redo.
</box>

Step 5. The user then decides to execute the command `list`. Commands that do not modify the address book, such as `list`, will usually not call `Model#commitAddressBook()`, `Model#undoAddressBook()` or `Model#redoAddressBook()`. Thus, the `addressBookStateList` remains unchanged.

<puml src="diagrams/UndoRedoState4.puml" alt="UndoRedoState4" />

Step 6. The user executes `delete 1`, which calls `Model#commitAddressBook()` in this proposed implementation. Since the `currentStatePointer` is not pointing at the end of the `addressBookStateList`, all address book states after the `currentStatePointer` will be purged. Reason: It no longer makes sense to redo the `add n/David …` command. This is the behavior that most modern desktop applications follow. The `clear` command only changes the displayed list, so it does not create a new address book state.

<puml src="diagrams/UndoRedoState5.puml" alt="UndoRedoState5" />

The following activity diagram summarizes what happens when a user executes a new command:

<puml src="diagrams/CommitActivityDiagram.puml" width="250" />

#### Design considerations:

**Aspect: How undo & redo execute:**

* **Alternative 1 (current choice):** Saves the entire address book.
  * Pros: Easy to implement.
  * Cons: May have performance issues in terms of memory usage.

* **Alternative 2:** Individual command knows how to undo/redo by
  itself.
  * Pros: Will use less memory (e.g. for `delete`, just save the person being deleted).
  * Cons: We must ensure that the implementation of each individual command is correct.



### \[Proposed\] Dynamic marking of future checkups

This feature is not implemented yet. Currently, `mark INDEX` changes only the completion flag and keeps
an appointment's scheduled date and time, even when the date is after today. Archiving retains marked
appointments to preserve the recorded completion status. `unarchive` restores retained appointments
without checking for booking conflicts, so restoring a contact can create a shared slot between active
contacts with different family HP numbers.

The proposed dynamic marking feature would record when a future-dated checkup was actually completed:

* When `mark INDEX` marks a checkup whose calendar date is after today, it would update the appointment
  to today's date and the time at which it was marked, and set its completion status to marked.
* The comparison and completion time would use the system's local date and time at execution.
* For appointments dated today or earlier, marking would keep the appointment date and time unchanged.
* The updated appointment and completion status would be saved together and shown on the contact card
  and in the details panel.

For example, if a checkup scheduled for 10 November is marked on 2 November at 14:30, the proposed
feature would record its appointment as 2 November at 14:30 and mark it as completed. Archiving would
retain that completion record.

This proposal changes marking behavior; it does not add conflict validation to `unarchive`. A policy for
restoring appointments into an occupied slot remains separate work.

### \[Proposed\] Past checkup tracking

#### Proposed Implementation

The proposed past-checkup feature would allow users to record a contact's previous checkups using
`checkup INDEX /past dd/mm/yyyy HH:mm`. The full history would be available through `view INDEX`,
separately from the contact's currently scheduled appointment.

`CheckupCommandParser` would recognise `/past` after the displayed index and retain the date/time input
for validation during execution. The command would first validate the displayed index, then require a
valid date/time earlier than the current local date and time. It would use the same fixed
`dd/MM/uuuu HH:mm` format and strict calendar validation as `CheckupAppointment`. Invalid input would
leave the history unchanged.

The proposal would extend `Person` with an immutable `pastCheckups` list and the following operations:

* `Person#getPastCheckups()` -- Returns the recorded past checkups without allowing callers to modify the list.
* `Person#withPastCheckup(appointment)` -- Returns a replacement contact with the entry added, preserving
  the upcoming appointment, its mark and other contact details.

Recording the same date/time twice for one contact would be rejected. Historical records would not
reserve booking slots or participate in the conflict checks for upcoming appointments.

`JsonAdaptedPerson` would store the history as an optional list of date/time strings. Older data files
without the field would load with an empty history. Successful additions would be saved through the
existing `LogicManager` and `Storage` flow.

`PersonDetailsFormatter` would include a **Past checkups** section in the details shown by `view INDEX`.
It would display every recorded entry, most recent first, or `None recorded` when the history is empty.
The existing scrollable details panel would make the full history accessible. Archiving or restoring a
contact would preserve its history, which would remain available through `view` in the archive list.

Given below is an example usage scenario and how the past-checkup history would behave at each step.

Step 1. The user executes `list` and identifies the contact at index 1. The contact may already have an
upcoming appointment, which would remain separate from its history.

Step 2. The user executes `checkup 1 /past 01/10/2026 10:00`, assuming that date/time is in the past.
CareBook would add the entry to the contact's history and save the updated contact.

Step 3. The user executes `checkup 1 /past 08/10/2026 14:30`, also for a past date/time. CareBook would
retain the first entry and add the second.

Step 4. The user executes `view 1`. The details panel would show both past checkups, with `08/10/2026 14:30`
before `01/10/2026 10:00`, alongside any upcoming appointment.

Step 5. The user restarts CareBook and executes `view 1` again. Both historical entries would still be
available, since they were saved with the contact.

#### Design considerations:

**Aspect: How past checkups are stored:**

* **Alternative 1 (proposed choice):** Keep a separate immutable list of historical checkups on each contact.
  * Pros: Supports multiple past records while preserving the existing upcoming appointment and its
    booking rules. The complete history can be displayed through `view`.
  * Cons: Requires a new model field, storage mapping and details-panel formatting.

* **Alternative 2:** Use one list for both historical and upcoming checkups.
  * Pros: All checkups share one collection.
  * Cons: Every scheduling, marking, deletion and booking-conflict operation would need to distinguish
    historical entries from the current appointment.

### \[Proposed\] Active and archive list headers

#### Proposed Implementation

The proposed list headers would identify whether the user is looking at active or archived contacts.
A label above the contact list would display **Active contacts** or **Archived contacts**, including when
a search or filter returns no matches.

The header would follow the list scope stored in `ModelManager`, rather than infer the scope from the
displayed contacts or the current filter predicate. The proposal would expose this state as a read-only
observable property through `Model` and `Logic`, and bind the label in `PersonListPanel` to that property.

* `ModelManager#setArchiveScope(boolean)` would update the observable scope when the user switches lists.
* `PersonListPanel` would display the corresponding header above the existing `ListView`.
* Searches, filters and sorting would retain the header for the current scope. Changing the displayed
  results would not, by itself, change the header.
* The header would be transient UI state derived from the model; no additional contact data would be saved.

Given below is an example usage scenario and how the list header would behave at each step.

Step 1. The user launches CareBook. The active list would be displayed, with the header **Active contacts**.

Step 2. The user executes `list archive`. The model would switch to archive scope, and the header would
change to **Archived contacts**.

Step 3. The user executes `find NobodyMatchesThisName`. The list would become empty, but the header would
remain **Archived contacts**, so the user could still identify the list being searched.

Step 4. The user executes `list`. The model would switch back to active scope, and the header would
change to **Active contacts**. Subsequent searches, filters and sorting would retain this header while
that scope is active.

#### Design considerations:

**Aspect: How the header follows the displayed list:**

* **Alternative 1 (proposed choice):** Bind the header to an observable list-scope property.
  * Pros: One source of scope information keeps the header correct across list switches, searches, filters,
    sorting and empty results.
  * Cons: Requires a read-only observable property to be exposed through the model and logic interfaces.

* **Alternative 2:** Set the header text separately in each command.
  * Pros: Does not require an observable scope property.
  * Cons: Commands must repeat UI update rules, and a missed update could leave the header inconsistent
    with the list scope.


--------------------------------------------------------------------------------------------------------------------

## **Documentation, logging, testing, dev-ops**

* [Documentation guide](Documentation.md)
* [Testing guide](Testing.md)
* [Logging guide](Logging.md)
* [DevOps guide](DevOps.md)

--------------------------------------------------------------------------------------------------------------------

## **Appendix: Requirements**

### Product scope

**Target user profile**:

* has a need to manage a significant number of contacts
* works in at a desktop workstation for post-op care
* requires quick access to patient care details and priority
* requires quick access to patients' relevant contacts

**Value proposition**: Streamlined platform for post-op care coordinators to keep track of critical patients and access family contact details.


### User stories

Priorities: High (must have) - `* * *`, Medium (nice to have) - `* *`, Low (unlikely to have) - `*`

| Priority | As a …                                    | I want to …             | So that I can…                                             |
|----------|--------------------------------------------|-------------------------|------------------------------------------------------------|
| `* * *`  | new user                                   | see usage instructions  | refer to instructions when I forget how to use the App     |
| `* * *`  | user                                       | add a new patient       | keep their details and their family's details in one place |
| `* * *`  | user                                       | delete a patient        | remove patients who have completed their recovery          |
| `* * *`  | user                                       | edit a contact's details | keep phone numbers, emails and addresses up to date when they change |
| `* *`    | user                                       | remove a contact's email | stop keeping an email address that is no longer used       |
| `* * *`  | user                                       | view a specific contact | see all of a contact's details quickly when the list gets long |
| `* *`    | caregiver                                  | temporarily hide displayed contact information | keep patient details out of sight without deleting saved records |
| `* *`    | caregiver                                  | close CareBook with a typed command | finish a session without switching to the mouse |
| `* *`    | user                                       | find any contact        | find contacts I might not remember the full names of       |
| `* *`    | user                                       | filter contacts by name, tag and address | focus on the patients I need to look after right now |
| `* *`    | user                                       | sort contacts by name, priority, checkup date or when they were added | see the most urgent or most recent patients first |
| `* * *`  | caregiver                                  | keep care notes on a contact | remember allergies, medication times and visiting rules |
| `* *`    | caregiver                                  | edit or delete one care note | keep notes up to date without retyping the others |
| `* *`    | caregiver                                  | add checkup appointment dates | keep track of future appointments |
| `* *`    | caregiver                                  | remove a checkup appointment | free the slot when an appointment is cancelled |
| `* *`    | caregiver                                  | mark a checkup as done, or unmark it | see which checkups have already taken place |
| `* * *`  | caregiver                                  | give a contact a priority | see which patients need attention first |
| `* * *`  | caregiver                                  | archive a contact I no longer look after | keep their details without crowding the active list |
| `* *`    | caregiver                                  | see archived contacts and restore one | look after a patient again when they return to my care |
| `* * *`  | user                                       | list all active contacts | return to the full list after a search, filter or archive view |
| `* *`    | caregiver                                  | delete all of a contact's notes at once | start their notes again without deleting them one by one |
| `* * *`  | user                                       | have my changes saved automatically | keep my data even if I forget to save or CareBook closes |



### Use cases

(For all use cases below, the **System** is the `CareBook` and the **Actor** is the `user`, unless specified otherwise)

**Use case: Add a contact**

**MSS:**

1. User requests to add a person with a name, patient phone number, family phone number and address,
   optionally providing patient email, family email and a priority tag.
2. CareBook recognises the command word ignoring case, validates the format, normalizes name spacing
   and checks the input fields and contact uniqueness.
3. CareBook adds the person to the active list and saves the contact list.
4. CareBook displays a confirmation with the person's details.

    Use case ends.

**Extensions**

* 2a. A required field is missing, a prefix is unknown or mistyped, a non-tag prefix is repeated, or a slash in a field value violates the short-part rule described under Add command.

    * 2a1. CareBook shows an error message with the correct format and leaves the contact list unchanged.

      Use case resumes at step 1.

* 2b. A field value is invalid, such as a name with invalid characters, a non-numeric phone number, an invalid email or priority tag.

    * 2b1. CareBook shows the field's validation error and leaves the contact list unchanged.

      Use case resumes at step 1.

* 2c. An active or archived contact has the same patient phone number or non-empty patient email, ignoring email case.

    * 2c1. CareBook shows `Someone with this patient HP or email already exists in CareBook.` and does not add the person.

      Use case resumes at step 1.

* 2d. Another contact has the same normalized name and address, but distinct patient contact details.

    * 2d1. CareBook compares the address ignoring case and whitespace, then shows
      `Another contact with this name already has this Address. Include a differentiator such as a number (e.g. Alice 2) or surname.`

      Use case resumes at step 1.

* 2e. Another contact has the same name, but the patient phone, patient email (if provided) and address do not conflict.

    * 2e1. CareBook allows the shared name. Shared family contact details are also allowed.

      Use case resumes at step 3.

**Use case: Delete a person**

**MSS:**

1.  User requests to list persons
2.  CareBook shows a list of persons
3.  User requests to delete a specific person in the list
4.  CareBook shows a warning with the person's details and the command to confirm the deletion
5.  User confirms the deletion with `delete INDEX /confirm`
6.  CareBook deletes the person

    Use case ends.

**Extensions**

* 2a. The list is empty.

    * 2a1. CareBook shows an error message saying there are no persons in the displayed list to delete.

      Use case ends.

* 3a. The given index is invalid.

    * 3a1. CareBook shows an error message.

      Use case resumes at step 2.

* 5a. User does not enter the confirm command.

    * 5a1. CareBook keeps the person. Nothing is deleted or saved.

      Use case ends.

**Use case: Edit a contact**

**MSS:**

1.  User requests to list persons
2.  CareBook shows a list of persons
3.  User requests to edit a specific person in the list, giving new values for one or more fields
4.  CareBook updates the person and shows the edited details

    Use case ends.

**Extensions**

* 2a. The list is empty.

  Use case ends.

* 3a. The given index is invalid.

    * 3a1. CareBook shows an error message.

      Use case resumes at step 2.

* 3b. No field to edit is given.

    * 3b1. CareBook shows an error message saying that at least one field must be provided.

      Use case resumes at step 2.

* 3c. A new value is invalid, such as a phone number with letters or an email without `@`.

    * 3c1. CareBook shows an error message describing the valid format and leaves the person unchanged.

      Use case resumes at step 2.

* 3d. The edited person would have the same patient phone number or patient email as another person, or the
  same name and address. Archived persons count too.

    * 3d1. CareBook shows an error message, saying if the matching person is archived, and leaves the person
      unchanged.

      Use case resumes at step 2.

* 3e. The user gives an email field with no value.

    * 3e1. CareBook removes that email from the person.

      Use case resumes at step 4.

* 3f. Every new value is the same as the person's current value.

    * 3f1. CareBook shows a message saying that no changes were made.

      Use case ends.

* 3g. The person has a checkup at the same date and time as another active person, and the new family HP is
  different from that person's.

    * 3g1. CareBook shows an error message asking the user to change one of the checkups first, and leaves the
      person unchanged.

      Use case resumes at step 2.

**Use case: Manage the care notes of a contact**

**MSS:**

1.  User requests to list persons
2.  CareBook shows a list of persons
3.  User requests to add, change or delete a note of a specific person in the list
4.  CareBook updates the person's notes and shows the result

    Use case ends.

**Extensions**

* 2a. The list is empty.

  Use case ends.

* 3a. The given index is invalid.

    * 3a1. CareBook shows an error message.

      Use case resumes at step 2.

* 3b. No note is given.

    * 3b1. CareBook shows the correct usage and leaves the person's notes unchanged.

      Use case resumes at step 2.

* 3c. The note would make the person's notes longer than 500 characters in total.

    * 3c1. CareBook shows an error message suggesting to delete notes, and leaves the notes unchanged.

      Use case resumes at step 2.

* 3d. The person already has the note being added.

    * 3d1. CareBook shows an error message with the number of that note, and leaves the notes unchanged.

      Use case resumes at step 2.

* 3e. The user requests to edit or delete a note number the person does not have.

    * 3e1. CareBook shows an error message with the number of notes the person has.

      Use case resumes at step 2.

* 3f. The user requests to delete all of the person's notes.

    * 3f1. CareBook deletes all of the person's notes and confirms it.

      Use case ends.

* 3g. The user requests to delete all of the person's notes, but the person has none.

    * 3g1. CareBook shows an error message saying that the person has no notes to clear.

      Use case resumes at step 2.

* 3h. The given note number is not a positive integer.

    * 3h1. CareBook shows an error message saying that the note number must be a positive integer.

      Use case resumes at step 2.

**Use case: List all active contacts**

**MSS:**

1. User requests to view all active contacts using `list`
2. CareBook validates the command format
3. CareBook displays all active (unarchived) contacts
4. CareBook displays a confirmation that all active contacts have been shown

   Use case ends.

**Extensions**

*2a. The command format is invalid. 

    *2a1. CareBook shows an error message
        
        Use case ends.

*2b. The command contains leading or trailing whitespace.

    *2b1. CareBook ignores the whitespace and validates the command.

        Use case resumes at step 3. 

*3a. No active contacts are stored in the address book.

    *3a1. CareBook shows `CareBook is empty`.

        Use case ends.

**Use case: List all archived contacts**

**MSS:**

1. User requests to view all archived contacts using `list archive`.
2. CareBook validates the command format.
3. CareBook displays all archived contacts in the order they were added, clearing any search filters and sorting.
4. CareBook displays a confirmation that all archived contacts have been shown.

   Use case ends.

**Extensions**

* 2a. The command format is invalid.

    * 2a1. CareBook shows an error message.

      Use case ends.

* 2b. The command contains leading or trailing whitespace.

    * 2b1. CareBook ignores the whitespace and validates the command.

      Use case resumes at step 3.

* 3a. No archived contacts are stored in the address book.

    * 3a1. CareBook displays an empty list and shows `No archived contacts`.

      Use case ends.

**Use case: View contact details**

**MSS:**

1. User requests to view a contact using its index in the currently displayed list.
2. CareBook validates the command format.
3. CareBook retrieves the contact at the specified index in the currently displayed list.
4. CareBook shows the contact's full details in the details panel and highlights the contact in the list.
   The current list and stored contact details remain unchanged.

   Use case ends.

**Extensions**

* 2a. The command word is invalid.

    * 2a1. CareBook displays an unknown-command error.

      Use case ends.

* 2b. The index is missing, non-numeric, non-positive, or followed by extra arguments.

    * 2b1. CareBook displays an invalid-format error with the correct usage and an example.

      Use case ends.

* 3a. The currently displayed list is empty, including after `clear` or a search with no matches.

    * 3a1. CareBook explains that no contacts are displayed and suggests using `list` or `find KEYWORD`.

      Use case ends.

* 3b. The index exceeds the number of contacts in the currently displayed list.

    * 3b1. CareBook reports `Invalid index.`

      Use case ends.

Leading and trailing whitespace is ignored during command validation. After filtering, the index refers
to a position in the filtered results, not the full stored list. Unsuccessful requests leave stored
contacts, selection and the displayed list unchanged; there is no fallback to the full stored list.

**Use case: Save contact list after an update**

**MSS:**

1. User requests to list persons.
2. CareBook shows a list of persons.
3. User requests a valid update to the contact list.
4. CareBook updates the contact list and automatically saves it to the local data file.
5. CareBook displays the updated contact list and a success message.

    Use case ends.

**Extensions**

* 3a. The requested change is invalid.

    * 3a1. CareBook displays an error message and leaves the contact list unchanged.

    * 3a2. User submits a corrected update request to the contact list.

      Use case resumes at step 4.

* 4a. CareBook cannot write the data file.

    * 4a1. CareBook undoes the change and displays an error message saying the data could not be saved.

        Use case ends.

**Use case: Find contacts**

**MSS:**

1. User requests to display the active or archived contact list.
2. CareBook displays the requested list.
3. User requests to find contacts by a keyword or one criterion: name, patient phone,
   family phone, address, patient email, family email, either email, priority tag or remark.
4. CareBook validates the command format and criterion ignoring case, trims surrounding
   whitespace and collapses repeated whitespace in text input.
5. CareBook searches the selected list scope for a case-insensitive substring matching the whole input phrase.
   A remark search matches within an individual care note; phone input matches consecutive digits.
6. CareBook displays matching contact cards and the match count without changing stored contacts.

    Use case ends.

**Extensions**

* 4a. Input is empty, a criterion has no input, plain keywords are mixed with a criterion, or multiple criteria are supplied.

    * 4a1. CareBook shows the same invalid-format error and usage, including the supported criteria, and leaves the display unchanged.

      Use case resumes at step 3.

* 4b. The criterion is unknown.

    * 4b1. CareBook reports the unknown criterion and leaves the display unchanged.

      Use case resumes at step 3.

* 4c. Phone input is not one sequence of at least two digits.

    * 4c1. CareBook shows an invalid-format error and leaves the display unchanged.

      Use case resumes at step 3.

* 5a. No contacts match the search phrase in the selected scope.

    * 5a1. CareBook displays an empty list and reports `0 person(s) listed!` followed by
      `Use 'list archive' to search through archived contacts` in the active scope, or
      `Use 'list' to search through active contacts` in the archive scope.

      Use case ends.

* 5b. The current scope is the archive list, including after a search, filter, sort or zero-match result.

    * 5b1. CareBook searches only archived contacts. In the active scope, it searches only active contacts.
      Each new search replaces the previous search criteria; it does not narrow the previous results.

      Use case resumes at step 6.

**Use case: Manage a checkup appointment**

**MSS:**

1. User requests to display contacts.
2. CareBook displays the requested contact list.
3. User requests to schedule, replace or delete the checkup appointment of a contact.
4. CareBook accepts any command-word or `/delete` option case and ignores extra spaces between arguments. For scheduling, it checks archive status before
   date/time validity, then checks for a booking conflict among active contacts.
5. CareBook updates or removes the appointment and its mark as appropriate, refreshes the contact card
   and saves the updated contact list.
6. CareBook displays a confirmation and closes any selected details panel.

    Use case ends.

**Extensions**

* 3a. The command is missing required arguments or contains an invalid index format.

    * 3a1. CareBook shows an invalid-format error with the correct usage and leaves the contact unchanged.

      Use case resumes at step 3.

* 4a. The index is outside the displayed list, including when the list is empty.

    * 4a1. CareBook shows an invalid-index error and leaves the contacts unchanged.

      Use case resumes at step 2.

* 4b. The selected contact is archived and the user requests to schedule or replace an appointment.

    * 4b1. CareBook shows `Appointments cannot be made with archived contacts. Unarchive the contact to do so.`
      This error takes priority over an invalid date or time once the contact index is valid.

      Use case resumes at step 3.

* 4c. The date/time is impossible, has the wrong format or is earlier than the current local date and time.

    * 4c1. CareBook shows the date/time validation error and preserves the existing appointment and mark.

      Use case resumes at step 3.

* 4d. Another active contact has the requested date and time but a different family HP.

    * 4d1. CareBook shows `A contact with this booking date and time already exists. Only contacts with the same family HP can be booked at the same time.`
      Hidden active contacts are checked; archived contacts do not block the slot. No contact is changed.

      Use case resumes at step 3.

* 4e. The user requests deletion, but the contact has no appointment.

    * 4e1. CareBook shows `This contact has no checkup appointment to delete.` and leaves the contact unchanged.

      Use case resumes at step 3.

* 5a. The user assigns the same appointment again.

    * 5a1. CareBook preserves the existing checkup mark. Assigning a different appointment clears the mark.

      Use case resumes at step 6.

* 5b. The user deletes the appointment.

    * 5b1. CareBook removes the appointment row and its status entirely from the contact card.
      The other contact details are preserved.

      Use case resumes at step 6.

      
**Use case: Mark or unmark a checkup**

**MSS:**

1. User requests to display contacts.
2. CareBook displays the requested contact list.
3. User requests to mark or unmark a contact's checkup using its displayed index.
4. CareBook validates the index, confirms that an appointment exists and checks the current mark.
5. CareBook updates the appointment status to `[X]` for mark or `[ ]` for unmark, preserves the appointment and
   other contact details, and saves the updated contact list.
6. CareBook displays a confirmation and closes any selected details panel.

    Use case ends.

**Extensions**

* 4a. The index is missing, malformed, non-positive or outside the displayed list, or extra arguments are given.

    * 4a1. CareBook shows `Invalid index.` and leaves the contacts unchanged.

      Use case resumes at step 3.

* 4b. The contact has no checkup appointment.

    * 4b1. CareBook shows `Contacts with no checkup appointments cannot be marked.` or
      `Contacts with no checkup appointments cannot be unmarked.` and leaves the contact unchanged.

      Use case resumes at step 3.

* 4c. The appointment is already marked for a mark request, or already unmarked for an unmark request.

    * 4c1. CareBook shows `Marked contact cannot be marked again.` or
      `Unmarked contact cannot be unmarked again.` and leaves the contact unchanged.

      Use case resumes at step 3.

**Use case: Filter contacts**

**MSS:**

1.  User requests to filter contacts by one or more criteria (name, tag, address)
2.  CareBook validates the command
3.  CareBook shows the contacts in the current list (active or archived) that match all of the criteria

    Use case ends.

**Extensions**

* 2a. No criterion is given, a prefix has an empty value, there is text before the first prefix, or a tag is not `HIGH`, `MEDIUM` or `LOW`.

    * 2a1. CareBook shows an invalid command format error with the correct usage. The displayed list does not change.

      Use case resumes at step 1.

* 3a. No contact matches the criteria.

    * 3a1. CareBook shows an empty list and the message "0 person(s) listed!". The next filter searches the same list (active or archived).

      Use case ends.

**Use case: Sort contacts**

**MSS:**

1.  User requests to sort the displayed contacts by a criterion (priority, name, checkup or recent) and an optional direction
2.  CareBook validates the command
3.  CareBook reorders the displayed contacts and shows a message describing the new order

    Use case ends.

**Extensions**

* 2a. The criterion is missing or unknown, the direction is unknown, a direction is given for `recent`, or there are extra arguments.

    * 2a1. CareBook shows an invalid command format error with the correct usage. The order does not change.

      Use case resumes at step 1.

* 3a. User wants to see the contacts in the order they were added again.

    * 3a1. User requests to list persons. CareBook clears the sort and shows all active contacts in the order they were added.

      Use case ends.

### Non-Functional Requirements

1.  Should work on any _mainstream OS_ as long as it has Java `25` or above installed
2.  Should be able to finish a command within 1 second for a contact book holding up to 1000 persons 
3. Invalid commands should show a clear error message and usage example
4. A user with above average typing speed for regular English text (i.e. not code, not system admin commands) should be able to accomplish most of the tasks faster using commands than using the mouse.
5. If saving fails, the user should be told what went wrong and CareBook should keep running
6. Should be able to handle empty or malformed commands without crashing
7. Should run without a remote server
8. The GUI should work well for standard screen resolutions of 1920x1080 and higher, and for screen scales of 100% and 125%. It should be usable, though not necessarily optimised, for resolutions of 1280x720 and higher and a screen scale of 150%


### Glossary

* **Mainstream OS**: Windows, Linux, Unix, or macOS
* **Recovery**: Patient is deemed to have reached their normal level of physical and physiological function
* **Relevant contacts**: A patient's emergency contact, or other family contacts
* **Active contact**: A contact that is not archived. `list` shows the active contacts
* **Archived contact**: A contact moved out of the active list with `archive` and shown by `list archive`. Its details are retained except for unmarked checkups scheduled after today, which are removed at archiving.
* **Displayed list**: The contacts currently shown, after any `list`, `find`, `filter` or `sort`. Indices refer to this list
* **Index**: The number shown next to a contact in the displayed list, starting from 1
* **Care note (note)**: One numbered piece of free text about a contact, such as an allergy, added with `remark`. A contact's notes together are its remark
* **Note number**: The number shown before a note on the contact's card, used by `remark INDEX /edit` and `/delete`
* **Priority tag**: A contact's priority, `HIGH`, `MEDIUM` or `LOW`
* **Data file**: `data/contacts.json`, where CareBook saves the contacts automatically
* **Patient HP**: The patient's own phone number, given with `/patientNo`. No two contacts can have the same one
* **Family HP**: The phone number of the patient's family contact, given with `/familyNo`. Contacts can share it
* **Prefix**: The word starting with `/` that comes before a value in a command, such as `/name` or `/patientNo`
* **Checkup appointment**: The date and time of a contact's next checkup, set with `checkup`
* **Checkup status**: Whether a checkup is marked as done, shown as `[X]`, or not, shown as `[ ]`
* **Shared checkup slot**: A checkup date and time that more than one active contact has. This is allowed only if their family HPs match


--------------------------------------------------------------------------------------------------------------------

## **Appendix: Instructions for manual testing**

Given below are instructions to test the app manually.

<box type="info" seamless>

**Note:** These instructions only provide a starting point for testers to work on;
testers are expected to do more *exploratory* testing.
</box>

### Launch and shutdown

1. Initial launch

   1. Download the JAR file and copy it into an empty folder.

   1. Open a terminal in that folder and run `java -jar carebook.jar`.<br>
      Expected: The GUI opens with an empty contact list. The window size may not be optimal.

1. Saving window preferences

   1. Resize the window to an optimal size. Move the window to a different location. Close the window.

   1. Relaunch the app with `java -jar carebook.jar`.<br>
       Expected: The most recent window size and location are retained.


### Viewing, clearing and exiting

1. Start with two active contacts visible. Enter `view 2`.<br>
   Expected: The second displayed contact is highlighted and its full details appear above the list;
   both contact cards remain visible.
1. Enter `view 3`, then `View 1`.<br>
   Expected: The first command reports `Invalid index.` and leaves the selection unchanged.
   `View 1` selects the first contact and shows its full details.
1. Enter `clear`, then `view 1`.<br>
   Expected: The list, highlight and details panel are hidden, but the contacts are not deleted.
   `view 1` reports that no contacts are currently displayed.
1. Enter `list`, then `view 1` again.<br>
   Expected: Both contacts return and the first contact can be selected. `list` does not reselect
   the previously selected contact by itself.
1. Enter `clear 1`, then `Clear`.<br>
   Expected: The first command reports the `clear` usage and leaves the displayed contacts unchanged.
   `Clear` hides the list, highlight and details panel without deleting contacts.
1. Enter `clear 1`.<br>
   Expected: The command reports the `clear` usage. The displayed contacts and selection stay unchanged.
1. Enter `Clear`, then `list`.<br>
   Expected: `Clear` hides the list, highlight and details panel without deleting contacts.
   `list` restores both active contacts without selecting either one.
1. Enter `CLEAR`, then `list`.<br>
   Expected: Uppercase `CLEAR` also hides the displayed contacts without deleting them;
   `list` restores both contacts.
1. Enter `exit 1`, then `Exit`.<br>
   Expected: The first reports that `exit` takes no arguments and leaves the application open.
   `Exit` closes the application. Relaunch CareBook and enter `LIST`. The same two contacts are still saved.

### Adding a person

1. Adding contacts with similar details

   1. Prerequisites: Use a test address book with no contact named `Kim Lee` and no patient phone numbers
      `91001001` or `91001002`.

   1. Test case: `ADD /name Kim    Lee /patientNo 91001001 /familyNo 92002001 /address 1 Test Street`<br>
      Expected: A contact named `Kim Lee` is added. Command capitalization and repeated name spaces are accepted.

   1. Test case: `add /name Kim Lee /patientNo 91001002 /familyNo 92002001 /address 2 Test Street`<br>
      Expected: A second contact is added despite sharing a name and family phone number. Both patient emails are absent.

   1. Test case: Add another contact with patient phone `91001001`, or reuse a non-empty patient email already in the test address book.<br>
      Expected: No contact is added. Duplicate detection includes hidden and archived contacts.

   1. Test case: `add /name Kim Lee /patientNo 91001003 /familyNo 92002001 /address 1TESTstreet`<br>
      Expected: No contact is added. The same-address warning asks for a name differentiator.

   1. Test case: Repeat the previous command with `/name Kim Lee 2`.<br>
      Expected: The contact is added because the name now distinguishes it.

1. Rejecting invalid prefixes and literal slashes

   1. Test case: `add /name Test Patient /patientNo 91001004 /familyNo 92002001 /address 4 St /phone 123`<br>
      Expected: Invalid command format. `/phone 123` is not saved into the address.

   1. Other incorrect address arguments to try: `/addressExtra 4 St` and `/address 4 St/patientNo 123`.<br>
      Expected: No contact is added. The second shows `Unexpected "/" in "St/patientNo"` and the usage.

   1. Test case: `add /name Ravi s/o Kumar /patientNo 91001005 /familyNo 92002001 /address Blk 4/5 Main St #01/02`<br>
      Expected: The contact is added with the name and address as typed.

### Finding contacts

1. Phrase matching and whitespace

   1. Prerequisites: Active contacts named `D Est`, `Dr D Est Tan`, `D Tan`, `Est Lee`, `D Middle Est`,
      `Alex Yeoh`, `David Li` and `Brandy`.

   1. Test case: `find D       Est   `<br>
      Expected: `D Est` and `Dr D Est Tan` match. `D Tan`, `Est Lee` and `D Middle Est` do not match.
      `find /name D Est` and mixed-case `FiNd /NAME d est` give the same results.

   1. Test case: `find alex david`<br>
      Expected: Neither `Alex Yeoh` nor `David Li` matches separately. A name containing `Alex David` would match.

   1. Test case: `find dy`<br>
      Expected: `Brandy` matches the contiguous substring. Surrounding spaces do not affect the search.

   1. Test cases: `find`, `find Alice /address A` and `find /name Alice /address A`.<br>
      Expected: The same invalid-format response with usage and supported criteria; the current display is unchanged.

1. Retaining archive scope

   1. Prerequisites: At least one active and one archived contact with `Kim` in their names.

   1. Test case: Run `list archive`, then `find Kim`.<br>
      Expected: Only archived matches appear.

   1. Test case: Run `find NobodyMatchesThis`, then `sort name`, then `find Kim`.<br>
      Expected: The first search shows zero matches; the final search still returns only archived matches.

   1. Test case: Run `filter /name Kim`, then `find Kim`, then `list`, then `find Kim`.<br>
      Expected: The search after the filter stays within archived contacts. After `list`, only active matches appear.

### Deleting a person

1. Deleting a person while all active contacts are being shown

   1. Prerequisites: List all active contacts using the `list` command, with multiple persons in the list.

   1. Test case: `delete 1`<br>
      Expected: No person is deleted. The status message shows a warning with the first contact's details and asks you to enter `delete 1 /confirm`.

   1. Test case: `delete 1 /confirm`<br>
      Expected: The first contact is deleted from the list. The status message shows the deleted contact's details.

   1. Test case: `delete 0`<br>
      Expected: No person is deleted. The status message shows error details.

   1. Other incorrect delete commands to try: `delete`, `delete abc`, `delete 1 2`, `delete 99` (where 99 is larger than the list size)<br>
      Expected: Similar to previous.

### Filtering persons

1. Filtering the contacts that are being shown

   1. Prerequisites: List all persons using the `list` command, with persons that have different names, priority tags and addresses.

   1. Test case: `filter /tag HIGH`<br>
      Expected: Only persons tagged `HIGH` are shown. The status message shows "N person(s) listed!" with the number shown.

   1. Test case: `filter /tag HIGH /tag LOW`<br>
      Expected: Persons tagged `HIGH` or `LOW` are shown.

   1. Test case: `filter /name ali /tag HIGH`<br>
      Expected: Only persons whose name contains `ali` and who are tagged `HIGH` are shown.

   1. Test case: `filter /name alice /NAME bob`<br>
      Expected: Contacts whose names contain `alice` or `bob` are shown, ignoring case. Adding `/tag HIGH`
      keeps only matches that also have the `HIGH` priority.

   1. Test case: `filter /nameExtra Alice`<br>
      Expected: `Invalid command format!` followed by filter usage. The displayed list is unchanged.

   1. Test case: `filter /address clementi ave`<br>
      Expected: Only persons whose address contains the text `clementi ave` are shown, ignoring case.

   1. Test case: `filter /tag URGENT`<br>
      Expected: The displayed list does not change. The status message shows `Invalid command format!` followed by the filter usage.

   1. Other incorrect filter commands to try: `filter`, `filter HIGH`, `filter /name`, `filter /tag HIGH LOW`<br>
      Expected: Similar to previous.

1. Filtering the archived contacts

   1. Prerequisites: Archive at least one contact tagged `HIGH`, then run `list archive`.

   1. Test case: `filter /tag HIGH`<br>
      Expected: Only archived persons tagged `HIGH` are shown. Running `list` and then `filter /tag HIGH` shows only active persons.

### Sorting persons

1. Sorting the contacts that are being shown

   1. Prerequisites: List all persons using the `list` command, with persons that have different names and priority tags.

   1. Test case: `sort name`<br>
      Expected: Persons are shown from A to Z, ignoring case. The status message shows "Sorted by name: A to Z."

   1. Test case: `sort name desc`<br>
      Expected: Persons are shown from Z to A. The status message shows "Sorted by name: Z to A."

   1. Test case: `sort priority`<br>
      Expected: `HIGH` persons are shown first, then `MEDIUM`, then `LOW`, and persons without a priority tag are last. Persons with the same priority are ordered by name. The status message shows "Sorted by priority: HIGH to LOW."

   1. Test case: `sort recent`<br>
      Expected: The most recently added person is shown first. The status message shows "Sorted by most recently added."

   1. Test case: `filter /tag HIGH` followed by `sort name`<br>
      Expected: Only `HIGH` persons are shown, from A to Z.

   1. Test case: `sort name`, then `edit 1 /name Zed Example`<br>
      Expected: The edited person stays first in the list, so index 1 still refers to them. Running `sort name` again moves them to the end.

   1. Test case: `list` after any sort<br>
      Expected: All active persons are shown again in the order they were added.

   1. Incorrect sort commands to try: `sort`, `sort address`, `sort priority up`, `sort recent desc`<br>
      Expected: The order does not change. The status message shows `Invalid command format!` followed by the sort usage.


### Editing a person

1. Editing a person while all active contacts are being shown

   1. Prerequisites: List all active contacts using the `list` command, with at least two persons in the list.

   1. Test case: `edit 1 /patientNo 91234567 /pEmail patient@example.com`<br>
      Expected: The first contact's patient phone number and patient email change, and its other fields stay the same. The status message shows the edited contact's details.

   1. Test case: `edit 1 /fEmail family@example.com`<br>
      Expected: The family email is set. The patient email from the previous test case is unchanged.

   1. Test case: `edit 1 /pEmail`<br>
      Expected: The patient email is removed. The family email is unchanged.

   1. Test case: `edit 1 /email someone@example.com`<br>
      Expected: No person is edited. The status message says that `/email` is no longer supported and to use `/pEmail` or `/fEmail` instead.

   1. Test case: `edit 1 /patientNo PHONE`, where `PHONE` is the second person's patient phone number.<br>
      Expected: No person is edited. The status message shows `Someone with this patient HP or email already exists in CareBook.`

   1. Other incorrect edit commands to try: `edit 1`, `edit 0 /name Amy`, `edit x /name Amy` (where x is larger than the list size), `edit 1 /patientNo abc` and `edit 1 /pEmail john`<br>
      Expected: No person is edited. The status message shows error details.

1. Editing a person in a filtered list

   1. Prerequisites: Use `find` with a name that matches some, but not all, persons.

   1. Test case: `edit 1 /address 1 Test Street`<br>
      Expected: The first person in the filtered list is edited, and the same search results stay on screen.

   1. Test case: `edit 1 /name Zachary Tan`<br>
      Expected: The first person is renamed and stays first in the results, even though the new name no longer
      matches the search. `list` shows the full list again.

1. Editing an archived contact

   1. Prerequisites: Archive a contact with `archive 1`, then run `list archive`.

   1. Test case: `edit 1 /address 2 Archive Road`<br>
      Expected: The address changes, the contact stays archived, and the archive list stays on screen.

### Checkup appointments

1. Scheduling, replacing and deleting an appointment

   1. Prerequisites: Use `list` to show at least three active contacts. Contacts 1 and 3 share a family HP;
      contact 2 has a different family HP. Choose an unused future date and replace `DATE` below with it
      in `dd/mm/yyyy` format.

   1. Test case: `CHECKUP 1   DATE  14:30   `<br>
      Expected: The first displayed contact has an appointment at `14:30`, followed by `[ ]` on its card.
      Extra spaces and command capitalization do not affect parsing.

   1. Test case: `checkup 2 DATE 14:30`, then `checkup 3 DATE 14:30`.<br>
      Expected: Contact 2 is rejected with the booking-conflict error; contact 3 is booked because the
      family HP matches contact 1. The check includes active contacts hidden by a filter, but excludes archived contacts.

   1. Test case: Mark contact 1, then assign the same appointment again; next run `checkup 1 DATE 15:30`.<br>
      Expected: The identical appointment preserves `[X]`. The changed appointment resets it to `[ ]`.

   1. Test case: `checkup 1 /delete`, then repeat the command.<br>
      Expected: The first command removes the appointment row and status entirely. The second
      reports `This contact has no checkup appointment to delete.` without changing the contact.

   1. Test case: Give contact 1 a long name, schedule an appointment, then run `mark 1` and `view 1`.
      Narrow the window to its minimum width.<br>
      Expected: The card's appointment row wraps and retains `[X]`, independently of name truncation.
      The details panel shows the full wrapping name and `Checkup status: [X]` on a separate line.
      Editing the name preserves this status. Run `unmark 1`, then `view 1`; both displays now show `[ ]`.
      After `checkup 1 /delete` and `view 1`, the card has no appointment row and the details panel shows
      `Checkup appointment: Not scheduled` without a status line.

   1. Test case: Schedule an appointment, close CareBook and relaunch it; then delete the appointment and relaunch again.<br>
      Expected: Each successful change survives reloading. Other contact details are unchanged.

1. Rejecting invalid bookings

   1. Test case: Schedule an appointment for an earlier time today.<br>
      Expected: `Checkup date and time must not be in the past.` The existing appointment and mark are unchanged.

   1. Test case: Archive a contact, run `list archive`, then try `checkup 1 31/02/2030 14:30`.<br>
      Expected: The archive error takes priority over the invalid-date error. Unarchiving the contact
      allows scheduling again, subject to date/time and booking-conflict validation.

   1. Other incorrect inputs to try: `checkup`, `checkup 0 DATE 14:30`, `checkup x DATE 14:30`,
      an out-of-range displayed index, `31/02/2030 14:30` and `DATE 24:00`.<br>
      Expected: Appropriate format, index or date/time errors. No contact data is changed.

### Marking and unmarking checkups

1. Changing and saving the mark

   1. Prerequisites: A displayed contact at index 1 with an unmarked checkup appointment.

   1. Test case: `MaRk   1  `, then `UNMARK 1`.<br>
      Expected: The appointment status changes from `[ ]` to `[X]`, then back to `[ ]`.
      The appointment date/time and other contact details remain unchanged. Each command confirms the change.

   1. Test case: Mark the appointment, restart CareBook, then unmark it and restart again.<br>
      Expected: The marked and unmarked states each survive reloading.

   1. Test case: Find or sort contacts, then mark and unmark a scheduled contact using its displayed index.
      Repeat after `list archive` with an archived contact that has an appointment.<br>
      Expected: The displayed contact is updated, list scope and order are preserved, and archived contacts
      remain archived.

1. Rejecting invalid requests

   1. Test case: Run `mark 1` twice, then `unmark 1` twice.<br>
      Expected: The repeated requests report `Marked contact cannot be marked again.` and
      `Unmarked contact cannot be unmarked again.` respectively, without changing the contact.

   1. Test case: Remove the appointment with `checkup 1 /delete`, then run `mark 1` and `unmark 1`.<br>
      Expected: Each command reports its no-appointment error; no mark indicator appears.

   1. Other incorrect inputs to try: `mark`, `mark 0`, `unmark x`, `unmark 1 extra` and an out-of-range index.<br>
      Expected: `Invalid index.` The contact data and current selection remain unchanged.

### Keeping care notes

1. Adding, editing and deleting notes

   1. Prerequisites: List all active contacts using the `list` command, with at least two persons in the list.

   1. Test case: `remark 1 Allergic to penicillin`<br>
      Expected: The first contact's card shows `Remark:` and `1. Allergic to penicillin`. The status message shows the added note.

   1. Test case: `remark 1 Takes /tag pills at 9am`<br>
      Expected: The card shows the note as typed, including `/tag`, as note 2.

   1. Test case: `remark 1 /edit 2 Takes pills at 10am`<br>
      Expected: Note 2 changes. Note 1 is unchanged.

   1. Test case: `remark 1 /delete 1`<br>
      Expected: The allergy note is deleted, and the pills note becomes note 1.

   1. Test case: `remark 1 /clear`<br>
      Expected: The card no longer shows a `Remark:` line.

   1. Test case: `remark 1`<br>
      Expected: No note changes. The status message shows the correct usage.

   1. Test case: `remark 1 Allergic to penicillin` twice<br>
      Expected: The second command is rejected because the person already has this note. The notes are unchanged.

   1. Other incorrect remark commands to try: `remark /clear` (no index), `remark 0 note`, `remark x note` (where x is larger than the list size), `remark 1 /delete 9` and `remark 1 /edit 1`<br>
      Expected: No note changes. The status message shows error details.

1. Keeping notes after other commands

   1. Test case: `remark 1 Visits after 3pm`, then `edit 1 /address 1 Test Street`<br>
      Expected: The address changes and the note is still shown.

   1. Test case: `find` with a name that matches some, but not all, persons, then `remark 1 Diabetic` and
      `remark 1 Uses a wheelchair`<br>
      Expected: The search results stay on screen, and the first result has both notes.

   1. Test case: `remark 1 Allergic to penicillin`, `find /remark penicillin`, then
      `remark 1 /edit 1 Allergic to aspirin`<br>
      Expected: The person stays first in the results with the new note, even though it no longer matches
      `penicillin`.

   1. Test case: Add a note, close CareBook and relaunch it.<br>
      Expected: The note is still shown.

   1. Test case: Archive a contact, run `list archive`, then `remark 1 Moved to a care home`<br>
      Expected: The note is added, and the contact stays archived and on screen in the archive list.

   1. Test case: Close CareBook, change a contact's `remarks` in `data/contacts.json` to `[ " " ]`, then relaunch.<br>
      Expected: The data file is treated as invalid: CareBook starts with an empty contact list and shows
      "Data file could not be loaded. Starting with an empty contact list."

### Priority tags

1. Adding priority tags

   1. Test case: `add /name Priority Test /patientNo 98765432 /familyNo 91234567 /address Test Street /TAG High /tag high` (use a name not already stored).<br>
      Expected: The contact is added with exactly one tag, displayed as `HIGH`. Repeat with a new name and `/tag HIGH /tag LOW`; the command should show `A contact can have at most one priority tag.` and add no contact.

   1. Test case: Add another uniquely named contact without `/tag`.<br>
      Expected: The contact has no priority tags.

1. Editing and validating priority tags

   1. Prerequisites: Run `list` and ensure at least one contact exists. The following commands act on contact 1.

   1. Test cases: `edit 1 /tag medium`, `edit 1 /TAG Medium`, and `edit 1 /TaG MEDIUM`.<br>
      Expected: All existing tags are replaced with `MEDIUM`.

   1. Test case: `edit 1 /address Updated Street`<br>
      Expected: The address changes and the `MEDIUM` tag remains.

   1. Test case: `edit 1 /tag HIGH /tag MEDIUM /tag LOW`<br>
      Expected: The command shows `A contact can have at most one priority tag.` and leaves the contact unchanged.

   1. Test cases: `edit 1 /tag friends`, `edit 1 /tag HIGH LOW`, and `edit 1 /tag HIGH /tag`.<br>
      Expected: Each shows `Tag names must be HIGH, MEDIUM or LOW`; the contact remains unchanged.

   1. Test case: `edit 1 /TAG`<br>
      Expected: All priority tags are cleared.

1. Saving and loading priority tags

   1. Test case: Set a contact's tag to `HIGH`, then close and relaunch CareBook.<br>
      Expected: The `HIGH` tag is restored. Repeat after clearing the tags; the contact should still have no tags after relaunch.

   1. Test case: Back up `data/contacts.json`, close CareBook, and change a stored tag string to `"friends"`, `"high"`, or `" HIGH "`. Also test a list containing `["HIGH", "LOW"]`. Relaunch for each case.<br>
      Expected: CareBook starts with an empty contact list and shows "Data file could not be loaded. Starting with an empty contact list." Close CareBook and restore the backup before continuing.

### Saving and loading data

1. Dealing with missing/corrupted data files

   1. Prerequisites: Run CareBook at least once and add a contact, so that `data/contacts.json` exists. Close CareBook before each test case.

   1. Test case: Delete `data/contacts.json`, then launch CareBook.<br>
      Expected: CareBook starts with an empty contact list and no error message. The file is created again after the next command that changes contacts, such as `add`.

   1. Test case: Replace the contents of `data/contacts.json` with `not json`, then launch CareBook.<br>
      Expected: CareBook starts with an empty contact list. The result box shows "Data file could not be loaded. Starting with an empty contact list." The file keeps its contents until the next command that changes contacts, which overwrites it.

   1. Test case: In `data/contacts.json`, change a `patientNo` to `abc`, or remove the `name` field of a contact, then launch CareBook.<br>
      Expected: Same as the previous test case.

   1. Test case: Launch CareBook with a valid `data/contacts.json`, then run `list`, `find` or `view 1`.<br>
      Expected: The commands run normally and the data file is not rewritten (its last modified time does not change).

1. Saving when the data file cannot be written

   1. Prerequisites: Launch CareBook with at least one contact, then make `data/contacts.json` read-only (on Windows, tick *Read-only* in the file's properties; on macOS or Linux, run `chmod 444 data/contacts.json`).

   1. Test case: `remark 1 Uses a wheelchair`<br>
      Expected: The result box shows `Could not save data to file ...` and `The command was undone, so your contacts are unchanged.` The first contact's card does not show the note.

   1. Test case: make the file writable again (untick *Read-only*, or run `chmod 644 data/contacts.json`), then enter `remark 1 Uses a wheelchair` again.<br>
      Expected: The note is added and saved. It is still shown after restarting CareBook.
