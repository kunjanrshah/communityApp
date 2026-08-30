package com.krs.community.auth

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route

/**
 * OkHttp [Authenticator] that intercepts 401 Unauthorized responses
 * from the GraphQL API and attempts an automated token refresh.
 *
 * How it works:
 *
 * 1. When the server returns 401, OkHttp calls [authenticate].
 * 2. The authenticator delegates to the chain of [AuthExceptionHandler]
 *    strategies. The first strategy that [canHandle]s the response
 *    is given a chance to [handle] it.
 * 3. If a strategy returns a new [Request] (with a refreshed token),
 *    OkHttp automatically retries the original call.
 * 4. If no strategy can resolve the 401 (or the refresh token is also
 *    expired), the strategies trigger the session-expiration flow
 *    via [TokenRefreshCallbackRegistry], and this authenticator
 *    returns null to prevent further retries.
 *
 * OkHttp [Authenticator]s are synchronous, so we use [runBlocking]
 * to bridge into the coroutine-based refresh logic.
 */
class TokenAuthenticator(
    private val strategies: List<AuthExceptionHandler>
) : Authenticator {

    companion object {
        private const val TAG = "TokenAuthenticator"
    }

    override fun authenticate(route: Route?, response: Response): Request? {
        Log.d(TAG, "Received ${response.code} from ${response.request.url}")

        if (response.code != 401) {
            return null
        }

        // Prevent infinite retry loops: if we already attempted a refresh
        // for this call chain, don't retry again.
        if (responseCount(response) >= 2) {
            Log.w(TAG, "Already retried ${responseCount(response)} times — giving up to avoid loop")
            return null
        }

        return runBlocking {
            withContext(Dispatchers.IO) {
                for (strategy in strategies) {
                    if (strategy.canHandle(response)) {
                        try {
                            val newRequest = strategy.handle(response)
                            if (newRequest != null) {
                                Log.d(
                                    TAG,
                                    "Strategy ${strategy::class.simpleName} produced a new request — retrying"
                                )
                                return@withContext newRequest
                            }
                        } catch (e: Exception) {
                            Log.e(
                                TAG,
                                "Strategy ${strategy::class.simpleName} threw: ${e.message}",
                                e
                            )
                        }
                    }
                }
                // No strategy could resolve the 401 — session is expired.
                Log.w(TAG, "No strategy could resolve 401 — session expired")
                null
            }
        }
    }

    /**
     * Counts how many times OkHttp has already attempted this request,
     * by walking the [Response].priorResponse chain.
     */
    private fun responseCount(response: Response): Int {
        var result = 1
        var prior = response.priorResponse
        while (prior != null) {
            result++
            prior = prior.priorResponse
        }
        return result
    }
}
