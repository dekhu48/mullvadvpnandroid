package net.mullvad.mullvadvpn.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@JvmInline @Parcelize value class WireguardConstraints(val port: Constraint<Port>) : Parcelable
