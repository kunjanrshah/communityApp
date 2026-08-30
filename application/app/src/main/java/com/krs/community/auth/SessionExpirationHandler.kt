package com.krs.community.auth

import android.app.Activity
import android.content.Intent
import android.util.Log
import cn.pedant.SweetAlert.SweetAlertDialog
import com.krs.community.R
import com.krs.community.activity.LoginActivity

/**
 * Handles the session-expiration flow.
 *
 * Implements [TokenRefreshCallback] so it is automatically notified
 * by [TokenRefreshCallbackRegistry] whenever the token-refresh logic
 * determines the session is permanently expired.
 *
 * On expiry it:
 * 1. Shows a [SweetAlertDialog] informing the user.
 * 2. On "OK", navigates to [LoginActivity] (clearing the task stack).
 * 3. Finishes the calling Activity if possible.
 *
 * The class holds a weak reference to the current [Activity] via
 * [currentActivity] so it can be registered/unregistered in
 * onResume/onPause without leaking.
 */
class SessionExpirationHandler : TokenRefreshCallback {

    companion object {
        private const val TAG = "SessionExpirationHandler"
    }

    @Volatile
    private var currentActivity: Activity? = null

    /**
     * Call from Activity.onResume to make this handler aware of the
     * active Activity (needed for dialog + navigation).
     */
    fun attach(activity: Activity) {
        currentActivity = activity
        TokenRefreshCallbackRegistry.register(this)
        Log.d(TAG, "Attached to ${activity::class.simpleName}")
    }

    /**
     * Call from Activity.onPause to avoid leaks and stale references.
     */
    fun detach() {
        currentActivity = null
        TokenRefreshCallbackRegistry.unregister(this)
        Log.d(TAG, "Detached")
    }

    override fun onSessionExpired() {
        val activity = currentActivity ?: run {
            Log.w(TAG, "No active activity — cannot show session-expired dialog")
            return
        }

        // If we're already on the Login screen, nothing to do.
        if (activity is LoginActivity) {
            Log.d(TAG, "Already on LoginActivity — ignoring session expiry")
            return
        }

        activity.runOnUiThread {
            SweetAlertDialog(activity, SweetAlertDialog.WARNING_TYPE)
                .setTitleText(activity.getString(R.string.session_expired_title))
                .setContentText(activity.getString(R.string.session_expired_message))
                .setConfirmText(activity.getString(R.string.ok))
                .setCancelText(activity.getString(R.string.cancel))
                .showCancelButton(false)
                .setConfirmClickListener { dialog: SweetAlertDialog ->
                    dialog.dismiss()
                    navigateToLogin(activity)
                }
                .show()
        }
    }

    private fun navigateToLogin(activity: Activity) {
        val intent = Intent(activity, LoginActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        activity.startActivity(intent)
        activity.finish()
    }
}
