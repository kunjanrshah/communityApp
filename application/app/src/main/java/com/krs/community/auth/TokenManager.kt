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
        get() = getString(R.string.access_token)

    val refreshToken: String?
        get() = getString(R.string.refresh_token)

    fun saveTokens(accessToken: String?, refreshToken: String?) {
        putString(R.string.access_token, accessToken)
        putString(R.string.refresh_token, refreshToken)
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
}
