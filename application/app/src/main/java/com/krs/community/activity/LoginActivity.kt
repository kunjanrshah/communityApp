package com.krs.community.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.Html
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.GraphRequest
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.github.squti.guru.Guru
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.common.api.ApiException
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.iid.FirebaseInstanceId
import com.krs.community.R
import com.krs.community.app.AppController
import com.krs.community.app.AppSignatureHashHelper
import com.krs.community.app.SMSReceiver
import com.krs.community.databinding.ActivityLoginwithBinding
import com.krs.community.interfaces.ILoginListener
import com.krs.community.model.LoginData
import com.krs.community.model.LoginModel
import com.krs.community.utils.AppConstants
import com.krs.community.utils.NotificationUtils
import com.krs.community.utils.Utility
import com.krs.community.utils.Utility.*
import com.krs.community.utils.toast
import com.krs.community.viewmodel.LoginViewModel
import com.krs.community.viewmodel.LoginViewModelFactory
import kotlinx.android.synthetic.main.activity_loginwith.*
import org.json.JSONException
import org.json.JSONObject
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance
import java.util.*


class LoginActivity : AppCompatActivity(), ILoginListener, KodeinAware, SMSReceiver.OTPReceiveListener {


    //private var verificationId: String? = null

    /*private val mRegistrationBroadcastReceiver = object : BroadcastReceiver() {
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
    }*/

    /* private val mCallBack = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

         override fun onCodeSent(s: String, forceResendingToken: PhoneAuthProvider.ForceResendingToken) {
             super.onCodeSent(s, forceResendingToken)
             verificationId = s
             btnContinue?.text = resources.getString(R.string.verify_otp)
         }

         override fun onVerificationCompleted(phoneAuthCredential: PhoneAuthCredential) {
             val code = phoneAuthCredential.smsCode
             if (code != null) {
                 squareField?.setText(code)
                 verifyCode(code)
             }else {
                 hideProgress()
             }
         }

         override fun onVerificationFailed(e: FirebaseException) {
             hideProgress()
             alert(this@LoginActivity, e.message)
         }
     }*/

    override val kodein by kodein()
    private val factory: LoginViewModelFactory by instance()
    private var smsReceiver: SMSReceiver? = null
    private val TAG = LoginActivity::class.java.simpleName
    private val isLogin = booleanArrayOf(false)
    private var mCallbackManager: CallbackManager? = null
    private var mAuth: FirebaseAuth? = null
    private var loginViewModel: LoginViewModel? = null
    private var loginModel: LoginModel?=null
    companion object {
        private val RC_SIGN_IN = 9001
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)

        loginViewModel = ViewModelProviders.of(this, factory).get(LoginViewModel::class.java)
        loginViewModel?.iLoginListener = this

        val binding = DataBindingUtil.setContentView<ActivityLoginwithBinding>(this@LoginActivity, R.layout.activity_loginwith)
        binding.lifecycleOwner = this
        binding.loginViewModel = loginViewModel


        mCallbackManager = CallbackManager.Factory.create()
        mAuth = FirebaseAuth.getInstance()
        var sourcestr = resources.getString(R.string.do_you_have_an_account_register_now)
        sourcestr = sourcestr + "<b>" + " " + getString(R.string.register_now) + "</b>"
        have_acc.text = Html.fromHtml(sourcestr)

        FirebaseInstanceId.getInstance().instanceId.addOnSuccessListener(this@LoginActivity) { instanceIdResult ->
            val newToken = instanceIdResult.token
            Log.e("newToken", newToken)
            Guru.putString(AppConstants.DEVICE_TOKEN, newToken)
        }

        val appSignatureHashHelper = AppSignatureHashHelper(this)
        Guru.putString(getString(R.string.hash_key), appSignatureHashHelper.appSignatures.get(0));
        Log.e(TAG,"hashcode: "+appSignatureHashHelper.appSignatures.get(0))
        startSMSListener()

        edt_mobile?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (edt_mobile?.text!!.length > 10) {
                    val str = edt_mobile?.text.toString().substring(0, 10)
                    edt_mobile?.setText(str)
                    edt_mobile.setSelection(str.length)
                }
            }

            override fun afterTextChanged(s: Editable) {

            }
        })

        btn_login_fb.setOnClickListener { v ->

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
                            LoginUsingFB(mJsonObject)
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
                    hideProgressDialog()
                    Log.d(TAG, "facebook:onCancel")
                }

                override fun onError(error: FacebookException) {
                    hideProgressDialog()
                    Log.d(TAG, "facebook:onError", error)
                }
            })
        }

        btn_login_google?.setOnClickListener { v ->
            val signInIntent = AppController.mApplication.mGoogleSignInClient.signInIntent
            startActivityForResult(signInIntent, RC_SIGN_IN)
        }

        have_acc.setOnClickListener { v ->
            val mIntent = Intent(this@LoginActivity, RegisterActivty::class.java)
            startActivity(mIntent)
            finish()
            fade(this)
        }

        btnContinue.setOnClickListener { v ->

            val mobilenumber = loginViewModel?.mobile
            if (mobilenumber!!.isEmpty()) {
                edt_mobile.error = "Phone number is required"
                edt_mobile.requestFocus()
                return@setOnClickListener
            }

            if (mobilenumber.length < 10) {
                edt_mobile.error = "Please enter a valid phone"
                edt_mobile.requestFocus()
                return@setOnClickListener
            }

            LoginUsingMobile(mobilenumber, Guru.getString(getString(R.string.hash_key), "").toString())
            // sendVerificationCode(num)
        }

        // val forgotPassRequest = AppConstants.ForgotPass()
        //    loginViewModel?.userForgotPassword(forgotPassRequest)
        /*  *//*get countries *//*
        spinnerCountries.setItems(CountryData.countryNames)
        spinnerCountries.setExpandTint(R.color.black)
        spinnerCountries.select(0)
        loginViewModel?.country_code=CountryData.countryAreaCodes[0]

        spinnerCountries.setOnItemClickListener({ pos->
            loginViewModel?.country_code =CountryData.countryAreaCodes[pos]
        })*/

    }

    private fun startSMSListener() {
        try {
            smsReceiver = SMSReceiver()
            smsReceiver!!.setOTPListener(this)

            val intentFilter = IntentFilter()
            intentFilter.addAction(SmsRetriever.SMS_RETRIEVED_ACTION)
            this.registerReceiver(smsReceiver, intentFilter)

            val client = SmsRetriever.getClient(this)

            val task = client.startSmsRetriever()
            task.addOnSuccessListener {
                //toast("API successfully started")
            }

            task.addOnFailureListener {
                // Fail to start API
                toast("Fail to start API")
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun LoginUsingMobile(loginuser: String, hashcode: String) {
        if (isOnline(this)) {
            if (loginuser.isNotEmpty()) {
                try {
                    val loginRequest = AppConstants.LoginRequest()
                    loginRequest.username = loginuser
                    loginRequest.hashcode = hashcode
                    if (isValidMobile(loginuser)) {
                        loginRequest.login_type = "1"
                        loginViewModel?.getLoginUser(loginRequest)
                        card_view_mobile.visibility= View.GONE
                        card_view_otp.visibility=View.VISIBLE
                        /*Handler().postDelayed(Runnable {
                            startProgress(this,"Seat back & Relax!","Loading...")
                        },2000);*/

                    } else {
                        alert(this@LoginActivity, resources.getString(R.string.err_msg_invalid_mobile))
                        return
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            } else {
                alert(this@LoginActivity, getString(R.string.err_msg_blank))
                return
            }
        }
    }

    private fun LoginUsingFB(data: JSONObject) {
        try {
            if (isOnline(this)) {
                val loginRequest = AppConstants.LoginRequest()
                val loginuser = data.getString("email")
                //fb_profile_url = data.getString("url")
                if (loginuser.isEmpty()) {
                    Toast.makeText(this, "Facebook user not found!", Toast.LENGTH_SHORT).show()
                    return
                }
                loginRequest.username = loginuser
                loginRequest.login_type = "0"
                loginViewModel?.getLoginUser(loginRequest)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun LoginUsingGoogle(user:FirebaseUser) {
        if (isOnline(this)) {
            val loginRequest = AppConstants.LoginRequest()
            Log.e(TAG, " email: " + user.email + " phone: " + user.phoneNumber + " Id: " + user.uid + " Name: " + user.displayName)
            val loginuser = user.email.toString()
            if (loginuser.isNotEmpty() && loginuser.isNotBlank()) {
                loginRequest.username = loginuser
                loginRequest.login_type = "0"
                loginViewModel?.getLoginUser(loginRequest)
            }
        }
    }

    private fun firebaseAuthWithGoogle(acct: GoogleSignInAccount) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.id!!)
        showProgressDialog(this)

        val credential = GoogleAuthProvider.getCredential(acct.idToken, null)
        mAuth?.signInWithCredential(credential)?.addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                Log.d(TAG, "signInWithCredential:success")
                val user = mAuth?.currentUser
                if (user != null) {
                    Log.d(TAG, "email: " + user.email + " phone: " + user.phoneNumber + " photo: " + user.photoUrl)
                    LoginUsingGoogle(user)
                }
            } else {
                Log.w(TAG, "signInWithCredential:failure", task.exception)
                Snackbar.make(findViewById(R.id.ll_login), "Authentication Failed.", Snackbar.LENGTH_SHORT).show()
            }
            hideProgressDialog()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            1 -> {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted
                } else {
                    // permission denied
                    Toast.makeText(this@LoginActivity, "Permission denied to SMS your External storage", Toast.LENGTH_SHORT).show()
                }
                return
            }
        }
    }

    override fun onOTPReceived(otp: String?) {
        val otp1=otp?.substring(31,35);
        Log.i(TAG, "OTP Received: $otp1")
        squareField.setText(otp1)
        if (smsReceiver != null) {
            unregisterReceiver(smsReceiver)
            smsReceiver = null
        }

        if(loginModel!=null){
            if(loginModel!!.otp.equals(otp1)){
                val intent = Intent(applicationContext, DashboardActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
    }

    override fun onOTPTimeOut() {
        toast("OTP Time out")
    }

    override fun getUserLogin(model: LoginModel) {
        /*Handler().postDelayed(Runnable {
            hideProgress()
        },3000);*/

        Log.d(TAG, "login data: $model")
        loginModel=model
    }

    override fun onOTPReceivedError(error: String?) {
        error?.let { toast(it) }
    }

    override fun getFailure(message: String) {
        Log.d(TAG, "login data: $message")
    }

    override fun userForgotPass(data: String) {
        Log.d(TAG, "forgot data: $data")
    }






    // Fetches reg id from shared preferences
   /* private fun displayFirebaseRegId() {
        val pref = applicationContext.getSharedPreferences(AppConstants.PREF_NAME, Context.MODE_PRIVATE)
        val regId = pref.getString(AppConstants.DEVICE_TOKEN, null)
        Log.e(TAG, "Firebase reg id: " + regId!!)
    }*/

    override fun onResume() {
        super.onResume()
        /*LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,IntentFilter(Config.REGISTRATION_COMPLETE))
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,IntentFilter(Config.PUSH_NOTIFICATION))*/

        // clear the notification area when the app is opened
        NotificationUtils.clearNotifications(applicationContext)
    }

    override fun onPause() {
        /*LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver)*/
        super.onPause()
    }

    override fun onStart() {
        super.onStart()
        /*FirebaseUser currentUser = mAuth.getCurrentUser();*/

        val is_home = Guru.getBoolean(AppConstants.IS_HOME, false)
        if (!is_home) {
            return
        }

        val mIntent = Intent(this@LoginActivity, DashboardActivity::class.java)
        mIntent.putExtra(AppConstants.USER_ID, Guru.getString(AppConstants.USER_ID, ""))

        if (!isLogin[0]) {
            isLogin[0] = true
            startActivity(mIntent)
            finish()
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        if (smsReceiver != null) {
            unregisterReceiver(smsReceiver)
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

    /*private fun handleFacebookAccessToken(token: AccessToken) {
        Log.d(TAG, "handleFacebookAccessToken:$token")

        val credential = FacebookAuthProvider.getCredential(token.token)
        mAuth!!.signInWithCredential(credential).addOnCompleteListener(this) { task ->

            if (task.isSuccessful) {
                // Sign in success, update UI with the signed-in user's information
                Log.d(TAG, "signInWithCredential:success")
                val user = mAuth?.currentUser
                if (user != null) {
                    LoginWS(user, null, is_from_fb)
                }

            } else {
                // If sign in fails, display a message to the user.
                Log.w(TAG, "signInWithCredential:failure", task.exception)
                Toast.makeText(this@LoginActivity, "Authentication failed.", Toast.LENGTH_SHORT).show()


                if (!task.isSuccessful) {
                    Log.w(TAG, "signInWithCredential", task.exception)
                    Toast.makeText(applicationContext, "Firebase Facebook login failed", Toast.LENGTH_SHORT).show()

                    if (task.exception is FirebaseAuthUserCollisionException) {
                        *//*FirebaseAuthUserCollisionException exception = (FirebaseAuthUserCollisionException) task.getException();
                        //exception.getErrorCode()
                        mAuth.fetchProvidersForEmail("kunjanrshah@gmail.com").addOnCompleteListener(new OnCompleteListener<ProviderQueryResult>() {
                            @Override
                            public void onComplete(@NonNull Task<ProviderQueryResult> task) {

                                if (task.isSuccessful()) {
                                    if (task.getResult().getProviders().contains(EmailAuthProvider.PROVIDER_ID)) {


                                    }
                                }
                            }
                        });*//*
                        Toast.makeText(applicationContext, "User with Email id already exists", Toast.LENGTH_SHORT).show()
                    }
                    LoginManager.getInstance().logOut()
                }

            }
        }
    }*/


    /*  private fun sendVerificationCode(number: String) {
          startProgress(this,"Seat back & Relax! while we verify your mobile number","Loading...")
          PhoneAuthProvider.getInstance().verifyPhoneNumber(number, 60, TimeUnit.SECONDS, TaskExecutors.MAIN_THREAD, mCallBack)
      }

      private fun verifyCode(code: String?) {
          val credential = PhoneAuthProvider.getCredential(verificationId!!, code!!)
          signInWithCredential(credential)
      }*/

    /* private fun signInWithCredential(credential: PhoneAuthCredential) {
         mAuth?.signInWithCredential(credential)?.addOnCompleteListener { task ->
             if (task.isSuccessful) {
                 hideProgress()
             } else {
                 hideProgress()
                 Toast.makeText(this@LoginActivity, task.exception?.message, Toast.LENGTH_LONG).show()
             }
         }
     }*/


   /* private fun LoginWS(user: FirebaseUser?, data: JSONObject?, is_from: Int) {

        if (isOnline(this)) {
            var loginuser: String = loginViewModel?.mobile.toString()
            val loginRequest = AppConstants.LoginRequest()
            if (is_from == is_from_normal) {
                if (loginuser.isNotEmpty()) {
                    try {
                        loginRequest.username = loginuser
                        if (isValidMobile(loginuser)) {
                            loginRequest.login_type = "1"
                            loginViewModel?.getLoginUser(loginRequest)
                        } else {
                            alert(this@LoginActivity, resources.getString(R.string.err_msg_invalid_mobile))
                            return
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                } else {
                    alert(this@LoginActivity, getString(R.string.err_msg_blank))
                    return
                }
            } else if (is_from == is_from_fb) {
                try {
                    loginuser = data!!.getString("email")
                    //fb_profile_url = data.getString("url")
                    if (loginuser.isEmpty()) {
                        Toast.makeText(this, "Facebook user not found!", Toast.LENGTH_SHORT).show()
                        return
                    }
                    loginRequest.username = loginuser
                    loginRequest.login_type = "2"
                    loginViewModel?.getLoginUser(loginRequest)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            } else if (is_from == is_from_google) {
                if (user != null) {
                    Log.e(TAG, " email: " + user.email + " phone: " + user.phoneNumber + " Id: " + user.uid + " Name: " + user.displayName)
                    loginuser = user.email.toString()
                    if (loginuser.isNotEmpty() && loginuser.isNotBlank()) {
                        loginRequest.username = loginuser
                        loginRequest.login_type = "2"
                        loginViewModel?.getLoginUser(loginRequest)
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
                    alert(this@LoginActivity, str)
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
            alert(this@LoginActivity, message)
        }) {
            override fun getHeaders(): Map<String, String> {
                val params = HashMap<String, String>()
                params[AppConstants.API_KEY] = AppConstants.API_KEY_VALUE
                params[AppConstants.DEVICE_TYPE] = AppConstants.DEVICE_TYPE_VALUE
                params[AppConstants.DEVICE_ID] = AppConstants.DEVICE_ID_VALUE
                params[AppConstants.DEVICE_TOKEN] = Guru.getString(AppConstants.DEVICE_TOKEN, "")!!
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

            Guru.putString(AppConstants.EMAIL, email)
            Guru.putString(AppConstants.PASSWORD, password)
            Guru.putString(AppConstants.USER_ID, user_id)
            Guru.putString(AppConstants.PROFILE_PIC_URL, profile_url)
            Guru.putString(AppConstants.FIRST_NAME, first_name)
            Guru.putString(AppConstants.LAST_NAME, last_name)
            Guru.putString(AppConstants.ACCESS_TOKEN, access_token)
            Guru.putString(AppConstants.UPDATED_TIME, updated_time)
            Guru.putString(AppConstants.ROLE, role)
            Guru.putString(AppConstants.TBTN_SHARE, is_location_enable)
            Guru.putString(AppConstants.OFFICE_LAT, office_lat)
            Guru.putString(AppConstants.OFFICE_LNG, office_lng)
            Guru.putString(AppConstants.HOME_LAT, home_lat)
            Guru.putString(AppConstants.HOME_LNG, home_lng)

            val fb_bundle = Bundle()
            fb_bundle.putInt(FirebaseAnalytics.Param.ITEM_ID, Integer.parseInt(user_id))
            fb_bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "$first_name $last_name")
            *//*  AppController.mApplication.firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, fb_bundle);
                if (user_id.equalsIgnoreCase(AppConstants.ADMIN_1) || user_id.equalsIgnoreCase(AppConstants.ADMIN_2)) {
                AppController.isAdmin = true;
              }*//*
            if (isLoginSuccess) {
                val mIntent = Intent(this@LoginActivity, DashboardActivity::class.java)
                mIntent.putExtra(AppConstants.USER_ID, Guru.getString(AppConstants.USER_ID, ""))
                if (!isLogin[0]) {
                    isLogin[0] = true
                    startActivity(mIntent)
                    finish()
                }
            }
        } else {
            alert(this@LoginActivity, getString(R.string.registraion_request_pending))
        }
    }*/



}
