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
import com.krs.community.utils.Utility
import com.krs.community.utils.Utility.checkPhoneCallPermission
import com.krs.community.utils.Utility.requestCallPermission


class ContactUsFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val layout = inflater.inflate(R.layout.fragment_contact_us, container, false)
        val card: CardView = layout.findViewById(R.id.card)
        card.setBackgroundResource(R.drawable.shadow_white_round_border)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Utility.changeStatusbarColor(activity, R.color.colorBG, false)
        }
        val ivProfile = layout.findViewById<ImageView>(R.id.iv_profile)
        val bmp = (activity!!.resources.getDrawable(R.drawable.user_profile) as BitmapDrawable).bitmap
        Glide.with(activity!!).load(Utility.getRoundedCornerBitmap(bmp, 80)).thumbnail(0.5f).into(ivProfile)
        val imgCall = layout.findViewById<ImageView>(R.id.img_call)
        Glide.with(activity!!).load(Utility.getRoundedCornerBitmap(getVectorDrawable(resources.getDrawable(R.drawable.phone_call)), 35)).thumbnail(0.5f).into(imgCall)
        imgCall.setOnClickListener { v: View? ->

            if (checkPhoneCallPermission(activity)) {
                val intent = Intent(Intent.ACTION_CALL)
                intent.data = Uri.parse("tel:" + getString(R.string.contact_number))
                startActivity(intent)
            } else {
                requestCallPermission(activity as AppCompatActivity?)
            }
        }
        val imgFb = layout.findViewById<ImageView>(R.id.img_fb)
        Glide.with(activity!!).load(Utility.getRoundedCornerBitmap(getVectorDrawable(resources.getDrawable(R.drawable.ic_fb)), 35)).thumbnail(0.5f).into(imgFb)

        imgFb.setOnClickListener {
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("http://www.google.com"))
            startActivity(browserIntent)
        }

        val imgTwitter = layout.findViewById<ImageView>(R.id.img_twitter)
        Glide.with(activity!!).load(Utility.getRoundedCornerBitmap(getVectorDrawable(resources.getDrawable(R.drawable.twitter)), 35)).thumbnail(0.5f).into(imgTwitter)
        val imgWhatsapp = layout.findViewById<ImageView>(R.id.img_whatsapp)
        Glide.with(activity!!).load(Utility.getRoundedCornerBitmap(getVectorDrawable(resources.getDrawable(R.drawable.whatsapp)), 35)).thumbnail(0.5f).into(imgWhatsapp)
        val imgSkype = layout.findViewById<ImageView>(R.id.img_skype)
        Glide.with(activity!!).load(Utility.getRoundedCornerBitmap(getVectorDrawable(resources.getDrawable(R.drawable.skype)), 35)).thumbnail(0.5f).into(imgSkype)
        val imgLinkedin = layout.findViewById<ImageView>(R.id.img_linkedin)
        Glide.with(activity!!).load(Utility.getRoundedCornerBitmap(getVectorDrawable(resources.getDrawable(R.drawable.linkedin)), 35)).thumbnail(0.5f).into(imgLinkedin)
        val imgGmail = layout.findViewById<ImageView>(R.id.img_gmail)
        Glide.with(activity!!).load(Utility.getRoundedCornerBitmap(getVectorDrawable(resources.getDrawable(R.drawable.gmail)), 35)).thumbnail(0.5f).into(imgGmail)
        val edt_message = layout.findViewById<EditText>(R.id.edt_message)
        val btn_send = layout.findViewById<Button>(R.id.btn_send)
        val tv_name = layout.findViewById<TextView>(R.id.tv_name)
        val tv_link = layout.findViewById<TextView>(R.id.tv_link)
        val iv_cancel = layout.findViewById<ImageView>(R.id.iv_cancel)
        iv_cancel.setOnClickListener { v: View? -> Utility.movetoFragment(activity, DashboardFragment()) }
        tv_link.text = "http://www.google.com"
        Linkify.addLinks(tv_link, Linkify.WEB_URLS)
        Linkify.addLinks(tv_link, Linkify.ALL)
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