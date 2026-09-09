package com.krs.community.auth

import android.content.Context
import com.github.squti.guru.Guru
import com.krs.community.R

/**
 * Single source of truth for auth token persistence.
 *
 * Encapsulates all direct SharedPreferences access so that token
 * read/write logic lives in one place and can be swapped or mocked
 * without touching callers.
 */
class TokenManager(
    private val context: Context
) {

    val accessToken: String?
        get() = normalizeToken(getString(R.string.access_token))

    val refreshToken: String?
        get() = normalizeToken(getString(R.string.refresh_token))

    fun saveTokens(accessToken: String?, refreshToken: String?) {
        putString(R.string.access_token, normalizeToken(accessToken))
        putString(R.string.refresh_token, normalizeToken(refreshToken))
    }

    fun clearTokens() {
        putString(R.string.access_token, null)
        putString(R.string.refresh_token, null)
    }

    private fun getString(keyRes: Int): String? =
        Guru.getString(context.getString(keyRes), null as String?)

    private fun putString(keyRes: Int, value: String?) {
        Guru.putString(context.getString(keyRes), value)
    }

    private fun normalizeToken(rawToken: String?): String? {
        if (rawToken.isNullOrBlank()) return null
        val trimmed = rawToken.trim()
        val withoutBearer = if (trimmed.startsWith("Bearer ", ignoreCase = true)) {
            trimmed.removePrefix("Bearer ").trim()
        } else {
            trimmed
        }
        return withoutBearer.removeSurrounding("\"")
            .trim()
            .ifEmpty { null }
    }
}
