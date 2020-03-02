package com.krs.community.bkservice

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Window
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.github.squti.guru.Guru
import com.krs.community.R
import com.krs.community.app.AppDatabase.Companion.invoke
import com.krs.community.model.Member
import com.krs.community.utils.Coroutines
import com.krs.community.utils.Utility
import de.hdodenhof.circleimageview.CircleImageView

class MyCustomDialog : Activity() {
    private lateinit var member: Member
    override fun onCreate(savedInstanceState: Bundle?) {
        try {
            requestWindowFeature(Window.FEATURE_NO_TITLE)
            setFinishOnTouchOutside(true)
            super.onCreate(savedInstanceState)
            setContentView(R.layout.truecaller_bottom_sheet)
            val ivProfile: CircleImageView = findViewById(R.id.iv_profile)
            val ivGender = findViewById<ImageView>(R.id.iv_gender)
            val ivCancel = findViewById<ImageView>(R.id.iv_cancel)
            val llUser = findViewById<LinearLayout>(R.id.ll_user)
            val llHome = findViewById<LinearLayout>(R.id.ll_home)
            val llOffice = findViewById<LinearLayout>(R.id.ll_office)
            val llCall = findViewById<LinearLayout>(R.id.ll_call)
            val llEmail = findViewById<LinearLayout>(R.id.ll_email)
            val tvName = findViewById<TextView>(R.id.tv_name)
            val tvAge = findViewById<TextView>(R.id.tv_age)
            val tvArea = findViewById<TextView>(R.id.tv_area)
            val tvCity = findViewById<TextView>(R.id.tv_city)
            val tvWork = findViewById<TextView>(R.id.tv_work)

            member = intent.getSerializableExtra(getString(R.string.member)) as Member
            val database = invoke(this)

            Coroutines.io {
                val subCastId = member.subCastId
                val lastname = database.getLastNameDao().getLastName(subCastId.toInt())
                val cityId = member.cityId
                val city = database.getCityDao().getcityName(cityId.toInt())
                val LocationEnable = member.isLocationEnable
                val str = getString(R.string.base_url_thumb) + member.profilePic
                var age = 34
                try {
                    if (!member.birthDate.isNullOrEmpty()) {
                        val mdate = Utility.changeDateFormat(member.birthDate, Utility.yyyy_MM_dd, Utility.dd_MM_yyyy)
                        age = Utility.getAge(mdate, Utility.dd_MM_yyyy)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }

                Log.e("Fname===", "" + member.firstName)

                Coroutines.main {
                    tvName.text = member.firstName + " " + lastname
                    tvAge.text = " ($age)"
                    tvArea.text = member.area
                    tvCity.text = city

                    //   if (member.companyName.isNotEmpty()) {
                    tvWork.text = "Mamaji graphics" //member.companyName
                    //  }
                    Glide.with(this).load(str).apply(RequestOptions.circleCropTransform()).thumbnail(0.5f).into(ivProfile)
                    if (member.gender == "Male") {
                        ivGender.setBackgroundResource(R.drawable.male)
                    } else {
                        ivGender.setBackgroundResource(R.drawable.female)
                    }
                }
            }

            llCall.setOnClickListener {
                val intent = Intent(Intent.ACTION_DIAL)
                intent.data = Uri.parse("tel:" + member.phone)
                startActivity(intent)
            }
            llEmail.setOnClickListener { sendEmail() }
            ivCancel.setOnClickListener {
                onBackPressed()
                Guru.putBoolean(this@MyCustomDialog.getString(R.string.isdialogApi), true)
            }
            llUser.setOnClickListener {
                if (member.isLocationEnable.equals("1", ignoreCase = true)) {
                    if (member.userLat != null && !member.userLat.isEmpty() && member.userLng != null && !member.userLng.isEmpty()) {
                        val userLat = member.userLat
                        val userLng = member.userLng
                        Utility.showDirections(this@MyCustomDialog, userLat.toDouble(), userLng.toDouble(), "")
                    }
                }
            }
            llHome.setOnClickListener {
                if (member.homeLat != null && !member.homeLat.isEmpty() && member.homeLng != null && !member.homeLng.isEmpty()) {
                    val homeLat = member.homeLat
                    val homeLng = member.homeLng
                    Utility.showDirections(this@MyCustomDialog, homeLat.toDouble(), homeLng.toDouble(), member.address)
                }
            }
            llOffice.setOnClickListener {
                if (member.officeLat != null && !member.officeLat.isEmpty() && member.officeLng != null && !member.officeLng.isEmpty()) {
                    val OfficeLat = member.officeLat
                    val OfficeLng = member.officeLng
                    Utility.showDirections(this@MyCustomDialog, OfficeLat.toDouble(), OfficeLng.toDouble(), member.businessAddress)
                }
            }
        } catch (e: Exception) {
            Log.d("Exception", e.toString())
            e.printStackTrace()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        Guru.putBoolean(this@MyCustomDialog.getString(R.string.isdialogApi), true)
    }

    override fun onDestroy() {
        super.onDestroy()

        Guru.putBoolean(this@MyCustomDialog.getString(R.string.isdialogApi), true)

    }

    private fun sendEmail() {
        try {
            val emailIntent = Intent(Intent.ACTION_SEND)
            emailIntent.type = "plain/text"
            emailIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf(member.emailAddress))
            this.startActivity(Intent.createChooser(emailIntent, "Sending email..."))
        } catch (t: Throwable) {
            Toast.makeText(this, "Request failed try again: $t", Toast.LENGTH_LONG).show()
        }
    }
}