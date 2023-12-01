package net.mullvad.mullvadvpn.ui.serviceconnection

import android.os.Messenger
import kotlinx.coroutines.CompletableDeferred
import net.mullvad.mullvadvpn.lib.ipc.Event
import net.mullvad.mullvadvpn.lib.ipc.EventDispatcher
import net.mullvad.mullvadvpn.lib.ipc.Request
import java.util.LinkedList

class AuthTokenCache(private val connection: Messenger, eventDispatcher: EventDispatcher) {
    private val fetchQueue = LinkedList<CompletableDeferred<String>>()

    init {
        eventDispatcher.registerHandler(Event.AuthToken::class) { event ->
            synchronized(this@AuthTokenCache) { fetchQueue.poll()?.complete(event.token ?: "") }
        }
    }

    suspend fun fetchAuthToken(): String {
        val authToken = CompletableDeferred<String>()

        synchronized(this) { fetchQueue.offer(authToken) }

        connection.send(Request.FetchAuthToken.message)

        return authToken.await()
    }

    fun onDestroy() {
        synchronized(this) {
            for (pendingFetch in fetchQueue) {
                pendingFetch.cancel()
            }

            fetchQueue.clear()
        }
    }
}
