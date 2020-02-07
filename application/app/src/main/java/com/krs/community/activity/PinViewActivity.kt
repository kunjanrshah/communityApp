
package com.krs.community.activity

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.github.squti.guru.Guru
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.kevalpatel.passcodeview.PinView
import com.kevalpatel.passcodeview.authenticator.PasscodeViewPinAuthenticator
import com.kevalpatel.passcodeview.indicators.CircleIndicator
import com.kevalpatel.passcodeview.interfaces.AuthenticationListener
import com.kevalpatel.passcodeview.keys.KeyNamesBuilder
import com.kevalpatel.passcodeview.keys.RoundKey
import com.krs.community.R
import com.krs.community.fragments.FamilyDetailActivity
import com.krs.community.listeners.ILoginListener
import com.krs.community.listeners.InnerLogoutListner
import com.krs.community.model.LoginResponse
import com.krs.community.model.Member
import com.krs.community.responses.UserInnerLogoutResponse
import com.krs.community.utils.Utility.*
import com.krs.community.utils.snackbar
import com.krs.community.viewmodel.FamilyDetailViewModel
import com.krs.community.viewmodelfactory.FamilyDetailViewModelFactory
import org.json.JSONObject
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance

class PinViewActivity : AppCompatActivity(), KodeinAware , ILoginListener,InnerLogoutListner {
    private lateinit var mPinView: PinView
    private lateinit var member: Member
    private lateinit var imgView: ImageView
    private lateinit var relative:RelativeLayout
    private lateinit var familyDetailViewModel: FamilyDetailViewModel
    private val familyDetailViewModelFactory: FamilyDetailViewModelFactory by instance()
    override val kodein by kodein()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        familyDetailViewModel = ViewModelProviders.of(this, familyDetailViewModelFactory).get(FamilyDetailViewModel::class.java)
        familyDetailViewModel.mILoginListener=this
        familyDetailViewModel.innerLogoutListner=this

        setContentView(R.layout.activity_pinview)
        relative=findViewById(R.id.ll_parent)
        mPinView = findViewById(R.id.pattern_view)
        member = intent.getSerializableExtra(getString(R.string.member)) as Member
        imgView = findViewById(R.id.imageView)
        if (!member.profilePic.isEmpty()) {
            try {
                val str = resources.getString(R.string.base_url_thumb) + member.profilePic
                // String str="https://muslimghanchisamaj.in/uploads/users/thumb/6338dfa3c3f1c07515fcb2503e242975.jpg";
                Glide.with(this).load(str).apply(RequestOptions.circleCropTransform()).thumbnail(0.5f).into(imgView)
            } catch (e: Exception) {
                e.message
            }
        }
        //member.profilePassword="123456"
        val pass = member.profilePassword
        var correctPattern: IntArray? = null
        if (pass != null && !pass.isEmpty()) {
            correctPattern = IntArray(pass.length)
            for (i in 0 until pass.length) {
                try {
                    correctPattern[i] = pass[i].toString().toInt()
                } catch (nfe: NumberFormatException) {
                }
            }
        }
        //final int[] correctPattern = new int[]{1, 2, 3, 5,4,7};
        if (correctPattern != null) {
            mPinView.setPinAuthenticator(PasscodeViewPinAuthenticator(correctPattern))
        }
        mPinView.setKey(RoundKey.Builder(mPinView)
                .setKeyPadding(R.dimen.key_padding)
                .setKeyStrokeColorResource(R.color.white)
                .setKeyStrokeWidth(R.dimen.key_stroke_width)
                .setKeyTextColorResource(R.color.white)
                .setKeyTextSize(R.dimen.key_text_size))
        mPinView.setIndicator(CircleIndicator.Builder(mPinView)
                .setIndicatorRadius(R.dimen.indicator_radius)
                .setIndicatorFilledColorResource(R.color.white)
                .setIndicatorStrokeColorResource(R.color.white)
                .setIndicatorStrokeWidth(R.dimen.indicator_stroke_width))
        mPinView.setPinLength(PinView.DYNAMIC_PIN_LENGTH)
        mPinView.setKeyNames(KeyNamesBuilder()
                .setKeyOne(this, R.string.key_1)
                .setKeyTwo(this, R.string.key_2)
                .setKeyThree(this, R.string.key_3)
                .setKeyFour(this, R.string.key_4)
                .setKeyFive(this, R.string.key_5)
                .setKeySix(this, R.string.key_6)
                .setKeySeven(this, R.string.key_7)
                .setKeyEight(this, R.string.key_8)
                .setKeyNine(this, R.string.key_9)
                .setKeyZero(this, R.string.key_0))
        mPinView.title = "Enter the PIN"
        mPinView.setAuthenticationListener(object : AuthenticationListener {
            override fun onAuthenticationSuccessful() {
                if (member.loginStatus == 1) {
                    getMemberLogout()
                } else {
                    getMemberLogin()
                }
            }

            override fun onAuthenticationFailed() {
            }
        })
    }

    private fun getMemberLogin(){
        startSweetProgress(this,"Enter",getString(R.string.loading))
        val jsonObject= JSONObject()
        jsonObject.put(getString(R.string.user_id), Guru.getString(getString(R.string.user_id),""))
        jsonObject.put(getString(R.string.access_token), Guru.getString(getString(R.string.access_token),""))
        jsonObject.put(getString(R.string.id),member.id)
        jsonObject.put(getString(R.string.profile_password),member.profilePassword)
        val records=  JsonParser().parse(jsonObject.toString()) as JsonObject
        familyDetailViewModel.innerLogin(records)
    }

    private fun getMemberLogout(){
        startSweetProgress(this,"Exit",getString(R.string.loading))
        val jsonObject= JSONObject()
        jsonObject.put(getString(R.string.user_id), Guru.getString(getString(R.string.user_id),""))
        jsonObject.put(getString(R.string.access_token), Guru.getString(getString(R.string.access_token),""))
        jsonObject.put(getString(R.string.id),member.id)
        val records=  JsonParser().parse(jsonObject.toString()) as JsonObject
        familyDetailViewModel.getInnerLogout(records)
    }

    override fun userLogin(response: LoginResponse) {
        hideSweetProgress()
        if(response.success){
            if(response.data!=null){
                val json= Gson().toJson(response.data)
                Guru.putString(getString(R.string.loginMember), json)
                Guru.putString(getString(R.string.member_id),response.data.id)
                startActivity(Intent(this, DashboardActivity::class.java))
                finish()
                fade(this)
            }else{
                relative.snackbar(getString(R.string.went_wrong),Snackbar.LENGTH_LONG)
            }
        }else{
            relative.snackbar(response.message,Snackbar.LENGTH_LONG)
        }
    }

    override fun userLogout(response: UserInnerLogoutResponse) {
         hideSweetProgress()
         if(response.success){
            Guru.putString(getString(R.string.loginMember),"")
            Guru.putString(getString(R.string.member_id), "")
             val intent = Intent(applicationContext, FamilyDetailActivity::class.java)
             if(member.headId=="0"){
                 intent.putExtra(getString(R.string.id), member.id)
             }else{
                 intent.putExtra(getString(R.string.id), member.headId)
             }
             startActivity(intent)
             finish()
             fade(this)
         }else{

         }
    }

    override suspend fun getFailure(message: String) {
        hideSweetProgress()
        relative.snackbar(message,Snackbar.LENGTH_SHORT)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putIntArray(ARG_CURRENT_PIN, mPinView.currentTypedPin)
        super.onSaveInstanceState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        mPinView.currentTypedPin = savedInstanceState.getIntArray(ARG_CURRENT_PIN)
    }

    companion object {
        private const val ARG_CURRENT_PIN = "current_pin"
    }

}