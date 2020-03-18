package com.krs.community.fragments

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.util.Linkify
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.krs.community.R
import com.krs.community.activity.DashboardActivity
import com.krs.community.app.AppController
import com.krs.community.utils.Utility

class ContactUsFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val layout = inflater.inflate(R.layout.fragment_contact_us, container, false)

        val mApp = (activity as AppCompatActivity).applicationContext as AppController
        mApp.FirebaseAnalytics(context, ContactUsFragment::class.simpleName)
        mApp.FacebookAnalytics(context, ContactUsFragment::class.simpleName)

        val card: CardView = layout.findViewById(R.id.card)
        card.setBackgroundResource(R.drawable.shadow_white_round_border)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Utility.changeStatusbarColor(activity, R.color.colorBG, false)
        }

        val ivProfile = layout.findViewById<ImageView>(R.id.iv_profile)
        val bmp = (activity!!.resources.getDrawable(R.drawable.user_profile) as BitmapDrawable).bitmap
        Glide.with(activity!!).load(Utility.getRoundedCornerBitmap(bmp, 80)).thumbnail(0.5f).into(ivProfile)

        val imgCall = layout.findViewById<ImageView>(R.id.img_call)
        imgCall.setOnClickListener { v: View? ->
            //if (checkPhoneCallPermission(activity)) {
                val intent = Intent(Intent.ACTION_DIAL)
                intent.data = Uri.parse("tel:" + getString(R.string.contact_number))
                startActivity(intent)
            /*} else {
                requestPhoneCallPermission(activity as AppCompatActivity?)
            }*/
        }

        val imgFb = layout.findViewById<ImageView>(R.id.img_fb)
        imgFb.setOnClickListener {
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(resources.getString(R.string.fb_url)))
            startActivity(browserIntent)
        }

        val imgTwitter = layout.findViewById<ImageView>(R.id.img_twitter)
        imgTwitter.setOnClickListener {

        }

        val imgWhatsapp = layout.findViewById<ImageView>(R.id.img_whatsapp)
        imgWhatsapp.setOnClickListener {
            Utility.sendWhatsappMessage(activity!!,getString(R.string.contact_number),"Hi Kunjan")
        }

        val imgSkype = layout.findViewById<ImageView>(R.id.img_skype)
        imgSkype.setOnClickListener {
            Utility.skype(getString(R.string.kunjanrshah),activity)
        }

        val imgLinkedin = layout.findViewById<ImageView>(R.id.img_linkedin)
        imgLinkedin.setOnClickListener {
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(resources.getString(R.string.linkedin_url)))
            startActivity(browserIntent)
        }

        val imgGmail = layout.findViewById<ImageView>(R.id.img_gmail)
        imgGmail.setOnClickListener {
            val email = arrayOf(getString(R.string.dev_email))
            Utility.shareToGMail(activity,email,getString(R.string.dev_gmail_subject),getString(R.string.dev_content))
        }

        val edtMessage = layout.findViewById<EditText>(R.id.edt_message)
        val btnSend = layout.findViewById<Button>(R.id.btn_send)
        btnSend.setOnClickListener {
            Utility.sendWhatsappMessage(activity!!, getString(R.string.contact_number), edtMessage.text.toString())
        }
        val tvName = layout.findViewById<TextView>(R.id.tv_name)
        val tvLink = layout.findViewById<TextView>(R.id.tv_link)
        val ivCancel = layout.findViewById<ImageView>(R.id.iv_cancel)
        ivCancel.setOnClickListener { v: View? -> Utility.movetoFragment(activity, DashboardFragment()) }
        tvLink.text = getString(R.string.dev_link)
        Linkify.addLinks(tvLink, Linkify.WEB_URLS)
        Linkify.addLinks(tvLink, Linkify.ALL)
        return layout
    }



    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == Utility.CALL_PHONE_REQUEST) {
            val intent = Intent(Intent.ACTION_CALL)
            intent.data = Uri.parse("tel:" + getString(R.string.contact_number))
            startActivity(intent)
        }
    }

    private fun getVectorDrawable(drawable: Drawable): Bitmap? {
        return try {
            val bitmap: Bitmap
            bitmap = Bitmap.createBitmap(drawable.intrinsicWidth, drawable.intrinsicHeight, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)
            drawable.setBounds(0, 0, canvas.width, canvas.height)
            drawable.draw(canvas)
            bitmap
        } catch (e: OutOfMemoryError) { // Handle the error
            null
        }
    }

    override fun onResume() {
        super.onResume()
        (activity as AppCompatActivity?)!!.supportActionBar!!.hide()
        DashboardActivity.binding.space.visibility = View.GONE
    }

    override fun onStop() {
        super.onStop()
        (activity as AppCompatActivity?)!!.supportActionBar!!.show()
        DashboardActivity.binding.space.visibility = View.VISIBLE
    }
}