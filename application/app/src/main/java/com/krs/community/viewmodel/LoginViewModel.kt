package com.krs.community.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.krs.community.interfaces.ILoginListener
import com.krs.community.repositories.LoginRepository
import com.krs.community.utils.ApiException
import com.krs.community.utils.AppConstants
import com.krs.community.utils.NoInternetException
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO

class LoginViewModel(private val loginRepository: LoginRepository,
                     var app: Application): AndroidViewModel(app) {

    lateinit  var iLoginListener: ILoginListener
    var TAG: String = LoginViewModel::class.java.simpleName
    var job_login: CompletableJob? = null
    var job_forgot: CompletableJob? = null
    var mobile:String?=""
    var country_code:String?=""


    fun cancelAllJobs() {
        job_login?.cancel()
        job_forgot?.cancel()
    }

    fun userForgotPassword(req_forgot:AppConstants.ForgotPass){
        job_forgot=Job()
        job_forgot.let {thejob->
            CoroutineScope(IO + thejob!!).launch {
                try{
                    val response = loginRepository.userForgotPass(req_forgot)
                    response.data?.let {
                        withContext(Dispatchers.Main) {
                            iLoginListener.userForgotPass(response.data)
                            thejob.complete()
                        }
                        return@launch
                    }
                    withContext(Dispatchers.Main) {
                        iLoginListener.getFailure(response.msg as String)
                    }
                } catch (e: ApiException) {
                    e.message?.let {
                        withContext(Dispatchers.Main) {
                            iLoginListener.getFailure(it)
                        }
                    }
                } catch (e: NoInternetException) {
                    e.message?.let {
                        withContext(Dispatchers.Main) {
                            iLoginListener.getFailure(it)
                        }
                    }
                } catch (e: Exception) {
                    e.message?.let {
                        withContext(Dispatchers.Main) {
                            iLoginListener.getFailure(it)
                        }
                    }
                }
                thejob.complete()
            }
        }
    }

    fun getLoginUser(req_login:AppConstants.LoginRequest){
        job_login= Job()

        job_login.let {thejob ->
            CoroutineScope(IO + thejob!!).launch {

                try {
                    val response = loginRepository.getLogin(req_login)
                    response.let {
                        withContext(Dispatchers.Main) {
                            iLoginListener.getUserLogin(response.data)
                            thejob.complete()
                        }
                        return@launch
                    }
                } catch (e: ApiException) {
                    e.message?.let {
                        withContext(Dispatchers.Main) {
                            iLoginListener.getFailure(it)
                        }
                    }
                } catch (e: NoInternetException) {
                    e.message?.let {
                        withContext(Dispatchers.Main) {
                            iLoginListener.getFailure(it)
                        }
                    }
                } catch (e: Exception) {
                    e.message?.let {
                        withContext(Dispatchers.Main) {
                            iLoginListener.getFailure(it)
                        }
                    }
                }
                thejob.complete()
            }
        }

    }


}