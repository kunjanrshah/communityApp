package com.krs.community.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.OvershootInterpolator
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import com.flaviofaria.kenburnsview.KenBurnsView
import com.flaviofaria.kenburnsview.RandomTransitionGenerator
import com.github.squti.guru.Guru
import com.krs.community.BuildConfig
import com.krs.community.R
import com.krs.community.activity.DashboardActivity
import com.krs.community.activity.LoginActivity
import com.krs.community.activity.RegisterActivty
import com.krs.community.activity.SplashActivity
import com.krs.community.adapter.PolicyAdapter
import com.krs.community.app.AppController
import com.krs.community.app.ConnectionLiveData.Companion.isNetworkConnected
import com.krs.community.fragments.FamilyDetailActivity
import com.krs.community.utils.Utility
import com.orhanobut.dialogplus.DialogPlus

class SplashActivity : AppCompatActivity() {
    private var kbv: KenBurnsView? = null
    private var darkoverlay: View? = null
    private var llSpinner: View? = null
    private var imgLogo: ImageView? = null
    private var llLogo: LinearLayout? = null
    private var btnLogin: Button? = null
    private var btnRegister: Button? = null
    private var splanguage: Spinner? = null
    private var dm: DisplayMetrics? = null
    private var isLogin = false
    private var isRegister = false


    companion object {
        var polictyDialog: DialogPlus? = null
    }


    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val policy = Guru.getBoolean(getString(R.string.policy), false)
        if (!policy) {
            val adapter = PolicyAdapter(this, "splash")
            polictyDialog = DialogPlus.newDialog(this)
                    .setAdapter(adapter)
                    .setGravity(Gravity.CENTER)
                    .setOnDismissListener {
                        onCreateMethod()
                    }
                    .setCancelable(false)
                    .setExpanded(false, 800)
                    .setContentBackgroundResource(R.drawable.popup_corner)
                    .create()
            polictyDialog?.show()
        } else {
            onCreateMethod()
        }
    }

    private fun onCreateMethod() {
        Utility.getHashKey(this)
        if (isNetworkConnected(this)) {
            setScreenLayout()
        } else {
            setNoInternetLayout()
        }
        val mApp = applicationContext as AppController
        mApp.firebaseAnalytics(this@SplashActivity, SplashActivity::class.java.simpleName)
        mApp.facebookAnalytics(this@SplashActivity, SplashActivity::class.java.simpleName)
    }


    private fun setNoInternetLayout() {
        setContentView(R.layout.no_internet_layout)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.colorPrimary))
        setSupportActionBar(toolbar)
        supportActionBar!!.title = resources.getString(R.string.app_name)
        val anim = AlphaAnimation(0f, 1f)
        anim.duration = 6000
        anim.repeatMode = AlphaAnimation.RESTART
        anim.repeatCount = Animation.INFINITE
        val imageView = findViewById<AppCompatImageView>(R.id.no_internet_image)
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
            setContentView(R.layout.activity_splash)
            MemoryAllocation()
            setAnimation()
            val languages = resources.getStringArray(R.array.languages)
            val aa = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, languages)
            splanguage!!.adapter = aa
            splanguage!!.setSelection(1)
            btnLogin!!.setOnTouchListener { v: View?, event: MotionEvent ->
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        btnLogin!!.setBackgroundColor(ContextCompat.getColor(this, R.color.colorDark))
                        return@setOnTouchListener true
                    }
                    MotionEvent.ACTION_UP -> {
                        btnLogin!!.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimary))
                        btnLogin!!.performClick()
                        return@setOnTouchListener true
                    }
                    else -> return@setOnTouchListener false
                }
            }
            btnRegister!!.setOnTouchListener { v: View?, event: MotionEvent ->
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        btnRegister!!.setBackgroundColor(ContextCompat.getColor(this, R.color.gray_btn_bg_pressed_color))
                        return@setOnTouchListener true
                    }
                    MotionEvent.ACTION_UP -> {
                        btnRegister!!.setBackgroundColor(ContextCompat.getColor(this, R.color.white))
                        btnRegister!!.performClick()
                        return@setOnTouchListener true
                    }
                    else -> return@setOnTouchListener false
                }
            }
            btnLogin!!.setOnClickListener { v: View? ->
                if (!isLogin) {
                    isLogin = true
                    val mIntent = Intent(this@SplashActivity, LoginActivity::class.java)
                    startActivity(mIntent)

                    //  Utility.fade(this);
                }
            }
            btnRegister!!.setOnClickListener { v: View? ->
                if (!isRegister) {
                    isRegister = true
                    val mIntent = Intent(this@SplashActivity, RegisterActivty::class.java)
                    startActivity(mIntent)

                    //    Utility.fade(this);
                }
            }

            /*splanguage.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    Utility.changeLang(SplashActivity.this, splanguage.getSelectedItem().toString());
                    btnLogin.setText(getResources().getString(R.string.login));
                    btnRegister.setText(getResources().getString(R.string.register));
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });*/
        }
    }

    @SuppressLint("NewApi")
    override fun onStart() {
        super.onStart()
        val userId = Guru.getString(getString(R.string.user_id), "")
        val member = Guru.getString(getString(R.string.loginMember), "")

        if ((userId == null || userId.isEmpty()) && (member == null || member.isEmpty())) {
        } else if (member == null || member.isEmpty()) {
            val headId = Guru.getString(getString(R.string.head_id), "")
            val mIntent = Intent(this@SplashActivity, FamilyDetailActivity::class.java)
            mIntent.putExtra(getString(R.string.id), headId)
            startActivity(mIntent)
            finish()
        } else {
            val mIntent = Intent(this@SplashActivity, DashboardActivity::class.java)
            mIntent.putExtra(getString(R.string.user_id), userId)
            startActivity(mIntent)
            finish()
        }
    }

    override fun onResume() {
        super.onResume()
        /*if (splanguage != null) {
            String locale = Guru.getString(getResources().getString(R.string.locale_sp), getResources().getString(R.string._english));
            if (locale.equalsIgnoreCase(getResources().getString(R.string._gujarati))) {
                splanguage.setSelection(2);
            } else if (locale.equalsIgnoreCase(getResources().getString(R.string._hindi))) {
                splanguage.setSelection(3);
            } else {
                splanguage.setSelection(1);
            }
        }*/
    }

    override fun onStop() {
        super.onStop()
        isLogin = false
        isRegister = false
    }

    @SuppressLint("NewApi")
    private fun setAnimation() {
        val generator = RandomTransitionGenerator(19000, AccelerateDecelerateInterpolator())
        kbv!!.setTransitionGenerator(generator)
        if (BuildConfig.FLAVOR == "medk") {
            imgLogo!!.visibility = View.VISIBLE
            imgLogo!!.animate().setStartDelay(3000).setDuration(2000).alpha(1f).start()
        } else {
            llLogo!!.visibility = View.VISIBLE
            llLogo!!.animate().setStartDelay(3000).setDuration(2000).alpha(1f).start()
        }
        darkoverlay!!.animate().setStartDelay(3000).setDuration(3000).alpha(0.6f).start()
        llSpinner!!.animate().translationY(dm!!.heightPixels.toFloat()).setStartDelay(0).setDuration(0).start()
        llSpinner!!.animate().translationY(0f).setDuration(2000).alpha(1f).setStartDelay(5000).start()
        btnLogin!!.animate().translationX(dm!!.widthPixels + btnLogin!!.measuredWidth.toFloat()).setDuration(0).setStartDelay(0).start()
        btnLogin!!.animate().translationX(0f).setStartDelay(5500).setDuration(2000).setInterpolator(OvershootInterpolator()).start()
        btnRegister!!.animate().translationX(dm!!.widthPixels + btnRegister!!.measuredWidth.toFloat()).setDuration(0).setStartDelay(0).start()
        btnRegister!!.animate().translationX(0f).setStartDelay(5500).setDuration(2000).setInterpolator(OvershootInterpolator()).start()
    }

    private fun MemoryAllocation() {
        imgLogo = findViewById(R.id.iv_logo)
        llLogo = findViewById(R.id.ll_logo)
        dm = resources.displayMetrics
        kbv = findViewById(R.id.fragmentloginKenBurnsView1)
        darkoverlay = findViewById(R.id.fragmentloginView1)
        llSpinner = findViewById(R.id.ll_spinner)
        splanguage = findViewById(R.id.splanguage)
        btnLogin = findViewById(R.id.btn_login)
        btnLogin?.setTag(0)
        btnRegister = findViewById(R.id.btn_register1)
        btnRegister?.setTag(0)
    }
}