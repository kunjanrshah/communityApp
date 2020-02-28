package com.krs.community.viewmodel

import android.app.Application
import android.content.Intent
import androidx.lifecycle.AndroidViewModel
import com.google.gson.JsonObject
import com.krs.community.bkservice.MyCustomDialog
import com.krs.community.repositories.ContactListRepository
import com.krs.community.utils.ApiException
import com.krs.community.utils.NoInternetException
import kotlinx.coroutines.*

class ContactListViewModel(
        private val contactListRepository: ContactListRepository,
        var app: Application) : AndroidViewModel(app) {

    var completableJob: CompletableJob? = null
    var TAG: String = ContactListViewModel::class.java.simpleName


    fun getContactList(jsonObject: JsonObject) {
        completableJob = Job()
        completableJob.let { thejob ->

            CoroutineScope(Dispatchers.IO + thejob!!).launch {
                try {
                    val response = contactListRepository.getContactList(jsonObject)
                    response.let {
                        withContext(Dispatchers.Main) {
                            val intent = Intent(app, MyCustomDialog::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
                            app.startActivity(intent)
                            thejob.complete()
                        }
                        return@launch
                    }
                } catch (e: ApiException) {
                    e.message?.let {
                    }
                } catch (e: NoInternetException) {
                    e.message?.let {
                    }
                } catch (e: Exception) {
                    e.message?.let {
                    }
                }
                thejob.complete()
            }
        }
    }

}