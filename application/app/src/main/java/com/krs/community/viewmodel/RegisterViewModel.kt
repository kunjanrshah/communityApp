package com.krs.community.viewmodel

import android.app.Activity
import android.app.Application
import android.content.Intent
import androidx.lifecycle.AndroidViewModel
import com.krs.community.R
import com.krs.community.activity.LoginActivity
import com.krs.community.app.ConnectionLiveData.Companion.isNetworkConnected
import com.krs.community.listeners.IRegisterListener
import com.krs.community.model.RegisterModel
import com.krs.community.repositories.RegisterRepository
import com.krs.community.type.RegisterInput
import com.krs.community.utils.ApiException
import com.krs.community.utils.NoInternetException
import com.krs.community.utils.Utility
import kotlinx.coroutines.CompletableJob
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File


class RegisterViewModel(
    private val registerRepository: RegisterRepository,
    var app: Application
) : AndroidViewModel(app) {

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
    var profilePic: File? = null
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

    fun getUserRegistration(isAdmin: Boolean) {
        // val register: AppConstants.UserRegister = AppConstants.UserRegister()

        if (fname.isNullOrBlank()) {
            iRegisterListener?.getRegisterFailure(
                app.applicationContext.getString(R.string.fname),
                1
            )
            return
        }

//        if (father.isNullOrBlank()) {
//            iRegisterListener?.getRegisterFailure(
//                app.applicationContext.getString(R.string.father),
//                13
//            )
//            return
//        }

//        if (bdate.isNullOrBlank()) {
//            iRegisterListener?.getRegisterFailure(
//                app.applicationContext.getString(R.string.birthdate),
//                14
//            )
//            return
//        }

//        if (nativeId == null) {
//            iRegisterListener?.getRegisterFailure(
//                app.applicationContext.getString(R.string.enter_native),
//                15
//            )
//            return
//        }

        if (lastnameId == null) {
            iRegisterListener?.getRegisterFailure(
                app.applicationContext.getString(R.string.lastnameId),
                2
            )
            return
        }

        if (email.isNullOrBlank() || !Utility.isEmailValid(email)) {
            iRegisterListener?.getRegisterFailure(
                app.applicationContext.getString(R.string.emailstr),
                3
            )
            return
        }

        if (gender.isNullOrBlank()) {
            iRegisterListener?.getRegisterFailure(
                app.applicationContext.getString(R.string.genderstr),
                4
            )
            return
        }

        if (mobile.isNullOrBlank() || mobile?.length != 10) {
            iRegisterListener?.getRegisterFailure(
                app.applicationContext.getString(R.string.mobilestr),
                5
            )
            return
        }
        if (pass.isNullOrBlank()) {
            iRegisterListener?.getRegisterFailure(
                app.applicationContext.getString(R.string.pass),
                6
            )
            return
        }

        if (pass?.length!! < 6) {
            iRegisterListener?.getRegisterFailure(
                app.applicationContext.getString(R.string.passsecond),
                6
            )
            return
        }

        if (cpass.isNullOrBlank()) {
            iRegisterListener?.getRegisterFailure(
                app.applicationContext.getString(R.string.cpass),
                7
            )
            return
        }

        if (cpass?.length!! < 6) {
            iRegisterListener?.getRegisterFailure(
                app.applicationContext.getString(R.string.cpasssecond),
                7
            )

            return
        }

        if (!pass.equals(cpass)) {
            iRegisterListener?.getRegisterFailure(
                app.applicationContext.getString(R.string.passequals),
                7
            )
            return
        }

        if (address.isNullOrBlank()) {
            iRegisterListener?.getRegisterFailure(
                app.applicationContext.getString(R.string.addressstr),
                8
            )
            return
        }

        if (stateId == null) {
            iRegisterListener?.getRegisterFailure(
                app.applicationContext.getString(R.string.stateis),
                9
            )
            return
        }

        if (cityId == null) {
            iRegisterListener?.getRegisterFailure(
                app.applicationContext.getString(R.string.cityid),
                10
            )
            return
        }

        if (subCommId == null) {
            iRegisterListener?.getRegisterFailure(
                app.applicationContext.getString(R.string.subcommid),
                11
            )
            return
        }

        if (localCommId == null) {
            iRegisterListener?.getRegisterFailure(
                app.applicationContext.getString(R.string.localcommid),
                12
            )
            return
        }

//        if (maritalStatus.isNullOrEmpty()) {
//            iRegisterListener?.getRegisterFailure(
//                app.applicationContext.getString(R.string.marital_status),
//                16
//            )
//            return
//        }

//        val jsonObject = JSONObject()
//        jsonObject.put(app.getString(R.string.first_name), fname)
//        jsonObject.put(app.getString(R.string.father_name), father)
//        jsonObject.put(app.getString(R.string.birth_date), Utility.changeDateFormat(bdate, Utility.dd_MM_yyyy, Utility.yyyy_MM_dd))
//        jsonObject.put(app.getString(R.string.sub_cast_id), lastnameId.toString())
//        jsonObject.put(app.getString(R.string.email_address), email)
//        jsonObject.put(app.getString(R.string.mobile), mobile)
//        jsonObject.put(app.getString(R.string.gender), gender)
//        jsonObject.put(app.getString(R.string.plain_password), pass)
//        jsonObject.put(app.getString(R.string.address), address)
//        jsonObject.put(app.getString(R.string.state_id), stateId.toString())
//        jsonObject.put(app.getString(R.string.city_id), cityId.toString())
//        jsonObject.put(app.getString(R.string.native_place_id), nativeId?.toString())
//        jsonObject.put(app.getString(R.string.sub_community_id), subCommId.toString())
//        jsonObject.put(app.getString(R.string.local_community_id), localCommId.toString())
//        jsonObject.put(app.getString(R.string.marital_status), maritalStatus)
//        jsonObject.put(app.getString(R.string.relation_id), 1)
//
//        if (profilePic.isNotEmpty()) {
//            jsonObject.put(app.getString(R.string.profile_pic), profilePic)
//        }
//        if (!isLogin) {
//            jsonObject.put(app.getString(R.string.is_admin), "1")
//        }
//
//        val updated = JsonParser.parseString(jsonObject.toString()) as JsonObject


        if (isNetworkConnected(app.applicationContext)) {
            jobRegistration = Job()
            jobRegistration.let { thejob ->
                CoroutineScope(IO + thejob!!).launch {
                    try {
//                        var body:MultipartBody.Part? =null
//                        if(profilePic!=null){
//                            val requestFile = profilePic?.asRequestBody("image/*".toMediaTypeOrNull())
//                            body = MultipartBody.Part.createFormData("profile_pic", profilePic?.name, requestFile!!)
//                        }
//                        val fname = fname?.toRequestBody("text/plain".toMediaTypeOrNull())
//                        val father = father?.toRequestBody("text/plain".toMediaTypeOrNull())
//                        val bdate = Utility.changeDateFormat(bdate, Utility.dd_MM_yyyy, Utility.yyyy_MM_dd)?.toRequestBody("text/plain".toMediaTypeOrNull())
//                        val lastNameId = lastnameId.toString()?.toRequestBody("text/plain".toMediaTypeOrNull())
//                        val email = email?.toRequestBody("text/plain".toMediaTypeOrNull())
//                        val mobile = mobile?.toRequestBody("text/plain".toMediaTypeOrNull())
//                        val gender = gender?.toRequestBody("text/plain".toMediaTypeOrNull())
//                        val pass = pass?.toRequestBody("text/plain".toMediaTypeOrNull())
//                        val address = address?.toRequestBody("text/plain".toMediaTypeOrNull())
//                        val state = stateId.toString()?.toRequestBody("text/plain".toMediaTypeOrNull())
//                        val city = cityId.toString()?.toRequestBody("text/plain".toMediaTypeOrNull())
//                        val native = nativeId?.toString()?.toRequestBody("text/plain".toMediaTypeOrNull())
//                        val subComm = subCommId.toString()?.toRequestBody("text/plain".toMediaTypeOrNull())
//                        val local = localCommId.toString()?.toRequestBody("text/plain".toMediaTypeOrNull())
//                        val marital = maritalStatus?.toRequestBody("text/plain".toMediaTypeOrNull())
//                        val relation = "1".toRequestBody("text/plain".toMediaTypeOrNull())
//                        var isAdmin = "-1".toRequestBody("text/plain".toMediaTypeOrNull())
//                        if (!isLogin) {
//                             isAdmin = "1".toRequestBody("text/plain".toMediaTypeOrNull())
//                        }


//                   val response = registerRepository.getUserRegister(body,fname,father,
//                   bdate,lastName,email,mobile,gender,pass,address,state,city,native,
//                   subComm,local, marital,relation,isAdmin)
                        var gen = false
                        if (gender.equals(app.applicationContext.getString(R.string.male))) {
                            gen = true
                        }

                        val input = RegisterInput(
                            first_name = fname!!,
                            last_name_id = lastnameId!!,
                            email = email!!,
                            mobile = mobile!!,
                            password = pass!!,
                            sub_community_id = subCommId!!,
                            local_community_id = localCommId!!,
                            gender = gen,
                            states_id = stateId!!,
                            city_id = cityId!!,
                            address = address!!,
                            status = isAdmin
                        )

                        val response: Result<RegisterModel> =
                            registerRepository.getUserRegister(input)

                        response.let {
                            withContext(Main) {

                                it.onSuccess {
                                    if (it.message == "success") {
                                        iRegisterListener?.getRegisterSuccess(it, isAdmin)
                                    } else {
                                        iRegisterListener?.getRegisterFailure(it.message, 0)
                                    }
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
