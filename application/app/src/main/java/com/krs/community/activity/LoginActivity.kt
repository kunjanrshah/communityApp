package com.krs.community.activity

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.Html
import android.text.InputFilter
import android.text.InputType
import android.text.TextWatcher
import android.util.Log
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import androidx.localbroadcastmanager.content.LocalBroadcastManager

import com.android.volley.AuthFailureError
import com.android.volley.DefaultRetryPolicy
import com.android.volley.NetworkError
import com.android.volley.ParseError
import com.android.volley.Request
import com.android.volley.ServerError
import com.android.volley.TimeoutError
import com.android.volley.VolleyLog
import com.android.volley.toolbox.JsonObjectRequest
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.GraphRequest
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.TaskExecutors
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.FirebaseException
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.messaging.FirebaseMessaging
import com.krs.community.R
import com.krs.community.app.AppController
import com.krs.community.app.Config
import com.krs.community.databinding.ActivityLoginBinding
import com.krs.community.interfaces.ILoginListener
import com.krs.community.model.LoginData
import com.krs.community.utils.AppConstants
import com.krs.community.utils.CountryData
import com.krs.community.utils.NotificationUtils
import com.krs.community.utils.Utility
import com.krs.community.viewmodel.LoginViewModel
import com.krs.community.viewmodel.LoginViewModelFactory
import org.json.JSONException
import org.json.JSONObject

import java.util.Arrays
import java.util.HashMap
import java.util.concurrent.TimeUnit

import com.android.volley.DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
import com.android.volley.DefaultRetryPolicy.DEFAULT_MAX_RETRIES
import com.krs.community.utils.AppConstants.INIT_TIMEOUT
import com.krs.community.utils.Utility.hideProgressDialog
import com.krs.community.utils.Utility.isValidEmail
import com.krs.community.utils.Utility.isValidMobile
import com.krs.community.utils.Utility.showProgressDialog
import org.kodein.di.generic.instance

import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein


class LoginActivity : AppCompatActivity(), ILoginListener , KodeinAware {
    private val TAG = LoginActivity::class.java.simpleName
    private val isLogin = booleanArrayOf(false)
    private val is_from_normal = 0
    private val is_from_fb = 1
    private val is_from_google = 2
    private val Mobile = "M"
    private val Email = "E"
    private var img_back: ImageView? = null
    private var img_login_fb: ImageView? = null
    private var img_login_google: ImageView? = null
    private var txt_forgot_pass: TextView? = null
    private var txt_do_you_have: TextView? = null
    private var txt_cancel: TextView? = null
    private var btn_mobile: Button? = null
    private var btn_email: Button? = null
    private var btn_login: Button? = null
    private var edt_username: EditText? = null
    private var edt_pass: EditText? = null
    private var edt_cpass: EditText? = null
    private var isShow = true
    private var mSharedPreferences: SharedPreferences? = null
    private var mEditor: SharedPreferences.Editor? = null
    private var btn_login_google: SignInButton? = null
    private var mCallbackManager: CallbackManager? = null
    private var mAuth: FirebaseAuth? = null
    private var verificationId: String? = null
    private var spinnerCountries: Spinner? = null
    private var rl_spinner: RelativeLayout? = null
    private var isSelected = Mobile
    private var mobile_no = ""
    private var loginViewModel: LoginViewModel? = null

    private val mRegistrationBroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {

            if (intent.action == Config.REGISTRATION_COMPLETE) {
                FirebaseMessaging.getInstance().subscribeToTopic(Config.TOPIC_GLOBAL)

                val token = intent.getStringExtra("token")
                Log.e(TAG, "Firebase token: $token")

                displayFirebaseRegId()
            } else if (intent.action == Config.PUSH_NOTIFICATION) {
                val message = intent.getStringExtra("message")
                Toast.makeText(applicationContext, "Push notification: $message", Toast.LENGTH_LONG).show()
            }
        }
    }

    private val mCallBack = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        override fun onCodeSent(s: String, forceResendingToken: PhoneAuthProvider.ForceResendingToken) {
            super.onCodeSent(s, forceResendingToken)
            verificationId = s
            btn_login!!.text = resources.getString(R.string.verify_otp)
        }

        override fun onVerificationCompleted(phoneAuthCredential: PhoneAuthCredential) {
            val code = phoneAuthCredential.smsCode
            if (code != null) {
                edt_username!!.setText(code)
                verifyCode(code)
            }
        }

        override fun onVerificationFailed(e: FirebaseException) {
            Utility.alert(this@LoginActivity, e.message)
        }
    }

    override val kodein by kodein()
    private val factory: LoginViewModelFactory by instance()

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        loginViewModel = ViewModelProviders.of(this,factory).get(LoginViewModel::class.java)
        loginViewModel?.iLoginListener = this

        val binding = DataBindingUtil.setContentView<ActivityLoginBinding>(this@LoginActivity, R.layout.activity_login)
        binding.lifecycleOwner = this
        binding.loginViewModel = loginViewModel

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Utility.changeStatusbarColor(this, R.color.colorBG, false)
        }

        MemoryAllocation()

        FirebaseInstanceId.getInstance().instanceId.addOnSuccessListener(this@LoginActivity) { instanceIdResult ->
            val newToken = instanceIdResult.token
            Log.e("newToken", newToken)
            mEditor?.putString(AppConstants.DEVICE_TOKEN, newToken)
            mEditor?.apply()
        }


        btn_mobile!!.setOnClickListener { v ->
            isSelected = Mobile
            rl_spinner?.visibility = View.VISIBLE
            edt_username?.hint = getString(R.string.enter_mobile_no)
            edt_username?.inputType = InputType.TYPE_CLASS_PHONE or InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS
            edt_username?.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon, 0, 0, 0)
            edt_username?.setText("")

            val filters = arrayOfNulls<InputFilter>(1)
            filters[0] = InputFilter.LengthFilter(10) //Filter to 10 characters
            edt_username?.filters = filters

            btn_mobile?.setBackgroundColor(resources.getColor(R.color.colorPrimaryDark))
            btn_mobile?.setTextColor(resources.getColor(R.color.mdtp_white))

            btn_email?.background = getDrawable(R.drawable.border)
            btn_email?.setTextColor(resources.getColor(R.color.mdtp_transparent_black))

            edt_pass?.hint = getString(R.string.password)
            edt_pass?.visibility = View.VISIBLE

            txt_cancel?.visibility = View.GONE
            btn_login?.text = getString(R.string.login)

            txt_forgot_pass?.visibility = View.VISIBLE
            txt_forgot_pass?.text = resources.getString(R.string._forgot_password)

        }

        btn_email?.setOnClickListener { v ->
            isSelected = Email
            rl_spinner?.visibility = View.GONE
            edt_username?.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS
            edt_username?.setCompoundDrawablesWithIntrinsicBounds(R.drawable.close_envelope, 0, 0, 0)
            edt_username?.setHint(R.string.enter_email_id)
            edt_username?.setText("")

            btn_email?.setBackgroundColor(resources.getColor(R.color.colorPrimaryDark))
            btn_email?.setTextColor(resources.getColor(R.color.mdtp_white))

            btn_mobile?.background = getDrawable(R.drawable.border)
            btn_mobile?.setTextColor(resources.getColor(R.color.mdtp_transparent_black))

            edt_pass?.hint = getString(R.string.password)
            edt_pass?.visibility = View.VISIBLE
            txt_cancel?.visibility = View.GONE
            btn_login?.text = getString(R.string.login)
            txt_forgot_pass?.visibility = View.VISIBLE
            txt_forgot_pass?.text = resources.getString(R.string._forgot_password)

        }

        /*edt_pass.setOnEditorActionListener((v, actionId, event) -> {
            if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                if (btn_login.getText().toString().toLowerCase().contains("Change".toLowerCase())) {
                    if (!edt_username.getText().toString().isEmpty() && !edt_pass.getText().toString().isEmpty()) {
                        if (edt_username.getText().toString().equals(edt_pass.getText().toString().isEmpty())) {
                            call_change_password_ws();
                        }
                    }
                } else {
                    btn_login.performClick();
                }
            }
            return false;
        });*/

        edt_username?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (isSelected.equals(Mobile, ignoreCase = true)) {
                    if (edt_username?.text!!.length > 10) {
                        val str = edt_username?.text.toString().substring(0, 10)
                        edt_username?.setText(str)
                        edt_username?.setSelection(str.length)
                    }
                }
            }

            override fun afterTextChanged(s: Editable) {

            }
        })

        txt_cancel?.setOnClickListener { v ->

            if (isSelected.equals(Email, ignoreCase = true)) {
                btn_email?.performClick()
            } else {
                btn_mobile?.performClick()
            }
            edt_username?.setText("")
            edt_pass?.setText("")
            edt_cpass?.setText("")
            edt_username?.visibility = View.VISIBLE
            edt_cpass?.visibility = View.GONE
            edt_pass?.hint = getString(R.string.password)
            edt_pass?.visibility = View.VISIBLE
            txt_cancel?.visibility = View.GONE
            btn_login?.text = getString(R.string.login)
            txt_forgot_pass?.visibility = View.VISIBLE
            txt_forgot_pass?.text = resources.getString(R.string._forgot_password)
        }

        img_login_fb?.setOnClickListener { v ->

            img_login_fb?.isEnabled = false
            LoginManager.getInstance().logInWithReadPermissions(this@LoginActivity, Arrays.asList("email", "user_birthday", "public_profile"))
            LoginManager.getInstance().registerCallback(mCallbackManager!!, object : FacebookCallback<LoginResult> {
                override fun onSuccess(loginResult: LoginResult) {
                    Utility.hideProgressDialog()
                    Log.d(TAG, "facebook:onSuccess:$loginResult")
                    // App code
                    val request = GraphRequest.newMeRequest(loginResult.accessToken) { `object`, response ->
                        Log.v(TAG, response.toString())
                        // Application code
                        try {
                            val email = `object`.getString("email")
                            val url = `object`.getJSONObject("picture").getJSONObject("data").getString("url")
                            val mJsonObject = JSONObject()
                            mJsonObject.put("email", email)
                            mJsonObject.put("url", url)
                            LoginWS(null, mJsonObject, is_from_fb)
                        } catch (e: JSONException) {
                            Toast.makeText(this@LoginActivity, "Error while getting records from Facebook", Toast.LENGTH_SHORT).show()
                            e.printStackTrace()
                        }
                    }
                    val parameters = Bundle()
                    parameters.putString("fields", "id,name,email,picture.width(200)")
                    request.parameters = parameters
                    request.executeAsync()
                    //handleFacebookAccessToken(loginResult.getAccessToken());
                }

                override fun onCancel() {
                    Utility.hideProgressDialog()
                    Log.d(TAG, "facebook:onCancel")
                    img_login_fb?.isEnabled = true
                }

                override fun onError(error: FacebookException) {
                    Utility.hideProgressDialog()
                    Log.d(TAG, "facebook:onError", error)
                    img_login_fb?.isEnabled = true
                }
            })

        }


        btn_login_google?.setOnClickListener { v -> signIn() }

        img_login_google?.setOnClickListener { v -> signIn() }

        txt_do_you_have?.setOnClickListener { v ->
            val mIntent = Intent(this@LoginActivity, RegisterActivty::class.java)
            startActivity(mIntent)
            finish()
            Utility.fade(this)
        }

        img_back?.setOnClickListener { v ->
            val mIntent = Intent(this@LoginActivity, SplashActivity::class.java)
            mIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(mIntent)
            finish()
            Utility.fade(this)
        }


        btn_login?.setOnClickListener { v ->


            val loginRequest = AppConstants.LoginRequest()
            loginViewModel?.getLoginUser(loginRequest)

            val forgotPassRequest = AppConstants.ForgotPass()
            loginViewModel?.userForgotPassword(forgotPassRequest)


            /* Intent mIntent = new Intent(LoginActivity.this, DashboardActivity.class);
            startActivity(mIntent);
            finish();*/
            /*
            String str = edt_username.getText().toString().trim();

            if (btn_login.getText().toString().contains(getString(R.string.send_otp))) {
                if (str.isEmpty() || str.length() < 10 || !isValidMobile(str)) {
                    edt_username.requestFocus();
                    Utility.alert(this, getString(R.string.err_msg_invalid_mobile));
                    return;
                }
                verifyValidUser(str, false);
            } else if (btn_login.getText().toString().contains(getString(R.string.email))) {
                if (str.isEmpty() || isValidEmail(str)) {
                    edt_username.requestFocus();
                    Utility.alert(this, getString(R.string.err_msg_email));
                    return;
                }
                verifyValidUser(str, true);
            } else if (btn_login.getText().toString().contains(getString(R.string.password))) {
                String str1 = edt_pass.getText().toString().trim();
                String str2 = edt_cpass.getText().toString().trim();
                if (!str1.isEmpty() && !str2.isEmpty()) {
                    if (str1.equals(str2)) {
                        call_change_password_ws(str1,str2);
                    } else {
                        Utility.alert(this, getString(R.string.err_msg_repeat_password));
                    }
                } else {
                    Utility.alert(this, getString(R.string.err_msg_password));
                }
            } else if (btn_login.getText().toString().contains(getResources().getString(R.string.verify_otp))) {
                verifyCode(edt_username.getText().toString().trim());
            } else if (btn_login.getText().toString().contains(getResources().getString(R.string.resend_otp))) {
                sendVerificationCode(mobile_no);
            } else {
                if (edt_pass.isShown()) {
                    LoginWS(null, null, is_from_normal);
                } else {
                    txt_cancel.performClick();
                }
            }*/
        }

        edt_cpass?.setOnTouchListener { v, event ->

            val DRAWABLE_RIGHT = 2
            if (event.action == MotionEvent.ACTION_UP) {
                if (event.rawX >= edt_cpass!!.right - edt_cpass!!.compoundDrawables[DRAWABLE_RIGHT].bounds.width()) {
                    if (isShow) {
                        edt_cpass?.setCompoundDrawablesWithIntrinsicBounds(R.drawable.lock, 0, R.drawable.pwd_show, 0)
                        edt_cpass?.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                        isShow = false
                    } else {
                        edt_cpass?.setCompoundDrawablesWithIntrinsicBounds(R.drawable.lock, 0, R.drawable.pwd_hide, 0)
                        edt_cpass?.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                        isShow = true
                    }
                    edt_cpass?.setSelection(edt_cpass!!.length())

                   return@setOnTouchListener true
                }
            }
           return@setOnTouchListener false
        }

        edt_pass?.setOnTouchListener { v, event ->

            val DRAWABLE_RIGHT = 2
            if (event.action == MotionEvent.ACTION_UP) {
                if (event.rawX >= edt_pass!!.right - edt_pass!!.compoundDrawables[DRAWABLE_RIGHT].bounds.width()) {
                    if (isShow) {
                        edt_pass?.setCompoundDrawablesWithIntrinsicBounds(R.drawable.lock, 0, R.drawable.pwd_show, 0)
                        edt_pass?.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                        isShow = false
                    } else {
                        edt_pass?.setCompoundDrawablesWithIntrinsicBounds(R.drawable.lock, 0, R.drawable.pwd_hide, 0)
                        edt_pass?.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                        isShow = true
                    }
                    edt_pass?.setSelection(edt_pass!!.length())

                    return@setOnTouchListener true
                }
            }
            return@setOnTouchListener false
        }


        txt_forgot_pass?.setOnClickListener { v ->
            txt_forgot_pass?.visibility = View.GONE
            edt_pass?.visibility = View.GONE
            txt_cancel?.visibility = View.VISIBLE
            val hint = edt_username?.hint.toString()
            if (hint.contains(getString(R.string.email))) {
                btn_login?.text = getString(R.string.send_email)
            } else {
                btn_login?.text = getString(R.string.send_sms)
            }
        }


        spinnerCountries?.adapter = object : ArrayAdapter<String>(this@LoginActivity, R.layout.my_spinner_style, CountryData.countryNames) {

            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val v = super.getView(position, convertView, parent)
                (v as TextView).textSize = 14f
                v.gravity = Gravity.LEFT or Gravity.CENTER_VERTICAL
                return v
            }

            override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
                val v = super.getDropDownView(position, convertView, parent)
                (v as TextView).textSize = 16f
                return v
            }

        }

        /*if (Build.VERSION.SDK_INT >= 23) {
            if (!Utility.haveSMS(this)) {
                new SweetAlertDialog(this, SweetAlertDialog.CUSTOM_IMAGE_TYPE)
                        .setTitleText("SMS Permission")
                        .setContentText("App will send SMS your password on your mobile. To work this feature allow SMS permission")
                        .setConfirmText("Yes, please!")
                        .setCancelText("No!")
                        .showCancelButton(true)
                        .setConfirmClickListener(sDialog -> {
                            sDialog.dismiss();
                            ActivityCompat.requestPermissions(LoginActivity.this, new String[]{Manifest.permission.SEND_SMS, Manifest.permission.RECEIVE_SMS}, 1);
                        })
                        .show();
            }
        }*/
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            1 -> {

                // If request is cancelled, the result arrays are empty.
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(this@LoginActivity, "Permission denied to SMS your External storage", Toast.LENGTH_SHORT).show()
                }
                return
            }
        }// other 'case' lines to check for other
        // permissions this app might request
    }

    // Fetches reg id from shared preferences
    private fun displayFirebaseRegId() {
        val pref = applicationContext.getSharedPreferences(AppConstants.PREF_NAME, Context.MODE_PRIVATE)
        val regId = pref.getString(AppConstants.DEVICE_TOKEN, null)
        Log.e(TAG, "Firebase reg id: " + regId!!)
    }

    private fun signIn() {
        val signInIntent = AppController.mApplication.mGoogleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    private fun MemoryAllocation() {
        mCallbackManager = CallbackManager.Factory.create()
        mAuth = FirebaseAuth.getInstance()
        mSharedPreferences = AppController.mApplication.mSharedPreferences
        mEditor = AppController.mApplication.mEditor
        img_back = findViewById(R.id.img_back)
        edt_username = findViewById(R.id.edt_username)
        edt_pass = findViewById(R.id.edt_pass)
        edt_cpass = findViewById(R.id.edt_cpass)
        btn_mobile = findViewById(R.id.btn_mobile)
        btn_email = findViewById(R.id.btn_email)
        btn_login = findViewById(R.id.btn_login)
        txt_cancel = findViewById(R.id.txt_cancel)
        spinnerCountries = findViewById(R.id.spinnerCountries)
        rl_spinner = findViewById(R.id.rl_spinner)
        btn_login_google = findViewById(R.id.btn_login_google)
        img_login_fb = findViewById(R.id.img_login_fb)
        img_login_google = findViewById(R.id.img_login_google)
        txt_forgot_pass = findViewById(R.id.txt_forgot_pass)
        txt_do_you_have = findViewById(R.id.txt_do_you_have)

        var sourcestr = resources.getString(R.string.do_you_have_an_account_register_now)
        sourcestr = sourcestr + "<b>" + " " + getString(R.string.register_now) + "</b>"
        txt_do_you_have?.text = Html.fromHtml(sourcestr)
    }

    override fun onResume() {
        super.onResume()
        // register GCM registration complete receiver
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                IntentFilter(Config.REGISTRATION_COMPLETE))

        // register new push message receiver
        // by doing this, the activity will be notified each time a new message arrives
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                IntentFilter(Config.PUSH_NOTIFICATION))

        // clear the notification area when the app is opened
        NotificationUtils.clearNotifications(applicationContext)
    }

    override fun onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver)
        super.onPause()
    }

    override fun onStart() {
        super.onStart()
        /*FirebaseUser currentUser = mAuth.getCurrentUser();*/

        val is_home = mSharedPreferences?.getBoolean(AppConstants.IS_HOME, false)
        if (!is_home!!) {
            return
        }
        val mIntent = Intent(this@LoginActivity, DashboardActivity::class.java)
        if (mSharedPreferences != null) {
            mIntent.putExtra(AppConstants.USER_ID, mSharedPreferences?.getString(AppConstants.USER_ID, ""))
        }
        if (!isLogin[0]) {
            isLogin[0] = true
            startActivity(mIntent)
            finish()
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account!!)
            } catch (e: ApiException) {
                Log.w(TAG, "Google sign in failed", e)
            }

        } else {
            mCallbackManager?.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun handleFacebookAccessToken(token: AccessToken) {
        Log.d(TAG, "handleFacebookAccessToken:$token")

        val credential = FacebookAuthProvider.getCredential(token.token)
        mAuth!!.signInWithCredential(credential).addOnCompleteListener(this) { task ->

            if (task.isSuccessful) {
                // Sign in success, update UI with the signed-in user's information
                Log.d(TAG, "signInWithCredential:success")
                img_login_fb?.isEnabled = true
                val user = mAuth?.currentUser
                if (user != null) {
                    LoginWS(user, null, is_from_fb)
                }

            } else {
                img_login_fb?.isEnabled = true
                // If sign in fails, display a message to the user.
                Log.w(TAG, "signInWithCredential:failure", task.exception)
                Toast.makeText(this@LoginActivity, "Authentication failed.", Toast.LENGTH_SHORT).show()


                if (!task.isSuccessful) {
                    Log.w(TAG, "signInWithCredential", task.exception)
                    Toast.makeText(applicationContext, "Firebase Facebook login failed", Toast.LENGTH_SHORT).show()

                    if (task.exception is FirebaseAuthUserCollisionException) {
                        /*FirebaseAuthUserCollisionException exception = (FirebaseAuthUserCollisionException) task.getException();
                        //exception.getErrorCode()
                        mAuth.fetchProvidersForEmail("kunjanrshah@gmail.com").addOnCompleteListener(new OnCompleteListener<ProviderQueryResult>() {
                            @Override
                            public void onComplete(@NonNull Task<ProviderQueryResult> task) {

                                if (task.isSuccessful()) {
                                    if (task.getResult().getProviders().contains(EmailAuthProvider.PROVIDER_ID)) {


                                    }
                                }
                            }
                        });*/
                        Toast.makeText(applicationContext, "User with Email id already exists", Toast.LENGTH_SHORT).show()
                    }
                    LoginManager.getInstance().logOut()
                }

            }
        }
    }

    private fun firebaseAuthWithGoogle(acct: GoogleSignInAccount) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.id!!)
        Utility.showProgressDialog(this)

        val credential = GoogleAuthProvider.getCredential(acct.idToken, null)
        mAuth?.signInWithCredential(credential)?.addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                Log.d(TAG, "signInWithCredential:success")
                val user = mAuth?.currentUser
                if (user != null) {
                    Log.d(TAG, "email: " + user.email + " phone: " + user.phoneNumber + " photo: " + user.photoUrl)
                    LoginWS(user, null, is_from_google)
                }
            } else {
                Log.w(TAG, "signInWithCredential:failure", task.exception)
                Snackbar.make(findViewById(R.id.ll_login), "Authentication Failed.", Snackbar.LENGTH_SHORT).show()
            }
            hideProgressDialog()
        }
    }


    private fun sendVerificationCode(number: String) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(number, 60, TimeUnit.SECONDS, TaskExecutors.MAIN_THREAD, mCallBack)
        Toast.makeText(this@LoginActivity, "OTP Send Please wait for a minute", Toast.LENGTH_LONG).show()
    }

    private fun verifyCode(code: String?) {
        val credential = PhoneAuthProvider.getCredential(verificationId!!, code!!)
        signInWithCredential(credential)
    }

    private fun signInWithCredential(credential: PhoneAuthCredential) {
        mAuth?.signInWithCredential(credential)?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                edt_username?.setText("")
                edt_pass?.setText("")
                edt_pass?.visibility = View.VISIBLE
                edt_cpass?.visibility = View.VISIBLE
                edt_username?.visibility = View.GONE
                btn_login?.text = getString(R.string.nav_item_change_password)
            } else {
                Toast.makeText(this@LoginActivity, task.exception?.message, Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun verifyValidUser(username: String, isEmail: Boolean) {
        val json = JSONObject()
        try {
            json.put(AppConstants.USERNAME, username.trim { it <= ' ' })
            json.put(AppConstants.IS_SOCIAL, "1")
        } catch (e: Exception) {
            e.printStackTrace()
        }

        Utility.showProgressDialog(this)
        val jsonObjReq = object : JsonObjectRequest(Request.Method.POST, AppConstants.LOGIN_URL, json, { response ->
            hideProgressDialog()
            val success: Boolean
            try {
                success = response.getBoolean(AppConstants.SUCCESS)
                if (success) {
                    if (isEmail) {
                       // ForgotPassword(username)
                    } else {
                        val data = response.getString(AppConstants.DATA)
                        val mjson_data = JSONObject(data)
                        val status = mjson_data.getString(AppConstants.STATUS)
                        if (status.equals("1", ignoreCase = true)) {
                            AfterValidCheck(response, false)
                            val code = CountryData.countryAreaCodes[spinnerCountries!!.selectedItemPosition]
                            val number = "+$code$username"
                            rl_spinner?.visibility = View.GONE
                            edt_username?.setText("")
                            btn_login?.text = getString(R.string.resend_otp)
                            edt_username?.hint = getString(R.string.type_otp)
                            mobile_no = number
                            sendVerificationCode(number)
                        } else {
                            Utility.alert(this@LoginActivity, getString(R.string.registraion_request_pending))
                        }
                    }
                } else {
                    Utility.alert(this, getString(R.string.invalid_username))
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }, { error ->
            hideProgressDialog()
            VolleyLog.d(TAG, "Error: " + error.message)
            var message: String? = null
            if (error is NetworkError) {
                message = getString(R.string.can_not_connect_to_internet)
            } else if (error is ServerError) {
                message = getString(R.string.server_could_not_found)
            } else if (error is AuthFailureError) {
                message = getString(R.string.can_not_connect_to_internet)
            } else if (error is ParseError) {
                message = getString(R.string.parsing_error)
            } else if (error is TimeoutError) {
                message = getString(R.string.connection_timeout)
            }
            Utility.alert(this, message)
        }) {
            override fun getHeaders(): Map<String, String> {
                val params = HashMap<String, String>()
                params[AppConstants.API_KEY] = AppConstants.API_KEY_VALUE
                params[AppConstants.DEVICE_TYPE] = AppConstants.DEVICE_TYPE_VALUE
                params[AppConstants.DEVICE_ID] = AppConstants.DEVICE_ID_VALUE
                /*params[AppConstants.DEVICE_TOKEN] {
                    if (mSharedPreferences != null) mSharedPreferences!!.getString(AppConstants.DEVICE_TOKEN, "") else null
                }*/
                if (mSharedPreferences != null) {
                    params[AppConstants.DEVICE_TOKEN] = mSharedPreferences?.getString(AppConstants.DEVICE_TOKEN, "")!!
                }
                return params
            }
        }
        jsonObjReq.retryPolicy = DefaultRetryPolicy(INIT_TIMEOUT, DEFAULT_MAX_RETRIES, DEFAULT_BACKOFF_MULT)
        AppController.mApplication.addToRequestQueue(jsonObjReq, "")
    }

   /* private fun call_change_password_ws(str1: String, str2: String) {

        if (Utility.isOnline(this)) {

            Utility.showProgressDialog(this)
            var mJsonObject: JSONObject? = null

            try {
                mJsonObject = JSONObject()
                mJsonObject.put(AppConstants.USER_ID, mSharedPreferences!!.getString(AppConstants.USER_ID, ""))
                mJsonObject.put(AppConstants.ACCESS_TOKEN, mSharedPreferences!!.getString(AppConstants.ACCESS_TOKEN, ""))
                mJsonObject.put(AppConstants.PASSWORD, str1)
                mJsonObject.put(AppConstants.REPEAT_PASSWORD, str2)
            } catch (e: Exception) {
                e.printStackTrace()
            }

            val password_url = AppConstants.CHANGE_PASSWORD_URL
            val jsonObjReq = object : JsonObjectRequest(Request.Method.POST, password_url, mJsonObject, Response.Listener { response ->
                Log.d(TAG, "profile_url: $password_url")
                Log.d(TAG, "response: $response")
                Utility.hideProgressDialog()

                try {
                    val message = response.getString(AppConstants.MESSAGE)
                    val success = response.getString(AppConstants.SUCCESS)
                    if (success.equals(AppConstants.TRUE, ignoreCase = true)) {
                        Toast.makeText(this@LoginActivity, message, Toast.LENGTH_SHORT).show()
                        val userid = mSharedPreferences!!.getString(AppConstants.USER_ID, "")
                        if (userid!!.isEmpty()) {
                            return@Listener
                        }
                        val mIntent = Intent(this@LoginActivity, DashboardActivity::class.java)
                        if (mSharedPreferences != null) {
                            mIntent.putExtra(AppConstants.USER_ID, mSharedPreferences!!.getString(AppConstants.USER_ID, ""))
                        }
                        if (!isLogin[0]) {
                            isLogin[0] = true
                            startActivity(mIntent)
                            finish()
                        }
                    } else {
                        Utility.alert(this@LoginActivity, message)
                        if (response.has(AppConstants.ERROR_CODE)) {
                            val error = response.getString(AppConstants.ERROR_CODE)
                            *//*if (error.equalsIgnoreCase(AppConstants.ERROR_13)) {
                                }*//*
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    Utility.hideProgressDialog()
                }
            }, { error ->
                VolleyLog.d(TAG, "Error: " + error.message)
                Utility.hideProgressDialog()
            }) {
                override fun getHeaders(): Map<String, String> {
                    val params = HashMap<String, String>()
                    params[AppConstants.API_KEY] = AppConstants.API_KEY_VALUE
                    params[AppConstants.DEVICE_TYPE] = AppConstants.DEVICE_TYPE_VALUE
                    params[AppConstants.DEVICE_ID] = AppConstants.DEVICE_ID_VALUE
                    params[AppConstants.DEVICE_TOKEN] = mSharedPreferences!!.getString(AppConstants.DEVICE_TOKEN, "")!!
                    return params
                }
            }

            jsonObjReq.retryPolicy = DefaultRetryPolicy(INIT_TIMEOUT, DEFAULT_MAX_RETRIES, DEFAULT_BACKOFF_MULT)
            AppController.mApplication.addToRequestQueue(jsonObjReq, "")
        }
    }

    private fun ForgotPassword(forgot_email: String) {
        val json = JSONObject()
        try {
            json.put(AppConstants.EMAIL_ADDRESS, forgot_email)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        val url = AppConstants.FORGOT_PASSWORD_URL
        Utility.showProgressDialog(this)
        val jsonObjReq = object : JsonObjectRequest(Request.Method.POST, url, json, Response.Listener { response ->
            Log.d(TAG, "ForgotPasswordWS: $response")

            try {
                hideProgressDialog()
                val success = response.getBoolean(AppConstants.SUCCESS)
                val message = response.getString(AppConstants.MESSAGE)

                if (success) {
                    if (response.has(AppConstants.PASSWORD) && response.has(AppConstants.MOBILE)) {
                        //    inputPassword.setText("");
                    }
                    Utility.alert(this@LoginActivity, message)
                } else {
                    Utility.alert(this@LoginActivity, message)
                    if (response.has(AppConstants.ERROR_CODE)) {
                        val error = response.getString(AppConstants.ERROR_CODE)
                        if (error.equals(AppConstants.ERROR_13, ignoreCase = true)) {
                            val mIntent = Intent(this@LoginActivity, LoginActivity::class.java)
                            mIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            startActivity(mIntent)
                            finish()
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }, { error ->
            VolleyLog.d(TAG, "Error: " + error.message)
            hideProgressDialog()
        }) {
            override fun getHeaders(): Map<String, String> {
                val params = HashMap<String, String>()
                params[AppConstants.API_KEY] = AppConstants.API_KEY_VALUE
                params[AppConstants.DEVICE_TYPE] = AppConstants.DEVICE_TYPE_VALUE
                return params
            }
        }
        // Adding request to request queue
        jsonObjReq.retryPolicy = DefaultRetryPolicy(INIT_TIMEOUT, AppConstants.DEFAULT_MAX_RETRIES, AppConstants.DEFAULT_BACKOFF_MULT)
        AppController.mApplication.addToRequestQueue(jsonObjReq, "")
    }*/

    private fun LoginWS(user: FirebaseUser?, data: JSONObject?, is_from: Int) {

        if (Utility.isOnline(this)) {
            var username: String? = ""
            var email_or_mobile = ""
            var password = ""
            val json = JSONObject()

            if (is_from == is_from_normal) {
                email_or_mobile = edt_username?.text.toString().trim { it <= ' ' }
                password = edt_pass?.text.toString()
                if (!email_or_mobile.isEmpty() && !password.isEmpty()) {
                    try {
                        if (isSelected.equals(Email, ignoreCase = true)) {
                            if (!isValidEmail(email_or_mobile)) {
                                json.put(AppConstants.USERNAME, email_or_mobile)
                                json.put(AppConstants.PASSWORD, password)
                                fetchLoginData(json)
                            } else {
                                Utility.alert(this@LoginActivity, resources.getString(R.string.invalid_email))
                                return
                            }
                        } else {
                            if (isValidMobile(email_or_mobile)) {
                                json.put(AppConstants.USERNAME, email_or_mobile)
                                json.put(AppConstants.PASSWORD, password)
                                fetchLoginData(json)
                            } else {
                                Utility.alert(this@LoginActivity, resources.getString(R.string.err_msg_invalid_mobile))
                                return
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }

                } else {
                    Utility.alert(this@LoginActivity, getString(R.string.err_msg_blank))
                    return
                }
            } else if (is_from == is_from_fb) {
                var fb_email = ""
                var fb_profile_url = ""
                try {
                    fb_email = data!!.getString("email")
                    fb_profile_url = data!!.getString("url")
                } catch (e: Exception) {
                    e.printStackTrace()
                }

                if (fb_email.isEmpty()) {
                    return
                }

                if (!fb_profile_url.isEmpty()) {

                    val finalFb_email = fb_email
                    val finalFb_profile_url = fb_profile_url
                    AlertDialog.Builder(this).setTitle(getString(R.string.app_name)).setMessage(resources.getString(R.string.update_profile_photo)).setIcon(R.drawable.app_icon).setCancelable(false).setPositiveButton(getString(R.string.yes)) { dialog, whichButton ->
                        try {
                            json.put(AppConstants.USERNAME, finalFb_email)
                            json.put(AppConstants.IS_SOCIAL, "1")
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }

                        GetBase64String(json).execute(finalFb_profile_url)
                    }.setNegativeButton(getString(R.string.no)) { dialog, which ->
                        try {
                            json.put(AppConstants.USERNAME, finalFb_email)
                            json.put(AppConstants.IS_SOCIAL, "1")
                            fetchLoginData(json)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }.show()
                } else {
                    try {
                        json.put(AppConstants.USERNAME, fb_email)
                        json.put(AppConstants.IS_SOCIAL, "1")
                        fetchLoginData(json)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }

                }
            } else if (is_from == is_from_google) {

                if (user != null) {
                    Log.e(TAG, " email: " + user.email + " phone: " + user.phoneNumber + " Id: " + user.uid + " Name: " + user.displayName)
                    username = user.email
                    if (username != null && !username.isEmpty()) {
                    } else {
                        username = user.phoneNumber
                        if (username != null && !username.isEmpty()) {
                        } else {
                            Utility.alert(this@LoginActivity, resources.getString(R.string.error_msg_get_data_social_site))
                            return
                            /* return AlertDialog.Builder(this).setTitle(getString(R.string.app_name)).setMessage(resources.getString(R.string.update_profile_photo)).setIcon(R.drawable.app_icon).setCancelable(false).setPositiveButton(getString(R.string.yes), dialog, whichButton) -> {
                                 try {
                                     json.put(AppConstants.USERNAME, finalFb_email);
                                     json.put(AppConstants.IS_SOCIAL, "1");
                                 } catch (Exception e) {
                                     e.printStackTrace();
                                 }
                                 new GetBase64String json.execute(finalFb_profile_url);
                             }).setNegativeButton*/
                        }
                    }

                    if (!user.photoUrl!!.toString().isEmpty()) {

                        val finalUsername = username
                        AlertDialog.Builder(this).setTitle(getString(R.string.app_name)).setMessage(resources.getString(R.string.update_profile_photo)).setIcon(R.drawable.app_icon).setCancelable(false).setPositiveButton(getString(R.string.yes)) { dialog, whichButton ->
                            try {
                                json.put(AppConstants.USERNAME, finalUsername)
                                json.put(AppConstants.IS_SOCIAL, "1")
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }

                            GetBase64String(json).execute(user.photoUrl?.toString()?.replace("s96-c", "s240-c"))
                        }.setNegativeButton(getString(R.string.no)) { dialog, which ->
                            try {
                                json.put(AppConstants.USERNAME, finalUsername)
                                json.put(AppConstants.IS_SOCIAL, "1")
                                fetchLoginData(json)
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }


                        }.show()
                    } else {
                        try {
                            json.put(AppConstants.USERNAME, username)
                            json.put(AppConstants.IS_SOCIAL, "1")
                            fetchLoginData(json)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }

                    }
                }
            }


        }

    }

    private fun fetchLoginData(json: JSONObject) {
        Utility.showProgressDialog(this)
        val jsonObjReq = object : JsonObjectRequest(Request.Method.POST, AppConstants.LOGIN_URL, json, { response ->
            Log.d(TAG, "LoginWS: $response")

            try {
                hideProgressDialog()
                val success = response.getBoolean(AppConstants.SUCCESS)
                val message = response.getString(AppConstants.MESSAGE).toLowerCase()
                if (success) {
                    AfterValidCheck(response, true)
                } else {
                    var str = ""
                    if (message.contains(getString(R.string.incorrect).toLowerCase())) {
                        str = resources.getString(R.string.invalid_username_password)
                    } else if (message.contains(getString(R.string.invalid_mobile).toLowerCase())) {
                        str = resources.getString(R.string.err_msg_invalid_mobile)
                    } else if (message.contains(getString(R.string.err_invalid_email).toLowerCase())) {
                        str = resources.getString(R.string.invalid_email)
                    } else if (message.contains(getString(R.string.err_invalid_email).toLowerCase())) {
                        str = resources.getString(R.string.invalid_email)
                    } else if (message.contains(getString(R.string.user_not).toLowerCase())) {
                        str = resources.getString(R.string.user_not_found)
                    } else {
                        str = message
                    }
                    Utility.alert(this@LoginActivity, str)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }


        }, { error ->
            hideProgressDialog()
            VolleyLog.d(TAG, "Error: " + error.message)
            var message: String? = null
            if (error is NetworkError) {
                message = getString(R.string.can_not_connect_to_internet)
            } else if (error is ServerError) {
                message = getString(R.string.server_could_not_found)
            } else if (error is AuthFailureError) {
                message = getString(R.string.can_not_connect_to_internet)
            } else if (error is ParseError) {
                message = getString(R.string.parsing_error)
            } else if (error is TimeoutError) {
                message = getString(R.string.connection_timeout)
            }
            Utility.alert(this@LoginActivity, message)
        }) {
            override fun getHeaders(): Map<String, String> {
                val params = HashMap<String, String>()
                params[AppConstants.API_KEY] = AppConstants.API_KEY_VALUE
                params[AppConstants.DEVICE_TYPE] = AppConstants.DEVICE_TYPE_VALUE
                params[AppConstants.DEVICE_ID] = AppConstants.DEVICE_ID_VALUE





                if (mSharedPreferences != null) {
                    params[AppConstants.DEVICE_TOKEN] = mSharedPreferences?.getString(AppConstants.DEVICE_TOKEN, "")!!
                }
                return params
            }
        }
        jsonObjReq.retryPolicy = DefaultRetryPolicy(INIT_TIMEOUT, DEFAULT_MAX_RETRIES, DEFAULT_BACKOFF_MULT)
        AppController.mApplication.addToRequestQueue(jsonObjReq, "")
    }

    @Throws(Exception::class)
    private fun AfterValidCheck(response: JSONObject, isLoginSuccess: Boolean) {
        val data = response.getString(AppConstants.DATA)
        val mjson_data = JSONObject(data)

        val status = mjson_data.getString(AppConstants.STATUS)
        if (status.equals("1", ignoreCase = true)) {

            val user_id = mjson_data.getString(AppConstants.ID)
            val email = mjson_data.getString(AppConstants.EMAIL_ADDRESS)
            val password = mjson_data.getString(AppConstants.PLAIN_PASSWORD)
            val profile_url = mjson_data.getString(AppConstants.PROFILE_PIC_URL)
            val first_name = mjson_data.getString(AppConstants.FIRST_NAME)
            val last_name = mjson_data.getString(AppConstants.LAST_NAME)

            val office_lat = mjson_data.getString(AppConstants.OFFICE_LAT)
            val office_lng = mjson_data.getString(AppConstants.OFFICE_LNG)
            val home_lat = mjson_data.getString(AppConstants.HOME_LAT)
            val home_lng = mjson_data.getString(AppConstants.HOME_LNG)

            val access_token = mjson_data.getString(AppConstants.ACCESS_TOKEN)
            val updated_time = mjson_data.getString(AppConstants.UPDATED_TIME)
            val role = mjson_data.getString(AppConstants.ROLE)
            val is_location_enable = mjson_data.getString(AppConstants.IS_LOCATION_ENABLE)

            mEditor?.putString(AppConstants.EMAIL, email)
            mEditor?.putString(AppConstants.PASSWORD, password)
            mEditor?.putString(AppConstants.USER_ID, user_id)
            mEditor?.putString(AppConstants.PROFILE_PIC_URL, profile_url)
            mEditor?.putString(AppConstants.FIRST_NAME, first_name)
            mEditor?.putString(AppConstants.LAST_NAME, last_name)
            mEditor?.putString(AppConstants.ACCESS_TOKEN, access_token)
            mEditor?.putString(AppConstants.UPDATED_TIME, updated_time)
            mEditor?.putString(AppConstants.ROLE, role)
            mEditor?.putString(AppConstants.TBTN_SHARE, is_location_enable)
            mEditor?.putString(AppConstants.OFFICE_LAT, office_lat)
            mEditor?.putString(AppConstants.OFFICE_LNG, office_lng)
            mEditor?.putString(AppConstants.HOME_LAT, home_lat)
            mEditor?.putString(AppConstants.HOME_LNG, home_lng)
            mEditor?.apply()

            val fb_bundle = Bundle()
            fb_bundle.putInt(FirebaseAnalytics.Param.ITEM_ID, Integer.parseInt(user_id))
            fb_bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "$first_name $last_name")
            /*  AppController.mApplication.firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, fb_bundle);
                if (user_id.equalsIgnoreCase(AppConstants.ADMIN_1) || user_id.equalsIgnoreCase(AppConstants.ADMIN_2)) {
                AppController.isAdmin = true;
              }*/
            if (isLoginSuccess) {
                val mIntent = Intent(this@LoginActivity, DashboardActivity::class.java)
                if (mSharedPreferences != null) {
                    mIntent.putExtra(AppConstants.USER_ID, mSharedPreferences!!.getString(AppConstants.USER_ID, ""))
                }
                if (!isLogin[0]) {
                    isLogin[0] = true
                    startActivity(mIntent)
                    finish()
                }
            }
        } else {
            Utility.alert(this@LoginActivity, getString(R.string.registraion_request_pending))
        }
    }

    override fun getFailure(message: String) {
        Log.d(TAG, "login data: $message")
    }

    override fun userForgotPass(data: String) {
        Log.d(TAG, "forgot data: $data")
    }

    override fun getUserLogin(data: LoginData) {
        Log.d(TAG, "login data: $data")
    }

    internal inner class GetBase64String(mJsonObject: JSONObject) : AsyncTask<String, Void, String>() {
        var mJsonObject: JSONObject? = null

        init {
            this.mJsonObject = mJsonObject
        }

        override fun onPreExecute() {
            super.onPreExecute()
            showProgressDialog(this@LoginActivity)
        }

        override fun doInBackground(vararg strings: String): String? {
            return Utility.getByteArrayFromImageURL(strings[0])
        }

        override fun onPostExecute(str: String) {
            super.onPostExecute(str)
            hideProgressDialog()
            if (mJsonObject != null) {
                try {
                    mJsonObject!!.put(AppConstants.PROFILE_PIC, str)
                } catch (e: Exception) {
                    e.printStackTrace()
                }

              //  fetchLoginData(mJsonObject)
            }

        }
    }

    companion object {

        private val RC_SIGN_IN = 9001
    }

}
