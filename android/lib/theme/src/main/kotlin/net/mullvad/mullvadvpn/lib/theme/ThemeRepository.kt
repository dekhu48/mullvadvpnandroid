package net.mullvad.mullvadvpn.lib.theme

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class ThemeRepository(
    private val dataStore: DataStore<Preferences>,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) {
    fun useMaterialYouTheme(): StateFlow<Boolean> =
        dataStore.data
            .map { it[booleanPreferencesKey(USE_MATERIAL_YOU_THEME)] ?: false }
            .stateIn(CoroutineScope(dispatcher), SharingStarted.Eagerly, false)

    suspend fun setUseMaterialYouTheme(useMaterialYouTheme: Boolean) {
        dataStore.edit { it[booleanPreferencesKey(USE_MATERIAL_YOU_THEME)] = useMaterialYouTheme }
    }

    companion object {
        private const val USE_MATERIAL_YOU_THEME = "use_material_you_theme"
    }
}
