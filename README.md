# JetBrains QA Internship Task

This repository contains the solution for the JetBrains SDET Internship task (Functional Automation).

## Task Description
The goal is to write a UI integration test using the `intellij.tools.ide.starter` framework that:
1. Opens an IDE (IntelliJ IDEA Community).
2. Opens the Settings dialog.
3. Navigates to **Version Control -> Changelists**.
4. Selects the **"Create changelists automatically"** checkbox.
5. Verifies that the checkbox is selected.
6. Clicks the **OK** button.

## Implementation Notes
To ensure the test is **fast, deterministic, and independent of network conditions**, I implemented the following optimization:
- Instead of downloading a heavy public repository from GitHub (which can lead to `ExecTimeoutException` during indexing on CI or slower machines), the test dynamically generates an **empty local directory** (`LocalProjectInfo`).
- This allows the IDE to index the project almost instantly, ensuring that `waitForIndicators` passes quickly and the test focuses strictly on the required UI interactions.

## How to run the test
Execute the following command in the root directory:

```bash
./gradlew test