---
  layout: default.md
  title: "Developer Guide"
  pageNav: 3
---

# AB-3 Developer Guide

<!-- * Table of Contents -->
<page-nav-print />

--------------------------------------------------------------------------------------------------------------------

## **Acknowledgements**

* _{List the sources of reused or adapted ideas, code, documentation, and third-party libraries here, with links to the originals.}_

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

The sequence diagram below illustrates the interactions within the `Logic` component, taking `execute("delete 1")` API call as an example.

<puml src="diagrams/DeleteSequenceDiagram.puml" alt="Interactions Inside the Logic Component for the `delete 1` Command" />

<box type="info" seamless>

**Note:** The lifeline for `DeleteCommandParser` should end at the destroy marker (X), but due to a limitation of PlantUML, the lifeline continues till the end of diagram.
</box>

How the `Logic` component works, using `delete 1` as the example:

1. When `Logic` is called upon to execute a command, the command is passed to an `AddressBookParser` object, which in turn creates a parser that matches the command (e.g., `DeleteCommandParser`) and uses it to parse the command.
1. `DeleteCommandParser` checks that the argument is a positive integer and throws a `ParseException` otherwise. If valid, it creates a `DeleteCommand` holding the `targetIndex`.
1. The `DeleteCommand` is returned to the `LogicManager` and executed. It first calls `Model#getFilteredPersonList()` to get the list currently shown to the user. If this list is empty, or `targetIndex` is beyond its size, a `CommandException` is thrown.
1. Otherwise, the `DeleteCommand` retrieves the `Person` at `targetIndex` and calls `Model#deletePerson(personToDelete)`.
1. The result of the command execution is encapsulated as a `CommandResult` object (containing the success message) which is returned from `Logic`.

The `filter` command follows the same flow, as shown in the sequence diagram below for `execute("filter /tag HIGH")`.

<puml src="diagrams/FilterSequenceDiagram.puml" alt="Interactions Inside the Logic Component for the `filter /tag HIGH` Command" />

How the `filter` command works:

1. `AddressBookParser` creates a `FilterCommandParser` and calls `parse(" /tag HIGH")`.
1. `FilterCommandParser` uses `ArgumentTokenizer` with the `/name`, `/tag` and `/address` prefixes. It throws a `ParseException` if there is text before the first prefix, if no criterion is given, if any prefix has an empty value, or if any `/tag` value is not one of `HIGH`, `MEDIUM` or `LOW` (case-insensitive). Name values are split on whitespace into keywords, while each `/tag` and `/address` value is kept whole, so repeating those prefixes supplies several alternatives. It then creates a `PersonMatchesFilterPredicate` from the three lists, and a `FilterCommand` that holds this predicate.
1. `PersonMatchesFilterPredicate` treats an empty list as "criterion not applied". Within a criterion, matching any one value is enough (OR); a person must satisfy every criterion that was given (AND).
1. When executed, `FilterCommand` calls `Model#updateFilteredPersonList(predicate)`, so the filtered list only contains persons matching the criteria. It does not modify the address book data.
1. `FilterCommand` calls `Model#getFilteredPersonList()` to count the persons now shown, and returns a `CommandResult` with the "N person(s) listed!" message.

Here are the other classes in `Logic` (omitted from the class diagram above) that are used for parsing a user command:

<puml src="diagrams/ParserClasses.puml" width="600"/>

How the parsing works:
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

CareBook saves the contact list to `data/contacts.json` only after a command that changes it. Each `Command` has a `modifiesData()` method that returns `true` by default. Commands that only change what is displayed or show information (`list`, `find`, `filter`, `view`, `clear`, `help` and `exit`) override it to return `false`.

The sequence diagram below shows how `LogicManager` decides whether to save after executing a command.

<puml src="diagrams/SaveSequenceDiagram.puml" alt="How LogicManager decides whether to save after a command" />

1. `LogicManager` parses the command text into a `Command` and executes it.
1. It then calls `modifiesData()` on the command.
1. If the command changes data, `LogicManager` gets the address book from the `Model` and passes it to `Storage#saveAddressBook`. If the save fails, the error becomes a `CommandException`, so the user sees `Could not save data to file: ...` and CareBook keeps running.
1. Otherwise, `LogicManager` only logs that the save was skipped, and the data file is left as it is.

**Design consideration:** saving after every command would be simpler, but it would rewrite the data file even when nothing changed. Skipping the save for commands that only show information means a command such as `list` never overwrites a data file that the user is editing by hand.

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

**Error handling:** Missing, non-numeric or non-positive indexes cause the parser to throw a `ParseException`
with the command usage. During execution, `ViewCommand#validateTargetIndex()` distinguishes an empty
displayed list from an index outside a non-empty list. An empty display produces recovery instructions
to use `list` or `find KEYWORD`; an out-of-range index reports the supplied index, displayed count and valid
range. Both cases throw a `CommandException` and never select from the full stored list as a fallback.
These errors leave stored contacts, selection and the display filter unchanged.

**Selection lifecycle:** `ModelManager` stores a transient contact reference, not its displayed index.
`setPerson` replaces that reference when the selected contact is edited, including a name change.
Deleting that contact or replacing the address book clears selection. Editing or deleting a different
contact leaves selection unchanged. Updating a filter clears selection only if it hides the contact;
therefore `clear` also hides the details panel. With no selection, the panel and its placeholder are
invisible and unmanaged, so they reserve no layout space. Selection is never serialized.

**Cross-feature integration:** `getFilteredPersonList()` exposes the `SortedList` over the filtered
contacts, so `view`, `remark` and `checkup` use the same displayed indices. Sorting keeps the selected
contact rather than selecting whichever contact moves into its former row. `PersonDetailsFormatter`
includes the optional checkup appointment and every numbered remark. Replacing the selected immutable
`Person` through `setPerson()` refreshes these details without changing the current filter or sort.

**Design rationale:** a separate details panel lets caregivers read complete contact information while
retaining their position in a search or filtered list. Content-sized panels grow up to height caps, with
vertical scrolling and bounded bottom-edge resize handles for longer content. The feedback panel uses
the same sizing behaviour for every command. A coloured card border and a `Selected contact` heading link the
details to the list without treating a changing list index as a permanent contact identifier.

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

### Remarks

The `remark` command keeps numbered care notes on a contact, such as allergies or visiting rules. One command
word accepts four formats:

* `remark INDEX NOTE` adds a note after the contact's last note.
* `remark INDEX /edit NOTE_NUMBER NOTE` replaces the note with that number.
* `remark INDEX /delete NOTE_NUMBER` deletes the note with that number.
* `remark INDEX /clear` deletes all of the contact's notes.

#### Model and storage

`Remark` is an immutable list of notes held by every `Person`. A person with no notes holds `Remark.EMPTY`, so
`Person#getRemark()` never returns `null`. Each note must not be blank, and the notes can have at most
`Remark.MAX_TOTAL_LENGTH` (500) characters in total, which stops one contact's card from filling the list.
The `Remark` constructor enforces both rules, so an invalid remark cannot exist in the model.

The notes count towards `Person#equals()` but not `Person#isSamePerson()`, so two contacts with the same name are
still duplicates whatever their notes. `EditCommand` copies the remark unchanged into the edited person.
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
   `withNoteRemoved()`), checks the total length, and replaces the person using `Person#withRemark()` and
   `Model#setPerson()`.
1. `view` now selects a contact without narrowing the displayed list. `Model#setPerson()` updates the
   selected contact reference, so changes to its notes appear immediately in the details panel while
   the current filter and sort order are preserved. The command retains a fallback for a predicate that
   no longer contains the replaced person; ordinary name, address and priority filters are unaffected
   by changes to notes.

**Error handling:** `remark INDEX` without a note is an error rather than a way to clear the notes, so notes
are never lost because of a missing argument. A missing or invalid index or note number causes a
`ParseException`. An index outside the displayed list, a note number the person does not have, or notes that
would be too long cause a `CommandException`. In each case the person is unchanged.

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

### Find command


The `find /CRITERION INPUT` command searches one selected contact field and returns the number of
matches in the result box. Supported criteria are `/name`, `/patientNo`, `/familyNo`, `/address`,
`/pEmail`, `/fEmail`, `/email` (either email), and `/tag`. Text searches match any whitespace-separated
keyword as a case-insensitive substring. Phone searches require one sequence of at least two digits and
match those digits consecutively: `87` matches `900087000`, but not `89700000`.
It searches all stored contacts and changes the display filter without modifying stored contacts.

The  `find KEYWORD [MORE_KEYWORDS]...` command only does name searches, of which any name matching
the case-insensitive substring is given.

The sequence diagram below shows a successful `find /patientNo 87` command. A successful search may
return zero matches. It focuses on parsing and execution; helper calls and UI updates are omitted for clarity.

<puml src="diagrams/FindSequenceDiagram.puml" alt="Sequence of parsing and executing a successful find /patientNo 87 command" />

1. `LogicManager#execute()` passes the input to `AddressBookParser#parseCommand()`, which recognises
   `find` and creates a `FindCommandParser`.
2. `FindCommandParser#parse()` extracts the slash-prefixed criterion and validates the input. It creates a
   `PersonContainsKeywordsPredicate` containing the criterion and keywords, then a `FindCommand` using that
   predicate. Legacy name searches continue to use `NameContainsKeywordsPredicate`.
3. `FindCommand#execute()` calls `Model#updateFilteredPersonList(predicate)` to replace the display filter.
   `PersonContainsKeywordsPredicate#test()` selects the requested field and checks for matching substrings.
   Absent optional emails or tags do not match. The UI observes the filtered list and updates the contact cards.
4. The command obtains `Model#getFilteredPersonList()` and uses its size to format
   `Messages.MESSAGE_PERSONS_LISTED_OVERVIEW` for the feedback in a new `CommandResult`.
5. `LogicManager` checks `FindCommand#modifiesData()`, which returns `false`, and skips saving.
   `MainWindow` displays the returned feedback in `ResultDisplay`.

**Error handling:** Missing or whitespace-only input, an unknown slash-prefixed criterion, multiple criteria,
or phone input other than a single sequence of at least two digits causes `FindCommandParser#parse()` to
throw a `ParseException` with the command usage. This leaves stored contacts and the display filter unchanged.
A search with no matches succeeds, displays an empty list, and reports `0 person(s) listed!`.
Use `list` to display all contacts again after a successful search.


### Sort command

The `sort` command changes the order in which contacts are displayed. It supports three criteria:
`sort priority [asc|desc]`, `sort name [asc|desc]` and `sort recent`.

**Design.** `ModelManager` keeps a `SortedList` that wraps the `FilteredList`, and
`Model#getFilteredPersonList()` returns the sorted list. This means:

* A sort is applied on top of the current filter, so `filter` and `sort` combine in either order.
* The stored contact order is never changed. `recent` depends on this: the address book keeps contacts in the
  order they were added, so `recent` is the reverse of that order and needs no timestamp.
* Commands that take an `INDEX` (`delete`, `edit`, `view`) read the displayed list, so the index shown on screen
  always refers to the contact that is acted on.
* `list` calls `Model#resetSortedPersonList()`, which restores the order contacts were added.

`PersonComparators` provides the orderings:

* `byName(ascending)` compares names ignoring case and compares each run of digits by its numeric value, so `Ethan2`
  comes before `Ethan10`. It is also the tie-breaker of `byPriority`.
* `byPriority(mostUrgentFirst)` orders `HIGH`, `MEDIUM`, `LOW` (or the reverse). Contacts without a priority tag
  are always last in both directions, and ties are ordered by name. In the user-facing command, `desc` means most
  urgent first, so `SortCommand` passes `!ascending` as `mostUrgentFirst`.

How the `sort` command works:

1. `AddressBookParser` recognises `sort` and creates a `SortCommandParser`.
2. `SortCommandParser#parse()` lowercases and splits the arguments. It throws a `ParseException` if there is no
   criterion, the criterion or direction is unknown, `recent` is given a direction, or there are extra arguments.
   Priority defaults to descending and name defaults to ascending. It creates a `SortCommand`.
3. `SortCommand#execute()` calls `Model#updateSortedPersonList(comparator)` (or `Model#sortPersonsByMostRecent()`
   for `recent`) and returns a `CommandResult` describing the order.
4. `SortCommand#modifiesData()` returns `false`, so `LogicManager` does not save. The UI observes the sorted list
   and reorders the contact cards.

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

_{more aspects and alternatives to be added}_

### \[Proposed\] Data archiving

_{Explain here how the data archiving feature will be implemented}_


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
| `* * *`  | user                                       | add a new patient       |                                                            |
| `* * *`  | user                                       | delete a patient        | remove entries who have completed their recovery           |
| `* * *`  | user                                       | edit a contact's details | keep phone numbers, emails and addresses up to date when they change |
| `* *`    | user                                       | remove a contact's email | stop keeping an email address that is no longer used       |
| `* * *`  | user                                       | view a specific contact | locate details of persons quickly when book gets populated |
| `* *`    | user                                       | find any contact        | find contacts I might not remember the full names of       |
| `* * *`  | caregiver                                  | keep care notes on a contact | remember allergies, medication times and visiting rules |
| `* *`    | caregiver                                  | edit or delete one care note | keep notes up to date without retyping the others |


*{More to be added}*

### Use cases

(For all use cases below, the **System** is the `CareBook` and the **Actor** is the `user`, unless specified otherwise)

**Use case: Add a contact**

**MSS:**

1. User requests to add a person with the person's name, patient phone number, family phone number, and address
2. CareBook validates the command format and input fields 
3. CareBook adds the person to the address book 
4. CareBook displays a confirmation with the person's details 

    Use case ends. 

**Extensions** 

*1a. The command format is invalid 

    *1a1. CareBook shows an error message with the correct command format.

        Use case ends.

*2a. The name contains invalid characters.

    *2a1. CareBook shows an error message.

        Use case resumes at step 1

*2b. A phone number contains non-numeric characters.

    *2b1. CareBook shows an error message.

        Use case resumes at step 1.

*2c. A required field is missing.

    *2c1. CareBook shows an error message.

        Use case resumes at step 1.

**Use case: Delete a person**

**MSS**

1.  User requests to list persons
2.  CareBook shows a list of persons
3.  User requests to delete a specific person in the list
4.  CareBook deletes the person

    Use case ends.

**Extensions**

* 2a. The list is empty.

  Use case ends.

* 3a. The given index is invalid.

    * 3a1. CareBook shows an error message.

      Use case resumes at step 2.

**Use case: Edit a contact**

**MSS**

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

* 3d. The new name belongs to another person in CareBook.

    * 3d1. CareBook shows an error message and leaves the person unchanged.

      Use case resumes at step 2.

* 3e. The user gives an email field with no value.

    * 3e1. CareBook removes that email from the person.

      Use case resumes at step 4.

**Use case: Keep a care note on a contact**

**MSS**

1.  User requests to list persons
2.  CareBook shows a list of persons
3.  User requests to add a note to a specific person in the list
4.  CareBook adds the note after the person's last note and shows it with its number

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

* 3d. The user requests to edit or delete one of the person's notes by its number instead.

    * 3d1. CareBook replaces or deletes that note and shows the result.

      Use case ends.

* 3e. The user requests to edit or delete a note number the person does not have.

    * 3e1. CareBook shows an error message with the number of notes the person has.

      Use case resumes at step 2.

* 3f. The user requests to clear the person's notes instead.

    * 3f1. CareBook deletes all of the person's notes, and the card no longer shows a `Remark:` line.

      Use case ends.

**Use case: List all contacts**

**MSS:**

1. User requests to view all contacts 
2. CareBook validates the command format
3. CareBook displays all contacts currently stored 
4. CareBook displays a confirmation that all contacts have been shown

   Use case ends.

**Extensions**

*2a. The command format is invalid. 

    *2a1. CareBook shows an error message
        
        Use case ends.

*2b. The command contains leading or trailing whitespace.

    *2b1. CareBook ignores the whitespace and validates the command.

        Use case resumes at step 3. 

*3a. No contacts are stored in the address book.

    *3a1. CareBook shows No contacts found.

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

    * 3b1. CareBook reports the entered index, displayed contact count and valid index range.

      Use case ends.

Leading and trailing whitespace is ignored during command validation. After filtering, the index refers
to a position in the filtered results, not the full stored list. Unsuccessful requests leave stored
contacts, selection and the displayed list unchanged; there is no fallback to the full stored list.

**Use case: Save contact list after an update**

**MSS**

1. User requests to list persons.
2. CareBook shows a list of persons.
3. User requests a valid update to the contact list.
4. CareBook updates the contact list and automatically saves it to the local data file.
5. CareBook displays the updated contact list and a success message.

    Use case ends.

**Extensions**

*3a. The requested change is invalid.

    *3a1. CareBook displays an error message and leaves the contact list unchanged.
    
    *3a2. User submits a corrected update request to the contact list.
    
        Use case resumes at step 4.

**Use case: Find contacts**

**MSS:**

1. User requests to find contacts with names matching the input
2. CareBook validates the command format
3. CareBook searches for contacts whose names contain the matching substring
4. CareBook displays the matching contact cards 

   Use case ends.

**Extensions**

*2a. User input is empty 

    *2a1. Carebook displays an error message and the displayed contacts do not change.

      
### Non-Functional Requirements

1.  Should work on any _mainstream OS_ as long as it has Java `25` or above installed
2.  Should be able to finish a command within 1 second for a contact book holding up to 1000 persons 
3. Invalid commands should show a clear error message and usage example
4. A user with above average typing speed for regular English text (i.e. not code, not system admin commands) should be able to accomplish most of the tasks faster using commands than using the mouse.
5. If saving fails, the user should be told what went wrong and CareBook should keep running
6. Should be able to handle empty or malformed commands without crashing
7. Should run without a remote server
8. Should be usable at all screen resolutions and scales


### Glossary

* **Mainstream OS**: Windows, Linux, Unix, or macOS
* **Recovery**: Patient is deemed to have reached their normal level of physical and physiological function
* **Relevant contacts**: A patient's emergency contact, or other family contacts


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

   1. Double-click the JAR file.<br>
      Expected: The GUI opens with an empty contact list. The window size may not be optimal.

1. Saving window preferences

   1. Resize the window to an optimal size. Move the window to a different location. Close the window.

   1. Relaunch the app by double-clicking the JAR file.<br>
       Expected: The most recent window size and location are retained.

1. _{ more test cases … }_

### Deleting a person

1. Deleting a person while all persons are being shown

   1. Prerequisites: List all persons using the `list` command, with multiple persons in the list.

   1. Test case: `delete 1`<br>
      Expected: The first contact is deleted from the list. The status message shows the deleted contact's details.

   1. Test case: `delete 0`<br>
      Expected: No person is deleted. The status message shows error details.

   1. Other incorrect delete commands to try: `delete`, `delete x`, `...` (where x is larger than the list size)<br>
      Expected: Similar to previous.

1. _{ more test cases … }_

### Editing a person

1. Editing a person while all persons are being shown

   1. Prerequisites: List all persons using the `list` command, with at least two persons in the list.

   1. Test case: `edit 1 /patientNo 91234567 /pEmail patient@example.com`<br>
      Expected: The first contact's patient phone number and patient email change, and its other fields stay the same. The status message shows the edited contact's details.

   1. Test case: `edit 1 /fEmail family@example.com`<br>
      Expected: The family email is set. The patient email from the previous test case is unchanged.

   1. Test case: `edit 1 /pEmail`<br>
      Expected: The patient email is removed. The family email is unchanged.

   1. Test case: `edit 1 /email someone@example.com`<br>
      Expected: No person is edited. The status message says that `/email` is no longer supported and to use `/pEmail` or `/fEmail` instead.

   1. Test case: `edit 1 /name NAME`, where `NAME` is the name of the second person.<br>
      Expected: No person is edited. The status message shows `This person already exists in CareBook.`

   1. Other incorrect edit commands to try: `edit 1`, `edit 0 /name Amy`, `edit x /name Amy` (where x is larger than the list size), `edit 1 /patientNo abc` and `edit 1 /pEmail john`<br>
      Expected: No person is edited. The status message shows error details.

1. Editing a person in a filtered list

   1. Prerequisites: Use `find` with a name that matches some, but not all, persons.

   1. Test case: `edit 1 /address 1 Test Street`<br>
      Expected: The first person in the filtered list is edited, and the full list is shown again.

### Keeping care notes

1. Adding, editing and deleting notes

   1. Prerequisites: List all persons using the `list` command, with at least two persons in the list.

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

   1. Other incorrect remark commands to try: `remark 0 note`, `remark x note` (where x is larger than the list size), `remark 1 /delete 9` and `remark 1 /edit 1`<br>
      Expected: No note changes. The status message shows error details.

1. Keeping notes after other commands

   1. Test case: `remark 1 Visits after 3pm`, then `edit 1 /address 1 Test Street`<br>
      Expected: The address changes and the note is still shown.

   1. Test case: `view 2`, then `remark 1 Diabetic` and `remark 1 Uses a wheelchair`<br>
      Expected: The second contact stays on screen and has both notes.

   1. Test case: Add a note, close CareBook and relaunch it.<br>
      Expected: The note is still shown.

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

### Saving data

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
