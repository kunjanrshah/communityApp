package com.krs.community.fragments;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatRadioButton;
import androidx.fragment.app.Fragment;

import com.github.squti.guru.Guru;
import com.krs.community.R;
import com.krs.community.app.AppController;
import com.krs.community.utils.Utility;

import static com.facebook.FacebookSdk.getApplicationContext;
import static com.krs.community.utils.Utility.changeStatusbarColor;

public class ChangeLanguageFragment extends Fragment {

    private AppCompatRadioButton rb_hindi, rb_gujarati, rb_english;
    private TextView tvChangeLang, tvEng, tvGuj, tvHindi;
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Nullable
    @Override

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_change_lan, container, false);

        AppController mApp = (AppController) getApplicationContext();
        mApp.firebaseAnalytics(getContext(), ChangeLanguageFragment.class.getSimpleName());
        mApp.facebookAnalytics(getContext(), ChangeLanguageFragment.class.getSimpleName());

        ImageView iv_lan_cancel = root.findViewById(R.id.iv_lan_cancel);
        iv_lan_cancel.setOnClickListener(v -> {
            Utility.backNavigation(getActivity());
        });

        changeStatusbarColor(getActivity(), R.color.colorBG, false);

        LinearLayout ll_english, ll_gujarati, ll_hindi;
        ll_english = root.findViewById(R.id.ll_english);
        ll_gujarati = root.findViewById(R.id.ll_gujarati);
        ll_hindi = root.findViewById(R.id.ll_hindi);

        rb_hindi = root.findViewById(R.id.rb_hindi);
        rb_gujarati = root.findViewById(R.id.rb_gujarati);
        rb_english = root.findViewById(R.id.rb_english);

        tvChangeLang = root.findViewById(R.id.tvChangeLang);
        tvEng = root.findViewById(R.id.tvEng);
        tvGuj = root.findViewById(R.id.tvGuj);
        tvHindi = root.findViewById(R.id.tvHindi);

        ll_hindi.setOnClickListener(v -> {
            if (!rb_hindi.isChecked()) {
                rb_hindi.setChecked(true);
                rb_gujarati.setChecked(false);
                rb_english.setChecked(false);

                Utility.changeLang(getContext(), "हिन्दी".toString());

                tvChangeLang.setText(getResources().getString(R.string.choose_language));
                tvHindi.setText(getResources().getString(R.string._hindi));
                tvGuj.setText(getResources().getString(R.string._gujarati));
                tvEng.setText(getResources().getString(R.string._english));
            }
        });

        ll_gujarati.setOnClickListener(v -> {
            if (!rb_gujarati.isChecked()) {
                rb_hindi.setChecked(false);
                rb_gujarati.setChecked(true);
                rb_english.setChecked(false);

                Utility.changeLang(getContext(), "ગુજરાતી".toString());

                tvChangeLang.setText(getResources().getString(R.string.choose_language));
                tvHindi.setText(getResources().getString(R.string._hindi));
                tvGuj.setText(getResources().getString(R.string._gujarati));
                tvEng.setText(getResources().getString(R.string._english));

            }
        });

        ll_english.setOnClickListener(v -> {
            if (!rb_english.isChecked()) {
                rb_hindi.setChecked(false);
                rb_gujarati.setChecked(false);
                rb_english.setChecked(true);

                Utility.changeLang(getContext(), "English".toString());

                tvChangeLang.setText(getResources().getString(R.string.choose_language));
                tvHindi.setText(getResources().getString(R.string._hindi));
                tvGuj.setText(getResources().getString(R.string._gujarati));
                tvEng.setText(getResources().getString(R.string._english));

            }
        });

        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
        String locale = Guru.getString(getResources().getString(R.string.locale_sp), getResources().getString(R.string._english));
        if (locale.equalsIgnoreCase(getResources().getString(R.string._gujarati))) {
            if (!rb_gujarati.isChecked()) {
                rb_hindi.setChecked(false);
                rb_gujarati.setChecked(true);
                rb_english.setChecked(false);

                Utility.changeLang(getContext(), "ગુજરાતી".toString());

                tvChangeLang.setText(getResources().getString(R.string.choose_language));
                tvHindi.setText(getResources().getString(R.string._hindi));
                tvGuj.setText(getResources().getString(R.string._gujarati));
                tvEng.setText(getResources().getString(R.string._english));

            }
        } else if (locale.equalsIgnoreCase(getResources().getString(R.string._hindi))) {
            if (!rb_hindi.isChecked()) {
                rb_hindi.setChecked(true);
                rb_gujarati.setChecked(false);
                rb_english.setChecked(false);


                Utility.changeLang(getContext(), "हिन्दी".toString());

                tvChangeLang.setText(getResources().getString(R.string.choose_language));
                tvHindi.setText(getResources().getString(R.string._hindi));
                tvGuj.setText(getResources().getString(R.string._gujarati));
                tvEng.setText(getResources().getString(R.string._english));
            }
        } else {
            if (!rb_english.isChecked()) {
                rb_hindi.setChecked(false);
                rb_gujarati.setChecked(false);
                rb_english.setChecked(true);

                Utility.changeLang(getContext(), "English".toString());

                tvChangeLang.setText(getResources().getString(R.string.choose_language));
                tvHindi.setText(getResources().getString(R.string._hindi));
                tvGuj.setText(getResources().getString(R.string._gujarati));
                tvEng.setText(getResources().getString(R.string._english));
            }
        }

    }

    @Override
    public void onStop() {
        super.onStop();
        ((AppCompatActivity) getActivity()).getSupportActionBar().show();


    }

}
