package net.mullvad.mullvadvpn.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import net.mullvad.mullvadvpn.compose.state.SettingsUiState
import net.mullvad.mullvadvpn.lib.theme.ThemeRepository
import net.mullvad.mullvadvpn.model.DeviceState
import net.mullvad.mullvadvpn.repository.DeviceRepository
import net.mullvad.mullvadvpn.ui.serviceconnection.ServiceConnectionManager

class SettingsViewModel(
    deviceRepository: DeviceRepository,
    serviceConnectionManager: ServiceConnectionManager,
    private val themeRepository: ThemeRepository
) : ViewModel() {
    private val _enterTransitionEndAction = MutableSharedFlow<Unit>()

    private val vmState: StateFlow<SettingsUiState> =
        combine(
                deviceRepository.deviceState,
                serviceConnectionManager.connectionState,
                themeRepository.useMaterialYouTheme()
            ) { deviceState, versionInfo, useMaterialYouTheme ->
                val cachedVersionInfo = versionInfo.readyContainer()?.appVersionInfoCache
                SettingsUiState(
                    isLoggedIn = deviceState is DeviceState.LoggedIn,
                    appVersion = cachedVersionInfo?.version ?: "",
                    isUpdateAvailable =
                        cachedVersionInfo?.let { it.isSupported.not() || it.isOutdated } ?: false,
                    isMaterialYouTheme = useMaterialYouTheme
                )
            }
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(),
                SettingsUiState(
                    appVersion = "",
                    isLoggedIn = false,
                    isUpdateAvailable = false,
                    isMaterialYouTheme = false
                )
            )

    val uiState =
        vmState.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(),
            SettingsUiState(
                appVersion = "",
                isLoggedIn = false,
                isUpdateAvailable = false,
                isMaterialYouTheme = false
            )
        )

    @Suppress("konsist.ensure public properties use permitted names")
    val enterTransitionEndAction = _enterTransitionEndAction.asSharedFlow()

    fun onTransitionAnimationEnd() {
        viewModelScope.launch { _enterTransitionEndAction.emit(Unit) }
    }

    fun setUseMaterialYouTheme(useMaterialYouTheme: Boolean) {
        viewModelScope.launch { themeRepository.setUseMaterialYouTheme(useMaterialYouTheme) }
    }
}
