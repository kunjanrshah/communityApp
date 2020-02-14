package com.krs.community.viewmodel

import android.app.Activity
import android.app.Application
import android.content.Intent
import androidx.lifecycle.AndroidViewModel
import com.krs.community.R
import com.krs.community.activity.LoginActivity
import com.krs.community.listeners.IRegisterListener
import com.krs.community.repositories.RegisterRepository
import com.krs.community.utils.*
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main

class RegisterViewModel(
        private val registerRepository: RegisterRepository,
        var app: Application) : AndroidViewModel(app) {

    var fname: String? = null
    var email: String? = null
    var pass: String? = null
    var cpass: String? = null
    var gender:String?=null
    var address: String? = null
    var mobile: String? = null
    var lastnameId:Int?=null
    var stateId:Int?=null
    var countryCode:String?=null
    var cityId:Int?=null
    var localCommId:Int?=null
    var subCommId:Int?=null

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

    fun getUserRegistration() {
        val register: AppConstants.UserRegister=AppConstants.UserRegister()

            if(fname.isNullOrBlank()){
                iRegisterListener?.getRegisterFailure(app.applicationContext.getString(R.string.enter_firstname),1)
                return
            }
            if(lastnameId == null){
                iRegisterListener?.getRegisterFailure(app.applicationContext.getString(R.string.enter_lastname),2)
                return
            }

            if(email.isNullOrBlank() || !Utility.isEmailValid(email)){
                iRegisterListener?.getRegisterFailure(app.applicationContext.getString(R.string.enter_email),3)
                return
            }

            if(gender.isNullOrBlank()){
                iRegisterListener?.getRegisterFailure(app.applicationContext.getString(R.string.enter_gender),4)
                return
            }


            /*if(country_code.isNullOrBlank()){
            iRegisterListener?.getRegisterFailure(app.applicationContext.getString(R.string.select_country_code),4)
                return
            }*/

            if(mobile.isNullOrBlank() || mobile?.length!=10){
                iRegisterListener?.getRegisterFailure(app.applicationContext.getString(R.string.enter_mobile),5)
                return
            }
            if(pass.isNullOrBlank()){
                iRegisterListener?.getRegisterFailure(app.applicationContext.getString(R.string.enter_password),6)
                return
            }

            if(pass?.length!! < 6){
                iRegisterListener?.getRegisterFailure(app.applicationContext.getString(R.string.make_strong_pass),6)
                return
            }
        
            if(cpass.isNullOrBlank()){
                iRegisterListener?.getRegisterFailure(app.applicationContext.getString(R.string.confirm_password),7)
                return
            }

            if(cpass?.length!! < 6){
                iRegisterListener?.getRegisterFailure(app.applicationContext.getString(R.string.make_strong_pass),7)
                return
            }
        
            if(!pass.equals(cpass)) {
                iRegisterListener?.getRegisterFailure(app.applicationContext.getString(R.string.password_mismatch),7)
                return
            }
            if(address.isNullOrBlank()){
                iRegisterListener?.getRegisterFailure(app.applicationContext.getString(R.string.enter_home_address),8)
                return
            }
            if(stateId==null){
                iRegisterListener?.getRegisterFailure(app.applicationContext.getString(R.string.select_city),9)
                return
            }

            if(cityId==null){
                iRegisterListener?.getRegisterFailure(app.applicationContext.getString(R.string.select_state),10)
                return
            }

            if(subCommId==null){
                iRegisterListener?.getRegisterFailure(app.applicationContext.getString(R.string.select_sub_comm),11)
                return
            }

            if(localCommId==null){
                iRegisterListener?.getRegisterFailure(app.applicationContext.getString(R.string.select_local),12)
                return
            }
        register.first_name=fname
        register.sub_cast_id=lastnameId.toString()
        register.email_address=email
        register.mobile=mobile
        register.gender=gender
        register.plain_password=pass
        register.address=address
        register.state_id=stateId.toString()
        register.city_id=cityId.toString()
        register.sub_community_id =subCommId.toString()
        register.local_community_id =localCommId.toString()



        jobRegistration = Job()
        jobRegistration.let { thejob ->
            CoroutineScope(IO + thejob!!).launch {
                try {
                    val response = registerRepository.getUserRegister(register)

                    response.let {
                        withContext(Main) {
                            if(response.success.toString().toLowerCase().equals("success")){
                                iRegisterListener?.getRegisterSuccess(response)
                            }else  {
                                iRegisterListener?.getRegisterFailure(response.message,0)
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

    fun getUserStates() {
        jobStates = Job()
        jobStates.let { thejob ->

            CoroutineScope(IO + thejob!!).launch {
                try {
                    val response = registerRepository.userState()
                    response.data?.let {
                        withContext(Main) {
                            iRegisterListener?.getStates(response.data)
                            thejob.complete()
                        }
                        return@launch
                    }
                    iRegisterListener?.getFailure(response.message as String)
                } catch (e: ApiException) {
                    e.message?.let {
                        iRegisterListener?.getFailure(it)
                    }
                } catch (e: NoInternetException) {
                    e.message?.let {
                        iRegisterListener?.getFailure(it)
                    }
                } catch (e: Exception) {
                    e.message?.let {
                        iRegisterListener?.getFailure(it)
                    }
                }
                thejob.complete()
            }
        }
    }

    fun fetchCitiesForStateId(id: Int) {
        jobCities = Job()

        jobCities.let { thejob ->
            CoroutineScope(IO + thejob!!).launch {
                try {
                    val response = registerRepository.userCity(id)
                    response.data?.let {
                        withContext(Main) {
                            iRegisterListener?.getCities(response.data)
                            thejob.complete()
                        }
                        return@launch
                    }
                    iRegisterListener?.getFailure(response.message)
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

    fun getUserLastName() {
        jobLastname=Job()
        jobLastname.let { thejob ->
            CoroutineScope(IO+thejob!!).launch {
                try {
                    val response = registerRepository.userLastName()
                    response.data?.let {
                        withContext(Main) {
                            iRegisterListener?.getLastname(response.data)
                            thejob.complete()
                        }
                        return@launch
                    }
                    iRegisterListener?.getFailure(response.message)
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

    fun getLstSubCommunity() {
        jobSubcommunity=Job()
        jobSubcommunity.let { thejob ->
            CoroutineScope(IO + thejob!!).launch {
                try {
                    val response = registerRepository.userSubCommunity()
                    response!!.data?.let {
                        withContext(Main) {
                            iRegisterListener?.getSubCommunity(response.data)
                            thejob.complete()
                        }
                        return@launch
                    }
                    iRegisterListener?.getFailure(response.message)
                } catch (e: ApiException) {
                    e.message?.let { iRegisterListener?.getFailure(it) }
                } catch (e: NoInternetException) {
                    e.message?.let { iRegisterListener?.getFailure(it) }
                }
                thejob.complete()
            }
        }
    }

    fun getLstLocalCommunity(id: Int) {
        jobLocalcommunity=Job()
        jobLocalcommunity.let { thejob ->
            CoroutineScope(IO +thejob!!).launch {
                try {
                    val response = registerRepository.getLocalCommunity(id)
                    response.data?.let {
                        withContext(Main) {
                            iRegisterListener?.getLocalCommunity(response.data)
                            thejob.complete()
                        }
                        return@launch
                    }
                    iRegisterListener?.getFailure(response.message)
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


    fun onTextAlreadyClicked(activity: Activity) {
        val mIntent = Intent(activity, LoginActivity::class.java)
        mIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        activity.startActivity(mIntent)
        activity.finish()
        Utility.fade(activity)
    }

    fun onHowRegisterClicked(activity: Activity) {
        Utility.watchYoutubeVideo(activity, activity.resources.getString(R.string.login_1))
    }
}
