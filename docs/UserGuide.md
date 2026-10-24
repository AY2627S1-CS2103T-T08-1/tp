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

   * `list` : Lists all contacts.

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

* Extraneous parameters for `help` and `exit` are ignored.<br>
  For example, `help 123` is interpreted as `help`. The `list` and `clear` commands reject extra parameters;
  enter either command on its own.

* If you are using a PDF version of this document, be careful when copying and pasting commands that span multiple lines as space characters surrounding line-breaks may be omitted when copied over to the application.
</box>

### Viewing help: `help`

Shows a message explaining how to access the help page.

![help message](images/helpMessage.png)

Format: `help`


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

### Listing all persons: `list`

Shows a list of all persons in CareBook.

Format: `list`

**Common errors:**

| Incorrect input | Error message | How to correct it |
| --- | --- | --- |
| `list 3` | `Invalid command format. List command format: list` | Remove the index. Use `list` to show all persons. |
| `list Alice` or `list n/Alice` | `Invalid command format. List command format: list` | Use `find Alice` to search by name, or `list` to show all persons. |
| `LIST` or `List` | `Unknown command.` | Use the lowercase command `list`. |

The `list` command accepts no arguments. Leading and trailing spaces are allowed.
If all persons are already displayed, `list` leaves the displayed list unchanged and shows `Listed all persons.`;
this is normal behavior. After a `find` command, use `list` to display all persons again.

### Viewing a person's full details: `view`

Displays only the specified person and shows their full details in the result box.

Format: `view INDEX`

* Displays the person at the specified `INDEX`.
* The index refers to the index number shown in the displayed person list.
* The index **must be a positive integer** 1, 2, 3, ...
* Use `list` to display all persons again.

Example:
* `view 2` displays the full details of the 2nd person in the displayed list.

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

### Locating persons: `find`

Finds persons by the selected contact field using partial matches.

Format: `find /CRITERION INPUT`

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
* Existing `find KEYWORD [MORE_KEYWORDS]...` name searches remain supported.

Examples:
* `find /patientNo 87` finds patient numbers containing `87` consecutively.
* `find /email gmail` searches both email fields for `gmail`.
* `find /address street` searches addresses for `street`.
* `find John` returns `john` and `John Doe`
* `find alex david` returns `Alex Yeoh`, `David Li`<br>
  ![result for 'find alex david'](images/findAlexDavidResult.png)
* `find j` returns `John Doe`<br> ![result for 'find j'](images/findJ.png)

### Filtering persons by tag: `filter`

Shows only the persons who are tagged with the given tag.

Format: `filter TAG`

* `TAG` must be one of `HIGH`, `MEDIUM` or `LOW`.
* The tag is case-insensitive; for example, `medium` matches persons tagged `MEDIUM`.
* Only persons who have the exact tag are returned.
* Exactly one tag must be given.

Examples:
* `filter MEDIUM` lists all persons tagged as `MEDIUM`.
* `filter high` lists all persons tagged as `HIGH`.

**Common errors:**

| Incorrect input | Error message | How to correct it |
| --- | --- | --- |
| `filter` (no tag) | `Invalid command format!` followed by the correct usage | Supply a tag, e.g. `filter MEDIUM`. |
| `filter URGENT` (not a valid tag) | `Invalid command format!` followed by the correct usage | Use one of `HIGH`, `MEDIUM` or `LOW`. |
| `filter HIGH LOW` (more than one tag) | `Invalid command format!` followed by the correct usage | Give only one tag per command. |

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

### Saving the data

CareBook automatically saves data after every command that changes your contacts (such as `add`, `edit` and `delete`). Commands that only show information, such as `list` and `find`, do not trigger a save. You do not need to save manually.

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
| `tags`      | No       | A list containing at most one priority: `HIGH`, `MEDIUM`, or `LOW`. Use `[]` for no tags.       |

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
**Delete** | `delete INDEX`<br> e.g., `delete 3`
**Edit**   | `edit INDEX [/name NAME] [/patientNo PATIENT_PHONE] [/familyNo FAMILY_PHONE] [/pEmail PATIENT_EMAIL] [/fEmail FAMILY_EMAIL] [/address ADDRESS] [/tag PRIORITY] `<br> e.g.,`edit 2 /name James Lee /pEmail jameslee@example.com`
**Find**   | `find /CRITERION INPUT`<br> e.g., `find /patientNo 87`<br>Legacy name search: `find James Jake`
**Filter** | `filter TAG`<br> e.g., `filter MEDIUM`
**List**   | `list`
**View**   | `view INDEX`<br> e.g., `view 2`
**Help**   | `help`
