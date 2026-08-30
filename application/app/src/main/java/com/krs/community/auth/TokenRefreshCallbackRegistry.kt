package com.krs.community.auth

import java.util.concurrent.CopyOnWriteArrayList

/**
 * Registry that manages [TokenRefreshCallback] observers.
 *
 * Uses [CopyOnWriteArrayList] so that notifications are safe across
 * threads even when observers register or unregister during iteration.
 *
 * This decouples the token-refresh infrastructure from any specific
 * Activity or Fragment — observers simply register/deregister
 * themselves and are notified when the session expires.
 */
object TokenRefreshCallbackRegistry {

    private val callbacks = CopyOnWriteArrayList<TokenRefreshCallback>()

    fun register(callback: TokenRefreshCallback) {
        if (!callbacks.contains(callback)) {
            callbacks.add(callback)
        }
    }

    fun unregister(callback: TokenRefreshCallback) {
        callbacks.remove(callback)
    }

    fun notifySessionExpired() {
        callbacks.forEach { it.onSessionExpired() }
    }
}
