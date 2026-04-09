import com.intellij.driver.sdk.invokeAction
import com.intellij.driver.sdk.ui.components.UiComponent.Companion.waitFound
import com.intellij.driver.sdk.ui.components.common.ideFrame
import com.intellij.driver.sdk.ui.components.elements.button
import com.intellij.driver.sdk.ui.components.elements.checkBox
import com.intellij.driver.sdk.ui.components.elements.dialog
import com.intellij.ide.starter.driver.engine.runIdeWithDriver
import com.intellij.ide.starter.ide.IdeProductProvider
import com.intellij.ide.starter.junit5.hyphenateWithClass
import com.intellij.ide.starter.models.TestCase
import com.intellij.ide.starter.project.LocalProjectInfo
import com.intellij.ide.starter.runner.CurrentTestMethod
import com.intellij.ide.starter.runner.Starter
import com.intellij.ide.starter.sdk.JdkDownloaderFacade.jdk21
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.nio.file.Files
import kotlin.time.Duration.Companion.minutes

class SettingsUiTest {

    @Test
    fun testEnableCreateChangelistsAutomatically() {
        // OPTIMIZATION: Using an empty local project instead of a remote GitHub repository.
        // This makes the UI test lightning fast, highly stable, and independent of network conditions,
        // avoiding TimeoutExceptions during heavy project indexing.
        val emptyProjectPath = Files.createTempDirectory("empty-test-project")

        val testCase = TestCase(
            IdeProductProvider.IC,
            LocalProjectInfo(emptyProjectPath)
        ).useRelease("2024.1")

        val testContext = Starter
            .newContext(CurrentTestMethod.hyphenateWithClass(), testCase)
            .setupSdk(jdk21.toSdk())
            .addProjectToTrustedLocations()
            .prepareProjectCleanImport()

        // Requirement 1: Open an IDE
        testContext.runIdeWithDriver().useDriverAndCloseIde {
            ideFrame {
                waitForIndicators(5.minutes)

                // Requirement 2: Open Settings
                invokeAction("ShowSettings", now = false)

                dialog(title = "Settings") {
                    waitFound()

                    // Requirement 3: Choose "Version Control" and then "Changelists"
                    x { byVisibleText("Version Control") }.waitFound().click()
                    x { byVisibleText("Changelists") }.waitFound().click()

                    val createChangelistsCheckbox = checkBox {
                        byVisibleText("Create changelists automatically")
                    }
                    createChangelistsCheckbox.waitFound()

                    // Requirement 4: Select the checkbox that is called "Create changelists automatically"
                    if (!createChangelistsCheckbox.isSelected()) {
                        createChangelistsCheckbox.click()
                    }

                    // Requirement 5: Check that it is selected.
                    assertTrue(createChangelistsCheckbox.isSelected()) {
                        "Checkbox 'Create changelists automatically' should be selected!"
                    }

                    // Requirement 6: Click on the OK button
                    button("OK").click()
                }
            }
        }
    }
}