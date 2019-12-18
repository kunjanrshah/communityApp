package com.krs.community.utils;

import android.app.Dialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.os.Bundle;
import android.text.format.DateFormat;

import androidx.annotation.IntDef;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StyleRes;
import androidx.fragment.app.DialogFragment;

import com.krs.community.R;
import com.philliphsu.numberpadtimepicker.BottomSheetNumberPadTimePickerDialog;
import com.philliphsu.numberpadtimepicker.NumberPadTimePickerDialog;
import com.philliphsu.numberpadtimepicker.NumberPadTimePickerDialogThemer;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class NumberPadTimePickerDialogFragment extends DialogFragment {

    static final int MODE_ALERT = 1;
    public static final int MODE_BOTTOM_SHEET = 2;

    @IntDef({MODE_ALERT, MODE_BOTTOM_SHEET})
    @Retention(RetentionPolicy.SOURCE)
    @interface DialogMode {}

    @DialogMode
    private int dialogMode;
    @StyleRes
    private int themeResId;
    private OnTimeSetListener listener;

    public static NumberPadTimePickerDialogFragment newInstance(OnTimeSetListener listener) {
        NumberPadTimePickerDialogFragment f = new NumberPadTimePickerDialogFragment();
        f.listener = listener;
        return f;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        themeResId = R.style.Theme_Design_BottomSheetDialog;
        dialogMode = 2;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog;

        boolean is24HourMode = DateFormat.is24HourFormat(getContext());
        BottomSheetNumberPadTimePickerDialog bottomSheetPicker = new BottomSheetNumberPadTimePickerDialog(getContext(),themeResId, listener, true);
        NumberPadTimePickerDialogThemer themer=bottomSheetPicker.getThemer();
        dialog = bottomSheetPicker;
        return dialog;
    }
}
