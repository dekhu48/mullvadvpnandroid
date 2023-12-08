package net.mullvad.mullvadvpn.compose.screen

import android.app.Activity
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import de.mannodermaus.junit5.compose.createComposeExtension
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import net.mullvad.mullvadvpn.compose.setContentWithTheme
import net.mullvad.mullvadvpn.compose.state.PaymentState
import net.mullvad.mullvadvpn.compose.test.PLAY_PAYMENT_INFO_ICON_TEST_TAG
import net.mullvad.mullvadvpn.lib.payment.model.PaymentProduct
import net.mullvad.mullvadvpn.lib.payment.model.PaymentStatus
import net.mullvad.mullvadvpn.lib.payment.model.ProductId
import net.mullvad.mullvadvpn.lib.payment.model.ProductPrice
import net.mullvad.mullvadvpn.lib.payment.model.PurchaseResult
import net.mullvad.mullvadvpn.util.toPaymentDialogData
import net.mullvad.mullvadvpn.viewmodel.AccountUiState
import net.mullvad.mullvadvpn.viewmodel.AccountViewModel
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

@ExperimentalTestApi
@OptIn(ExperimentalMaterial3Api::class)
class AccountScreenTest {
    @JvmField @RegisterExtension val composeExtension = createComposeExtension()

    @BeforeEach
    fun setup() {
        MockKAnnotations.init(this)
    }

    @Test
    fun testDefaultState() =
        composeExtension.use {
            // Arrange
            setContentWithTheme {
                AccountScreen(
                    showSitePayment = true,
                    uiState =
                        AccountUiState(
                            deviceName = DUMMY_DEVICE_NAME,
                            accountNumber = DUMMY_ACCOUNT_NUMBER,
                            accountExpiry = null
                        ),
                    uiSideEffect =
                        MutableSharedFlow<AccountViewModel.UiSideEffect>().asSharedFlow(),
                    enterTransitionEndAction = MutableSharedFlow<Unit>().asSharedFlow()
                )
            }

            // Assert
            onNodeWithText("Redeem voucher").assertExists()
            onNodeWithText("Log out").assertExists()
        }

    @Test
    fun testManageAccountClick() =
        composeExtension.use {
            // Arrange
            val mockedClickHandler: () -> Unit = mockk(relaxed = true)
            setContentWithTheme {
                AccountScreen(
                    showSitePayment = true,
                    uiState =
                        AccountUiState(
                            deviceName = DUMMY_DEVICE_NAME,
                            accountNumber = DUMMY_ACCOUNT_NUMBER,
                            accountExpiry = null
                        ),
                    uiSideEffect =
                        MutableSharedFlow<AccountViewModel.UiSideEffect>().asSharedFlow(),
                    enterTransitionEndAction = MutableSharedFlow<Unit>().asSharedFlow(),
                    onManageAccountClick = mockedClickHandler
                )
            }

            // Act
            onNodeWithText("Manage account").performClick()

            // Assert
            verify(exactly = 1) { mockedClickHandler.invoke() }
        }

    @Test
    fun testShowFetchProductsErrorDialog() =
        // Arrange
        composeExtension.use {
            setContentWithTheme {
                AccountScreen(
                    showSitePayment = true,
                    uiState =
                        AccountUiState.default()
                            .copy(
                                paymentDialogData =
                                    PurchaseResult.Error.FetchProductsError(ProductId(""), null)
                                        .toPaymentDialogData()
                            ),
                    uiSideEffect =
                        MutableSharedFlow<AccountViewModel.UiSideEffect>().asSharedFlow(),
                    enterTransitionEndAction = MutableSharedFlow<Unit>().asSharedFlow()
                )
            }

            // Assert
            onNodeWithText("Google Play unavailable").assertExists()
        }

    @Test
    fun testShowBillingErrorPaymentButton() =
        composeExtension.use {
            // Arrange
            setContentWithTheme {
                AccountScreen(
                    showSitePayment = true,
                    uiState =
                        AccountUiState.default()
                            .copy(billingPaymentState = PaymentState.Error.Billing),
                    uiSideEffect =
                        MutableSharedFlow<AccountViewModel.UiSideEffect>().asSharedFlow(),
                    enterTransitionEndAction = MutableSharedFlow<Unit>().asSharedFlow()
                )
            }

            // Assert
            onNodeWithText("Add 30 days time").assertExists()
        }

    @Test
    fun testShowBillingPaymentAvailable() =
        composeExtension.use {
            // Arrange
            val mockPaymentProduct: PaymentProduct = mockk()
            every { mockPaymentProduct.price } returns ProductPrice("$10")
            every { mockPaymentProduct.status } returns null
            setContentWithTheme {
                AccountScreen(
                    showSitePayment = true,
                    uiState =
                        AccountUiState.default()
                            .copy(
                                billingPaymentState =
                                    PaymentState.PaymentAvailable(listOf(mockPaymentProduct))
                            ),
                    uiSideEffect =
                        MutableSharedFlow<AccountViewModel.UiSideEffect>().asSharedFlow(),
                    enterTransitionEndAction = MutableSharedFlow<Unit>().asSharedFlow()
                )
            }

            // Assert
            onNodeWithText("Add 30 days time ($10)").assertExists()
        }

    @Test
    fun testShowPendingPayment() =
        composeExtension.use {
            // Arrange
            val mockPaymentProduct: PaymentProduct = mockk()
            every { mockPaymentProduct.price } returns ProductPrice("$10")
            every { mockPaymentProduct.status } returns PaymentStatus.PENDING
            setContentWithTheme {
                AccountScreen(
                    showSitePayment = true,
                    uiState =
                        AccountUiState.default()
                            .copy(
                                billingPaymentState =
                                    PaymentState.PaymentAvailable(listOf(mockPaymentProduct))
                            ),
                    uiSideEffect =
                        MutableSharedFlow<AccountViewModel.UiSideEffect>().asSharedFlow(),
                    enterTransitionEndAction = MutableSharedFlow<Unit>().asSharedFlow()
                )
            }

            // Assert
            onNodeWithText("Google Play payment pending").assertExists()
        }

    @Test
    fun testShowPendingPaymentInfoDialog() =
        composeExtension.use {
            // Arrange
            val mockPaymentProduct: PaymentProduct = mockk()
            every { mockPaymentProduct.price } returns ProductPrice("$10")
            every { mockPaymentProduct.status } returns PaymentStatus.PENDING
            setContentWithTheme {
                AccountScreen(
                    showSitePayment = true,
                    uiState =
                        AccountUiState.default()
                            .copy(
                                billingPaymentState =
                                    PaymentState.PaymentAvailable(listOf(mockPaymentProduct))
                            ),
                    uiSideEffect =
                        MutableSharedFlow<AccountViewModel.UiSideEffect>().asSharedFlow(),
                    enterTransitionEndAction = MutableSharedFlow<Unit>().asSharedFlow()
                )
            }

            // Act
            onNodeWithTag(PLAY_PAYMENT_INFO_ICON_TEST_TAG).performClick()

            // Assert

            onNodeWithText(
                    "We are currently verifying your purchase, this might take some time. Your time will be added if the verification is successful."
                )
                .assertExists()
        }

    @Test
    fun testShowVerificationInProgress() =
        composeExtension.use {
            // Arrange
            val mockPaymentProduct: PaymentProduct = mockk()
            every { mockPaymentProduct.price } returns ProductPrice("$10")
            every { mockPaymentProduct.status } returns PaymentStatus.VERIFICATION_IN_PROGRESS
            setContentWithTheme {
                AccountScreen(
                    showSitePayment = true,
                    uiState =
                        AccountUiState.default()
                            .copy(
                                billingPaymentState =
                                    PaymentState.PaymentAvailable(listOf(mockPaymentProduct))
                            ),
                    uiSideEffect =
                        MutableSharedFlow<AccountViewModel.UiSideEffect>().asSharedFlow(),
                    enterTransitionEndAction = MutableSharedFlow<Unit>().asSharedFlow()
                )
            }

            // Assert
            onNodeWithText("Verifying purchase").assertExists()
        }

    @Test
    fun testOnPurchaseBillingProductClick() =
        composeExtension.use {
            // Arrange
            val clickHandler: (ProductId, () -> Activity) -> Unit = mockk(relaxed = true)
            val mockPaymentProduct: PaymentProduct = mockk()
            every { mockPaymentProduct.price } returns ProductPrice("$10")
            every { mockPaymentProduct.productId } returns ProductId("PRODUCT_ID")
            every { mockPaymentProduct.status } returns null
            setContentWithTheme {
                AccountScreen(
                    showSitePayment = true,
                    uiState =
                        AccountUiState.default()
                            .copy(
                                billingPaymentState =
                                    PaymentState.PaymentAvailable(listOf(mockPaymentProduct))
                            ),
                    onPurchaseBillingProductClick = clickHandler,
                    uiSideEffect =
                        MutableSharedFlow<AccountViewModel.UiSideEffect>().asSharedFlow(),
                    enterTransitionEndAction = MutableSharedFlow<Unit>().asSharedFlow()
                )
            }

            // Act
            onNodeWithText("Add 30 days time ($10)").performClick()

            // Assert
            verify { clickHandler.invoke(ProductId("PRODUCT_ID"), any()) }
        }

    companion object {
        private const val DUMMY_DEVICE_NAME = "fake_name"
        private const val DUMMY_ACCOUNT_NUMBER = "fake_number"
    }
}
