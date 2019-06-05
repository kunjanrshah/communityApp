package com.krs.vastipatrak.fragments;


import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.krs.vastipatrak.R;
import com.krs.vastipatrak.model.FavProfiles;
import com.krs.vastipatrak.model.RecentMenu;
import com.smarteist.autoimageslider.DefaultSliderView;
import com.smarteist.autoimageslider.IndicatorAnimations;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderLayout;

import java.util.ArrayList;

public class DashboardFragment extends Fragment {

    SliderLayout sliderLayout;
    RecyclerView lstFavProfile;
    RecyclerView lstMenu;
    EditText edt_search;
    ArrayList<FavProfiles> listProfiles = new ArrayList<>();
    ArrayList<RecentMenu> listMenus = new ArrayList<>();
    String[] ProfileNames = {"Rajendra", "Tejas", "Kunjan", "Mukund", "Kushal"};
    int[] ProfileImages = {R.drawable.man_reg, R.drawable.man_reg, R.drawable.man_reg, R.drawable.man_reg, R.drawable.man_reg};
    String[] MenuNames = {"My Profile", "Donation", "Matrimony", "QR Code", "Search"};
    int[] MenuImages = {R.drawable.dark_icon, R.drawable.dark_icon, R.drawable.dark_icon, R.drawable.dark_icon, R.drawable.dark_icon};
    private boolean isTouch = false;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_dashboard, container, false);

        lstMenu = rootView.findViewById(R.id.lstMenu);
        lstFavProfile = rootView.findViewById(R.id.lstFavProfile);
        edt_search = rootView.findViewById(R.id.edt_search);
        sliderLayout = rootView.findViewById(R.id.imageSlider);
        sliderLayout.setIndicatorAnimation(IndicatorAnimations.SWAP); //set indicator animation by using SliderLayout.IndicatorAnimations. :WORM or THIN_WORM or COLOR or DROP or FILL or NONE or SCALE or SCALE_DOWN or SLIDE and SWAP!!
        sliderLayout.setSliderTransformAnimation(SliderAnimations.FADETRANSFORMATION);
        sliderLayout.setScrollTimeInSec(3); //set scroll delay in seconds :
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Home");
        edt_search.setInputType(InputType.TYPE_NULL);
        edt_search.setKeyListener(null);
        edt_search.setOnTouchListener((v, event) -> {

            if (!isTouch) {
                isTouch = true;
                Fragment fragment = new SearchListFragment();
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.setCustomAnimations(R.anim.pull_in_left, R.anim.push_out_right);
                fragmentTransaction.replace(R.id.container_body, fragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }

            return false;
        });


        setSliderViews();
        setRecentActivity();
        setFavoriteList();

        return rootView;
    }


    @Override
    public void onResume() {
        super.onResume();
        isTouch=false;
    }

    private void setRecentActivity() {
        listMenus.clear();
        for (int i = 0; i < MenuNames.length; i++) {
            RecentMenu item = new RecentMenu();
            item.setCardName(MenuNames[i]);
            item.setImageResourceId(MenuImages[i]);
            listMenus.add(item);
        }

        lstMenu.setHasFixedSize(true);
        LinearLayoutManager MyLayoutManager = new LinearLayoutManager(getActivity());
        MyLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        if (listMenus.size() > 0 & lstMenu != null) {
            lstMenu.setAdapter(new RecentMenuAdapter(listMenus));
        }
        lstMenu.setLayoutManager(MyLayoutManager);
    }

    private void setFavoriteList() {
        listProfiles.clear();
        for (int i = 0; i < ProfileNames.length; i++) {
            FavProfiles item = new FavProfiles();
            item.setCardName(ProfileNames[i]);
            item.setImageResourceId(ProfileImages[i]);
            item.setIsfav(0);
            item.setIsturned(0);
            listProfiles.add(item);
        }

        lstFavProfile.setHasFixedSize(true);
        LinearLayoutManager MyLayoutManager = new LinearLayoutManager(getActivity());
        MyLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        if (listProfiles.size() > 0 & lstFavProfile != null) {
            lstFavProfile.setAdapter(new FavProfileAdapter(listProfiles));
        }
        lstFavProfile.setLayoutManager(MyLayoutManager);
    }

    private void setSliderViews() {

        for (int i = 0; i <= 3; i++) {

            DefaultSliderView sliderView = new DefaultSliderView(getActivity());


            switch (i) {
                case 0:
                    sliderView.setImageDrawable(R.drawable.ic_launcher_background);
                    break;
                case 1:
                    sliderView.setImageUrl("https://images.pexels.com/photos/218983/pexels-photo-218983.jpeg?auto=compress&cs=tinysrgb&dpr=2&h=750&w=1260");
                    break;
                case 2:
                    sliderView.setImageUrl("https://images.pexels.com/photos/747964/pexels-photo-747964.jpeg?auto=compress&cs=tinysrgb&h=750&w=1260");
                    break;
                case 3:
                    sliderView.setImageUrl("https://images.pexels.com/photos/929778/pexels-photo-929778.jpeg?auto=compress&cs=tinysrgb&dpr=2&h=750&w=1260");
                    break;
            }

            sliderView.setImageScaleType(ImageView.ScaleType.CENTER_CROP);
            sliderView.setDescription("The quick brown fox jumps over the lazy dog.\n" + "Jackdaws love my big sphinx of quartz. " + (i + 1));
            final int finalI = i;

            sliderView.setOnSliderClickListener(sliderView1 ->
            {
                Fragment fragment = new NewsFragment();
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.container_body, fragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
                Toast.makeText(getActivity(), "This is slider " + (finalI + 1), Toast.LENGTH_SHORT).show();

            });

            //at last add this view in your layout :
            sliderLayout.addSliderView(sliderView);
        }
    }


    public class RecentMenuAdapter extends RecyclerView.Adapter<RecentActivityViewHolder> {
        private ArrayList<RecentMenu> list;

        public RecentMenuAdapter(ArrayList<RecentMenu> Data) {
            list = Data;
        }

        @Override
        public RecentActivityViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            // create a new view
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recent_menu, parent, false);
            RecentActivityViewHolder holder = new RecentActivityViewHolder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(final RecentActivityViewHolder holder, int position) {

            holder.titleTextView.setText(list.get(position).getCardName());
            holder.coverImageView.setImageResource(list.get(position).getImageResourceId());
            holder.coverImageView.setTag(list.get(position).getImageResourceId());

        }

        @Override
        public int getItemCount() {
            return list.size();
        }
    }

    class RecentActivityViewHolder extends RecyclerView.ViewHolder {

        TextView titleTextView;
        ImageView coverImageView;

        RecentActivityViewHolder(View v) {
            super(v);
            titleTextView = v.findViewById(R.id.titleTextView);
            coverImageView = v.findViewById(R.id.coverImageView);
        }
    }

    public class FavProfileAdapter extends RecyclerView.Adapter<FavProfileViewHolder> {
        private ArrayList<FavProfiles> list;

        FavProfileAdapter(ArrayList<FavProfiles> Data) {
            list = Data;
        }

        @Override
        public FavProfileViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            // create a new view
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fav_profle, parent, false);
            return new FavProfileViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final FavProfileViewHolder holder, int position) {
            holder.titleTextView.setText(list.get(position).getCardName());
            holder.coverImageView.setImageResource(list.get(position).getImageResourceId());
            holder.coverImageView.setTag(list.get(position).getImageResourceId());
            holder.likeImageView.setTag(R.drawable.ic_like);
        }

        @Override
        public int getItemCount() {
            return list.size();
        }
    }



    class FavProfileViewHolder extends RecyclerView.ViewHolder {

        TextView titleTextView;
        ImageView coverImageView;
        ImageView likeImageView;
        ImageView shareImageView;

        FavProfileViewHolder(View v) {
            super(v);
            titleTextView = v.findViewById(R.id.titleTextView);
            coverImageView = v.findViewById(R.id.coverImageView);
            likeImageView = v.findViewById(R.id.likeImageView);
            shareImageView = v.findViewById(R.id.shareImageView);
            likeImageView.setOnClickListener(v12 -> {

                int id = (int) likeImageView.getTag();
                if (id == R.drawable.ic_like) {

                    likeImageView.setTag(R.drawable.ic_liked);
                    likeImageView.setImageResource(R.drawable.ic_liked);

                    Toast.makeText(getActivity(), titleTextView.getText() + " added to favourites", Toast.LENGTH_SHORT).show();

                } else {
                    likeImageView.setTag(R.drawable.ic_like);
                    likeImageView.setImageResource(R.drawable.ic_like);
                    Toast.makeText(getActivity(), titleTextView.getText() + " removed from favourites", Toast.LENGTH_SHORT).show();
                }

            });

            shareImageView.setOnClickListener(v1 -> {

                Uri imageUri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + getResources().getResourcePackageName(coverImageView.getId()) + '/' + "drawable" + '/' + getResources().getResourceEntryName((int) coverImageView.getTag()));

                Intent shareIntent = new Intent();
                shareIntent.setAction(Intent.ACTION_SEND);
                shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri);
                shareIntent.setType("image/jpeg");
                startActivity(Intent.createChooser(shareIntent, "Send"));
            });
        }
    }

}
