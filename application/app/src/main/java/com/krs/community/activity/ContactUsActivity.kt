package com.krs.community.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.ActivityInfo
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.util.Linkify
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade
import com.bumptech.glide.request.RequestOptions
import com.github.squti.guru.Guru
import com.krs.community.R
import com.krs.community.app.AppController
import com.krs.community.fragments.DashboardFragment
import com.krs.community.utils.Utility

class ContactUsActivity : AppCompatActivity() {

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_contact_us)

        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        val mApp = applicationContext as AppController
        mApp.firebaseAnalytics(this, ContactUsActivity::class.simpleName)
        mApp.facebookAnalytics(this, ContactUsActivity::class.simpleName)

        val card: CardView = findViewById(R.id.card)
        card.setBackgroundResource(R.drawable.shadow_white_round_border)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Utility.changeStatusbarColor(this, R.color.colorBG, false)
        }

        val ivProfile = findViewById<ImageView>(R.id.iv_profile)

        Glide.with(this).load(ContextCompat.getDrawable(this, R.drawable.mamaji))
                .thumbnail(0.5f)
                .transition(withCrossFade())
                .apply(RequestOptions.circleCropTransform())
                .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.ALL))
                .into(ivProfile)

        val imgCall = findViewById<ImageView>(R.id.img_call)
        imgCall.setOnClickListener { v: View? ->
            val intent = Intent(Intent.ACTION_DIAL)
            intent.data = Uri.parse("tel:" + getString(R.string.contact_number))
            startActivity(intent)
        }

        val imgFb = findViewById<ImageView>(R.id.img_fb)
        imgFb.setOnClickListener {
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(resources.getString(R.string.fb_url)))
            startActivity(browserIntent)
        }

        val imgTwitter = findViewById<ImageView>(R.id.img_twitter)
        val llScroll = findViewById<ScrollView>(R.id.ll_scroll)

        imgTwitter.setOnClickListener {
            // Utility.displaySnackBarWithBottomMargin(llScroll,"mobile: 9377133222")
        }

        val imgWhatsapp = findViewById<ImageView>(R.id.img_whatsapp)
        imgWhatsapp.setOnClickListener {
            Utility.sendWhatsAppMessage(this, getString(R.string.contact_number), "Hi")
        }

        val imgSkype = findViewById<ImageView>(R.id.img_skype)
        imgSkype.setOnClickListener {
            Utility.skype(getString(R.string.kunjanrshah), this)
        }

        val imgLinkedin = findViewById<ImageView>(R.id.img_linkedin)
        imgLinkedin.setOnClickListener {
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(resources.getString(R.string.linkedin_url)))
            startActivity(browserIntent)
        }

        val imgGmail = findViewById<ImageView>(R.id.img_gmail)
        imgGmail.setOnClickListener {
            val email = arrayOf(getString(R.string.dev_email))
            Utility.shareToGMail(this, email, getString(R.string.dev_gmail_subject), getString(R.string.dev_content))
        }

        val edtMessage = findViewById<EditText>(R.id.edt_message)
        val btnSend = findViewById<Button>(R.id.btn_send)
        btnSend.setOnClickListener {
            Utility.sendWhatsAppMessage(this, getString(R.string.contact_number), edtMessage.text.toString())
        }
        val tvName = findViewById<TextView>(R.id.tv_name)
        val tvLink = findViewById<TextView>(R.id.tv_link)
        val ivCancel = findViewById<ImageView>(R.id.iv_cancel)
        ivCancel.setOnClickListener { v: View? ->
            val id = Guru.getString(getString(R.string.member_id), "")
            if (id.isNullOrEmpty()) {
                finish()
            } else {
                Utility.movetoFragment(this, DashboardFragment())
            }
        }
        tvLink.text = getString(R.string.dev_link)
        Linkify.addLinks(tvLink, Linkify.WEB_URLS)
        Linkify.addLinks(tvLink, Linkify.ALL)
    }

}