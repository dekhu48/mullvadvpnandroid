package net.mullvad.mullvadvpn.lib.endpoint

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.net.InetSocketAddress

@Parcelize
data class ApiEndpoint(
    val address: InetSocketAddress,
    val disableAddressCache: Boolean,
    val disableTls: Boolean,
    val forceDirectConnection: Boolean,
) : Parcelable
