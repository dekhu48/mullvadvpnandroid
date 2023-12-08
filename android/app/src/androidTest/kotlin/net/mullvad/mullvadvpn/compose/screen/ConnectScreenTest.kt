package net.mullvad.mullvadvpn.compose.screen

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import de.mannodermaus.junit5.compose.createComposeExtension
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.mockk
import io.mockk.unmockkAll
import io.mockk.verify
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import net.mullvad.mullvadvpn.compose.setContentWithTheme
import net.mullvad.mullvadvpn.compose.state.ConnectUiState
import net.mullvad.mullvadvpn.compose.test.CIRCULAR_PROGRESS_INDICATOR
import net.mullvad.mullvadvpn.compose.test.CONNECT_BUTTON_TEST_TAG
import net.mullvad.mullvadvpn.compose.test.LOCATION_INFO_TEST_TAG
import net.mullvad.mullvadvpn.compose.test.NOTIFICATION_BANNER_ACTION
import net.mullvad.mullvadvpn.compose.test.RECONNECT_BUTTON_TEST_TAG
import net.mullvad.mullvadvpn.compose.test.SCROLLABLE_COLUMN_TEST_TAG
import net.mullvad.mullvadvpn.compose.test.SELECT_LOCATION_BUTTON_TEST_TAG
import net.mullvad.mullvadvpn.model.GeoIpLocation
import net.mullvad.mullvadvpn.model.TunnelState
import net.mullvad.mullvadvpn.relaylist.RelayItem
import net.mullvad.mullvadvpn.repository.InAppNotification
import net.mullvad.mullvadvpn.ui.VersionInfo
import net.mullvad.mullvadvpn.viewmodel.ConnectViewModel
import net.mullvad.talpid.net.TransportProtocol
import net.mullvad.talpid.net.TunnelEndpoint
import net.mullvad.talpid.tunnel.ActionAfterDisconnect
import net.mullvad.talpid.tunnel.ErrorState
import net.mullvad.talpid.tunnel.ErrorStateCause
import org.joda.time.DateTime
import org.junit.After
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

@OptIn(ExperimentalTestApi::class)
class ConnectScreenTest {
    @JvmField @RegisterExtension val composeExtension = createComposeExtension()

    @BeforeEach
    fun setup() {
        MockKAnnotations.init(this)
    }

    @After
    fun teardown() {
        unmockkAll()
    }

    @Test
    fun testDefaultState() =
        composeExtension.use {
            // Arrange
            setContentWithTheme {
                ConnectScreen(
                    uiState = ConnectUiState.INITIAL,
                    uiSideEffect = MutableSharedFlow<ConnectViewModel.UiSideEffect>().asSharedFlow()
                )
            }

            // Assert
            apply {
                onNodeWithTag(SCROLLABLE_COLUMN_TEST_TAG).assertExists()
                onNodeWithText("UNSECURED CONNECTION").assertExists()
                onNodeWithText("Secure my connection").assertExists()
            }
        }

    @Test
    fun testConnectingState() =
        composeExtension.use {
            // Arrange
            setContentWithTheme {
                ConnectScreen(
                    uiState =
                        ConnectUiState(
                            location = null,
                            relayLocation = null,
                            tunnelUiState = TunnelState.Connecting(null, null),
                            tunnelRealState = TunnelState.Connecting(null, null),
                            inAddress = null,
                            outAddress = "",
                            showLocation = false,
                            isTunnelInfoExpanded = false,
                            deviceName = "",
                            daysLeftUntilExpiry = null,
                            inAppNotification = InAppNotification.TunnelStateBlocked
                        ),
                    uiSideEffect = MutableSharedFlow<ConnectViewModel.UiSideEffect>().asSharedFlow()
                )
            }

            // Assert
            apply {
                onNodeWithTag(CIRCULAR_PROGRESS_INDICATOR).assertExists()
                onNodeWithText("CREATING SECURE CONNECTION").assertExists()
                onNodeWithText("Switch location").assertExists()
                onNodeWithText("Cancel").assertExists()
                onNodeWithText("BLOCKING INTERNET").assertExists()
            }
        }

    @Test
    fun testConnectingStateQuantumSecured() =
        composeExtension.use {
            // Arrange
            val mockTunnelEndpoint: TunnelEndpoint = mockk(relaxed = true)
            every { mockTunnelEndpoint.quantumResistant } returns true
            setContentWithTheme {
                ConnectScreen(
                    uiState =
                        ConnectUiState(
                            location = null,
                            relayLocation = null,
                            tunnelUiState =
                                TunnelState.Connecting(endpoint = mockTunnelEndpoint, null),
                            tunnelRealState =
                                TunnelState.Connecting(endpoint = mockTunnelEndpoint, null),
                            inAddress = null,
                            outAddress = "",
                            showLocation = false,
                            isTunnelInfoExpanded = false,
                            deviceName = "",
                            daysLeftUntilExpiry = null,
                            inAppNotification = InAppNotification.TunnelStateBlocked
                        ),
                    uiSideEffect = MutableSharedFlow<ConnectViewModel.UiSideEffect>().asSharedFlow()
                )
            }

            // Assert
            apply {
                onNodeWithTag(CIRCULAR_PROGRESS_INDICATOR).assertExists()
                onNodeWithText("CREATING QUANTUM SECURE CONNECTION").assertExists()
                onNodeWithText("Switch location").assertExists()
                onNodeWithText("Cancel").assertExists()
                onNodeWithText("BLOCKING INTERNET").assertExists()
            }
        }

    @Test
    fun testConnectedState() =
        composeExtension.use {
            // Arrange
            val mockTunnelEndpoint: TunnelEndpoint = mockk(relaxed = true)
            setContentWithTheme {
                ConnectScreen(
                    uiState =
                        ConnectUiState(
                            location = null,
                            relayLocation = null,
                            tunnelUiState = TunnelState.Connected(mockTunnelEndpoint, null),
                            tunnelRealState = TunnelState.Connected(mockTunnelEndpoint, null),
                            inAddress = null,
                            outAddress = "",
                            showLocation = false,
                            isTunnelInfoExpanded = false,
                            deviceName = "",
                            daysLeftUntilExpiry = null,
                            inAppNotification = null
                        ),
                    uiSideEffect = MutableSharedFlow<ConnectViewModel.UiSideEffect>().asSharedFlow()
                )
            }

            // Assert
            apply {
                onNodeWithText("SECURE CONNECTION").assertExists()
                onNodeWithText("Switch location").assertExists()
                onNodeWithText("Disconnect").assertExists()
            }
        }

    @Test
    fun testConnectedStateQuantumSecured() =
        composeExtension.use {
            // Arrange
            val mockTunnelEndpoint: TunnelEndpoint = mockk(relaxed = true)
            every { mockTunnelEndpoint.quantumResistant } returns true
            setContentWithTheme {
                ConnectScreen(
                    uiState =
                        ConnectUiState(
                            location = null,
                            relayLocation = null,
                            tunnelUiState = TunnelState.Connected(mockTunnelEndpoint, null),
                            tunnelRealState = TunnelState.Connected(mockTunnelEndpoint, null),
                            inAddress = null,
                            outAddress = "",
                            showLocation = false,
                            isTunnelInfoExpanded = false,
                            deviceName = "",
                            daysLeftUntilExpiry = null,
                            inAppNotification = null
                        ),
                    uiSideEffect = MutableSharedFlow<ConnectViewModel.UiSideEffect>().asSharedFlow()
                )
            }

            // Assert
            apply {
                onNodeWithText("QUANTUM SECURE CONNECTION").assertExists()
                onNodeWithText("Switch location").assertExists()
                onNodeWithText("Disconnect").assertExists()
            }
        }

    @Test
    fun testDisconnectingState() =
        composeExtension.use {
            // Arrange
            val mockRelayLocation: RelayItem = mockk(relaxed = true)
            val mockLocationName = "Home"
            every { mockRelayLocation.locationName } returns mockLocationName
            setContentWithTheme {
                ConnectScreen(
                    uiState =
                        ConnectUiState(
                            location = null,
                            relayLocation = mockRelayLocation,
                            tunnelUiState =
                                TunnelState.Disconnecting(ActionAfterDisconnect.Nothing),
                            tunnelRealState =
                                TunnelState.Disconnecting(ActionAfterDisconnect.Nothing),
                            inAddress = null,
                            outAddress = "",
                            showLocation = true,
                            isTunnelInfoExpanded = false,
                            deviceName = "",
                            daysLeftUntilExpiry = null,
                            inAppNotification = null
                        ),
                    uiSideEffect = MutableSharedFlow<ConnectViewModel.UiSideEffect>().asSharedFlow()
                )
            }

            // Assert
            apply {
                onNodeWithText("UNSECURED CONNECTION").assertExists()
                onNodeWithText(mockLocationName).assertExists()
                onNodeWithText("Disconnect").assertExists()
            }
        }

    @Test
    fun testDisconnectedState() =
        composeExtension.use {
            // Arrange
            val mockRelayLocation: RelayItem = mockk(relaxed = true)
            val mockLocationName = "Home"
            every { mockRelayLocation.locationName } returns mockLocationName
            setContentWithTheme {
                ConnectScreen(
                    uiState =
                        ConnectUiState(
                            location = null,
                            relayLocation = mockRelayLocation,
                            tunnelUiState = TunnelState.Disconnected,
                            tunnelRealState = TunnelState.Disconnected,
                            inAddress = null,
                            outAddress = "",
                            showLocation = true,
                            isTunnelInfoExpanded = false,
                            deviceName = "",
                            daysLeftUntilExpiry = null,
                            inAppNotification = null
                        ),
                    uiSideEffect = MutableSharedFlow<ConnectViewModel.UiSideEffect>().asSharedFlow()
                )
            }

            // Assert
            apply {
                onNodeWithText("UNSECURED CONNECTION").assertExists()
                onNodeWithText(mockLocationName).assertExists()
                onNodeWithText("Secure my connection").assertExists()
            }
        }

    @Test
    fun testErrorStateBlocked() =
        composeExtension.use {
            // Arrange
            val mockRelayLocation: RelayItem = mockk(relaxed = true)
            val mockLocationName = "Home"
            every { mockRelayLocation.locationName } returns mockLocationName
            setContentWithTheme {
                ConnectScreen(
                    uiState =
                        ConnectUiState(
                            location = null,
                            relayLocation = mockRelayLocation,
                            tunnelUiState =
                                TunnelState.Error(
                                    ErrorState(ErrorStateCause.StartTunnelError, true)
                                ),
                            tunnelRealState =
                                TunnelState.Error(
                                    ErrorState(ErrorStateCause.StartTunnelError, true)
                                ),
                            inAddress = null,
                            outAddress = "",
                            showLocation = true,
                            isTunnelInfoExpanded = false,
                            deviceName = "",
                            daysLeftUntilExpiry = null,
                            inAppNotification =
                                InAppNotification.TunnelStateError(
                                    ErrorState(ErrorStateCause.StartTunnelError, true)
                                )
                        ),
                    uiSideEffect = MutableSharedFlow<ConnectViewModel.UiSideEffect>().asSharedFlow()
                )
            }

            // Assert
            apply {
                onNodeWithText("BLOCKED CONNECTION").assertExists()
                onNodeWithText(mockLocationName).assertExists()
                onNodeWithText("Disconnect").assertExists()
                onNodeWithText("BLOCKING INTERNET").assertExists()
            }
        }

    @Test
    fun testErrorStateNotBlocked() =
        composeExtension.use {
            // Arrange
            val mockRelayLocation: RelayItem = mockk(relaxed = true)
            val mockLocationName = "Home"
            every { mockRelayLocation.locationName } returns mockLocationName
            setContentWithTheme {
                ConnectScreen(
                    uiState =
                        ConnectUiState(
                            location = null,
                            relayLocation = mockRelayLocation,
                            tunnelUiState =
                                TunnelState.Error(
                                    ErrorState(ErrorStateCause.StartTunnelError, false)
                                ),
                            tunnelRealState =
                                TunnelState.Error(
                                    ErrorState(ErrorStateCause.StartTunnelError, false)
                                ),
                            inAddress = null,
                            outAddress = "",
                            showLocation = true,
                            isTunnelInfoExpanded = false,
                            deviceName = "",
                            daysLeftUntilExpiry = null,
                            inAppNotification =
                                InAppNotification.TunnelStateError(
                                    ErrorState(ErrorStateCause.StartTunnelError, false)
                                )
                        ),
                    uiSideEffect = MutableSharedFlow<ConnectViewModel.UiSideEffect>().asSharedFlow()
                )
            }

            // Assert
            apply {
                onNodeWithText("FAILED TO SECURE CONNECTION").assertExists()
                onNodeWithText(mockLocationName).assertExists()
                onNodeWithText("Dismiss").assertExists()
                onNodeWithText(
                        text = "Critical error (your attention is required)",
                        ignoreCase = true
                    )
                    .assertExists()
            }
        }

    @Test
    fun testReconnectingState() =
        composeExtension.use {
            // Arrange
            setContentWithTheme {
                ConnectScreen(
                    uiState =
                        ConnectUiState(
                            location = null,
                            relayLocation = null,
                            tunnelUiState =
                                TunnelState.Disconnecting(ActionAfterDisconnect.Reconnect),
                            tunnelRealState =
                                TunnelState.Disconnecting(ActionAfterDisconnect.Reconnect),
                            inAddress = null,
                            outAddress = "",
                            showLocation = false,
                            isTunnelInfoExpanded = false,
                            deviceName = "",
                            daysLeftUntilExpiry = null,
                            inAppNotification = InAppNotification.TunnelStateBlocked
                        ),
                    uiSideEffect = MutableSharedFlow<ConnectViewModel.UiSideEffect>().asSharedFlow()
                )
            }

            // Assert
            apply {
                onNodeWithTag(CIRCULAR_PROGRESS_INDICATOR).assertExists()
                onNodeWithText("CREATING SECURE CONNECTION").assertExists()
                onNodeWithText("Switch location").assertExists()
                onNodeWithText("Disconnect").assertExists()
                onNodeWithText("BLOCKING INTERNET").assertExists()
            }
        }

    @Test
    fun testDisconnectingBlockState() =
        composeExtension.use {
            // Arrange
            val mockRelayLocation: RelayItem = mockk(relaxed = true)
            val mockLocationName = "Home"
            every { mockRelayLocation.locationName } returns mockLocationName
            setContentWithTheme {
                ConnectScreen(
                    uiState =
                        ConnectUiState(
                            location = null,
                            relayLocation = mockRelayLocation,
                            tunnelUiState = TunnelState.Disconnecting(ActionAfterDisconnect.Block),
                            tunnelRealState =
                                TunnelState.Disconnecting(ActionAfterDisconnect.Block),
                            inAddress = null,
                            outAddress = "",
                            showLocation = true,
                            isTunnelInfoExpanded = false,
                            deviceName = "",
                            daysLeftUntilExpiry = null,
                            inAppNotification = InAppNotification.TunnelStateBlocked
                        ),
                    uiSideEffect = MutableSharedFlow<ConnectViewModel.UiSideEffect>().asSharedFlow()
                )
            }

            // Assert
            apply {
                onNodeWithText("SECURE CONNECTION").assertExists()
                onNodeWithText(mockLocationName).assertExists()
                onNodeWithText("Disconnect").assertExists()
                onNodeWithText("BLOCKING INTERNET").assertExists()
            }
        }

    @Test
    fun testClickSelectLocationButton() =
        composeExtension.use {
            // Arrange
            val mockRelayLocation: RelayItem = mockk(relaxed = true)
            val mockLocationName = "Home"
            every { mockRelayLocation.locationName } returns mockLocationName
            val mockedClickHandler: () -> Unit = mockk(relaxed = true)
            setContentWithTheme {
                ConnectScreen(
                    uiState =
                        ConnectUiState(
                            location = null,
                            relayLocation = mockRelayLocation,
                            tunnelUiState = TunnelState.Disconnected,
                            tunnelRealState = TunnelState.Disconnected,
                            inAddress = null,
                            outAddress = "",
                            showLocation = false,
                            isTunnelInfoExpanded = false,
                            deviceName = "",
                            daysLeftUntilExpiry = null,
                            inAppNotification = null
                        ),
                    uiSideEffect =
                        MutableSharedFlow<ConnectViewModel.UiSideEffect>().asSharedFlow(),
                    onSwitchLocationClick = mockedClickHandler
                )
            }

            // Act
            onNodeWithTag(SELECT_LOCATION_BUTTON_TEST_TAG).performClick()

            // Assert
            verify { mockedClickHandler.invoke() }
        }

    @Test
    fun testOnDisconnectClick() =
        composeExtension.use {
            // Arrange
            val mockTunnelEndpoint: TunnelEndpoint = mockk(relaxed = true)
            val mockedClickHandler: () -> Unit = mockk(relaxed = true)
            setContentWithTheme {
                ConnectScreen(
                    uiState =
                        ConnectUiState(
                            location = null,
                            relayLocation = null,
                            tunnelUiState = TunnelState.Connected(mockTunnelEndpoint, null),
                            tunnelRealState = TunnelState.Connected(mockTunnelEndpoint, null),
                            inAddress = null,
                            outAddress = "",
                            showLocation = false,
                            isTunnelInfoExpanded = false,
                            deviceName = "",
                            daysLeftUntilExpiry = null,
                            inAppNotification = null
                        ),
                    uiSideEffect =
                        MutableSharedFlow<ConnectViewModel.UiSideEffect>().asSharedFlow(),
                    onDisconnectClick = mockedClickHandler
                )
            }

            waitForIdle()
            // Act
            onNodeWithTag(CONNECT_BUTTON_TEST_TAG).performClick()

            // Assert
            verify { mockedClickHandler.invoke() }
        }

    @Test
    fun testOnReconnectClick() =
        composeExtension.use {
            // Arrange
            val mockTunnelEndpoint: TunnelEndpoint = mockk(relaxed = true)
            val mockedClickHandler: () -> Unit = mockk(relaxed = true)
            setContentWithTheme {
                ConnectScreen(
                    uiState =
                        ConnectUiState(
                            location = null,
                            relayLocation = null,
                            tunnelUiState = TunnelState.Connected(mockTunnelEndpoint, null),
                            tunnelRealState = TunnelState.Connected(mockTunnelEndpoint, null),
                            inAddress = null,
                            outAddress = "",
                            showLocation = false,
                            isTunnelInfoExpanded = false,
                            deviceName = "",
                            daysLeftUntilExpiry = null,
                            inAppNotification = null
                        ),
                    uiSideEffect =
                        MutableSharedFlow<ConnectViewModel.UiSideEffect>().asSharedFlow(),
                    onReconnectClick = mockedClickHandler
                )
            }

            // Act
            onNodeWithTag(RECONNECT_BUTTON_TEST_TAG).performClick()

            // Assert
            verify { mockedClickHandler.invoke() }
        }

    @Test
    fun testOnConnectClick() =
        composeExtension.use {
            // Arrange
            val mockedClickHandler: () -> Unit = mockk(relaxed = true)
            setContentWithTheme {
                ConnectScreen(
                    uiState =
                        ConnectUiState(
                            location = null,
                            relayLocation = null,
                            tunnelUiState = TunnelState.Disconnected,
                            tunnelRealState = TunnelState.Disconnected,
                            inAddress = null,
                            outAddress = "",
                            showLocation = false,
                            isTunnelInfoExpanded = false,
                            deviceName = "",
                            daysLeftUntilExpiry = null,
                            inAppNotification = null
                        ),
                    uiSideEffect =
                        MutableSharedFlow<ConnectViewModel.UiSideEffect>().asSharedFlow(),
                    onConnectClick = mockedClickHandler
                )
            }

            // Act
            onNodeWithTag(CONNECT_BUTTON_TEST_TAG).performClick()

            // Assert
            verify { mockedClickHandler.invoke() }
        }

    @Test
    fun testOnCancelClick() =
        composeExtension.use {
            // Arrange
            val mockedClickHandler: () -> Unit = mockk(relaxed = true)
            setContentWithTheme {
                ConnectScreen(
                    uiState =
                        ConnectUiState(
                            location = null,
                            relayLocation = null,
                            tunnelUiState = TunnelState.Connecting(null, null),
                            tunnelRealState = TunnelState.Connecting(null, null),
                            inAddress = null,
                            outAddress = "",
                            showLocation = false,
                            isTunnelInfoExpanded = false,
                            deviceName = "",
                            daysLeftUntilExpiry = null,
                            inAppNotification = null
                        ),
                    uiSideEffect =
                        MutableSharedFlow<ConnectViewModel.UiSideEffect>().asSharedFlow(),
                    onCancelClick = mockedClickHandler
                )
            }

            // Act
            onNodeWithTag(CONNECT_BUTTON_TEST_TAG).performClick()

            // Assert
            verify { mockedClickHandler.invoke() }
        }

    @Test
    fun testToggleTunnelInfo() =
        composeExtension.use {
            // Arrange
            val mockedClickHandler: () -> Unit = mockk(relaxed = true)
            val dummyLocation = GeoIpLocation(null, null, "dummy country", null, "dummy hostname")
            setContentWithTheme {
                ConnectScreen(
                    uiState =
                        ConnectUiState(
                            location = dummyLocation,
                            relayLocation = null,
                            tunnelUiState = TunnelState.Connecting(null, null),
                            tunnelRealState = TunnelState.Connecting(null, null),
                            inAddress = null,
                            outAddress = "",
                            showLocation = false,
                            isTunnelInfoExpanded = false,
                            deviceName = "",
                            daysLeftUntilExpiry = null,
                            inAppNotification = null
                        ),
                    uiSideEffect =
                        MutableSharedFlow<ConnectViewModel.UiSideEffect>().asSharedFlow(),
                    onToggleTunnelInfo = mockedClickHandler
                )
            }

            // Act
            onNodeWithTag(LOCATION_INFO_TEST_TAG).performClick()

            // Assert
            verify { mockedClickHandler.invoke() }
        }

    @Test
    fun showLocationInfo() =
        composeExtension.use {
            // Arrange
            val mockLocation: GeoIpLocation = mockk(relaxed = true)
            val mockTunnelEndpoint: TunnelEndpoint = mockk(relaxed = true)
            val mockHostName = "Host-Name"
            val mockPort = 99
            val mockHost = "Host"
            val mockProtocol = TransportProtocol.Udp
            val mockInAddress = Triple(mockHost, mockPort, mockProtocol)
            val mockOutAddress = "HostAddressV4 / HostAddressV4"
            every { mockLocation.hostname } returns mockHostName
            setContentWithTheme {
                ConnectScreen(
                    uiState =
                        ConnectUiState(
                            location = mockLocation,
                            relayLocation = null,
                            tunnelUiState = TunnelState.Connected(mockTunnelEndpoint, null),
                            tunnelRealState = TunnelState.Connected(mockTunnelEndpoint, null),
                            inAddress = mockInAddress,
                            outAddress = mockOutAddress,
                            showLocation = false,
                            isTunnelInfoExpanded = true,
                            deviceName = "",
                            daysLeftUntilExpiry = null,
                            inAppNotification = null
                        ),
                    uiSideEffect = MutableSharedFlow<ConnectViewModel.UiSideEffect>().asSharedFlow()
                )
            }

            // Assert
            apply {
                onNodeWithText(mockHostName).assertExists()
                onNodeWithText("WireGuard").assertExists()
                onNodeWithText("In $mockHost:$mockPort UDP").assertExists()
                onNodeWithText("Out $mockOutAddress").assertExists()
            }
        }

    @Test
    fun testOutdatedVersionNotification() =
        composeExtension.use {
            // Arrange
            val versionInfo =
                VersionInfo(
                    currentVersion = "1.0",
                    upgradeVersion = "1.1",
                    isOutdated = true,
                    isSupported = true
                )
            setContentWithTheme {
                ConnectScreen(
                    uiState =
                        ConnectUiState(
                            location = null,
                            relayLocation = null,
                            tunnelUiState = TunnelState.Connecting(null, null),
                            tunnelRealState = TunnelState.Connecting(null, null),
                            inAddress = null,
                            outAddress = "",
                            showLocation = false,
                            isTunnelInfoExpanded = false,
                            deviceName = "",
                            daysLeftUntilExpiry = null,
                            inAppNotification = InAppNotification.UpdateAvailable(versionInfo)
                        ),
                    uiSideEffect = MutableSharedFlow<ConnectViewModel.UiSideEffect>().asSharedFlow()
                )
            }

            // Assert
            apply {
                onNodeWithText("UPDATE AVAILABLE").assertExists()
                onNodeWithText("Install Mullvad VPN (1.1) to stay up to date").assertExists()
            }
        }

    @Test
    fun testUnsupportedVersionNotification() =
        composeExtension.use {
            // Arrange
            val versionInfo =
                VersionInfo(
                    currentVersion = "1.0",
                    upgradeVersion = "1.1",
                    isOutdated = true,
                    isSupported = false
                )
            setContentWithTheme {
                ConnectScreen(
                    uiState =
                        ConnectUiState(
                            location = null,
                            relayLocation = null,
                            tunnelUiState = TunnelState.Connecting(null, null),
                            tunnelRealState = TunnelState.Connecting(null, null),
                            inAddress = null,
                            outAddress = "",
                            showLocation = false,
                            isTunnelInfoExpanded = false,
                            deviceName = "",
                            daysLeftUntilExpiry = null,
                            inAppNotification = InAppNotification.UnsupportedVersion(versionInfo)
                        ),
                    uiSideEffect = MutableSharedFlow<ConnectViewModel.UiSideEffect>().asSharedFlow()
                )
            }

            // Assert
            apply {
                onNodeWithText("UNSUPPORTED VERSION").assertExists()
                onNodeWithText(
                        "Your privacy might be at risk with this unsupported app version. Please update now."
                    )
                    .assertExists()
            }
        }

    @Test
    fun testAccountExpiredNotification() =
        composeExtension.use {
            // Arrange
            val expiryDate = DateTime(2020, 11, 11, 10, 10)
            setContentWithTheme {
                ConnectScreen(
                    uiState =
                        ConnectUiState(
                            location = null,
                            relayLocation = null,
                            tunnelUiState = TunnelState.Connecting(null, null),
                            tunnelRealState = TunnelState.Connecting(null, null),
                            inAddress = null,
                            outAddress = "",
                            showLocation = false,
                            isTunnelInfoExpanded = false,
                            deviceName = "",
                            daysLeftUntilExpiry = null,
                            inAppNotification = InAppNotification.AccountExpiry(expiryDate)
                        ),
                    uiSideEffect = MutableSharedFlow<ConnectViewModel.UiSideEffect>().asSharedFlow()
                )
            }

            // Assert
            apply {
                onNodeWithText("ACCOUNT CREDIT EXPIRES SOON").assertExists()
                onNodeWithText("Out of time").assertExists()
            }
        }

    @Test
    fun testOnUpdateVersionClick() =
        composeExtension.use {
            // Arrange
            val mockedClickHandler: () -> Unit = mockk(relaxed = true)
            val versionInfo =
                VersionInfo(
                    currentVersion = "1.0",
                    upgradeVersion = "1.1",
                    isOutdated = true,
                    isSupported = false
                )
            setContentWithTheme {
                ConnectScreen(
                    onUpdateVersionClick = mockedClickHandler,
                    uiState =
                        ConnectUiState(
                            location = null,
                            relayLocation = null,
                            tunnelUiState = TunnelState.Connecting(null, null),
                            tunnelRealState = TunnelState.Connecting(null, null),
                            inAddress = null,
                            outAddress = "",
                            showLocation = false,
                            isTunnelInfoExpanded = false,
                            deviceName = "",
                            daysLeftUntilExpiry = null,
                            inAppNotification = InAppNotification.UnsupportedVersion(versionInfo)
                        ),
                    uiSideEffect = MutableSharedFlow<ConnectViewModel.UiSideEffect>().asSharedFlow()
                )
            }

            waitForIdle()
            // Act
            onNodeWithTag(NOTIFICATION_BANNER_ACTION).performClick()

            // Assert
            verify { mockedClickHandler.invoke() }
        }

    @Test
    fun testOnShowAccountClick() =
        composeExtension.use {
            // Arrange
            val mockedClickHandler: () -> Unit = mockk(relaxed = true)
            val expiryDate = DateTime(2020, 11, 11, 10, 10)
            setContentWithTheme {
                ConnectScreen(
                    onManageAccountClick = mockedClickHandler,
                    uiState =
                        ConnectUiState(
                            location = null,
                            relayLocation = null,
                            tunnelUiState = TunnelState.Connecting(null, null),
                            tunnelRealState = TunnelState.Connecting(null, null),
                            inAddress = null,
                            outAddress = "",
                            showLocation = false,
                            isTunnelInfoExpanded = false,
                            deviceName = "",
                            daysLeftUntilExpiry = null,
                            inAppNotification = InAppNotification.AccountExpiry(expiryDate)
                        ),
                    uiSideEffect = MutableSharedFlow<ConnectViewModel.UiSideEffect>().asSharedFlow()
                )
            }

            // Act
            onNodeWithTag(NOTIFICATION_BANNER_ACTION).performClick()

            // Assert
            verify { mockedClickHandler.invoke() }
        }

    @Test
    fun testOpenAccountView() =
        composeExtension.use {
            // Arrange
            setContentWithTheme {
                ConnectScreen(
                    uiState = ConnectUiState.INITIAL,
                    uiSideEffect =
                        MutableStateFlow(
                            ConnectViewModel.UiSideEffect.OpenAccountManagementPageInBrowser("222")
                        )
                )
            }

            // Assert
            apply { onNodeWithTag(SCROLLABLE_COLUMN_TEST_TAG).assertDoesNotExist() }
        }

    @Test
    fun testOpenOutOfTimeScreen() =
        composeExtension.use {
            // Arrange
            val mockedOpenScreenHandler: () -> Unit = mockk(relaxed = true)
            setContentWithTheme {
                ConnectScreen(
                    uiState = ConnectUiState.INITIAL,
                    uiSideEffect =
                        MutableStateFlow(ConnectViewModel.UiSideEffect.OpenOutOfTimeView),
                    onOpenOutOfTimeScreen = mockedOpenScreenHandler
                )
            }

            // Assert
            verify { mockedOpenScreenHandler.invoke() }
        }
}
