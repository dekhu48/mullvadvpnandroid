package net.mullvad.mullvadvpn.viewmodel

import androidx.lifecycle.viewModelScope
import app.cash.turbine.test
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import io.mockk.verify
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import net.mullvad.mullvadvpn.PaymentProvider
import net.mullvad.mullvadvpn.compose.state.OutOfTimeUiState
import net.mullvad.mullvadvpn.compose.state.PaymentState
import net.mullvad.mullvadvpn.lib.common.test.TestCoroutineRule
import net.mullvad.mullvadvpn.lib.common.test.assertLists
import net.mullvad.mullvadvpn.lib.payment.PaymentRepository
import net.mullvad.mullvadvpn.lib.payment.model.PaymentAvailability
import net.mullvad.mullvadvpn.lib.payment.model.PaymentProduct
import net.mullvad.mullvadvpn.lib.payment.model.PurchaseResult
import net.mullvad.mullvadvpn.lib.payment.model.VerificationResult
import net.mullvad.mullvadvpn.model.AccountExpiry
import net.mullvad.mullvadvpn.model.DeviceState
import net.mullvad.mullvadvpn.model.TunnelState
import net.mullvad.mullvadvpn.repository.AccountRepository
import net.mullvad.mullvadvpn.repository.DeviceRepository
import net.mullvad.mullvadvpn.ui.serviceconnection.AuthTokenCache
import net.mullvad.mullvadvpn.ui.serviceconnection.ConnectionProxy
import net.mullvad.mullvadvpn.ui.serviceconnection.ServiceConnectionContainer
import net.mullvad.mullvadvpn.ui.serviceconnection.ServiceConnectionManager
import net.mullvad.mullvadvpn.ui.serviceconnection.ServiceConnectionState
import net.mullvad.mullvadvpn.ui.serviceconnection.authTokenCache
import net.mullvad.mullvadvpn.ui.serviceconnection.connectionProxy
import net.mullvad.talpid.util.EventNotifier
import org.joda.time.DateTime
import org.joda.time.ReadableInstant
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class OutOfTimeViewModelTest {
    @get:Rule val testCoroutineRule = TestCoroutineRule()

    private val serviceConnectionState =
        MutableStateFlow<ServiceConnectionState>(ServiceConnectionState.Disconnected)
    private val accountExpiryState = MutableStateFlow<AccountExpiry>(AccountExpiry.Missing)
    private val deviceState = MutableStateFlow<DeviceState>(DeviceState.Initial)
    private val paymentAvailability =
        MutableSharedFlow<PaymentAvailability>(extraBufferCapacity = 1, replay = 1)
    private val purchaseResult =
        MutableSharedFlow<PurchaseResult>(extraBufferCapacity = 1, replay = 1)
    private val verifyPurchases =
        MutableSharedFlow<VerificationResult>(extraBufferCapacity = 1, replay = 1)

    // Service connections
    private val mockServiceConnectionContainer: ServiceConnectionContainer = mockk()
    private val mockConnectionProxy: ConnectionProxy = mockk()

    // Event notifiers
    private val eventNotifierTunnelRealState = EventNotifier<TunnelState>(TunnelState.Disconnected)

    private val mockAccountRepository: AccountRepository = mockk()
    private val mockDeviceRepository: DeviceRepository = mockk()
    private val mockServiceConnectionManager: ServiceConnectionManager = mockk()
    private val mockPaymentProvider: PaymentProvider = mockk(relaxed = true)
    private val mockPaymentRepository: PaymentRepository = mockk()

    private lateinit var viewModel: OutOfTimeViewModel

    @Before
    fun setUp() {
        mockkStatic(SERVICE_CONNECTION_MANAGER_EXTENSIONS)

        every { mockServiceConnectionManager.connectionState } returns serviceConnectionState

        every { mockServiceConnectionContainer.connectionProxy } returns mockConnectionProxy

        every { mockConnectionProxy.onStateChange } returns eventNotifierTunnelRealState

        every { mockAccountRepository.accountExpiryState } returns accountExpiryState

        every { mockDeviceRepository.deviceState } returns deviceState

        coEvery { mockPaymentRepository.verifyPurchases() } returns verifyPurchases

        coEvery { mockPaymentRepository.purchaseBillingProduct(any()) } returns purchaseResult

        coEvery { mockPaymentRepository.queryPaymentAvailability() } returns paymentAvailability

        every { mockPaymentProvider.paymentRepository } returns mockPaymentRepository

        viewModel =
            OutOfTimeViewModel(
                accountRepository = mockAccountRepository,
                serviceConnectionManager = mockServiceConnectionManager,
                deviceRepository = mockDeviceRepository,
                paymentProvider = mockPaymentProvider,
                pollAccountExpiry = false
            )
    }

    @After
    fun tearDown() {
        viewModel.viewModelScope.coroutineContext.cancel()
        unmockkAll()
    }

    @Test
    fun testSitePaymentClick() =
        runTest(testCoroutineRule.testDispatcher) {
            // Arrange
            val mockToken = "4444 5555 6666 7777"
            val mockAuthTokenCache: AuthTokenCache = mockk(relaxed = true)
            every { mockServiceConnectionManager.authTokenCache() } returns mockAuthTokenCache
            coEvery { mockAuthTokenCache.fetchAuthToken() } returns mockToken

            // Act, Assert
            viewModel.uiSideEffect.test {
                viewModel.onSitePaymentClick()
                val action = awaitItem()
                assertIs<OutOfTimeViewModel.UiSideEffect.OpenAccountView>(action)
                assertEquals(mockToken, action.token)
            }
        }

    @Test
    fun testUpdateTunnelState() =
        runTest(testCoroutineRule.testDispatcher) {
            // Arrange
            val tunnelRealStateTestItem = TunnelState.Connected(mockk(), mockk())

            // Act, Assert
            viewModel.uiState.test {
                assertEquals(OutOfTimeUiState(deviceName = ""), awaitItem())
                serviceConnectionState.value =
                    ServiceConnectionState.ConnectedReady(mockServiceConnectionContainer)
                eventNotifierTunnelRealState.notify(tunnelRealStateTestItem)
                val result = awaitItem()
                assertEquals(tunnelRealStateTestItem, result.tunnelState)
            }
        }

    @Test
    fun testOpenConnectScreen() =
        runTest(testCoroutineRule.testDispatcher) {
            // Arrange
            val mockExpiryDate: DateTime = mockk()
            every { mockExpiryDate.isAfter(any<ReadableInstant>()) } returns true

            // Act, Assert
            viewModel.uiSideEffect.test {
                accountExpiryState.value = AccountExpiry.Available(mockExpiryDate)
                val action = awaitItem()
                assertIs<OutOfTimeViewModel.UiSideEffect.OpenConnectScreen>(action)
            }
        }

    @Test
    fun testOnDisconnectClick() =
        runTest(testCoroutineRule.testDispatcher) {
            // Arrange
            val mockProxy: ConnectionProxy = mockk(relaxed = true)
            every { mockServiceConnectionManager.connectionProxy() } returns mockProxy

            // Act
            viewModel.onDisconnectClick()

            // Assert
            verify { mockProxy.disconnect() }
        }

    @Test
    fun testBillingProductsUnavailableState() = runTest {
        // Arrange
        val mockPaymentAvailability = PaymentAvailability.ProductsUnavailable

        // Act, Assert
        viewModel.uiState.test {
            // Default item
            awaitItem()
            paymentAvailability.tryEmit(mockPaymentAvailability)
            serviceConnectionState.value =
                ServiceConnectionState.ConnectedReady(mockServiceConnectionContainer)
            val result = awaitItem().billingPaymentState
            assertIs<PaymentState.NoPayment>(result)
        }
    }

    @Test
    fun testBillingProductsGenericErrorState() = runTest {
        // Arrange
        val mockPaymentAvailability = PaymentAvailability.Error.Other(mockk())

        // Act, Assert
        viewModel.uiState.test {
            // Default item
            assertIs<PaymentState.Loading>(awaitItem().billingPaymentState)
            paymentAvailability.tryEmit(mockPaymentAvailability)
            viewModel.fetchPaymentAvailability()
            serviceConnectionState.value =
                ServiceConnectionState.ConnectedReady(mockServiceConnectionContainer)
            val result = awaitItem().billingPaymentState
            assertIs<PaymentState.Error.GenericError>(result)
        }
    }

    @Test
    fun testBillingProductsBillingErrorState() = runTest {
        // Arrange
        val mockPaymentAvailability = PaymentAvailability.Error.BillingUnavailable

        // Act, Assert
        viewModel.uiState.test {
            // Default item
            assertIs<PaymentState.Loading>(awaitItem().billingPaymentState)
            paymentAvailability.tryEmit(mockPaymentAvailability)
            viewModel.fetchPaymentAvailability()
            serviceConnectionState.value =
                ServiceConnectionState.ConnectedReady(mockServiceConnectionContainer)
            val result = awaitItem().billingPaymentState
            assertIs<PaymentState.Error.BillingError>(result)
        }
    }

    @Test
    fun testBillingProductsPaymentAvailableState() = runTest {
        // Arrange
        val mockProduct: PaymentProduct = mockk()
        val expectedProductList = listOf(mockProduct)
        val mockPaymentAvailability = PaymentAvailability.ProductsAvailable(listOf(mockProduct))

        // Act, Assert
        viewModel.uiState.test {
            // Default item
            assertIs<PaymentState.Loading>(awaitItem().billingPaymentState)
            paymentAvailability.tryEmit(mockPaymentAvailability)
            serviceConnectionState.value =
                ServiceConnectionState.ConnectedReady(mockServiceConnectionContainer)
            val result = awaitItem().billingPaymentState
            assertIs<PaymentState.PaymentAvailable>(result)
            assertLists(expectedProductList, result.products)
        }
    }

    @Test
    fun testBillingVerificationError() = runTest {
        // Arrange
        val mockPurchaseResult = PurchaseResult.Error.VerificationError(null)

        // Act, Assert
        viewModel.uiState.test {
            // Default item
            awaitItem()
            purchaseResult.tryEmit(mockPurchaseResult)
            viewModel.startBillingPayment(productId = "mockId")
            serviceConnectionState.value =
                ServiceConnectionState.ConnectedReady(mockServiceConnectionContainer)
            val result = awaitItem().purchaseResult
            assertIs<PurchaseResult.Error.VerificationError>(result)
        }
    }

    @Test
    fun testBillingUserCancelled() = runTest {
        // Arrange
        val mockPurchaseResult = PurchaseResult.PurchaseCancelled

        // Act, Assert
        viewModel.uiState.test {
            // Default item
            awaitItem()
            purchaseResult.tryEmit(mockPurchaseResult)
            serviceConnectionState.value =
                ServiceConnectionState.ConnectedReady(mockServiceConnectionContainer)
            viewModel.startBillingPayment(productId = "mockId")
            assertIs<PurchaseResult.PurchaseCancelled>(awaitItem().purchaseResult)
            ensureAllEventsConsumed()
        }
    }

    @Test
    fun testBillingPurchaseCompleted() = runTest {
        // Arrange
        val mockPurchaseResult = PurchaseResult.PurchaseCompleted

        // Act, Assert
        viewModel.uiState.test {
            // Default item
            awaitItem()
            purchaseResult.tryEmit(mockPurchaseResult)
            serviceConnectionState.value =
                ServiceConnectionState.ConnectedReady(mockServiceConnectionContainer)
            viewModel.startBillingPayment(productId = "mockId")
            val result = awaitItem().purchaseResult
            assertIs<PurchaseResult.PurchaseCompleted>(result)
        }
    }

    @Test
    fun testStartBillingPayment() {
        // Arrange
        val mockProductId = "MOCK"
        coEvery { mockPaymentRepository.purchaseBillingProduct(mockProductId) } returns
            mockk(relaxed = true)

        // Act
        viewModel.startBillingPayment(mockProductId)

        // Assert
        coVerify { mockPaymentRepository.purchaseBillingProduct(mockProductId) }
    }

    companion object {
        private const val SERVICE_CONNECTION_MANAGER_EXTENSIONS =
            "net.mullvad.mullvadvpn.ui.serviceconnection.ServiceConnectionManagerExtensionsKt"
    }
}
