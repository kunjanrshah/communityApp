package com.krs.community.fragments;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.squti.guru.Guru;
import com.google.gson.Gson;
import com.krishna.debug_tools.activity.ActivityDebugTools;
import com.krs.community.R;
import com.krs.community.activity.FavoriteProfileActivity;
import com.krs.community.activity.QRCodeActivity;
import com.krs.community.activity.RegisterActivty;
import com.krs.community.model.FavProfiles;
import com.krs.community.model.Member;
import com.krs.community.utils.ExpandableHeightGridView;
import com.krs.community.utils.Utility;
import com.smarteist.autoimageslider.DefaultSliderView;
import com.smarteist.autoimageslider.IndicatorAnimations;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderLayout;

import java.util.ArrayList;

public class DashboardFragment extends Fragment {

    SliderLayout sliderLayout;
    RecyclerView lstFavProfile;
    ExpandableHeightGridView gridMenu;
    EditText edt_search;
    ArrayList<FavProfiles> listProfiles = new ArrayList<>();
    String[] ProfileNames = {"Rajendra", "Tejas", "Kunjan", "Kushal","Mukund"};
    int[] ProfileImages = {R.drawable.man_reg, R.drawable.man_reg, R.drawable.man_reg, R.drawable.man_reg, R.drawable.man_reg};

    String[] MenuNames = {"Browse","My QRCode", "By Distance", "Matrimony", "Documents", "Paytm","App Tour", "Admins", "NonActives", "Add New", "Share Event"};
    int[] MenuImages = {R.drawable.dark_icon, R.drawable.dark_icon, R.drawable.dark_icon, R.drawable.dark_icon, R.drawable.dark_icon,R.drawable.dark_icon, R.drawable.dark_icon, R.drawable.dark_icon, R.drawable.dark_icon, R.drawable.dark_icon, R.drawable.dark_icon};
    private boolean isTouch = false;

    public static final String TAG=DashboardFragment.class.getSimpleName();

    @RequiresApi(api = Build.VERSION_CODES.M)
    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_dashboard, container, false);
        Utility.changeStatusbarColor(getActivity(),R.color.colorPrimary,true);
        lstFavProfile = rootView.findViewById(R.id.lstFavProfile);
        gridMenu = rootView.findViewById(R.id.grid_view);
        gridMenu.setExpanded(true);
        edt_search = rootView.findViewById(R.id.edt_search);
        sliderLayout = rootView.findViewById(R.id.imageSlider);
        sliderLayout.setIndicatorAnimation(IndicatorAnimations.SWAP); //set indicator animation by using SliderLayout.IndicatorAnimations. :WORM or THIN_WORM or COLOR or DROP or FILL or NONE or SCALE or SCALE_DOWN or SLIDE and SWAP!!
        sliderLayout.setSliderTransformAnimation(SliderAnimations.FADETRANSFORMATION);
        sliderLayout.setScrollTimeInSec(3); //set scroll delay in seconds :
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Home");
        edt_search.setInputType(InputType.TYPE_NULL);
        edt_search.setKeyListener(null);
        edt_search.setOnTouchListener((v, event) -> {
            if (!isTouch) {
                isTouch = true;
                Utility.movetoFragment(getActivity(),new SearchListFragment());
            }
            return false;
        });

        setSliderViews();
        setFavoriteList();
        gridMenu.setAdapter(new MenuAdapter(getActivity()));

        TextView tv_all_favorites=rootView.findViewById(R.id.tv_all_favorites);
        tv_all_favorites.setOnClickListener(v -> {
            Intent mIntent=new Intent(getActivity(), FavoriteProfileActivity.class);
            startActivity(mIntent);
            Utility.fade(getActivity());
        });

        TextView tv_all_news=rootView.findViewById(R.id.tv_all_news);
        tv_all_news.setOnClickListener(v -> {
            Utility.movetoFragment(getActivity(),new NewsListFragment());
        });

        Utility.changeStatusbarColor(getActivity(),R.color.white,false);
        return rootView;
    }


    @Override
    public void onResume() {
        super.onResume();
        isTouch = false;
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

            sliderView.setOnSliderClickListener(sliderView1 -> {
                Toast.makeText(getActivity(), "This is slider " + (finalI + 1), Toast.LENGTH_SHORT).show();
                Utility.movetoFragment(getActivity(),new NewsListFragment());

                /*Fragment fragment = new NewsFragment();
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.container_body, fragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();*/
            });

            //at last add this view in your layout :
            sliderLayout.addSliderView(sliderView);
        }
    }


    private void setFavoriteList() {
        listProfiles.clear();
        for (int i = 0; i < ProfileNames.length; i++) {
            FavProfiles item = new FavProfiles();
            item.setCardName(ProfileNames[i]);
            item.setImageResourceId(ProfileImages[i]);
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


    class MenuAdapter extends BaseAdapter {
        private Context mContext;

        MenuAdapter(Context c) {
            mContext = c;
        }

        @Override
        public int getCount() {
            Log.d(DashboardFragment.class.getSimpleName(),"len: "+MenuImages.length);
            return MenuImages.length;
        }

        @Override
        public Object getItem(int position) {
            return MenuImages[position];
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolderItem viewHolder;

            LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.dashboard_menu, parent, false);
                viewHolder = new ViewHolderItem();
                viewHolder.image = convertView.findViewById(R.id.image);
                viewHolder.textView = convertView.findViewById(R.id.name);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolderItem) convertView.getTag();
            }

            viewHolder.image.setImageResource(MenuImages[position]);
            viewHolder.textView.setText(MenuNames[position]);

            convertView.setOnClickListener(v -> {
                switch (position)
                {
                   case 0:
                        Utility.movetoFragment(getActivity(),new BrowseByCityFragment());
                       break;
                    case 1:

                        Bundle mBundle=new Bundle();
                        String loginMember= Guru.getString(getString(R.string.loginUser),"");
                        Member member=new Gson().fromJson(loginMember, Member.class);
                        mBundle.putSerializable(getString(R.string.member),member);
                        Intent intent1  = new Intent(getActivity(), QRCodeActivity.class);
                        intent1.putExtras(mBundle);
                        startActivity(intent1);
                        Utility.fade(getActivity());
                        break;
                    case 2:
                        Utility.movetoFragment(getActivity(),new SearchByDistanceFragment());
                        break;
                    case 3:
                        Utility.movetoFragment(getActivity(),new MatrimonyFragment());
                        break;
                    case 4:
                        startActivity(new Intent(getActivity(), ActivityDebugTools.class));
                        //Utility.movetoFragment(getActivity(),new DocumentsFragment());
                        break;
                    case 5:
                        Utility.movetoFragment(getActivity(),new PaytmFragment());
                        break;
                    case 6:
                        Utility.movetoFragment(getActivity(),new TourVideoFragment());
                        break;
                    case 7:
                        Utility.movetoFragment(getActivity(),new AdminsFragment());
                        break;
                    case 8:
                        Utility.movetoFragment(getActivity(),new NonActivesFragment());
                        break;
                    case 9:
                        Intent intent=new Intent(getActivity(),RegisterActivty.class);
                        startActivity(intent);
                        Utility.fade(getActivity());

                        break;
                    case 10:
                        Utility.movetoFragment(getActivity(),new ShareEventFragment());
                        break;
                }

            });

            return convertView;
        }
    }

    class ViewHolderItem {
        ImageView image;
        TextView textView;
    }

    public class FavProfileAdapter extends RecyclerView.Adapter<FavProfileViewHolder> {
        private ArrayList<FavProfiles> list;

        FavProfileAdapter(ArrayList<FavProfiles> Data) {
            list = Data;
        }

        @Override
        public FavProfileViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fav_profle, parent, false);
            return new FavProfileViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final FavProfileViewHolder holder, int position) {
            holder.titleTextView.setText(list.get(position).getCardName());
            holder.coverImageView.setImageResource(list.get(position).getImageResourceId());
            holder.coverImageView.setTag(list.get(position).getImageResourceId());
        }

        @Override
        public int getItemCount() {
            return list.size();
        }
    }

    class FavProfileViewHolder extends RecyclerView.ViewHolder {

        TextView titleTextView;
        ImageView coverImageView;

        FavProfileViewHolder(View v) {
            super(v);
            titleTextView = v.findViewById(R.id.titleTextView);
            coverImageView = v.findViewById(R.id.coverImageView);
        }
    }
}
