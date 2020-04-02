package com.krs.community.app

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.widget.Toast
import com.github.squti.guru.Guru
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.krs.community.R
import com.krs.community.repositories.RoomMemberRepository
import com.krs.community.responses.searchByKeywordsResponse
import com.krs.community.retrofit.ApiServices
import com.krs.community.utils.ApiException
import com.krs.community.utils.NoInternetException
import kotlinx.coroutines.*
import org.json.JSONObject
import java.net.URLEncoder

class NotificationReceiver : BroadcastReceiver() {
    var jobChangeStatus: CompletableJob? = null
    lateinit var mRoomMemberRepository: RoomMemberRepository

    override fun onReceive(context: Context, intent: Intent) {
        val s = intent.getStringExtra("Phone")
        val userId = intent.getStringExtra("userId")
        val action = intent.getStringExtra("action")
        when {
            action.equals("Call", ignoreCase = true) -> {
                val uri = "tel:$s"
                val intentCall = Intent(Intent.ACTION_DIAL)
                intentCall.data = Uri.parse(uri)
                intentCall.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                context.startActivity(intentCall)
                val it = Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)
                context.sendBroadcast(it)
            }
            action.equals("WhatsApp", ignoreCase = true) -> {
                try {
                    val uri = Uri.parse("whatsapp://send?phone=+91" + s + "&text=" + URLEncoder.encode("message", "UTF-8"))
                    val i = Intent(Intent.ACTION_VIEW, uri)
                    i.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    context.startActivity(i)
                } catch (e: Exception) {
                    e.printStackTrace()
                    Toast.makeText(context, "WhatsApp not installed.", Toast.LENGTH_SHORT).show()
                }

                val it = Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)
                context.sendBroadcast(it)

            }
            action.equals("Approve", ignoreCase = true) -> {
                Log.e("Click--", "Approve")

                val jsonObject = JSONObject()
                jsonObject.put(context.getString(R.string.access_token), Guru.getString(context.getString(R.string.access_token), ""))
                jsonObject.put(context.getString(R.string.user_id), Guru.getString(context.getString(R.string.user_id), ""))
                jsonObject.put(context.getString(R.string.id), Guru.getString(context.getString(R.string.member_id), ""))
                jsonObject.put("status", "1")
                jsonObject.put(context.getString(R.string.idList), userId)
                val updated = JsonParser().parse(jsonObject.toString()) as JsonObject
                changeStatus(updated, context)
                val it = Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)
                context.sendBroadcast(it)
            }
        }
    }

    fun changeStatus(jsonObject: JsonObject, context: Context) {
        jobChangeStatus = Job()
        jobChangeStatus.let { thejob ->
            mRoomMemberRepository = RoomMemberRepository(ApiServices(), AppDatabase(context))

            CoroutineScope(Dispatchers.IO + thejob!!).launch {
                try {
                    val response = mRoomMemberRepository.changeStatus(jsonObject)
                    response.let {
                        withContext(Dispatchers.Main) {
                            getMembers(response)
                            thejob.complete()
                        }
                        return@launch
                    }
                    // mRoomMemberListener?.getFailure(response.message as String)
                } catch (e: ApiException) {
                    e.message?.let {
                        // mRoomMemberListener?.getFailure(it)
                    }
                } catch (e: NoInternetException) {
                    e.message?.let {
                        // mRoomMemberListener?.getFailure(it)
                    }
                } catch (e: Exception) {
                    e.message?.let {
                        // mRoomMemberListener?.getFailure(it)
                    }
                }
                thejob.complete()
            }
        }
    }

    private fun getMembers(response: searchByKeywordsResponse) {

        var success = response.success
        Log.e("success", "" + success)

        if (success.equals("true")) {
            Log.e("success", "" + success)
        }

    }
}