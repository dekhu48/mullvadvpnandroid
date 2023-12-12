package net.mullvad.mullvadvpn.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.net.InetAddress

@Parcelize
data class CustomDnsOptions(val addresses: ArrayList<InetAddress>) : Parcelable
