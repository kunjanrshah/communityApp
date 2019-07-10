package com.krs.community.fragments;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

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
import com.zfdang.multiple_images_selector.ImagesSelectorActivity;
import com.zfdang.multiple_images_selector.SelectorSettings;

import java.util.ArrayList;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

public class ShareEventFragment extends Fragment {

    // class variables
    private static final int REQUEST_CODE = 123;
    //ArrayList<Integer> listImages = new ArrayList<Integer>();
    //int[] Images = {R.drawable.ic_launcher_background, R.drawable.ic_launcher_background, R.drawable.ic_launcher_background, R.drawable.ic_launcher_background, R.drawable.ic_launcher_background};
    ImagesAdapter adapter;
    private TextView tvResults;
    private ArrayList<String> mResults = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_share_event, container, false);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Utility.changeStatusbarColor(getActivity(), R.color.color_mid_light_gray, false);
        }

        ImageView iv_cancel = root.findViewById(R.id.iv_cancel);
        iv_cancel.setOnClickListener(v -> {
            Utility.movetoFragment(getActivity(), new SettingFragment());
        });

        tvResults = root.findViewById(R.id.tvResults);

        RecyclerView rv_images = root.findViewById(R.id.rv_images);
        rv_images.setHasFixedSize(true);
        LinearLayoutManager MyLayoutManager = new LinearLayoutManager(getActivity());
        MyLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        adapter = new ImagesAdapter();
        rv_images.setAdapter(adapter);
        rv_images.setLayoutManager(MyLayoutManager);

        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
        DashboardActivity.spaceNavigationView.setVisibility(View.GONE);
    }

    @Override
    public void onStop() {
        super.onStop();
        ((AppCompatActivity) getActivity()).getSupportActionBar().show();
        DashboardActivity.spaceNavigationView.setVisibility(View.VISIBLE);
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
                tvResults.setText(sb.toString());
                adapter.notifyDataSetChanged();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
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

            if (position == 0) {
                Glide.with(getContext()).load(R.drawable.photo).thumbnail(0.5f).transition(withCrossFade()).apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.ALL)).into(holder.iv_event);
            } else {
                //Utility.getRoundedCornerBitmap()
                Log.d("ShareEvent", "size: " + mResults.size() + " position:" + position);
                if (mResults.size() > 0 && mResults.size() > (position - 1)) {
                    Glide.with(getContext()).load(mResults.get(position - 1)).thumbnail(0.5f).transition(withCrossFade()).apply(RequestOptions.centerInsideTransform()).apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.ALL)).into(holder.iv_event);
                }
            }

            holder.iv_event.setOnClickListener(v -> {
                if (position == 0) {
                    Intent intent = new Intent(getActivity(), ImagesSelectorActivity.class);
                    intent.putExtra(SelectorSettings.SELECTOR_MAX_IMAGE_NUMBER, 15);
                    intent.putExtra(SelectorSettings.SELECTOR_MIN_IMAGE_SIZE, 100000);
                    intent.putExtra(SelectorSettings.SELECTOR_SHOW_CAMERA, true);
                    intent.putStringArrayListExtra(SelectorSettings.SELECTOR_INITIAL_SELECTED_LIST, mResults);
                    startActivityForResult(intent, REQUEST_CODE);
                }
            });
        }

        @Override
        public int getItemCount() {

            if (mResults.size() > 0) {
                return (mResults.size() + 1);
            } else {
                return 1;
            }
        }
    }

    class ImageViewHolder extends RecyclerView.ViewHolder {

        ImageView iv_event;

        ImageViewHolder(View v) {
            super(v);
            iv_event = v.findViewById(R.id.iv_event);
        }
    }
}
