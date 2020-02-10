package com.krs.community.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Bundle
import android.text.Editable
import android.text.Html
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import com.facebook.*
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.github.squti.guru.Guru
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.android.gms.common.api.ApiException
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.iid.FirebaseInstanceId
import com.google.gson.Gson
import com.krs.community.R
import com.krs.community.app.AppController
import com.krs.community.app.AppSignatureHashHelper
import com.krs.community.app.SMSReceiver
import com.krs.community.databinding.ActivityLoginwithBinding
import com.krs.community.fragments.FamilyDetailActivity
import com.krs.community.listeners.ILoginListener
import com.krs.community.model.LoginResponse
import com.krs.community.model.Member
import com.krs.community.utils.AppConstants
import com.krs.community.utils.NotificationUtils
import com.krs.community.utils.Utility.*
import com.krs.community.utils.toast
import com.krs.community.viewmodel.LoginViewModel
import com.krs.community.viewmodelfactory.LoginViewModelFactory
import kotlinx.android.synthetic.main.activity_loginwith.*
import org.json.JSONException
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance
import java.util.*


class LoginActivity : AppCompatActivity(), ILoginListener, KodeinAware, SMSReceiver.OTPReceiveListener {


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

    override val kodein by kodein()
    private val factory: LoginViewModelFactory by instance()
    private var smsReceiver: SMSReceiver? = null
    private val TAG = LoginActivity::class.java.simpleName
    private val isLogin = booleanArrayOf(false)
    private var mCallbackManager: CallbackManager? = null
    private var loginViewModel: LoginViewModel? = null
    private var ReceviedOTP:String?=null
    private lateinit var  member: Member
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

        val appSignatureHashHelper = AppSignatureHashHelper(this)
        var hashkey:String=appSignatureHashHelper.appSignatures.get(0);
        hashkey= hashkey.replace("+","%2B")
        Guru.putString(getString(R.string.hash_key),hashkey)
        Log.e(TAG,"hashcode: "+hashkey)

       getHashKey(this)


        val binding = DataBindingUtil.setContentView<ActivityLoginwithBinding>(this@LoginActivity, R.layout.activity_loginwith)
        binding.lifecycleOwner = this
        binding.loginViewModel = loginViewModel

        mCallbackManager = CallbackManager.Factory.create()
        loginViewModel?.mAuth = FirebaseAuth.getInstance()
        var sourcestr = resources.getString(R.string.do_you_have_an_account_register_now)
        sourcestr = sourcestr + "<b>" + " " + getString(R.string.register_now) + "</b>"
        have_acc.text = Html.fromHtml(sourcestr)

        FirebaseInstanceId.getInstance().instanceId.addOnSuccessListener(this@LoginActivity) { instanceIdResult ->
            val newToken = instanceIdResult.token
            Log.e("newToken", newToken)
            Guru.putString(AppConstants.DEVICE_TOKEN, newToken)
        }

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

        edt_mobile?.setOnEditorActionListener(TextView.OnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                btnContinue.performClick()
                true
            }
            false;
        })

        img_cancel.setOnClickListener {
            card_view_otp.visibility=View.GONE
            card_view_mobile.visibility=View.VISIBLE
            loginViewModel?.cancelTimer()
        }

        btn_login_fb.setOnClickListener { v ->

            LoginManager.getInstance().logInWithReadPermissions(this@LoginActivity, Arrays.asList("email", "public_profile"))
            LoginManager.getInstance().registerCallback(mCallbackManager!!, object : FacebookCallback<LoginResult> {
                override fun onSuccess(loginResult: LoginResult) {
                    showProgressDialog(this@LoginActivity)
                    Log.d(TAG, "facebook:onSuccess:$loginResult")
                    val request = GraphRequest.newMeRequest(loginResult.accessToken) { `object`, response ->
                        Log.v(TAG, response.toString())
                        try {
                            val email = `object`.getString("email")
                            //val url = `object`.getJSONObject("picture").getJSONObject("data").getString("url")
                            loginViewModel?.loginWithFB(email)
                        } catch (e: JSONException) {
                            Toast.makeText(this@LoginActivity, "Error while getting records from Facebook", Toast.LENGTH_SHORT).show()
                            e.printStackTrace()
                        }
                    }
                    val parameters = Bundle()
                    parameters.putString("fields", "id,name,email,picture.width(200)")
                    request.parameters = parameters
                    request.executeAsync()
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

        tv_resend.setOnClickListener {
            ReceviedOTP=""
            btnContinue.performClick()
        }

        btnContinue.setOnClickListener { v ->

            if(!ReceviedOTP.isNullOrEmpty() && ReceviedOTP==squareField.text.toString()){
                goToFamilyDetailScreen()
                return@setOnClickListener
            }

            if(tv_resend.isEnabled){
                val mobilenumber = loginViewModel?.mobile
                if (mobilenumber!!.isEmpty()) {
                    edt_mobile.error = "Phone number is required"
                    edt_mobile.requestFocus()
                    return@setOnClickListener
                }

                if (mobilenumber.length < 10 || !isValidMobile(mobilenumber)) {
                    edt_mobile.error = "Please enter a valid phone"
                    edt_mobile.requestFocus()
                    return@setOnClickListener
                }
                if (isOnline(this)) {
                    startSweetProgress(this, getString(R.string.otp_send), getString(R.string.loading))
                    loginViewModel?.loginWithMobile()
                }
            }
        }

        loginViewModel?.stopTime?.observe(this, androidx.lifecycle.Observer {stopTIme ->
            if(stopTIme==true){
                tv_resend.isClickable=true
                tv_resend.isEnabled=true
                tv_resend.setTextColor(resources.getColor(R.color.black1))
            }
        })

        loginViewModel?.status?.observe(this, androidx.lifecycle.Observer {status ->
           if (status==false){
               loginViewModel?.status?.value = null
               hideProgressDialog()
               hideSweetProgress()
              Snackbar.make(findViewById(R.id.ll_login), "Authentication Failed.", Snackbar.LENGTH_LONG).show()
            }
        })

        /*
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
                tv_resend.isClickable=false
                tv_resend.isEnabled=false
                tv_resend.setTextColor(resources.getColor(R.color.light_gray))
                loginViewModel?.cancelTimer()
                loginViewModel?.startTimer()
                //toast("API successfully started")
                Log.d(TAG,"API successfully started")
            }

            task.addOnFailureListener {
                // Fail to start API
                tv_resend.isEnabled=true
                tv_resend.isClickable=true
                tv_resend.setTextColor(resources.getColor(R.color.black1))
                Log.d(TAG,"Fail to start API")
                //toast("Fail to start API")
            }
        } catch (e: Exception) {
            e.printStackTrace()
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
        if(ReceviedOTP!=null){
            if(ReceviedOTP.equals(otp1)){
                goToFamilyDetailScreen()
            }
        }
    }

    override fun onOTPTimeOut() {
        Log.d(TAG,"OTP Time out")
        //toast("OTP Time out")
    }

    override fun userLogin(response: LoginResponse) {
        hideSweetProgress()
        hideProgressDialog()
        Log.d(TAG, "login data: $response")

        member=response.data
        if(!response.otp.isNullOrBlank()){
            card_view_mobile.visibility= View.GONE
            card_view_otp.visibility=View.VISIBLE
            startSMSListener()
            ReceviedOTP=response.otp
        }else{
            if(response.success){
                goToFamilyDetailScreen()
            }else{
                Snackbar.make(findViewById(R.id.ll_login), response.message, Snackbar.LENGTH_LONG).show()
            }
        }
    }

    private fun goToFamilyDetailScreen(){
        Guru.putString(getString(R.string.loginMember),Gson().toJson(member))
        Guru.putString(getString(R.string.user_id),member.id)
        Guru.putString(getString(R.string.access_token),member.accessToken)
        val intent = Intent(applicationContext, FamilyDetailActivity::class.java)
        intent.putExtra(getString(R.string.id), member.id)
        startActivity(intent)
        finish()
    }

    override fun onOTPReceivedError(error: String?) {
        error?.let { toast(it) }
    }

    override suspend fun getFailure(message: String) {
        loginViewModel!!.status.value=false
        Log.d(TAG, "login data: $message")
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
        val currentUser = loginViewModel?.mAuth?.currentUser
        if(currentUser!=null){
            Log.d(TAG,currentUser?.email)
        }
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
        loginViewModel?.cancelTimer()
        loginViewModel?.cancelAllJobs()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            try {
                startSweetProgress(this@LoginActivity, getString(R.string.seat_back_relax), getString(R.string.loading))
                loginViewModel?.loginWithGoogle(data)
            } catch (e: ApiException) {
                Log.w(TAG, "Google sign in failed", e)
            }
        } else {
            mCallbackManager?.onActivityResult(requestCode, resultCode, data)
        }
    }
}
