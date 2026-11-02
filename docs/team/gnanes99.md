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
  * Highlights: notes are saved to the data file, and a person cannot have the same note twice. Pull requests [\#133](https://github.com/AY2627S1-CS2103T-T08-1/tp/pull/133), [\#134](https://github.com/AY2627S1-CS2103T-T08-1/tp/pull/134)

* **New Feature**: Implemented the `edit` command.
  * What it does: changes the details of an existing contact.
  * Highlights: the command keeps the displayed list in place, stays in the archive list, and checks the checkup booking rules when the family HP changes. Pull requests [\#101](https://github.com/AY2627S1-CS2103T-T08-1/tp/pull/101), [\#247](https://github.com/AY2627S1-CS2103T-T08-1/tp/pull/247), [\#280](https://github.com/AY2627S1-CS2103T-T08-1/tp/pull/280), [\#287](https://github.com/AY2627S1-CS2103T-T08-1/tp/pull/287)

* **Enhancements to existing features**:
  * Built data saving and loading for CareBook (Pull requests [\#55](https://github.com/AY2627S1-CS2103T-T08-1/tp/pull/55), [\#77](https://github.com/AY2627S1-CS2103T-T08-1/tp/pull/77))
  * Made a failed save keep the data file and the displayed list unchanged (Pull requests [\#234](https://github.com/AY2627S1-CS2103T-T08-1/tp/pull/234), [\#304](https://github.com/AY2627S1-CS2103T-T08-1/tp/pull/304))
  * Made loading reject invalid data files (Pull requests [\#233](https://github.com/AY2627S1-CS2103T-T08-1/tp/pull/233), [\#259](https://github.com/AY2627S1-CS2103T-T08-1/tp/pull/259))

* **Code contributed**: [RepoSense link]()

* **Project management**:
  * Prepared the `v1.3`, `v1.4` and `v1.5` releases (Pull requests [\#79](https://github.com/AY2627S1-CS2103T-T08-1/tp/pull/79), [\#121](https://github.com/AY2627S1-CS2103T-T08-1/tp/pull/121), [\#298](https://github.com/AY2627S1-CS2103T-T08-1/tp/pull/298))

* **Documentation**:
  * User Guide:
    * Added the `remark` and `edit` sections and the contact field rules (Pull requests [\#134](https://github.com/AY2627S1-CS2103T-T08-1/tp/pull/134), [\#250](https://github.com/AY2627S1-CS2103T-T08-1/tp/pull/250))
  * Developer Guide:
    * Added the `edit` and `remark` sections and the save sequence diagram (Pull requests [\#105](https://github.com/AY2627S1-CS2103T-T08-1/tp/pull/105), [\#134](https://github.com/AY2627S1-CS2103T-T08-1/tp/pull/134), [\#264](https://github.com/AY2627S1-CS2103T-T08-1/tp/pull/264))

* **Community**:
  * PRs reviewed (with non-trivial review comments):
  * Contributed to forum discussions:
  * Reported bugs and suggestions for other teams in the class:
