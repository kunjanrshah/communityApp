package com.krs.community.viewmodel

import android.app.Activity
import android.app.Application
import android.content.Intent
import androidx.lifecycle.AndroidViewModel
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.krs.community.R
import com.krs.community.activity.LoginActivity
import com.krs.community.app.ConnectionLiveData.Companion.isNetworkConnected
import com.krs.community.listeners.IRegisterListener
import com.krs.community.repositories.RegisterRepository
import com.krs.community.utils.ApiException
import com.krs.community.utils.NoInternetException
import com.krs.community.utils.Utility
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import org.json.JSONObject


class RegisterViewModel(
        private val registerRepository: RegisterRepository,
        var app: Application) : AndroidViewModel(app) {

    var fname: String? = null
    var father: String? = null
    var bdate: String? = null
    var email: String? = null
    var pass: String? = null
    var cpass: String? = null
    var gender: String? = null
    var address: String? = null
    var mobile: String? = null
    var lastnameId: Int? = null
    var nativeId: Int? = null
    var stateId: Int? = null
    var countryCode: String? = null
    var cityId: Int? = null
    var profilePic: String = ""
    var localCommId: Int? = null
    var subCommId: Int? = null
    var maritalStatus: String? = null
    var iRegisterListener: IRegisterListener? = null
    var TAG: String = RegisterViewModel::class.java.simpleName

    private var jobStates: CompletableJob? = null
    private var jobCities: CompletableJob? = null
    private var jobLastname: CompletableJob? = null
    private var jobSubcommunity: CompletableJob? = null
    private var jobLocalcommunity: CompletableJob? = null
    private var jobRegistration: CompletableJob? = null

    fun cancelAllJobs() {
        jobStates?.cancel()
        jobCities?.cancel()
        jobLastname?.cancel()
        jobSubcommunity?.cancel()
        jobLocalcommunity?.cancel()
        jobRegistration?.cancel()
    }

    fun getUserRegistration(isLogin: Boolean) {
        // val register: AppConstants.UserRegister = AppConstants.UserRegister()

        if (fname.isNullOrBlank()) {
            iRegisterListener?.getRegisterFailure(app.applicationContext.getString(R.string.fname), 1)
            return
        }

        if (father.isNullOrBlank()) {
            iRegisterListener?.getRegisterFailure(app.applicationContext.getString(R.string.father), 13)
            return
        }

        if (bdate.isNullOrBlank()) {
            iRegisterListener?.getRegisterFailure(app.applicationContext.getString(R.string.birthdate), 14)
            return
        }

        if (nativeId == null) {
            iRegisterListener?.getRegisterFailure(app.applicationContext.getString(R.string.enter_native), 15)
            return
        }

        if (lastnameId == null) {
            iRegisterListener?.getRegisterFailure(app.applicationContext.getString(R.string.lastnameId), 2)
            return
        }

        if (email.isNullOrBlank() || !Utility.isEmailValid(email)) {
            iRegisterListener?.getRegisterFailure(app.applicationContext.getString(R.string.emailstr), 3)
            return
        }

        if (gender.isNullOrBlank()) {
            iRegisterListener?.getRegisterFailure(app.applicationContext.getString(R.string.genderstr), 4)
            return
        }

        /*if(country_code.isNullOrBlank()){
        iRegisterListener?.getRegisterFailure(app.applicationContext.getString(R.string.select_country_code),4)
            return
        }*/

        if (mobile.isNullOrBlank() || mobile?.length != 10) {
            iRegisterListener?.getRegisterFailure(app.applicationContext.getString(R.string.mobilestr), 5)
            return
        }
        if (pass.isNullOrBlank()) {
            iRegisterListener?.getRegisterFailure(app.applicationContext.getString(R.string.pass), 6)
            return
        }

        if (pass?.length!! < 6) {
            iRegisterListener?.getRegisterFailure(app.applicationContext.getString(R.string.passsecond), 6)
            return
        }

        if (cpass.isNullOrBlank()) {
            iRegisterListener?.getRegisterFailure(app.applicationContext.getString(R.string.cpass), 7)
            return
        }

        if (cpass?.length!! < 6) {
            iRegisterListener?.getRegisterFailure(app.applicationContext.getString(R.string.cpasssecond), 7)

            return
        }

        if (!pass.equals(cpass)) {
            iRegisterListener?.getRegisterFailure(app.applicationContext.getString(R.string.passequals), 7)
            return
        }

        if (address.isNullOrBlank()) {
            iRegisterListener?.getRegisterFailure(app.applicationContext.getString(R.string.addressstr), 8)
            return
        }

        if (stateId == null) {
            iRegisterListener?.getRegisterFailure(app.applicationContext.getString(R.string.stateis), 9)
            return
        }

        if (cityId == null) {
            iRegisterListener?.getRegisterFailure(app.applicationContext.getString(R.string.cityid), 10)
            return
        }

        if (subCommId == null) {
            iRegisterListener?.getRegisterFailure(app.applicationContext.getString(R.string.subcommid), 11)
            return
        }

        if (localCommId == null) {
            iRegisterListener?.getRegisterFailure(app.applicationContext.getString(R.string.localcommid), 12)
            return
        }

        if (maritalStatus.isNullOrEmpty()) {
            iRegisterListener?.getRegisterFailure(app.applicationContext.getString(R.string.marital_status), 16)
            return
        }

        /* register.first_name = fname
         register.nativePlaceId = nativeId?.toString()
         register.father = father
         register.setBirthdate(Utility.changeDateFormat(bdate, Utility.dd_MM_yyyy, Utility.yyyy_MM_dd))
         register.sub_cast_id = lastnameId.toString()
         register.email_address = email
         register.mobile = mobile
         register.gender = gender
         register.profile_password = pass
         register.address = address
         register.state_id = stateId.toString()
         register.city_id = cityId.toString()
         register.sub_community_id = subCommId.toString()
         register.local_community_id = localCommId.toString()
         register.profilePic = profilePic*/
        // register.headId="0"

        val jsonObject = JSONObject()
        jsonObject.put(app.getString(R.string.first_name), fname)
        jsonObject.put(app.getString(R.string.father_name), father)
        jsonObject.put(app.getString(R.string.birth_date), Utility.changeDateFormat(bdate, Utility.dd_MM_yyyy, Utility.yyyy_MM_dd))
        jsonObject.put(app.getString(R.string.sub_cast_id), lastnameId.toString())
        jsonObject.put(app.getString(R.string.email_address), email)
        jsonObject.put(app.getString(R.string.mobile), mobile)
        jsonObject.put(app.getString(R.string.gender), gender)
        jsonObject.put(app.getString(R.string.plain_password), pass)
        jsonObject.put(app.getString(R.string.address), address)
        jsonObject.put(app.getString(R.string.state_id), stateId.toString())
        jsonObject.put(app.getString(R.string.city_id), cityId.toString())
        jsonObject.put(app.getString(R.string.native_place_id), nativeId?.toString())
        jsonObject.put(app.getString(R.string.sub_community_id), subCommId.toString())
        jsonObject.put(app.getString(R.string.local_community_id), localCommId.toString())
        jsonObject.put(app.getString(R.string.marital_status), maritalStatus)

        if (profilePic.isNotEmpty()) {
            jsonObject.put(app.getString(R.string.profile_pic), profilePic)
        }
        if (!isLogin) {
            jsonObject.put(app.getString(R.string.is_admin), "1")
        }
        val updated = JsonParser().parse(jsonObject.toString()) as JsonObject

        if (isNetworkConnected(app.applicationContext)) {
            jobRegistration = Job()
            jobRegistration.let { thejob ->
                CoroutineScope(IO + thejob!!).launch {
                    try {
                        val response = registerRepository.getUserRegister(updated)

                        response.let {
                            withContext(Main) {
                                if (response.success.toString().toLowerCase().equals("success")) {
                                    iRegisterListener?.getRegisterSuccess(response)
                                } else {
                                    iRegisterListener?.getRegisterFailure(response.message, 0)
                                }
                                thejob.complete()
                            }
                            return@launch
                        }
                    } catch (e: ApiException) {
                        e.message?.let { iRegisterListener?.getFailure(it) }
                    } catch (e: NoInternetException) {
                        e.message?.let { iRegisterListener?.getFailure(it) }
                    } catch (e: Exception) {
                        e.message?.let { iRegisterListener?.getFailure(it) }
                    }
                    thejob.complete()
                }
            }
        }
    }

    fun onTextAlreadyClicked(activity: Activity) {
        val mIntent = Intent(activity, LoginActivity::class.java)
        mIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        activity.startActivity(mIntent)
        activity.finish()
        //Utility.fade(activity)
    }

    fun onHowRegisterClicked(activity: Activity) {
        Utility.watchYoutubeVideo(activity, activity.resources.getString(R.string.login_1))
    }

}
