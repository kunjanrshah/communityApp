package com.krs.community.bkservice;

/**
 * Created by jeet on 24/12/16.
 */
import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.CallLog;
import android.util.Log;
import android.view.Gravity;
import android.view.Window;
import android.widget.TextView;


import com.krs.community.R;
import com.krs.community.activity.SplashActivity;
import com.krs.community.adapter.TruecallerAdapter;
import com.orhanobut.dialogplus.DialogPlus;

import java.sql.Date;
import java.text.SimpleDateFormat;

public class MyCustomDialog extends Activity
{
    //TextView tv_client;
    String phone_no;

    TextView relation;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        try
        {
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            this.setFinishOnTouchOutside(true);
            super.onCreate(savedInstanceState);
           // setContentView(R.layout.dialog_second_third);
            initializeContent();

            Log.e("Hii","MyCustomDialog Runing");

            TruecallerAdapter adapter = new TruecallerAdapter(MyCustomDialog.this);
            DialogPlus setLocationDialog = DialogPlus.newDialog(MyCustomDialog.this)
               .setAdapter(adapter)
               .setGravity(Gravity.BOTTOM)
               .setCancelable(true)
               .setExpanded(false, 470)
               .setContentBackgroundResource(R.drawable.popup_top_corner)
               .create();

            setLocationDialog.show();

           /* StringBuffer sb = new StringBuffer();
            phone_no    =   "9586517742";

          //  tv_client.setText(""+phone_no +" is calling you");

            Log.e("phone_no---",""+phone_no);
            String strOrder = CallLog.Calls.DATE + " DESC";


            Cursor managedCursor = managedQuery(CallLog.Calls.CONTENT_URI, null,
                    null, null, strOrder);
            int number = managedCursor.getColumnIndex(CallLog.Calls.NUMBER);
            int type = managedCursor.getColumnIndex(CallLog.Calls.TYPE);
            int date = managedCursor.getColumnIndex(CallLog.Calls.DATE);
            int duration = managedCursor.getColumnIndex(CallLog.Calls.DURATION);
            sb.append("\nLast Call Log :");
            while (managedCursor.moveToNext()) {
                String phNum = managedCursor.getString(number);
                String callTypeCode = managedCursor.getString(type);

                long seconds=managedCursor.getLong(date);
                SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy  hh:mm:ss a");
                String dateString = formatter.format(new Date(seconds));


                String strcallDate = managedCursor.getString(date);
                Date callDate = new Date(Long.valueOf(strcallDate));


                String callDuration = managedCursor.getString(duration);
                String callType = null;
                int callcode = Integer.parseInt(callTypeCode);
                switch (callcode) {
                    case CallLog.Calls.OUTGOING_TYPE:
                        callType = "Outgoing";
                        break;
                    case CallLog.Calls.INCOMING_TYPE:
                        callType = "Incoming";
                        break;
                    case CallLog.Calls.MISSED_TYPE:
                        callType = "Missed";
                        break;
                }
                if(phone_no.equalsIgnoreCase(phNum))
                {
                    sb.append("\n----------------------------------");
                    sb.append("\nPhone Number:--- " + phNum + " \nCall Type:--- "
                            + callType + " \nCall Date:--- " + dateString
                            + " \nCall duration in sec :--- " + callDuration);
                    sb.append("\n----------------------------------");
                    break;
                }

            }
            managedCursor.close();


          //  tv_client.setText(""+phone_no +" is calling you");
            relation.setText(sb);
*/
        }
        catch (Exception e)
        {
            Log.d("Exception", e.toString());
            e.printStackTrace();
        }
    }

    private void initializeContent()
    {
      //  tv_client   = (TextView) findViewById(R.id.text_name);


    }
}