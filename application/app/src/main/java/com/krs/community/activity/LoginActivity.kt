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
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.inputmethod.EditorInfo
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.Toolbar
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.GraphRequest
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.github.squti.guru.Guru
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.android.gms.common.api.ApiException
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.iid.FirebaseInstanceId
import com.krs.community.R
import com.krs.community.app.AppController
import com.krs.community.app.AppSignatureHashHelper
import com.krs.community.app.ConnectionLiveData.Companion.isNetworkConnected
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


    override val kodein by kodein()
    private val factory: LoginViewModelFactory by instance<LoginViewModelFactory>()
    private var smsReceiver: SMSReceiver? = null
    private val TAG = LoginActivity::class.java.simpleName
    private var mCallbackManager: CallbackManager? = null
    private var loginViewModel: LoginViewModel? = null
    private var ReceviedOTP: String? = null
    private lateinit var member: Member


    companion object {
        private val RC_SIGN_IN = 9001
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (isNetworkConnected(this)) {
            requestWindowFeature(Window.FEATURE_NO_TITLE)
            window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)

            loginViewModel = ViewModelProvider(this, factory).get(LoginViewModel::class.java)
            loginViewModel?.iLoginListener = this

            val appSignatureHashHelper = AppSignatureHashHelper(this)
            var hashkey: String = appSignatureHashHelper.appSignatures.get(0)
            hashkey = hashkey.replace("+", "%2B")
            Guru.putString(getString(R.string.hash_key), hashkey)
            Log.e(TAG, "hashcode: $hashkey")
            setScreenLayout()
        } else {
            setNoInternetLayout()
        }

        val mApp = applicationContext as AppController
        mApp.firebaseAnalytics(this@LoginActivity, LoginActivity.javaClass.simpleName)
        mApp.facebookAnalytics(this@LoginActivity, LoginActivity.javaClass.simpleName)

        AppController.mApplication.connectionLiveData.observeForever {
            it?.let {
                if (it) {
                    setScreenLayout()
                } else {
                    setNoInternetLayout()
                }
            }
        }
    }

    private fun setNoInternetLayout() {
        setContentView(R.layout.no_internet_layout)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        toolbar.setTitleTextColor(resources.getColor(R.color.colorPrimary))
        setSupportActionBar(toolbar)
        supportActionBar!!.title = resources.getString(R.string.app_name)
        val anim = AlphaAnimation(0f, 1f)
        anim.duration = 6000
        anim.repeatMode = AlphaAnimation.RESTART
        anim.repeatCount = Animation.INFINITE
        val imageView = findViewById<ImageView>(R.id.no_internet_image)
        imageView.animation = anim
        val retryButton = findViewById<AppCompatButton>(R.id.retry_button)
        retryButton.setOnClickListener { v: View? ->
            if (isNetworkConnected(this)) {
                setScreenLayout()
            }
        }
    }

    private fun setScreenLayout() {
        if (isNetworkConnected(this)) {
            val binding = DataBindingUtil.setContentView<ActivityLoginwithBinding>(this@LoginActivity, R.layout.activity_loginwith)
            binding.lifecycleOwner = this
            binding.loginViewModel = loginViewModel

            mCallbackManager = CallbackManager.Factory.create()
            loginViewModel?.mAuth = FirebaseAuth.getInstance()
            var sourcestr = resources.getString(R.string.do_you_have_an_account_register_now)
            sourcestr = sourcestr + "<b>" + " " + getString(R.string.register_now) + "</b>"
            have_acc.text = Html.fromHtml(sourcestr)

            Guru.putBoolean(getString(R.string.isdialogshow), true)
            FirebaseInstanceId.getInstance().instanceId.addOnSuccessListener(this@LoginActivity) { instanceIdResult ->
                val newToken = instanceIdResult.token
                Log.e("newToken", newToken)
                Guru.putString(AppConstants.DEVICE_TOKEN, newToken)
            }

            binding.edtMobile.addTextChangedListener(object : TextWatcher {
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

            binding.edtMobile.setOnEditorActionListener { v, actionId, event ->
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    btnContinue.performClick()
                    true
                }
                false
            }

            binding.imgCancel.setOnClickListener {
                card_view_otp.visibility = View.GONE
                card_view_mobile.visibility = View.VISIBLE
                loginViewModel?.cancelTimer()
            }

            /*binding.fabLogin.setOnClickListener {

                val lstNumber = ArrayList<String>()
                val lstCarrier = ArrayList<String>()

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
                    if (checkReadPhoneStatePermission(this)) {
                        try {
                            val subscriptionManager: SubscriptionManager = SubscriptionManager.from(applicationContext)
                            val subsInfoList: List<SubscriptionInfo> = subscriptionManager.activeSubscriptionInfoList

                            for (subscriptionInfo in subsInfoList) {
                                var number: String = subscriptionInfo.number
                                val carrier: String = subscriptionInfo.carrierName.toString()
                                if (number.isNotEmpty()) {
                                    if (number.length > 10) {
                                        number = number.substring((number.length - 10), number.length)
                                    }
                                    lstNumber.add(number)
                                    lstCarrier.add(carrier)
                                }
                            }
                        } catch (e: java.lang.Exception) {
                            Toast.makeText(this, "Feature not supported!", Toast.LENGTH_SHORT).show()
                            Snackbar.make(findViewById(R.id.ll_login), "Feature not supported!", Snackbar.LENGTH_LONG).show()
                        }
                    } else {
                        requestPermissions(this@LoginActivity)
                    }
                }

                if (lstNumber.size == 0) {
                    displaySnackBarWithBottomMargin(binding.llLogin, "SIMCard not found!")
                } else if (lstNumber.size == 1) {
                    startSweetProgress(this@LoginActivity, "Login with ${lstNumber[0]}", getString(R.string.loading))
                    loginViewModel?.loginWithMobile(lstNumber[0])
                } else if (lstNumber.size > 1) {
                    TTFancyGifDialog.Builder(this@LoginActivity)
                            .setTitle("Choose SIM")
                            .setMessage("Family Head Device Login")
                            .setPositiveBtnText(lstCarrier[0])
                            .setPositiveBtnBackground("#22b573")
                            .setNegativeBtnText(lstCarrier[1])
                            .setNegativeBtnBackground("#c1272d")
                            .setGifResource(R.drawable.gif_dialog)
                            .isCancellable(true)
                            .OnPositiveClicked {
                                startSweetProgress(this@LoginActivity, "Login with ${lstNumber[0]}", getString(R.string.loading))
                                loginViewModel?.loginWithMobile(lstNumber.get(0))
                            }
                            .OnNegativeClicked {
                                startSweetProgress(this@LoginActivity, "Login with ${lstNumber[1]}", getString(R.string.loading))
                                loginViewModel?.loginWithMobile(lstNumber.get(1))
                            }
                            .build()
                }
            }*/

            binding.btnLoginFb.setOnClickListener { v ->

                LoginManager.getInstance().logInWithReadPermissions(this@LoginActivity, Arrays.asList("email", "public_profile"))
                LoginManager.getInstance().registerCallback(mCallbackManager!!, object : FacebookCallback<LoginResult> {
                    override fun onSuccess(loginResult: LoginResult) {
                        showProgressDialog(this@LoginActivity)
                        Log.d(TAG, "facebook:onSuccess:$loginResult")
                        val request = GraphRequest.newMeRequest(loginResult.accessToken) { `object`, response ->
                            Log.v(TAG, response.toString())
                            try {
                                val email = `object`.getString("email")

                                Log.e("email", "" + email)
                                //val url = `object`.getJSONObject("picture").getJSONObject("data").getString("url")
                                loginViewModel?.loginWithFB(email)
                            } catch (e: JSONException) {
                                hideProgressDialog()
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
            binding.btnLoginGoogle.setOnClickListener { v ->
                val signInIntent = AppController.mApplication.mGoogleSignInClient.signInIntent
                startActivityForResult(signInIntent, RC_SIGN_IN)
            }

            binding.haveAcc.setOnClickListener { v ->
                val mIntent = Intent(this@LoginActivity, RegisterActivty::class.java)
                startActivity(mIntent)
                //  fade(this)
            }
            binding.tvResend.setOnClickListener {
                ReceviedOTP = ""
                btnContinue.performClick()
            }

            binding.ivHelp.setOnClickListener {
                val intent = Intent(this, ContactUsActivity::class.java)
                startActivity(intent)
            }

            binding.btnContinue.setOnClickListener { v ->

                if (!ReceviedOTP.isNullOrEmpty() && ReceviedOTP == squareField.text.toString()) {
                    goToFamilyDetailScreen()
                    return@setOnClickListener
                }

                if (tv_resend.isEnabled) {
                    val mobilenumber = loginViewModel?.mobile
                    if (mobilenumber!!.isEmpty()) {
                        edt_mobile.error = getString(R.string.phoneNumber)
                        edt_mobile.requestFocus()
                        return@setOnClickListener
                    }

                    if (mobilenumber.length < 10 || !isValidMobile(mobilenumber)) {
                        edt_mobile.error = getString(R.string.pleaseEnterValidPhone)
                        edt_mobile.requestFocus()
                        return@setOnClickListener
                    }

                    if (isNetworkConnected(this)) {
                        startSweetProgress(this, getString(R.string.otp_send), getString(R.string.loading))
                        loginViewModel?.loginWithOTP()
                    }
                }
            }

            loginViewModel?.stopTime?.observe(this, androidx.lifecycle.Observer { stopTIme ->
                if (stopTIme == true) {
                    tv_resend.isClickable = true
                    tv_resend.isEnabled = true
                    tv_resend.setTextColor(resources.getColor(R.color.black1))
                }
            })

            loginViewModel?.status?.observe(this, androidx.lifecycle.Observer { status ->
                if (status == false) {
                    loginViewModel?.status?.value = null
                    hideProgressDialog()
                    hideSweetProgress()
                    Snackbar.make(findViewById(R.id.ll_login), getString(R.string.Authenticationfailed), Snackbar.LENGTH_LONG).show()
                }
            })
            requestPermissions(this@LoginActivity)
        }
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
                tv_resend.isClickable = false
                tv_resend.isEnabled = false
                tv_resend.setTextColor(resources.getColor(R.color.light_gray))
                loginViewModel?.cancelTimer()
                loginViewModel?.startTimer()
                //toast("API successfully started")
                Log.d(TAG, "API successfully started")
            }

            task.addOnFailureListener {
                // Fail to start API
                tv_resend.isEnabled = true
                tv_resend.isClickable = true
                tv_resend.setTextColor(resources.getColor(R.color.black1))
                Log.d(TAG, "Fail to start API")
                //toast("Fail to start API")
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            1 -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                } else {
                    Toast.makeText(this@LoginActivity, getString(R.string.SmsExternalStorage), Toast.LENGTH_SHORT).show()
                }
                return
            }
        }
    }

    override fun onOTPReceived(otp: String?) {
        val otp1 = otp?.substring(31, 35)
        Log.i(TAG, "OTP Received: $otp1")
        squareField.setText(otp1)
        if (smsReceiver != null) {
            unregisterReceiver(smsReceiver)
            smsReceiver = null
        }
        if (ReceviedOTP != null) {
            if (ReceviedOTP.equals(otp1)) {
                goToFamilyDetailScreen()
            }
        }
    }

    override fun onOTPTimeOut() {
        Log.d(TAG, "OTP Time out")
        //toast("OTP Time out")
    }

    override fun userLogin(response: LoginResponse, isForgot: Boolean) {
        hideSweetProgress()
        hideProgressDialog()
        Log.d(TAG, "login data: $response")

        member = response.data
        if (!response.otp.isNullOrBlank()) {
            if (response.otp != "FAILED") {
                card_view_mobile.visibility = View.GONE
                card_view_otp.visibility = View.VISIBLE
                tv_otp.text = loginViewModel?.mobile
                startSMSListener()
                ReceviedOTP = response.otp
            } else {
                Snackbar.make(findViewById(R.id.ll_login), "OTP sending fail!", Snackbar.LENGTH_LONG).show()
            }
        } else {
            if (response.success) {
                goToFamilyDetailScreen()
            } else {
                Snackbar.make(findViewById(R.id.ll_login), response.message, Snackbar.LENGTH_LONG).show()
            }
        }
    }

    private fun goToFamilyDetailScreen() {
        Guru.putString(getString(R.string.user_id), member.id)
        Guru.putString(getString(R.string.access_token), member.accessToken)
        val intent = Intent(applicationContext, FamilyDetailActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        intent.putExtra(getString(R.string.id), member.id)
        startActivity(intent)
        finish()
        //   fade(this)
    }

    override fun onOTPReceivedError(error: String?) {
        error?.let { toast(it) }
    }

    override suspend fun getFailure(message: String) {
        loginViewModel!!.status.value = false
        Log.d(TAG, "login data: $message")
    }

    override fun onResume() {
        super.onResume()
        NotificationUtils.clearNotifications(applicationContext)
    }

    override fun onStart() {
        super.onStart()

        val currentUser = loginViewModel?.mAuth?.currentUser
        if (currentUser != null) {
            Log.d(TAG, currentUser.email)
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

        Log.e(TAG, "Google sign requestCode: $requestCode")

        if (requestCode == RC_SIGN_IN && resultCode != 0) {
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
