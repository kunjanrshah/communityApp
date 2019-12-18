package com.krs.community.viewmodel

import android.app.Activity
import android.app.Application
import android.content.Intent
import androidx.lifecycle.AndroidViewModel
import com.krs.community.R
import com.krs.community.activity.LoginActivity
import com.krs.community.interfaces.IRegisterListener
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
    var address: String? = null
    var mobile: String? = null
    var lastname_id:Int?=null
    var state_id:Int?=null
    var country_code:String?=null
    var city_id:Int?=null
    var local_comm_id:Int?=null
    var sub_comm_id:Int?=null

    var iRegisterListener: IRegisterListener? = null
    var TAG: String = RegisterViewModel::class.java.simpleName

    var job_states: CompletableJob? = null
    var job_cities: CompletableJob? = null
    var job_lastname: CompletableJob? = null
    var job_subcommunity: CompletableJob? = null
    var job_localcommunity: CompletableJob? = null
    var job_registration: CompletableJob? = null


    fun cancelAllJobs() {
        job_states?.cancel()
        job_cities?.cancel()
        job_lastname?.cancel()
        job_subcommunity?.cancel()
        job_localcommunity?.cancel()
        job_registration?.cancel()
    }

    fun getUserRegistration() {
        val register: AppConstants.UserRegister=AppConstants.UserRegister()

            if(fname.isNullOrBlank()){
                iRegisterListener?.getRegisterFailure(app.applicationContext.getString(R.string.enter_firstname),1)
                return
            }
            if(lastname_id == null){
                iRegisterListener?.getRegisterFailure(app.applicationContext.getString(R.string.enter_lastname),2)
                return
            }

            if(email.isNullOrBlank() || !Utility.isEmailValid(email)){
                iRegisterListener?.getRegisterFailure(app.applicationContext.getString(R.string.enter_email),3)
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
            if(state_id==null){
                iRegisterListener?.getRegisterFailure(app.applicationContext.getString(R.string.select_city),9)
                return
            }

            if(city_id==null){
                iRegisterListener?.getRegisterFailure(app.applicationContext.getString(R.string.select_state),10)
                return
            }

            if(sub_comm_id==null){
                iRegisterListener?.getRegisterFailure(app.applicationContext.getString(R.string.select_sub_comm),11)
                return
            }

            if(local_comm_id==null){
                iRegisterListener?.getRegisterFailure(app.applicationContext.getString(R.string.select_local),12)
                return
            }

        register.first_name=fname
        register.sub_cast_id=lastname_id.toString()
        register.email_address=email
        register.mobile=mobile
        register.plain_password=pass
        register.address=address
        register.state_id=state_id.toString()
        register.city_id=city_id.toString()
        register.sub_community_id =sub_comm_id.toString()
        register.local_community_id =local_comm_id.toString()


        job_registration = Job()
        job_registration.let {thejob ->
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
        job_states = Job()
        job_states.let { thejob ->

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
        job_cities = Job()

        job_cities.let {thejob ->
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
        job_lastname=Job()
        job_lastname.let {thejob ->
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
        job_subcommunity=Job()
        job_subcommunity.let {thejob ->
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
        job_localcommunity=Job()
        job_localcommunity.let {thejob ->
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
