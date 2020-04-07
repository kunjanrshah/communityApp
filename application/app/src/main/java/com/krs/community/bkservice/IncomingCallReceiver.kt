package com.krs.community.bkservice


import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.telephony.PhoneStateListener
import android.telephony.TelephonyManager
import android.util.Log
import com.github.squti.guru.Guru
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.krs.community.R
import com.krs.community.activity.IncomingCallActivity
import com.krs.community.repositories.ContactListRepository
import com.krs.community.retrofit.ApiServices
import com.krs.community.utils.ApiException
import com.krs.community.utils.NoInternetException
import kotlinx.coroutines.*
import org.json.JSONArray
import org.json.JSONObject


class IncomingCallReceiver : BroadcastReceiver() {

    private var completableJob: CompletableJob? = null
    private var contactListRepository = ContactListRepository(ApiServices(), null)
    private var isShow: Boolean = true

    override fun onReceive(context: Context, intent: Intent) {
        try {
            val tm = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
            tm.listen(object : PhoneStateListener() {
                override fun onCallStateChanged(state: Int, incomingNumber: String) {
                    super.onCallStateChanged(state, incomingNumber)

                    Log.e("incomingNumber---", "number===" + incomingNumber)
                    Log.e("state---", "" + state)

                    isShow = Guru.getBoolean(context.getString(R.string.isdialogshow), true)
                    if (state == 1 && incomingNumber.isNotEmpty()) {
                        if (isShow) {
                            val jsonObject = JSONObject()
                            jsonObject.put(context.getString(R.string.user_id), Guru.getString(context.getString(R.string.user_id), ""))
                            jsonObject.put(context.getString(R.string.id), Guru.getString(context.getString(R.string.member_id), ""))
                            jsonObject.put(context.getString(R.string.access_token), Guru.getString(context.getString(R.string.access_token), ""))
                            val jsonArray = JSONArray()
                            val number = incomingNumber.replace("+91", "")
                            jsonArray.put(number)
                            jsonObject.put("mobiles", jsonArray)
                            val updated = JsonParser().parse(jsonObject.toString()) as JsonObject
                            Log.e("updated---", "" + updated)
                            getContactList(context, updated)
                        }
                    }
                }
            }, PhoneStateListener.LISTEN_CALL_STATE)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun getContactList(context: Context, jsonObject: JsonObject) {

        completableJob = Job()
        completableJob.let { thejob ->

            CoroutineScope(Dispatchers.IO + thejob!!).launch {
                try {
                    val response = contactListRepository.getContactList(jsonObject)
                    response.let {
                        withContext(Dispatchers.Main) {

                            Log.e("response", " " + response.success)
                            if (response.success) {
                                Log.e("Frist Time", " success ")
                                val intent = Intent(context, IncomingCallActivity::class.java)
                                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
                                val mBundle = Bundle()
                                mBundle.putSerializable(context.getString(R.string.member), response.members[0])
                                intent.putExtras(mBundle)
                                context.startActivity(intent)
                                thejob.complete()

                            }
                        }
                        return@launch
                    }
                } catch (e: ApiException) {
                    e.message?.let {
                        Log.e("ApiException--", "" + e.toString())
                    }
                } catch (e: NoInternetException) {
                    e.message?.let {
                        Log.e("NoInternetException--", "" + e.toString())
                    }
                } catch (e: Exception) {
                    e.message?.let {
                        Log.e("Exception--", "" + e.toString())
                    }
                }
                thejob.complete()
            }
        }
    }
}