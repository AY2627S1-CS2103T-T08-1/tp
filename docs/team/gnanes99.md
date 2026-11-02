---
  layout: default.md
  title: "Ramakrishnan Gnaneswar's Project Portfolio Page"
---

### Project: CareBook

CareBook is a desktop application for in-house caregivers and healthcare administrators to manage patient and family contact details. The user interacts with it using a CLI, and it has a GUI created with JavaFX.

Given below are my contributions to the project.

* **New Feature**: Added the `remark` command.
  * What it does: lets the user add, edit, delete and clear numbered notes on a contact.
  * Justification: caregivers need to keep details that do not fit the fixed contact fields.
  * Highlights: notes are saved to the data file, a contact cannot have the same note twice, and each wrong format gets its own error message. Pull requests [\#133](https://github.com/AY2627S1-CS2103T-T08-1/tp/pull/133), [\#134](https://github.com/AY2627S1-CS2103T-T08-1/tp/pull/134), [\#249](https://github.com/AY2627S1-CS2103T-T08-1/tp/pull/249), [\#267](https://github.com/AY2627S1-CS2103T-T08-1/tp/pull/267), [\#300](https://github.com/AY2627S1-CS2103T-T08-1/tp/pull/300)

* **New Feature**: Implemented the `edit` command.
  * What it does: changes the details of an existing contact.
  * Highlights: the command keeps the displayed list in place, stays in the archive list, and checks the checkup booking rules when the family HP changes. Pull requests [\#101](https://github.com/AY2627S1-CS2103T-T08-1/tp/pull/101), [\#235](https://github.com/AY2627S1-CS2103T-T08-1/tp/pull/235), [\#247](https://github.com/AY2627S1-CS2103T-T08-1/tp/pull/247), [\#280](https://github.com/AY2627S1-CS2103T-T08-1/tp/pull/280), [\#287](https://github.com/AY2627S1-CS2103T-T08-1/tp/pull/287)

* **Enhancements to existing features**:
  * Built data saving and loading for CareBook (Pull requests [\#55](https://github.com/AY2627S1-CS2103T-T08-1/tp/pull/55), [\#77](https://github.com/AY2627S1-CS2103T-T08-1/tp/pull/77))
  * Made a failed save undo the command, so the data file and the displayed list stay unchanged, and made the data file be replaced in one step, so a failed write does not damage it (Pull requests [\#234](https://github.com/AY2627S1-CS2103T-T08-1/tp/pull/234), [\#260](https://github.com/AY2627S1-CS2103T-T08-1/tp/pull/260), [\#304](https://github.com/AY2627S1-CS2103T-T08-1/tp/pull/304))
  * Made loading reject invalid data files instead of crashing or loading bad values (Pull requests [\#135](https://github.com/AY2627S1-CS2103T-T08-1/tp/pull/135), [\#233](https://github.com/AY2627S1-CS2103T-T08-1/tp/pull/233), [\#259](https://github.com/AY2627S1-CS2103T-T08-1/tp/pull/259))
  * Allowed `/` in names and addresses where it joins short parts, as in `s/o` and `4/5`, while still catching a prefix typed in the wrong place, and made `add` and `edit` say which `/` is wrong (Pull requests [\#325](https://github.com/AY2627S1-CS2103T-T08-1/tp/pull/325), [\#329](https://github.com/AY2627S1-CS2103T-T08-1/tp/pull/329))
  * Made `edit` and `remark` errors say what is wrong, including how many contacts are shown and whether a matching contact is archived (Pull requests [\#257](https://github.com/AY2627S1-CS2103T-T08-1/tp/pull/257), [\#258](https://github.com/AY2627S1-CS2103T-T08-1/tp/pull/258), [\#281](https://github.com/AY2627S1-CS2103T-T08-1/tp/pull/281), [\#282](https://github.com/AY2627S1-CS2103T-T08-1/tp/pull/282))

* **Code contributed**: [RepoSense link](https://nus-cs2103-ay2627-s1.github.io/tp-dashboard/?search=Gnanes99&breakdown=true&sort=groupTitle&sortWithin=title&since=2026-09-18&timeframe=commit&mergegroup=&groupSelect=groupByRepos&checkedFileTypes=docs~functional-code~test-code~other)

* **Project management**:
  * Prepared the `v1.3`, `v1.4` and `v1.5` releases (Pull requests [\#79](https://github.com/AY2627S1-CS2103T-T08-1/tp/pull/79), [\#121](https://github.com/AY2627S1-CS2103T-T08-1/tp/pull/121), [\#298](https://github.com/AY2627S1-CS2103T-T08-1/tp/pull/298))

* **Contributions to team-based tasks**:
  * Replaced the AddressBook branding in the app and set up the site-wide settings of the project website (Pull requests [\#40](https://github.com/AY2627S1-CS2103T-T08-1/tp/pull/40), [\#81](https://github.com/AY2627S1-CS2103T-T08-1/tp/pull/81))
  * Added the Codecov badge, enabled assertions when running the app, and removed the unused sample data (Pull requests [\#52](https://github.com/AY2627S1-CS2103T-T08-1/tp/pull/52), [\#117](https://github.com/AY2627S1-CS2103T-T08-1/tp/pull/117), [\#123](https://github.com/AY2627S1-CS2103T-T08-1/tp/pull/123))
  * Fixed the master branch when it stopped building after merges, and restored UG and DG lines lost in a merge (Pull requests [\#244](https://github.com/AY2627S1-CS2103T-T08-1/tp/pull/244), [\#327](https://github.com/AY2627S1-CS2103T-T08-1/tp/pull/327))
  * Fixed code-quality issues in the edit, remark, saving and parsing code (Pull requests [\#263](https://github.com/AY2627S1-CS2103T-T08-1/tp/pull/263), [\#293](https://github.com/AY2627S1-CS2103T-T08-1/tp/pull/293), [\#332](https://github.com/AY2627S1-CS2103T-T08-1/tp/pull/332))

* **Documentation**:
  * User Guide:
    * Added the `remark` and `edit` sections and the contact field rules (Pull requests [\#134](https://github.com/AY2627S1-CS2103T-T08-1/tp/pull/134), [\#250](https://github.com/AY2627S1-CS2103T-T08-1/tp/pull/250), [\#268](https://github.com/AY2627S1-CS2103T-T08-1/tp/pull/268), [\#325](https://github.com/AY2627S1-CS2103T-T08-1/tp/pull/325))
    * Wrote the Saving the data and Editing the data file sections (Pull requests [\#283](https://github.com/AY2627S1-CS2103T-T08-1/tp/pull/283), [\#316](https://github.com/AY2627S1-CS2103T-T08-1/tp/pull/316))
  * Developer Guide:
    * Added the `edit` and `remark` sections, with the `EditSequenceDiagram` and `RemarkSequenceDiagram` (Pull requests [\#134](https://github.com/AY2627S1-CS2103T-T08-1/tp/pull/134), [\#264](https://github.com/AY2627S1-CS2103T-T08-1/tp/pull/264), [\#288](https://github.com/AY2627S1-CS2103T-T08-1/tp/pull/288))
    * Added the Saving data section and the `SaveSequenceDiagram`, and updated the model and storage class diagrams (Pull requests [\#105](https://github.com/AY2627S1-CS2103T-T08-1/tp/pull/105), [\#134](https://github.com/AY2627S1-CS2103T-T08-1/tp/pull/134), [\#264](https://github.com/AY2627S1-CS2103T-T08-1/tp/pull/264), [\#316](https://github.com/AY2627S1-CS2103T-T08-1/tp/pull/316))
    * Wrote the design considerations for the `/` rule, the data file write and data files that cannot be loaded, and completed the edit, remark and save use cases (Pull requests [\#325](https://github.com/AY2627S1-CS2103T-T08-1/tp/pull/325), [\#334](https://github.com/AY2627S1-CS2103T-T08-1/tp/pull/334))
