package com.krs.community.fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.krs.community.R;
import com.krs.community.activity.DashboardActivity;
import com.krs.community.utils.Utility;
import com.orhanobut.dialogplus.DialogPlus;
import com.zfdang.multiple_images_selector.ImagesSelectorActivity;
import com.zfdang.multiple_images_selector.SelectorSettings;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import ru.slybeaver.slycalendarview.SlyCalendarDialog;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

public class ShareEventFragment extends Fragment implements SlyCalendarDialog.Callback {

    // class variables
    private final int REQUEST_CODE = 123;
    private ImagesAdapter adapter;
    private ArrayList<String> mResults = new ArrayList<>();
    private ArrayList<String> yURLs = new ArrayList<>();
    TextView txt_start, edt_end_date, txt_end_time, txt_start_time;
    boolean isStart;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_share_event, container, false);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Utility.changeStatusbarColor(getActivity(), R.color.color_mid_light_gray, false);
        }

        ImageView iv_cancel = root.findViewById(R.id.iv_cancel);
        iv_cancel.setOnClickListener(v -> {
            Utility.movetoFragment(getActivity(), new DashboardFragment());
        });

        RecyclerView rv_images = root.findViewById(R.id.rv_images);
        rv_images.setHasFixedSize(true);
        LinearLayoutManager MyLayoutManager = new LinearLayoutManager(getActivity());
        MyLayoutManager.setOrientation(RecyclerView.HORIZONTAL);
        adapter = new ImagesAdapter();
        rv_images.setAdapter(adapter);
        rv_images.setLayoutManager(MyLayoutManager);

        RecyclerView rv_parent = root.findViewById(R.id.rv_parent);
        EditText edt_title = root.findViewById(R.id.edt_title);
        EditText edt_address = root.findViewById(R.id.edt_address);
        EditText edt_description = root.findViewById(R.id.edt_description);


        rv_parent.setHasFixedSize(true);
        LinearLayoutManager MyLayoutManager1 = new LinearLayoutManager(getActivity());
        MyLayoutManager1.setOrientation(RecyclerView.VERTICAL);

        yURLs.add("1");
        yURLs.add("2");
        yURLs.add("3");
        URLAdapter adapter1 = new URLAdapter();
        rv_parent.setAdapter(adapter1);
        rv_parent.setLayoutManager(MyLayoutManager1);

        ImageView iv_add_url = root.findViewById(R.id.iv_add_url);
        iv_add_url.setOnClickListener(v -> {
            yURLs.add("test");
            adapter1.notifyDataSetChanged();
        });

        ImageView iv_upload = root.findViewById(R.id.iv_upload);
        iv_upload.setOnClickListener(v -> {

            Intent intent = new Intent(getActivity(), ImagesSelectorActivity.class);
            intent.putExtra(SelectorSettings.SELECTOR_MAX_IMAGE_NUMBER, 15);
            intent.putExtra(SelectorSettings.SELECTOR_MIN_IMAGE_SIZE, 100000);
            intent.putExtra(SelectorSettings.SELECTOR_SHOW_CAMERA, true);
            intent.putStringArrayListExtra(SelectorSettings.SELECTOR_INITIAL_SELECTED_LIST, mResults);
            startActivityForResult(intent, REQUEST_CODE);

        });

        Button btnShare, btnCreate;
        btnShare = root.findViewById(R.id.btnShare);
        btnCreate = root.findViewById(R.id.btnCreate);
        txt_start = root.findViewById(R.id.edt_start);
        edt_end_date = root.findViewById(R.id.edt_end_date);
        txt_start_time = root.findViewById(R.id.txt_start_time);
        txt_end_time = root.findViewById(R.id.txt_end_time);

        txt_start.setOnClickListener(view -> {
            isStart = true;
            showCalendar();
        });
        edt_end_date.setOnClickListener(view -> {
            isStart = false;
            showCalendar();
        });

        txt_start_time.setOnClickListener(view -> {
            isStart = true;
            showTimerSelection();
        });
        txt_end_time.setOnClickListener(view -> {
            isStart = false;
            showTimerSelection();
        });

        btnCreate.setOnClickListener(v -> {
            boolean isValidated = true;
            if (edt_title.getText().toString().length() == 0) {
                edt_title.setError("Event title is required");
                 isValidated = false;
            }
            if (edt_address.getText().toString().length() == 0) {
                edt_address.setError("Event address is required");
                isValidated = false;
            }
            if (edt_description.getText().toString().length() == 0) {
                edt_description.setError("Event description is required");
                isValidated = false;
            }

            if (txt_start.getText().toString().length() == 0) {
                txt_start.setError("Start date is required");
                isValidated = false;
            }else {
                txt_start.setError(null);
            }

            if (edt_end_date.getText().toString().length() == 0) {
                edt_end_date.setError("End date is required");
                isValidated = false;
            }else {
                edt_end_date.setError(null);
            }

            if (txt_start_time.getText().toString().length() == 0) {
                txt_start_time.setError("Start time is required");
                isValidated = false;
            }else{
                txt_start_time.setError(null);
            }

            if (txt_end_time.getText().toString().length() == 0) {
                txt_end_time.setError("End time is required");
                isValidated = false;
            }else{
                txt_end_time.setError(null);
            }

            if (isValidated){
                Toast.makeText(getActivity(),"Api call",Toast.LENGTH_SHORT).show();
            }

        });

        btnShare.setOnClickListener(v -> {
            ShareEventAdapter adapter = new ShareEventAdapter();
            DialogPlus dialog = DialogPlus.newDialog(getContext()).setAdapter(adapter).setGravity(Gravity.BOTTOM).setCancelable(true).setExpanded(true, 900).setContentBackgroundResource(R.drawable.popup_top_corner).create();
            dialog.show();
        });

        return root;
    }

    private void showCalendar() {
        new SlyCalendarDialog()
                .setSingle(false)
                .setCallback(this)
                .setHeaderColor(getResources().getColor(R.color.colorPrimary))
                .setBackgroundColor(Color.parseColor("#ffffff"))
                .setSelectedColor(Color.parseColor("#c48395"))
                .show(getActivity().getSupportFragmentManager(), "TAG_SLYCALENDAR");
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // get selected images from selector
        if (requestCode == REQUEST_CODE) {
            if (resultCode == getActivity().RESULT_OK) {
                mResults = data.getStringArrayListExtra(SelectorSettings.SELECTOR_RESULTS);
                assert mResults != null;

                // show results in textview
                StringBuffer sb = new StringBuffer();
                sb.append(String.format("Totally %d images selected:", mResults.size())).append("\n");
                for (String result : mResults) {
                    sb.append(result).append("\n");
                }
                //   tvResults.setText(sb.toString());
                adapter.notifyDataSetChanged();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onCancelled() {

    }

    void showTimerSelection() {
        Calendar mcurrentTime = Calendar.getInstance();
        int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
        int minute = mcurrentTime.get(Calendar.MINUTE);
        TimePickerDialog mTimePicker;
        mTimePicker = new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                if (isStart){
                    txt_start_time.setError(null);
                    txt_start_time.setText(selectedHour<10?"0"+selectedHour:selectedHour + ":" + (selectedMinute<10 ?"0"+selectedMinute:selectedMinute));}
                else{
                    txt_end_time.setText(selectedHour<10?"0"+selectedHour:selectedHour + ":" + (selectedMinute<10 ?"0"+selectedMinute:selectedMinute));
                    txt_end_time.setError(null);
                }
            }
        }, hour, minute, true);//Yes 24 hour time
        mTimePicker.setTitle("Select Time");
        mTimePicker.show();


    }

    @Override
    public void onDataSelected(Calendar firstDate, Calendar secondDate, int hours, int minutes) {
        String str = new SimpleDateFormat(getString(R.string.dateFormat_first)).format(firstDate.getTime());
        if (isStart){
            txt_start.setError(null);
            txt_start.setText(str);}
        else{
            edt_end_date.setError(null);
            edt_end_date.setText(str);}
    }

    class URLAdapter extends RecyclerView.Adapter<URLViewHolder> {

        @NonNull
        @Override
        public URLViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_youtube_url, parent, false);
            return new URLViewHolder(view);
        }

        @SuppressLint("ClickableViewAccessibility")
        @Override
        public void onBindViewHolder(@NonNull URLViewHolder holder, int position) {



            holder.edt_yurl.setOnTouchListener((v, event) -> {
                final int DRAWABLE_RIGHT = 2;

                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getRawX() >= (holder.edt_yurl.getRight() - holder.edt_yurl.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        Log.d("YoutubeURL", "position: " + position);
                        yURLs.remove(position);
                        notifyDataSetChanged();

                        return true;
                    }
                }
                return false;
            });

        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public int getItemCount() {
            return yURLs.size();
        }

    }

    private class URLViewHolder extends RecyclerView.ViewHolder {
        EditText edt_yurl;

        URLViewHolder(View view) {
            super(view);
            edt_yurl = view.findViewById(R.id.edt_yurl);
        }
    }

    private class ShareEventAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return 1;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ShareEventHolder viewHolder;

            LayoutInflater mInflater = (LayoutInflater) getActivity().getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.bottom_sheet_share_event, null);
                viewHolder = new ShareEventHolder(convertView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ShareEventHolder) convertView.getTag();
            }
            return convertView;
        }
    }

    private class ShareEventHolder {
        TextView textView;

        ShareEventHolder(View view) {
            textView = view.findViewById(R.id.tv_d);
        }
    }

    private class ImagesAdapter extends RecyclerView.Adapter<ImageViewHolder> {

        @NonNull
        @Override
        public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_event_image, parent, false);
            return new ImageViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ImageViewHolder holder, int position) {

            String filepath = "";
            try {
                filepath = mResults.get(position);
                Uri uri = Uri.fromFile(new File(filepath));
                Bitmap bitmap = null;
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), uri);
                    if (bitmap != null) {
                        Bitmap bmp = Utility.getRoundedCornerBitmap(bitmap, 100);
                        Glide.with(getContext()).load(bmp).thumbnail(0.5f).transition(withCrossFade()).apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.ALL)).into(holder.iv_event);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                holder.iv_cancel.setVisibility(View.VISIBLE);
            } catch (Exception e) {
                holder.iv_cancel.setVisibility(View.GONE);
                Glide.with(getContext()).load(R.drawable.photo).thumbnail(0.5f).transition(withCrossFade()).apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.ALL)).into(holder.iv_event);
                e.printStackTrace();
            }


            holder.iv_cancel.setOnClickListener(v -> {
                mResults.remove(position);
                notifyDataSetChanged();
            });
        }

        @Override
        public int getItemCount() {
            if (mResults.size() < 3) {
                return 3;
            } else {
                return mResults.size();
            }
        }
    }

    class ImageViewHolder extends RecyclerView.ViewHolder {

        ImageView iv_event;
        ImageView iv_cancel;

        ImageViewHolder(View v) {
            super(v);
            iv_event = v.findViewById(R.id.iv_event);
            iv_cancel = v.findViewById(R.id.iv_cancel);
        }
    }
}
