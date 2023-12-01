package net.mullvad.mullvadvpn.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.net.InetAddress

@Parcelize
data class GeoIpLocation(
    val ipv4: InetAddress?,
    val ipv6: InetAddress?,
    val country: String,
    val city: String?,
    val hostname: String?,
) : Parcelable
