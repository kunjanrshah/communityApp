package com.krs.community.bkservice

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.PhoneStateListener
import android.telephony.TelephonyManager
import android.util.Log
import android.widget.Toast


class IncomingCallReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        try {
            val tm = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
            tm.listen(object : PhoneStateListener() {
                override fun onCallStateChanged(state: Int, incomingNumber: String) {
                    super.onCallStateChanged(state, incomingNumber)
                    //Log.e("incomingNumber---", "" + incomingNumber)
                   // println("incomingNumber : $incomingNumber")
                   // Toast.makeText(context, "incomingNumber $incomingNumber", Toast.LENGTH_SHORT).show()

               /*   val adapter = TruecallerAdapter(context)
                    val setLocationDialog = DialogPlus.newDialog(context)
                            .setAdapter(adapter)
                            .setGravity(Gravity.BOTTOM)
                            .setCancelable(true)
                            .setExpanded(true, 470)
                            .setContentBackgroundResource(R.drawable.popup_top_corner)
                            .create()

                    setLocationDialog.show()*/

                    val intent = Intent(context, MyCustomDialog::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
                    context.startActivity(intent)
                }
            }, PhoneStateListener.LISTEN_CALL_STATE)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


}