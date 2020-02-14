package com.krs.community.bkservice

import android.content.Context
import android.location.Location
import android.util.Log
import android.widget.Toast
import com.example.easywaylocation.Listener
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.krs.community.R
import com.krs.community.app.AppController
import com.krs.community.app.AppDatabase
import com.krs.community.listeners.EditMemberListener
import com.krs.community.repositories.ProfileDetailRepository
import com.krs.community.retrofit.ApiServices
import com.krs.community.utils.ApiException
import kotlinx.coroutines.*
import org.json.JSONObject
import java.util.*

abstract class Call : PhonecallReceiver() , Listener {

    /*private val mProfileDetailRepository: ProfileDetailRepository

        get() {
            TODO()
        }*/

    lateinit var mEditMemberListener: EditMemberListener

    private lateinit var job_by_update: CompletableJob
    private lateinit var completableJob: CompletableJob
    override fun onIncomingCallStarted(ctx: Context?, number: String?, start: Date?) {

        Toast.makeText(ctx, "onIncomingCallStarted" + number, Toast.LENGTH_LONG).show();
        //  SmartFilterApiData(ctx)
    //    SmartFilterApiData(number);

    }

    override fun onOutgoingCallStarted(ctx: Context?, number: String?, start: Date?) { //	Toast.makeText(ctx, " onOutgoingCallStarted" + number, Toast.LENGTH_LONG).show();
    }

    override fun onIncomingCallEnded(ctx: Context?, number: String?, start: Date?, end: Date?) { //Toast.makeText(ctx, "onIncomingCallEnded" + number, Toast.LENGTH_LONG).show();
    }

    override fun onOutgoingCallEnded(ctx: Context?, number: String?, start: Date?, end: Date?) { //Toast.makeText(ctx, "onOutgoingCallEnded" + number, Toast.LENGTH_LONG).show();
    }

    override fun onMissedCall(ctx: Context?, number: String?, start: Date?) { //Toast.makeText(ctx, "onMissedCall " + number, Toast.LENGTH_LONG).show();
    }


   /* fun SmartFilterApiData(number: String?) {

        val jsonObject = JSONObject()
        jsonObject.put("" + AppController.mApplication.start, "0")
        jsonObject.put("" + AppController.mApplication.length, "1")
        val jsonObj = JSONObject()
        jsonObj.put("mobile", number)

        jsonObject.put("filter_by", jsonObj)
        val updated = JsonParser().parse(jsonObject.toString()) as JsonObject

        completableJob = Job()

        completableJob.let { thejob ->

            CoroutineScope(Dispatchers.IO + thejob).launch {
                try {
                    val response = mProfileDetailRepository.searchFilter(updated)
                    response.let {
                        withContext(Dispatchers.Main) {
                            mEditMemberListener.getScanResult(response)


                            Log.e("mEditMemberListener--",""+mEditMemberListener.toString())
                            thejob.complete()
                        }
                        return@launch
                    }
                }catch (e: ApiException){

                }
                thejob.complete()
            }
        }
    }
*/
    override fun locationCancelled() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun locationOn() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun currentLocation(location: Location?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}
