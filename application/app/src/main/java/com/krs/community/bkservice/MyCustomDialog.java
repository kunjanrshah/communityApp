package com.krs.community.bkservice;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.widget.ImageView;
import com.krs.community.R;

public class MyCustomDialog extends Activity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        try
        {
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            this.setFinishOnTouchOutside(true);
            super.onCreate(savedInstanceState);
            setContentView(R.layout.truecaller_bottom_sheet);
            ImageView imageView=findViewById(R.id.iv_profile);

        }
        catch (Exception e)
        {
            Log.d("Exception", e.toString());
            e.printStackTrace();
        }
    }


}