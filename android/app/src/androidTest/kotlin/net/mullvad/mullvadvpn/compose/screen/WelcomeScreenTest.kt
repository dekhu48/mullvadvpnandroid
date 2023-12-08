package net.mullvad.mullvadvpn.compose.screen

import android.app.Activity
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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import net.mullvad.mullvadvpn.compose.setContentWithTheme
import net.mullvad.mullvadvpn.compose.state.PaymentState
import net.mullvad.mullvadvpn.compose.state.WelcomeUiState
import net.mullvad.mullvadvpn.compose.test.PLAY_PAYMENT_INFO_ICON_TEST_TAG
import net.mullvad.mullvadvpn.lib.payment.model.PaymentProduct
import net.mullvad.mullvadvpn.lib.payment.model.PaymentStatus
import net.mullvad.mullvadvpn.lib.payment.model.ProductId
import net.mullvad.mullvadvpn.lib.payment.model.ProductPrice
import net.mullvad.mullvadvpn.lib.payment.model.PurchaseResult
import net.mullvad.mullvadvpn.util.toPaymentDialogData
import net.mullvad.mullvadvpn.viewmodel.WelcomeViewModel
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

@OptIn(ExperimentalTestApi::class)
class WelcomeScreenTest {
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
                WelcomeScreen(
                    showSitePayment = true,
                    uiState = WelcomeUiState(),
                    uiSideEffect = MutableSharedFlow(),
                    onSitePaymentClick = {},
                    onRedeemVoucherClick = {},
                    onSettingsClick = {},
                    onAccountClick = {},
                    openConnectScreen = {},
                    onPurchaseBillingProductClick = { _, _ -> },
                    onClosePurchaseResultDialog = {}
                )
            }

            // Assert
            apply {
                onNodeWithText("Congrats!").assertExists()
                onNodeWithText("Here’s your account number. Save it!").assertExists()
            }
        }

    @Test
    fun testDisableSitePayment() =
        composeExtension.use {
            // Arrange
            setContentWithTheme {
                WelcomeScreen(
                    showSitePayment = false,
                    uiState = WelcomeUiState(),
                    uiSideEffect = MutableSharedFlow(),
                    onSitePaymentClick = {},
                    onRedeemVoucherClick = {},
                    onSettingsClick = {},
                    onAccountClick = {},
                    openConnectScreen = {},
                    onPurchaseBillingProductClick = { _, _ -> },
                    onClosePurchaseResultDialog = {}
                )
            }

            // Assert
            apply {
                onNodeWithText(
                        "Either buy credit on our website or redeem a voucher.",
                        substring = true
                    )
                    .assertDoesNotExist()
                onNodeWithText("Buy credit").assertDoesNotExist()
            }
        }

    @Test
    fun testShowAccountNumber() =
        composeExtension.use {
            // Arrange
            val rawAccountNumber = "1111222233334444"
            val expectedAccountNumber = "1111 2222 3333 4444"
            setContentWithTheme {
                WelcomeScreen(
                    showSitePayment = true,
                    uiState = WelcomeUiState(accountNumber = rawAccountNumber),
                    uiSideEffect = MutableSharedFlow(),
                    onSitePaymentClick = {},
                    onRedeemVoucherClick = {},
                    onSettingsClick = {},
                    onAccountClick = {},
                    openConnectScreen = {},
                    onPurchaseBillingProductClick = { _, _ -> },
                    onClosePurchaseResultDialog = {}
                )
            }

            // Assert
            apply { onNodeWithText(expectedAccountNumber).assertExists() }
        }

    @Test
    fun testOpenAccountView() =
        composeExtension.use {
            // Arrange
            setContentWithTheme {
                WelcomeScreen(
                    showSitePayment = true,
                    uiState = WelcomeUiState(),
                    uiSideEffect =
                        MutableStateFlow(WelcomeViewModel.UiSideEffect.OpenAccountView("222")),
                    onSitePaymentClick = {},
                    onRedeemVoucherClick = {},
                    onSettingsClick = {},
                    onAccountClick = {},
                    openConnectScreen = {},
                    onPurchaseBillingProductClick = { _, _ -> },
                    onClosePurchaseResultDialog = {}
                )
            }

            // Assert
            apply { onNodeWithText("Congrats!").assertDoesNotExist() }
        }

    @Test
    fun testOpenConnectScreen() =
        composeExtension.use {
            // Arrange
            val mockClickListener: () -> Unit = mockk(relaxed = true)
            setContentWithTheme {
                WelcomeScreen(
                    showSitePayment = true,
                    uiState = WelcomeUiState(),
                    uiSideEffect =
                        MutableStateFlow(WelcomeViewModel.UiSideEffect.OpenConnectScreen),
                    onSitePaymentClick = {},
                    onRedeemVoucherClick = {},
                    onSettingsClick = {},
                    onAccountClick = {},
                    openConnectScreen = mockClickListener,
                    onPurchaseBillingProductClick = { _, _ -> },
                    onClosePurchaseResultDialog = {}
                )
            }

            // Assert
            verify(exactly = 1) { mockClickListener.invoke() }
        }

    @Test
    fun testClickSitePaymentButton() =
        composeExtension.use {
            // Arrange
            val mockClickListener: () -> Unit = mockk(relaxed = true)
            setContentWithTheme {
                WelcomeScreen(
                    showSitePayment = true,
                    uiState = WelcomeUiState(),
                    uiSideEffect =
                        MutableStateFlow(WelcomeViewModel.UiSideEffect.OpenConnectScreen),
                    onSitePaymentClick = mockClickListener,
                    onRedeemVoucherClick = {},
                    onSettingsClick = {},
                    onAccountClick = {},
                    openConnectScreen = {},
                    onPurchaseBillingProductClick = { _, _ -> },
                    onClosePurchaseResultDialog = {}
                )
            }

            // Act
            apply { onNodeWithText("Buy credit").performClick() }

            // Assert
            verify(exactly = 1) { mockClickListener.invoke() }
        }

    @Test
    fun testClickRedeemVoucher() =
        composeExtension.use {
            // Arrange
            val mockClickListener: () -> Unit = mockk(relaxed = true)
            setContentWithTheme {
                WelcomeScreen(
                    showSitePayment = true,
                    uiState = WelcomeUiState(),
                    uiSideEffect =
                        MutableStateFlow(WelcomeViewModel.UiSideEffect.OpenConnectScreen),
                    onSitePaymentClick = {},
                    onRedeemVoucherClick = mockClickListener,
                    onSettingsClick = {},
                    onAccountClick = {},
                    openConnectScreen = {},
                    onPurchaseBillingProductClick = { _, _ -> },
                    onClosePurchaseResultDialog = {}
                )
            }

            // Act
            apply { onNodeWithText("Redeem voucher").performClick() }

            // Assert
            verify(exactly = 1) { mockClickListener.invoke() }
        }

    @Test
    fun testShowPurchaseCompleteDialog() =
        composeExtension.use {
            // Arrange
            setContentWithTheme {
                WelcomeScreen(
                    showSitePayment = true,
                    uiState =
                        WelcomeUiState(
                            paymentDialogData =
                                PurchaseResult.Completed.Success.toPaymentDialogData()
                        ),
                    uiSideEffect =
                        MutableStateFlow(WelcomeViewModel.UiSideEffect.OpenConnectScreen),
                    onSitePaymentClick = {},
                    onRedeemVoucherClick = {},
                    onSettingsClick = {},
                    onAccountClick = {},
                    openConnectScreen = {},
                    onPurchaseBillingProductClick = { _, _ -> },
                    onClosePurchaseResultDialog = {}
                )
            }

            // Assert
            onNodeWithText("Time was successfully added").assertExists()
        }

    @Test
    fun testShowVerificationErrorDialog() =
        composeExtension.use {
            // Arrange
            setContentWithTheme {
                WelcomeScreen(
                    showSitePayment = true,
                    uiState =
                        WelcomeUiState(
                            paymentDialogData =
                                PurchaseResult.Error.VerificationError(null).toPaymentDialogData()
                        ),
                    uiSideEffect =
                        MutableStateFlow(WelcomeViewModel.UiSideEffect.OpenConnectScreen),
                    onSitePaymentClick = {},
                    onRedeemVoucherClick = {},
                    onSettingsClick = {},
                    onAccountClick = {},
                    openConnectScreen = {},
                    onPurchaseBillingProductClick = { _, _ -> },
                    onClosePurchaseResultDialog = {}
                )
            }

            // Assert
            onNodeWithText("Verifying purchase").assertExists()
        }

    @Test
    fun testShowFetchProductsErrorDialog() =
        composeExtension.use {
            // Arrange
            setContentWithTheme {
                WelcomeScreen(
                    showSitePayment = true,
                    uiState =
                        WelcomeUiState()
                            .copy(
                                paymentDialogData =
                                    PurchaseResult.Error.FetchProductsError(ProductId(""), null)
                                        .toPaymentDialogData()
                            ),
                    uiSideEffect =
                        MutableSharedFlow<WelcomeViewModel.UiSideEffect>().asSharedFlow(),
                    onSitePaymentClick = {},
                    onRedeemVoucherClick = {},
                    onSettingsClick = {},
                    onAccountClick = {},
                    openConnectScreen = {},
                    onPurchaseBillingProductClick = { _, _ -> },
                    onClosePurchaseResultDialog = {}
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
                WelcomeScreen(
                    showSitePayment = true,
                    uiState =
                        WelcomeUiState().copy(billingPaymentState = PaymentState.Error.Billing),
                    uiSideEffect =
                        MutableSharedFlow<WelcomeViewModel.UiSideEffect>().asSharedFlow(),
                    onSitePaymentClick = {},
                    onRedeemVoucherClick = {},
                    onSettingsClick = {},
                    onAccountClick = {},
                    openConnectScreen = {},
                    onClosePurchaseResultDialog = {},
                    onPurchaseBillingProductClick = { _, _ -> }
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
                WelcomeScreen(
                    showSitePayment = true,
                    uiState =
                        WelcomeUiState(
                            billingPaymentState =
                                PaymentState.PaymentAvailable(listOf(mockPaymentProduct))
                        ),
                    uiSideEffect =
                        MutableStateFlow(WelcomeViewModel.UiSideEffect.OpenConnectScreen),
                    onSitePaymentClick = {},
                    onRedeemVoucherClick = {},
                    onSettingsClick = {},
                    onAccountClick = {},
                    openConnectScreen = {},
                    onPurchaseBillingProductClick = { _, _ -> },
                    onClosePurchaseResultDialog = {}
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
                WelcomeScreen(
                    showSitePayment = true,
                    uiState =
                        WelcomeUiState()
                            .copy(
                                billingPaymentState =
                                    PaymentState.PaymentAvailable(listOf(mockPaymentProduct))
                            ),
                    uiSideEffect =
                        MutableSharedFlow<WelcomeViewModel.UiSideEffect>().asSharedFlow(),
                    onSitePaymentClick = {},
                    onRedeemVoucherClick = {},
                    onSettingsClick = {},
                    onAccountClick = {},
                    openConnectScreen = {},
                    onPurchaseBillingProductClick = { _, _ -> },
                    onClosePurchaseResultDialog = {}
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
                WelcomeScreen(
                    showSitePayment = true,
                    uiState =
                        WelcomeUiState()
                            .copy(
                                billingPaymentState =
                                    PaymentState.PaymentAvailable(listOf(mockPaymentProduct))
                            ),
                    uiSideEffect =
                        MutableSharedFlow<WelcomeViewModel.UiSideEffect>().asSharedFlow(),
                    onSitePaymentClick = {},
                    onRedeemVoucherClick = {},
                    onSettingsClick = {},
                    onAccountClick = {},
                    openConnectScreen = {},
                    onPurchaseBillingProductClick = { _, _ -> },
                    onClosePurchaseResultDialog = {}
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
                WelcomeScreen(
                    showSitePayment = true,
                    uiState =
                        WelcomeUiState()
                            .copy(
                                billingPaymentState =
                                    PaymentState.PaymentAvailable(listOf(mockPaymentProduct))
                            ),
                    uiSideEffect =
                        MutableSharedFlow<WelcomeViewModel.UiSideEffect>().asSharedFlow(),
                    onSitePaymentClick = {},
                    onRedeemVoucherClick = {},
                    onSettingsClick = {},
                    onAccountClick = {},
                    openConnectScreen = {},
                    onPurchaseBillingProductClick = { _, _ -> },
                    onClosePurchaseResultDialog = {}
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
                WelcomeScreen(
                    showSitePayment = true,
                    uiState =
                        WelcomeUiState(
                            billingPaymentState =
                                PaymentState.PaymentAvailable(listOf(mockPaymentProduct))
                        ),
                    uiSideEffect =
                        MutableStateFlow(WelcomeViewModel.UiSideEffect.OpenConnectScreen),
                    onSitePaymentClick = {},
                    onRedeemVoucherClick = {},
                    onSettingsClick = {},
                    onAccountClick = {},
                    openConnectScreen = {},
                    onPurchaseBillingProductClick = clickHandler,
                    onClosePurchaseResultDialog = {}
                )
            }

            // Act
            onNodeWithText("Add 30 days time ($10)").performClick()

            // Assert
            verify { clickHandler(ProductId("PRODUCT_ID"), any()) }
        }
}
