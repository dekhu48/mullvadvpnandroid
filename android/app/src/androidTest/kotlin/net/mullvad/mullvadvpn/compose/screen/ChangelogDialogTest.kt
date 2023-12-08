package net.mullvad.mullvadvpn.compose.screen

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import de.mannodermaus.junit5.compose.createComposeExtension
import io.mockk.MockKAnnotations
import io.mockk.Runs
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.verify
import kotlinx.coroutines.flow.MutableStateFlow
import net.mullvad.mullvadvpn.compose.dialog.ChangelogDialog
import net.mullvad.mullvadvpn.compose.setContentWithTheme
import net.mullvad.mullvadvpn.viewmodel.ChangelogDialogUiState
import net.mullvad.mullvadvpn.viewmodel.ChangelogViewModel
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

@OptIn(ExperimentalTestApi::class)
class ChangelogDialogTest {
    @JvmField @RegisterExtension val composeExtension = createComposeExtension()

    @MockK lateinit var mockedViewModel: ChangelogViewModel

    @BeforeEach
    fun setup() {
        MockKAnnotations.init(this)
    }

    @Test
    fun testShowChangeLogWhenNeeded() =
        composeExtension.use {
            // Arrange
            every { mockedViewModel.uiState } returns
                MutableStateFlow(ChangelogDialogUiState.Show(listOf(CHANGELOG_ITEM)))
            every { mockedViewModel.dismissChangelogDialog() } just Runs

            setContentWithTheme {
                ChangelogDialog(
                    changesList = listOf(CHANGELOG_ITEM),
                    version = CHANGELOG_VERSION,
                    onDismiss = { mockedViewModel.dismissChangelogDialog() }
                )
            }

            // Check changelog content showed within dialog
            onNodeWithText(CHANGELOG_ITEM).assertExists()

            // perform click on Got It button to check if dismiss occur
            onNodeWithText(CHANGELOG_BUTTON_TEXT).performClick()

            // Assert
            verify { mockedViewModel.dismissChangelogDialog() }
        }

    companion object {
        private const val CHANGELOG_BUTTON_TEXT = "Got it!"
        private const val CHANGELOG_ITEM = "Changelog item"
        private const val CHANGELOG_VERSION = "1234.5"
    }
}
