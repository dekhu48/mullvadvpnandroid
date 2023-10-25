package net.mullvad.mullvadvpn.service.endpoint

import kotlin.properties.Delegates.observable
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.launch
import net.mullvad.mullvadvpn.lib.ipc.Event
import net.mullvad.mullvadvpn.lib.ipc.Request
import net.mullvad.mullvadvpn.model.Constraint
import net.mullvad.mullvadvpn.model.GeographicLocationConstraint
import net.mullvad.mullvadvpn.model.LocationConstraint
import net.mullvad.mullvadvpn.model.RelayConstraintsUpdate
import net.mullvad.mullvadvpn.model.RelayList
import net.mullvad.mullvadvpn.model.RelaySettingsUpdate
import net.mullvad.mullvadvpn.model.WireguardConstraints
import net.mullvad.mullvadvpn.service.MullvadDaemon

class RelayListListener(
    endpoint: ServiceEndpoint,
    val dispatcher: CoroutineDispatcher = Dispatchers.IO
) {
    private val scope = CoroutineScope(SupervisorJob() + dispatcher)
    private val daemon = endpoint.intermittentDaemon

    var relayList by
        observable<RelayList?>(null) { _, _, relays ->
            endpoint.sendEvent(Event.NewRelayList(relays))
        }
        private set

    init {
        daemon.registerListener(this) { newDaemon ->
            newDaemon?.let { daemon ->
                setUpListener(daemon)
                fetchInitialRelayList(daemon)
            }
        }

        scope.launch {
            endpoint.dispatcher.parsedMessages
                .filterIsInstance<Request.SetRelayLocation>()
                .collect { request ->
                    val update = RelaySettingsUpdate.Normal(updateLocation(request.relayLocation))
                    daemon.await().updateRelaySettings(update)
                }
        }

        scope.launch {
            endpoint.dispatcher.parsedMessages
                .filterIsInstance<Request.SetWireguardConstraints>()
                .collect { request ->
                    val update =
                        RelaySettingsUpdate.Normal(
                            updateWireguardConstraint(request.wireguardConstraints)
                        )
                    daemon.await().updateRelaySettings(update)
                }
        }
    }

    fun onDestroy() {
        daemon.unregisterListener(this)
        scope.cancel()
    }

    private fun setUpListener(daemon: MullvadDaemon) {
        daemon.onRelayListChange = { relayLocations -> relayList = relayLocations }
    }

    private fun fetchInitialRelayList(daemon: MullvadDaemon) {
        synchronized(this) {
            if (relayList == null) {
                relayList = daemon.getRelayLocations()
            }
        }
    }

    private fun updateLocation(
        location: Constraint<GeographicLocationConstraint>
    ): RelayConstraintsUpdate =
        RelayConstraintsUpdate(
            location =
                when (location) {
                    is Constraint.Only ->
                        Constraint.Only(LocationConstraint.Location(location.value))
                    else -> Constraint.Any()
                },
            wireguardConstraints = null,
            ownership = null
        )

    private fun updateWireguardConstraint(
        wireguardConstraints: WireguardConstraints
    ): RelayConstraintsUpdate =
        RelayConstraintsUpdate(
            location = null,
            wireguardConstraints = wireguardConstraints,
            ownership = null
        )
}
