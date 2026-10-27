---
  layout: default.md
  title: "User Guide"
  pageNav: 3
---

# CareBook User Guide

CareBook is a **desktop application for in-house caregivers and healthcare administrators to manage patient and family contact details, optimized for use through a Command Line Interface (CLI)** while retaining the benefits of a Graphical User Interface (GUI). If you type quickly, CareBook can help you store and retrieve contact details efficiently during day-to-day care.

<!-- * Table of Contents -->
<page-nav-print />

--------------------------------------------------------------------------------------------------------------------

## Quick start

1. Ensure that Java `25` or later is installed on your computer.<br>
   **Mac users:** Ensure you have the precise JDK version prescribed [here](https://se-education.org/guides/tutorials/javaInstallationMac.html).

1. Download the latest `.jar` file from [CareBook releases](https://github.com/AY2627S1-CS2103T-T08-1/tp/releases).

1. Copy the file to the folder you want to use as the _home folder_ for CareBook.

1. Open a terminal, `cd` to the folder containing the JAR file, and run `java -jar carebook.jar`.<br>
   A GUI similar to the one below should appear in a few seconds. The app starts with an empty contact list the first time you run it.<br>
   ![Ui](images/Ui.png)

1. Type a command in the command box and press Enter to execute it. For example, type **`help`** and press Enter to open the help window.<br>
   Some example commands you can try:

   * `list` : Lists all active contacts. Use `list archive` for archived contacts.

   * `add /name John Doe /patientNo 98765432 /familyNo 91234567 /pEmail johnd@example.com /fEmail family@example.com /address John street, block 123, #01-01` : Adds a contact named `John Doe` to the CareBook.

   * `delete 3` : Deletes the 3rd contact shown in the current list.

   * `clear` : Clears the displayed contact list without deleting contacts.

   * `exit` : Exits the app.

1. Refer to the [Features](#features) section below for details of each command.

--------------------------------------------------------------------------------------------------------------------

## Features

<box type="info" seamless>

**Notes about the command format:**<br>

* Words in `UPPER_CASE` are the parameters to be supplied by the user.<br>
  For example, in `add /name NAME`, replace `NAME` with a value such as `John Doe`.

* Items in square brackets are optional.<br>
  For example, `/name NAME [/tag PRIORITY]` can be used as `/name John Doe /tag HIGH` or as `/name John Doe`.

* Items followed by `...` can appear zero or more times.<br>
  For example, `find KEYWORD [MORE_KEYWORDS]...` can be written as `find John` or `find John Mary`.

* Parameters can be in any order.<br>
  For example, if the command specifies `/name NAME /patientNo PATIENT_PHONE`, `/patientNo PATIENT_PHONE /name NAME` is also acceptable.

* The `help`, `clear`, and `exit` commands reject extra parameters; enter these commands on their own.<br>
  For example, `help 123` will be rejected. The `list` command accepts only the optional `archive` argument.

* If you are using a PDF version of this document, be careful when copying and pasting commands that span multiple lines as space characters surrounding line-breaks may be omitted when copied over to the application.
</box>

### Viewing help: `help`

Opens a resizable, scrollable Help window listing the available commands, their usage, and examples.
The command list includes `add`, `edit`, `delete`, `find`, `filter`, `list`, `sort`, `view`,
`checkup`, `remark`, `clear`, `help`, and `exit`. It covers search criteria, sorting by name,
priority or recency, scheduling checkups, and adding, editing, deleting or clearing patient notes.

The window also provides the user guide URL and a **Copy URL** button.

Format: `help`

* The command is case-insensitive: `help`, `HELP`, and `HeLp` all open the same window.
* Whitespace before and after the command is allowed.
* Additional text is rejected: `help 123`, `help me`, `abc help`, and `helpabc` are invalid.
* You can also open the window using **Help > Help** in the menu or pressing **F1**.
* If the Help window is already open, running `help` focuses that window.

Examples:

* `help` — displays the command list.
* `HELP` — displays the same command list.
* `help 123` — displays an invalid command format error; use `help` on its own.


### Adding a person: `add`

Adds a person to CareBook.

Format: `add /name NAME /patientNo PATIENT_PHONE /familyNo FAMILY_PHONE [/pEmail PATIENT_EMAIL] [/fEmail FAMILY_EMAIL] /address ADDRESS [/tag PRIORITY] `

Note: `FAMILY_PHONE` and `FAMILY_EMAIL` can refer to the patient's emergency contact or another specified family member. 

<box type="tip" seamless>

**Tip:** A person can have no priority tag or one priority tag: `HIGH`, `MEDIUM`, or `LOW`. Repeating the same tag stores it only once.
</box>

Examples:
* `add /name Aunty Rosalind /patientNo 81268767 /familyNo 90783658 /address Goonvale Street Block 2 #01-09`
* `add /name John Doe /patientNo 98765432 /familyNo 91234567 /pEmail johnd@example.com /fEmail family@example.com /address John street, block 123, #01-01 /tag HIGH`

### Priority tags

Use `/tag PRIORITY` with `add` or `edit` to label a person's priority. Choose one of the three supported values: `HIGH`, `MEDIUM`, or `LOW`. Custom priority names are not supported.

* The `/tag` prefix and priority values are case-insensitive: `/tag High`, `/TAG high`, and `/TaG HIGH` all assign `HIGH`. Tags are displayed and saved in uppercase. Free-form tags such as `/tag friend` are invalid.
* Spaces before and after a priority value are ignored in commands.
* Only one distinct priority is allowed. `/tag HIGH /tag LOW` is rejected with `A contact can have at most one priority tag.` Repeating `/tag High /TAG high` stores one `HIGH` tag. `/tag HIGH LOW` is also invalid.
* Omitting `/tag` when adding a person leaves them without priority tags. A bare `/tag` is invalid for `add`.
* The assigned priority tag appears on the person's card.

An invalid priority displays `Tag names must be HIGH, MEDIUM or LOW` and leaves the contacts unchanged.

### Archiving a contact: `archive`

Archives a contact that you no longer need in the active list while keeping its details in CareBook.
The contact disappears from the current active list and can be viewed using `list archive`.

Format: `archive INDEX`

The command word is case-insensitive: `archive 3`, `Archive 3`, and `ARCHIVE 3` all archive the third displayed contact.

* `INDEX` must be a positive integer shown in the current list, including a filtered list after `find`.
* `list archive` shows archived contacts. Use `list` to return to active contacts.
* `find` searches active contacts. Archived contacts remain archived when edited.
* Archiving an already archived contact reports an error without changing it.
* Archived status is saved automatically and survives restarting CareBook.
* Archived contacts retain their identity, so adding a duplicate is still rejected.

**Example workflow:**

1. Enter `list` to display active contacts.
2. Enter `archive 3` to archive the third contact shown. CareBook displays `Archived person: ...` with the contact's details, and removes that contact from the active list.
3. Enter `list archive` to switch to the archive list. CareBook displays `Listed archived persons.`
4. Enter `list` to return to the active list. The archived contact remains in the archive list.

After archiving, the displayed indices may change. Check the current list before archiving another contact.
For example, after `find Alice`, `archive 1` archives the first contact in those search results.

**Common errors:**

| Incorrect input or situation | Result | How to correct it |
| --- | --- | --- |
| `archive`, `archive Alice`, or `archive 1 2` | Invalid command format; usage instructions are shown. | Enter exactly one index, such as `archive 3`. |
| `archive 0` or `archive -1` | Invalid index. | Use a positive integer shown in the current list. |
| `archive 4` when only three contacts are displayed | The index is out of range. | Check the displayed indices and select an existing contact. |
| `archive 1` when the displayed list is empty | `There are no persons in the displayed list to archive.` | Use `list` to check for active contacts. |
| Archiving a contact in `list archive` | `This person is already archived.` | Use `list` to select an active contact instead. |

### Restoring an archived contact: `unarchive`

Restores an archived contact to the active list, preserving all its details.

Format: `unarchive INDEX`

* Enter `list archive` first, then use the index shown in that list. Filtered and sorted archive indices are supported.
* `INDEX` must be a positive integer referring to a displayed contact. Only one index and surrounding whitespace are accepted.
* The command word is case-insensitive: `unarchive 3`, `Unarchive 3`, and `UNARCHIVE 3` all work.
* Missing indices, zero, negative numbers, decimals, words, and extra arguments are rejected.
* An active contact cannot be unarchived; an error is shown without changing it.
* The restored contact disappears from the archive list. Enter `list` to see it in the active list.
* The change is saved automatically and survives restarting CareBook.

Example: `list archive`, followed by `unarchive 3`, restores the third displayed archived contact.
Check the updated indices before restoring another contact.

### Listing persons: `list`

Use `list` to show all active (non-archived) persons in CareBook, or `list archive` to show only archived persons.

Format: `list` or `list archive`

**Common errors:**

| Incorrect input | Error message | How to correct it |
| --- | --- | --- |
| `list 3` | `Invalid command format. List command format: list [archive]` | Remove the index. Use `list` to show all persons. |
| `list Alice` or `list n/Alice` | `Invalid command format. List command format: list [archive]` | Use `find Alice` to search by name, or `list` to show all persons. |
| `LIST` or `List` | `Unknown command.` | Use the lowercase command `list`. |

The `list` command accepts only the optional `archive` argument, which is case-insensitive: `list Archive`, `list ARCHIVE`, and `list ARchive` all show archived contacts. The command word `list` remains lowercase. Leading and trailing spaces are allowed.
If all active persons are already displayed, `list` leaves the displayed list unchanged and shows `Listed all persons.`;
this is normal behavior. After a `find` command or `list archive`, use `list` to display all active persons again.
If no contacts have been archived, `list archive` displays an empty list. `list` also clears any `sort`, so persons are 
shown in the order they were added.

### Sorting persons: `sort`

Changes the order in which persons are displayed. Sorting only changes what you see; it does not
reorder the saved data, and it works on top of any `find` or `filter` result.

Format: `sort priority [asc|desc]`, `sort name [asc|desc]` or `sort recent`

* `sort priority` orders by priority tag.
  * `desc` (the default) shows `HIGH`, then `MEDIUM`, then `LOW`.
  * `asc` is the reverse: `LOW`, then `MEDIUM`, then `HIGH`.
  * Persons without a priority tag are always shown last, in either direction.
  * Persons with the same priority are ordered by name, A to Z.
* `sort name` orders alphabetically by name, ignoring case.
  * `asc` (the default) shows A to Z; `desc` shows Z to A.
* `sort recent` shows the most recently added person first. It takes no direction.
* The criterion and direction are case-insensitive, so `sort Name DESC` works. The command word `sort` must be
  lowercase.
* Use `list` to show all persons again in the order they were added.

Examples:
* `sort priority` lists `HIGH` persons first.
* `sort priority asc` lists `LOW` persons first.
* `sort name desc` lists persons from Z to A.
* `filter /tag HIGH` followed by `sort name` lists only `HIGH` persons, from A to Z.
* `sort recent` lists the most recently added person at the top.

**Common errors:**

| Incorrect input | Error message | How to correct it |
| --- | --- | --- |
| `sort` (no criterion) | `Invalid command format!` followed by the correct usage | Give a criterion, e.g. `sort priority`. |
| `sort address` (unknown criterion) | `Invalid command format!` followed by the correct usage | Use `priority`, `name` or `recent`. |
| `sort priority up` (unknown direction) | `Invalid command format!` followed by the correct usage | Use `asc` or `desc`. |
| `sort recent desc` (direction with `recent`) | `Invalid command format!` followed by the correct usage | Use `sort recent` on its own. |

### Viewing a person's full details: `view`

Shows the specified person's full details in a dedicated panel above the contact list.
The current list stays visible, so you can read details without losing your search or filter context.

Format: `view INDEX`

* Displays the person at the specified `INDEX`.
* The index refers to the index number shown in the displayed person list.
* The index **must be a positive integer** 1, 2, 3, ...
* The selected contact has a coloured border in the list. Use another `view INDEX` to switch contacts.
* The feedback and details panels fit their contents, up to height limits of 160 and 260 pixels respectively.
  Longer text wraps and scrolls instead of taking over the contact list. Drag a panel's bottom handle to
  resize it within its limit; double-click the handle to fit its contents again. New content or a change
  in window width restores automatic sizing. With no selection, the details panel is hidden entirely.
* Missing optional emails are shown as `Not provided`; a missing priority is shown as `Not assigned`.
* The details panel also shows the checkup appointment (`Not scheduled` if absent) and all numbered
  remarks (`None` if absent). Updating the selected contact's appointment or remarks refreshes the panel.
* After filtering or sorting, use the index in the list currently on screen. For example,
  `filter /tag HIGH`, `sort name desc`, then `view 1` selects the first contact in those sorted results.
* Editing the selected contact refreshes the details. Deleting it clears the panel.
* A new search or filter clears selection if it hides the selected contact. `clear` clears both panels
  without deleting any contacts. `list` restores the contact list, but does not reselect a contact.

Example:
* `view 2` displays the full details of the 2nd person in the displayed list.

Common errors:

| Input | Error message shown | How to fix |
| --- | --- | --- |
| `view 3` when 2 contacts are displayed | `Index 3 is out of range. The displayed list has 2 contacts. Enter an index from 1 to 2.` | Use an index shown in the current list. |
| `view 1` when no contacts are displayed, including after `clear` | `No contacts are currently displayed. Use list to show all contacts, or find KEYWORD to search.` | Run `list` or `find KEYWORD` first. If no contacts are saved, add one. |

These errors leave contacts, the current list and selection unchanged. `view` never falls back to
selecting a contact from the full stored list when that contact is not currently displayed.

### Editing a person: `edit`

Edits an existing person in CareBook.

Format: `edit INDEX [/name NAME] [/patientNo PATIENT_PHONE] [/familyNo FAMILY_PHONE] [/pEmail PATIENT_EMAIL] [/fEmail FAMILY_EMAIL] [/address ADDRESS] [/tag PRIORITY] `

* Edits the person at the specified `INDEX`. The index refers to the index number shown in the displayed person list. The index **must be a positive integer** 1, 2, 3, ...
* At least one of the optional fields must be provided.
* Existing values will be updated to the input values. Fields you leave out, including both emails, stay as they are.
* To remove an email, supply `/pEmail` or `/fEmail` without a value.
* The old `/email` prefix is not accepted. Use `/pEmail` for the patient's email and `/fEmail` for the family member's email.
* Priority values must be `HIGH`, `MEDIUM`, or `LOW`, in any letter case (see [Priority tags](#priority-tags)).
* Supplying `/tag PRIORITY` replaces the person's existing priority tag. Omitting `/tag` leaves their tags unchanged.
* To remove a person's priority tag, supply a single `/tag` without a value. Combining an empty `/tag` with other tags is invalid.

Examples:
*  `edit 1 /patientNo 91234567 /pEmail johndoe@example.com` Edits the patient phone number and patient email of the 1st person to be `91234567` and `johndoe@example.com` respectively.
*  `edit 3 /fEmail daughter@example.com` Sets the family email of the 3rd person to `daughter@example.com`. Their patient email is unchanged.
*  `edit 2 /pEmail` Removes the patient email of the 2nd person.
*  `edit 1 /TAG Medium` Replaces the priority tag of the 1st person with `MEDIUM`.
*  `edit 2 /name Betsy Crower /tag` Edits the name of the 2nd person to be `Betsy Crower` and removes their priority tag.

**Common errors:**

| Incorrect input | Error message | How to correct it |
| --- | --- | --- |
| `edit 1` (no field) | `At least one field to edit must be provided.` | Add at least one field, e.g. `edit 1 /name John Doe`. |
| `edit 1 /email john@example.com` | `/email is no longer supported. Use /pEmail for the patient's email or /fEmail for the family member's email.` | Use `/pEmail` for the patient's email or `/fEmail` for the family member's email. |
| `edit 0 /name John Doe` | `Invalid command format!` followed by the correct usage | Use an index of 1 or more. |
| `edit 99 /name John Doe` (index not in the list) | `The person index provided is invalid.` | Use an index shown in the displayed list. |
| `edit 1 /pEmail john` | `Emails should be of the format local-part@domain ...` followed by the rules | Enter a full email address, e.g. `john@example.com`. |
| `edit 1 /name Bernice Yu` (another person already has this name) | `This person already exists in CareBook.` | Use a name that no other person in CareBook has. |
| `edit 1 /pEmail a@example.com /pEmail b@example.com` | `Multiple values specified for the following single-valued field(s): /pEmail` | Give each field only once. |

### Scheduling or removing a checkup: `checkup`

Sets, replaces or removes the checkup appointment for the person at the specified displayed index.

Format: `checkup INDEX dd/mm/yyyy HH:mm` or `checkup INDEX delete`

* `INDEX` must be a positive index shown in the current person list, including a filtered or sorted list.
* The command word is case-insensitive: `checkup`, `Checkup` and `CHECKUP` are accepted.
* Use day/month/year and a 24-hour time, for example `25/12/2030 14:30` for 2:30 PM.
  The format is fixed even if the computer uses a different regional date format or a 12-hour clock.
* The appointment must not be earlier than the computer's current local date **and time**.
* Archived contacts cannot be given a new appointment or have an appointment rescheduled. Unarchive the contact first.
* Two contacts can share the exact same appointment date and time **only if their family HP numbers match**.
  Booking checks include active contacts outside the displayed list. Archived contacts do not reserve a slot.
* Scheduling a checkup shows the date and time on the person's card and adds `[ ]` beside their name.
  Replacing the appointment clears an existing `[X]` mark. Setting the same appointment again preserves its mark.
* `checkup INDEX delete` removes the appointment and its mark. The appointment row and the `[ ]` or `[X]`
  indicator disappear entirely from the person card. `delete` is case-insensitive.
* Other contact details are preserved, and appointment changes are saved automatically.

Examples:

* `checkup 1 25/12/2030 14:30` schedules or replaces the first displayed person's checkup.
* `CHECKUP 2   26/12/2030  09:00` schedules the second displayed person's checkup with extra spaces.
* `checkup 1 delete` removes the first displayed person's appointment.

**Common errors:**

| Incorrect input or situation | Error message | How to correct it |
| --- | --- | --- |
| `checkup`, a missing date/time, or a non-positive or non-numeric index | Invalid command format followed by usage | Give a displayed index and a date/time, or `delete`. |
| An index outside the displayed list | `The person index provided is invalid.` | Check the current list and use an index shown there. |
| An archived contact is selected for scheduling | `Appointments cannot be made with archived contacts. Unarchive the contact to do so.` | Unarchive the contact first. This error takes priority over date/time errors for a valid contact index. |
| An impossible date or time, such as `31/02/2030 14:30` or `25/12/2030 24:00` | `Checkup must be a valid date and time in dd/mm/yyyy HH:mm format (24-hour clock).` followed by usage | Use a real date and a time from `00:00` to `23:59`. |
| An appointment earlier than now, including an earlier time today | `Checkup date and time must not be in the past.` | Choose a future date and time. |
| Another contact has that date and time but a different family HP | `A contact with this booking date and time already exists. Only contacts with the same family HP can be booked at the same time.` | Choose another slot, unless the contacts share the same family HP. |
| `checkup INDEX delete` when no appointment is scheduled | `This contact has no checkup appointment to delete.` | Select a contact with an appointment. |

### Keeping care notes on a person: `remark`

Keeps numbered notes on a person, such as allergies, medication times or visiting rules.

Format:
* `remark INDEX NOTE` adds `NOTE` after the person's last note.
* `remark INDEX /edit NOTE_NUMBER NOTE` replaces note `NOTE_NUMBER` with `NOTE`.
* `remark INDEX /delete NOTE_NUMBER` deletes note `NOTE_NUMBER`.
* `remark INDEX /clear` deletes all of the person's notes.

* The index refers to the index number shown in the displayed person list. The index and note number **must be positive integers** 1, 2, 3, ...
* The notes appear on the person's card under `Remark:`, numbered 1., 2., 3., ... A person with no notes shows no `Remark:` line.
* Everything after the index is kept exactly as you type it, including `/`, so a note such as `Takes /med at 9am` is saved as one note.
* `/edit`, `/delete` and `/clear` are only recognised straight after the index, in any letter case.
* `remark INDEX` without a note does nothing and shows the correct usage, so notes are never deleted by accident.
* A person's notes can have at most 500 characters in total.
* `edit` does not change a person's notes.

Examples:
* `remark 1 Allergic to penicillin` adds the note `Allergic to penicillin` to the 1st person.
* `remark 1 Visits after 3pm only` adds a 2nd note to the 1st person.
* `remark 1 /edit 2 Visits after 4pm only` replaces the 1st person's 2nd note.
* `remark 1 /delete 1` deletes the 1st person's 1st note, so `Visits after 4pm only` becomes note 1.
* `remark 3 /clear` deletes all notes of the 3rd person.

**Common errors:**

| Incorrect input | Error message | How to correct it |
| --- | --- | --- |
| `remark 1` (no note) | `Invalid command format!` followed by the correct usage | Add a note, e.g. `remark 1 Allergic to penicillin`, or use `remark 1 /clear` to delete all notes. |
| `remark 0 Diabetic` | `Index must be a positive integer.` | Use an index of 1 or more. |
| `remark 99 Diabetic` (index not in the list) | `The person index provided is invalid.` | Use an index shown in the displayed list. |
| `remark 1 /delete 5` (the person has 2 notes) | `There is no note 5. Alex Yeoh has 2 note(s).` | Use a note number shown on the person's card. |
| `remark 1 /delete abc` | `Note number must be a positive integer.` | Use the number shown before the note, e.g. `remark 1 /delete 2`. |
| `remark 1 /edit 2` (no new note) | `Invalid command format!` followed by the correct usage | Add the new note, e.g. `remark 1 /edit 2 Visits after 4pm`. |
| `remark 1 /clear all` | `Invalid command format!` followed by the correct usage | Enter `remark 1 /clear` with nothing after it. |
| A note that takes the person's notes past 500 characters | `A contact's notes can be at most 500 characters in total. Use /delete to remove a note or /clear to remove them all.` | Shorten the note, or delete notes you no longer need. |

### Locating persons: `find`

Finds persons by the selected contact field using partial matches.

Format: `find /CRITERION INPUT` and `find KEYWORD`

For `find /CRITERION INPUT`:

| Criterion | Searches |
|-----------|----------|
| `/name` | Contact name |
| `/patientNo` | Patient phone number |
| `/familyNo` | Family phone number |
| `/address` | Address |
| `/pEmail` | Patient email |
| `/fEmail` | Family email |
| `/email` | Either patient or family email |
| `/tag` | Priority tag |

* `find KEYWORD` searches by name. Any name containing the input substring, case-insensitive, will be returned.

For `find /CRITERION INPUT`:

* Use exactly one criterion, spelled as shown above, and provide a non-empty input.
* Phone searches require one sequence of at least **two digits**, with no spaces or other characters.
  The digits must occur consecutively: `find /patientNo 87` matches `900087000`, but not `89700000`.
* Other fields use case-insensitive partial matches. For example, `find /name ha` matches `Hans`,
  `Johan` and `Anne Ha`; `find /address ain` matches `Main Street`.
* For non-phone searches, whitespace separates keywords. A contact matches if the selected field contains
  at least one keyword (an `OR` search); keyword order does not matter.
* Contacts without an optional email or tag do not match searches on that absent field.
* Searches consider all stored contacts, replacing the current display filter without changing stored data.
  Use `list` to display all contacts again. A search with no matches displays an empty list.
* Missing input, an unknown criterion, multiple criteria, or invalid phone input produces an error and
  leaves the displayed list unchanged.

Examples:
* `find /patientNo 87` finds patient numbers containing `87` consecutively.
* `find /email gmail` searches both email fields for `gmail`.
* `find /address street` searches addresses for `street`.
* `find John` returns `john` and `John Doe`
* `find alex david` returns `Alex Yeoh`, `David Li`<br>
  ![result for 'find alex david'](images/findAlexDavidResult.png)
* `find j` returns `John Doe`<br> ![result for 'find j'](images/findJ.png)

### Filtering persons by name, tag and address: `filter`

Shows only the persons in the current active or archived list who match all the criteria you give.

After `list`, `filter` excludes archived contacts. After `list archive`, it searches only archived contacts.
Each `filter` replaces the previous filter criteria while keeping the same list, even when no contacts match.
`find` switches back to searching active contacts.

Format: `filter [/name NAME_KEYWORDS] [/tag TAG]... [/address ADDRESS]...`

* At least one of `/name`, `/tag` or `/address` must be given.
* **Name:** `NAME_KEYWORDS` are separated by spaces. A person matches if their name contains **any** keyword. Matching is case-insensitive and partial matches are allowed, as in `find`.
* **Tag:** `TAG` must be one of `HIGH`, `MEDIUM` or `LOW` (case-insensitive). To match several tags, repeat `/tag`; a person matches if they have **any** of them.
* **Address:** `ADDRESS` is matched as a phrase, case-insensitively, and partial matches are allowed. To match several addresses, repeat `/address`; a person matches if their address contains **any** of the phrases.
  * Unlike names, an address is **not** split into separate keywords: `/address clementi ave` only matches addresses containing the exact text `clementi ave`, whereas `/name ali bob` matches `ali` **or** `bob`. This is because addresses often contain common words such as `Ave`, `Blk` or `Street`, which would match almost every contact if each word were searched on its own.
* When you give more than one kind of criterion, a person must match **all** of them. For example, `/name ali /tag HIGH` returns only persons whose name contains `ali` **and** who are tagged `HIGH`.

Examples:
* `filter /tag MEDIUM` lists all persons tagged as `MEDIUM`.
* `filter /tag HIGH /tag MEDIUM` lists all persons tagged as `HIGH` or `MEDIUM`.
* `filter /name ali bob` lists all persons whose name contains `ali` or `bob`.
* `filter /address clementi ave /address jurong` lists all persons whose address contains `clementi ave` or `jurong`.
* `filter /name meier /tag HIGH /address street` lists all persons whose name contains `meier`, who are tagged `HIGH`, and whose address contains `street`.

**Common errors:**

| Incorrect input | Error message | How to correct it |
| --- | --- | --- |
| `filter` (no criteria) | `Invalid command format!` followed by the correct usage | Give at least one criterion, e.g. `filter /tag MEDIUM`. |
| `filter MEDIUM` (no prefix) | `Invalid command format!` followed by the correct usage | Start each criterion with its prefix, e.g. `filter /tag MEDIUM`. |
| `filter /name` (empty value) | `Invalid command format!` followed by the correct usage | Put a value after each prefix, e.g. `filter /name ali`. |
| `filter /tag URGENT` (not a valid tag) | `Invalid command format!` followed by the correct usage | Use one of `HIGH`, `MEDIUM` or `LOW`. |
| `filter /tag HIGH LOW` (several tags in one prefix) | `Invalid command format!` followed by the correct usage | Repeat the prefix: `filter /tag HIGH /tag LOW`. |

After a `filter` command, use `list` to display all persons again.

### Deleting a person: `delete`

Deletes the specified person from CareBook.

Format: `delete INDEX`

* Deletes the person at the specified `INDEX`.
* The index refers to the index number shown in the displayed person list.
* The index **must be a positive integer** 1, 2, 3, ...
* The command word `delete` is case-insensitive, e.g. `Delete 1` and `DELETE 1` also work.

Examples:
* `list` followed by `delete 2` deletes the 2nd person in CareBook.
* `find Betsy` followed by `delete 1` deletes the 1st person in the results of the `find` command.

Common errors:

Input | Error message shown | How to fix
------|---------------------|-----------
`delete` (no index) | `Invalid command format!` followed by the correct usage | Supply an index, e.g. `delete 1`
`delete abc` | `Invalid command format!` followed by the correct usage | Use a number, not text
`delete 1 2` | `Invalid command format!` followed by the correct usage | Use a single index per command
`delete 0` / `delete -1` | `Index must be a positive integer.` | Use an index of 1 or more
`delete 99` (out of range) | `The index provided is out of range. The displayed list has 3 person(s), so the index must be from 1 to 3.` | Use an index shown in the displayed list
`delete 1` (displayed list is empty) | `There are no persons in the displayed list to delete.` | Run `list` to show all persons, or add a person first 

### Clearing the displayed contact list: `clear`

Hides all contacts from the displayed list without deleting or changing any stored contact details.
The result box shows a confirmation instead of the previous command's output.

Format: `clear`

* Takes no arguments. Surrounding whitespace is allowed.
* Works even if the displayed list is already empty.
* Use `list` to show all contacts again, or `find KEYWORD` to search the stored contacts.
* Commands such as `view INDEX`, `edit INDEX` and `delete INDEX` use the currently displayed list.
  After `clear`, display contacts with `list` or `find` before using these index-based commands.
* Does not write to the contact data file. Saved contacts remain available after restarting CareBook.

Example:
* `clear` followed by `list` hides all contacts, then shows them again.

Common errors:

Input | Error message shown | How to fix
------|---------------------|-----------
`clear Alice` or `clear 1` | `Invalid command format!` followed by the correct usage | Enter `clear` without arguments. To delete a contact, use `delete INDEX`.

### Exiting the program: `exit`

Exits the program.

Format: `exit`

Trailing whitespace is allowed. Extra arguments are rejected to prevent accidental exits:
`exit Alice` or `exit 1` leaves CareBook open without changing any contacts and shows:

```text
Invalid command format!
Exit takes no arguments.
To close CareBook, enter: exit
No contacts have been changed.
```

### Saving the data

CareBook automatically saves data after every command that changes your contacts (such as `add`, `edit`, `archive` and `delete`). Commands that only show information, such as `list` and `find`, do not trigger a save. You do not need to save manually.

### Editing the data file

CareBook data is saved automatically as a JSON file `[JAR file location]/data/contacts.json`. Advanced users are welcome to update data directly by editing that data file.

The file is a JSON object with a `contacts` list. Each contact has these fields:

| Field       | Required | Description                                  |
|-------------|----------|----------------------------------------------|
| `name`      | Yes      | The contact's name.                          |
| `patientNo` | Yes      | The patient's phone number.                  |
| `familyNo`  | Yes      | The family member's phone number.            |
| `patientEmail` | No    | The patient's email address. Leave it out if the patient has no email. |
| `familyEmail` | No     | The family member's email address. Leave it out if the family member has no email. |
| `address`   | Yes      | The contact's address.                       |
| `archived` | No | `true` for archived contacts; defaults to `false` when omitted. |
| `tags`      | No       | A list containing at most one priority: `HIGH`, `MEDIUM`, or `LOW`. Use `[]` for no tags.       |
| `remarks`   | No       | A list of the contact's notes in order, e.g. `[ "Allergic to penicillin" ]`. Each note must not be blank or start with a space, and the notes can have at most 500 characters in total. Leave it out or use `[]` for no notes. |

Example:

```
{
  "contacts" : [ {
    "name" : "Alice Pauline",
    "patientNo" : "94351253",
    "familyNo" : "81234567",
    "address" : "123, Jurong West Ave 6, #08-111",
    "tags" : [ "HIGH" ],
    "patientEmail" : "alice@example.com"
  } ]
}
```

Commands accept any letter case, but priority strings edited directly in the data file must match exactly, including uppercase letters and no surrounding spaces. Older free-form tags such as `"friends"` are no longer valid and are not converted automatically. Before opening an older data file in this version, back it up and, with CareBook closed, keep only one appropriate priority per contact or clear the list to `[]`. Lists with multiple distinct priorities are invalid and prevent the data file from loading. Omitting the `tags` field also means no priority tags.

<box type="warning" seamless>

**Caution:**
If your changes make the data file invalid, CareBook shows the message "Data file could not be loaded. Starting with an empty contact list." and starts with an empty contact list. The invalid file remains on disk until you run a command that changes your contacts, which overwrites it. Still, we recommend backing up the file before editing it.<br>
Furthermore, certain edits can cause CareBook to behave in unexpected ways (e.g., if a value entered is outside of the acceptable range). Therefore, edit the data file only if you are confident that you can update it correctly.
</box>

### Archiving data files `[coming in v2.0]`

_Details coming soon ..._

--------------------------------------------------------------------------------------------------------------------

## FAQ

**Q**: How do I transfer my data to another computer?<br>
**A**: Install the app on the other computer and overwrite the data file it creates with the data file from your previous CareBook home folder.

--------------------------------------------------------------------------------------------------------------------

## Known issues

1. **When using multiple screens**, if you move the application to a secondary screen, and later switch to using only the primary screen, the GUI will open off-screen. The remedy is to delete the `preferences.json` file created by the application before running the application again.
2. **If you minimize the Help Window** and then run the `help` command (or use the `Help` menu, or the keyboard shortcut `F1`) again, the original Help Window will remain minimized, and no new Help Window will appear. The remedy is to manually restore the minimized Help Window.

--------------------------------------------------------------------------------------------------------------------

## Command summary

Action     | Format, Examples
-----------|----------------------------------------------------------------------------------------------------------------------------------------------------------------------
**Add**    | `add /name NAME /patientNo PATIENT_PHONE /familyNo FAMILY_PHONE [/pEmail PATIENT_EMAIL] [/fEmail FAMILY_EMAIL] /address ADDRESS [/tag PRIORITY] ` <br> e.g., `add /name James Ho /patientNo 22224444 /familyNo 93334444 /pEmail jamesho@example.com /fEmail jamesfamily@example.com /address 123, Clementi Rd, 1234665 /tag HIGH`
**Clear**  | `clear`
**Checkup** | `checkup INDEX dd/mm/yyyy HH:mm` or `checkup INDEX delete`<br> e.g., `checkup 1 25/12/2030 14:30`, `checkup 1 delete`
**Delete** | `delete INDEX`<br> e.g., `delete 3`
**Edit**   | `edit INDEX [/name NAME] [/patientNo PATIENT_PHONE] [/familyNo FAMILY_PHONE] [/pEmail PATIENT_EMAIL] [/fEmail FAMILY_EMAIL] [/address ADDRESS] [/tag PRIORITY] `<br> e.g.,`edit 2 /name James Lee /pEmail jameslee@example.com`
**Unarchive** | `unarchive INDEX`<br> e.g., `unarchive 3`
**Archive** | `archive INDEX`<br> e.g., `archive 3`
**List**   | `list` or `list archive`
**Filter** | `filter [/name NAME_KEYWORDS] [/tag TAG]... [/address ADDRESS]...`<br> e.g., `filter /name ali /tag HIGH /tag MEDIUM`
**Find**   | `find /CRITERION INPUT`<br> e.g., `find /patientNo 87`<br>Legacy name search: `find James Jake`
**Sort**   | `sort priority [asc\|desc]`, `sort name [asc\|desc]` or `sort recent`<br> e.g., `sort priority`, `sort name desc`
**Remark** | `remark INDEX NOTE`, `remark INDEX /edit NOTE_NUMBER NOTE`, `remark INDEX /delete NOTE_NUMBER`, `remark INDEX /clear`<br> e.g., `remark 2 Allergic to penicillin`
**View**   | `view INDEX`<br> e.g., `view 2`
**Help**   | `help`
