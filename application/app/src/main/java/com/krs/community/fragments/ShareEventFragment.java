package com.krs.community.fragments;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.krs.community.R;
import com.krs.community.activity.DashboardActivity;
import com.krs.community.utils.Utility;

import java.util.ArrayList;

public class ShareEventFragment extends Fragment {

    ArrayList<Integer> listImages = new ArrayList<Integer>();
    int[] Images = {R.drawable.ic_launcher_background, R.drawable.ic_launcher_background, R.drawable.ic_launcher_background, R.drawable.ic_launcher_background, R.drawable.ic_launcher_background};

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View root=inflater.inflate(R.layout.fragment_share_event,container,false);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Utility.changeStatusbarColor(getActivity(),R.color.colorBG,false);
        }

        ImageView iv_cancel=root.findViewById(R.id.iv_cancel);
        iv_cancel.setOnClickListener(v -> {
            Utility.movetoFragment(getActivity(),new SettingFragment());
        });


        listImages.clear();
        for (int i = 0; i < Images.length; i++) {
            listImages.add(Images[i]);
        }

        RecyclerView rv_images=root.findViewById(R.id.rv_images);
        rv_images.setHasFixedSize(true);
        LinearLayoutManager MyLayoutManager = new LinearLayoutManager(getActivity());
        MyLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        if (listImages.size() > 0) {
            rv_images.setAdapter(new ImagesAdapter(listImages));
        }
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


    private class ImagesAdapter extends RecyclerView.Adapter<ImageViewHolder> {

        private ArrayList<Integer> list;

        public ImagesAdapter(ArrayList<Integer> listImages) {
            list = listImages;
        }

        @NonNull
        @Override
        public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_event_image, parent, false);
            return new ImageViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ImageViewHolder holder, int position) {
            holder.iv_event.setImageResource(list.get(position));
            holder.iv_event.setTag(list.get(position));
        }

        @Override
        public int getItemCount() {
            return list.size();
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
