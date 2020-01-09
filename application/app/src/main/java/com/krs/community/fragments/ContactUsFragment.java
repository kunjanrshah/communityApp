package com.krs.community.fragments;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.krs.community.R;
import com.krs.community.activity.DashboardActivity;
import com.krs.community.utils.Utility;
import static com.bumptech.glide.Glide.*;

public class ContactUsFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_contact_us, container, false);
        CardView card=layout.findViewById(R.id.card);

        card.setBackgroundResource(R.drawable.shadow_white_round_border);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Utility.changeStatusbarColor(getActivity(),R.color.colorBG,false);
        }

        ImageView iv_profile=layout.findViewById(R.id.iv_profile);
        Bitmap bmp = ((BitmapDrawable)getActivity().getResources().getDrawable(R.drawable.user_profile)).getBitmap();
        with(getActivity()).load(Utility.getRoundedCornerBitmap(bmp,80)).thumbnail(0.5f).into(iv_profile);

        ImageView img_call=layout.findViewById(R.id.img_call);
        with(getActivity()).load(Utility.getRoundedCornerBitmap(getVectorDrawable(getResources().getDrawable(R.drawable.phone_call)),35)).thumbnail(0.5f).into(img_call);

        ImageView img_fb=layout.findViewById(R.id.img_fb);
        with(getActivity()).load(Utility.getRoundedCornerBitmap(getVectorDrawable(getResources().getDrawable(R.drawable.ic_fb)),35)).thumbnail(0.5f).into(img_fb);


        ImageView img_twitter=layout.findViewById(R.id.img_twitter);
        with(getActivity()).load(Utility.getRoundedCornerBitmap(getVectorDrawable(getResources().getDrawable(R.drawable.twitter)),35)).thumbnail(0.5f).into(img_twitter);

        ImageView img_whatsapp=layout.findViewById(R.id.img_whatsapp);
        with(getActivity()).load(Utility.getRoundedCornerBitmap(getVectorDrawable(getResources().getDrawable(R.drawable.whatsapp)),35)).thumbnail(0.5f).into(img_whatsapp);

        ImageView img_skype=layout.findViewById(R.id.img_skype);
        with(getActivity()).load(Utility.getRoundedCornerBitmap(getVectorDrawable(getResources().getDrawable(R.drawable.skype)),35)).thumbnail(0.5f).into(img_skype);

        ImageView img_linkedin=layout.findViewById(R.id.img_linkedin);
        with(getActivity()).load(Utility.getRoundedCornerBitmap(getVectorDrawable(getResources().getDrawable(R.drawable.linkedin)),35)).thumbnail(0.5f).into(img_linkedin);

        ImageView img_gmail=layout.findViewById(R.id.img_gmail);
        with(getActivity()).load(Utility.getRoundedCornerBitmap(getVectorDrawable(getResources().getDrawable(R.drawable.gmail)),35)).thumbnail(0.5f).into(img_gmail);

        EditText edt_message=layout.findViewById(R.id.edt_message);
        Button btn_send =layout.findViewById(R.id.btn_send);

        TextView tv_name=layout.findViewById(R.id.tv_name);
        TextView tv_link=layout.findViewById(R.id.tv_link);

        ImageView iv_cancel=layout.findViewById(R.id.iv_cancel);
        iv_cancel.setOnClickListener(v -> {
            Utility.movetoFragment(getActivity(),new DashboardFragment());
        });
        tv_link.setText("http://www.google.com");
        Linkify.addLinks(tv_link, Linkify.WEB_URLS);
        Linkify.addLinks(tv_link, Linkify.ALL );

        return layout;
    }

    private Bitmap getVectorDrawable(Drawable drawable)
    {
        try {
            Bitmap bitmap;

            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);

            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            drawable.draw(canvas);
            return bitmap;
        } catch (OutOfMemoryError e) {
            // Handle the error
            return null;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
        DashboardActivity.binding.space.setVisibility(View.GONE);
    }

    @Override
    public void onStop() {
        super.onStop();
        ((AppCompatActivity) getActivity()).getSupportActionBar().show();
        DashboardActivity.binding.space.setVisibility(View.VISIBLE);
    }
}
