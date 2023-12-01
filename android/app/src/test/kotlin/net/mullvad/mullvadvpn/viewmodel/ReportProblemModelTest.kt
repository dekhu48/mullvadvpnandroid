package net.mullvad.mullvadvpn.viewmodel

import androidx.lifecycle.viewModelScope
import app.cash.turbine.test
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import net.mullvad.mullvadvpn.dataproxy.MullvadProblemReport
import net.mullvad.mullvadvpn.dataproxy.SendProblemReportResult
import net.mullvad.mullvadvpn.dataproxy.UserReport
import net.mullvad.mullvadvpn.lib.common.test.TestCoroutineRule
import net.mullvad.mullvadvpn.repository.ProblemReportRepository
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals

class ReportProblemModelTest {
    @get:Rule val testCoroutineRule = TestCoroutineRule()

    @MockK private lateinit var mockMullvadProblemReport: MullvadProblemReport

    @MockK(relaxed = true)
    private lateinit var mockProblemReportRepository: ProblemReportRepository

    private val problemReportFlow = MutableStateFlow(UserReport("", ""))

    private lateinit var viewModel: ReportProblemViewModel

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        coEvery { mockMullvadProblemReport.collectLogs() } returns true
        coEvery { mockProblemReportRepository.problemReport } returns problemReportFlow
        viewModel = ReportProblemViewModel(mockMullvadProblemReport, mockProblemReportRepository)
    }

    @After
    fun tearDown() {
        viewModel.viewModelScope.coroutineContext.cancel()
    }

    @Test
    fun sendReportFailedToCollectLogs() = runTest {
        // Arrange
        coEvery { mockMullvadProblemReport.sendReport(any()) } returns
            SendProblemReportResult.Error.CollectLog
        val email = "my@email.com"

        // Act, Assert
        viewModel.uiState.test {
            assertEquals(null, awaitItem().sendingState)
            viewModel.sendReport(email, "My description")
            assertEquals(SendingReportUiState.Sending, awaitItem().sendingState)
            assertEquals(
                SendingReportUiState.Error(SendProblemReportResult.Error.CollectLog),
                awaitItem().sendingState,
            )
        }
    }

    @Test
    fun sendReportFailedToSendReport() = runTest {
        // Arrange
        coEvery { mockMullvadProblemReport.sendReport(any()) } returns
            SendProblemReportResult.Error.SendReport
        val email = "my@email.com"

        // Act, Assert
        viewModel.uiState.test {
            assertEquals(null, awaitItem().sendingState)
            viewModel.sendReport(email, "My description")
            assertEquals(SendingReportUiState.Sending, awaitItem().sendingState)
            assertEquals(
                SendingReportUiState.Error(SendProblemReportResult.Error.SendReport),
                awaitItem().sendingState,
            )
        }
    }

    @Test
    fun sendReportWithoutEmailSuccessfully() = runTest {
        // Arrange
        coEvery { mockMullvadProblemReport.sendReport(any()) } returns
            SendProblemReportResult.Success
        val email = ""

        // Act, Assert
        viewModel.uiState.test {
            assertEquals(ReportProblemUiState(false, null), awaitItem())
            viewModel.sendReport(email, "My description")
            assertEquals(ReportProblemUiState(true, null), awaitItem())
            viewModel.sendReport(email, "My description")
            assertEquals(ReportProblemUiState(false, SendingReportUiState.Sending), awaitItem())
            assertEquals(
                ReportProblemUiState(false, SendingReportUiState.Success(null)),
                awaitItem(),
            )
        }
    }

    @Test
    fun sendReportSuccessfully() = runTest {
        // Arrange
        coEvery { mockMullvadProblemReport.collectLogs() } returns true
        coEvery { mockMullvadProblemReport.sendReport(any()) } returns
            SendProblemReportResult.Success
        val email = "my@email.com"

        // Act, Assert
        viewModel.uiState.test {
            assertEquals(awaitItem(), ReportProblemUiState(false, null))
            viewModel.sendReport(email, "My description")

            assertEquals(awaitItem(), ReportProblemUiState(false, SendingReportUiState.Sending))
            assertEquals(
                awaitItem(),
                ReportProblemUiState(false, SendingReportUiState.Success(email)),
            )
        }
    }

    @Test
    fun testUpdateEmail() = runTest {
        // Arrange
        val email = "my@email.com"

        // Act
        viewModel.updateEmail(email)

        // Assert
        verify { mockProblemReportRepository.setEmail(email) }
    }

    @Test
    fun testUpdateDescription() = runTest {
        // Arrange
        val description = "My description"

        // Act
        viewModel.updateDescription(description)

        // Assert
        verify { mockProblemReportRepository.setDescription(description) }
    }

    @Test
    fun testUpdateProblemReport() = runTest {
        // Arrange
        val userReport = UserReport("my@email.com", "My description")

        // Act, Assert
        viewModel.uiState.test {
            awaitItem()
            problemReportFlow.value = userReport
            val result = awaitItem()
            assertEquals(userReport.email, result.email)
            assertEquals(userReport.description, result.description)
        }
    }
}
