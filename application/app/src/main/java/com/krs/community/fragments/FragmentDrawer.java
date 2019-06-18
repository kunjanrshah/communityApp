package com.krs.community.fragments;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.Image;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.krs.community.R;
import com.krs.community.adapter.NavigationDrawerAdapter;
import com.krs.community.app.AppController;
import com.krs.community.model.NavDrawerItem;
import com.krs.community.utils.AppConstants;
import com.krs.community.utils.Utility;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class FragmentDrawer extends Fragment {

    @Nullable
    private static String[] titles = null;
    @Nullable
    public ActionBarDrawerToggle mDrawerToggle;
    public DrawerLayout mDrawerLayout;
    public View containerView;
    private SharedPreferences mSharedPreferences;
    private FragmentDrawerListener drawerListener;

    public FragmentDrawer() {

    }

    @NonNull
    private static List<NavDrawerItem> getData() {
        List<NavDrawerItem> data = new ArrayList<>();


        // preparing navigation drawer items
        assert titles != null;
        for (String title : titles) {
            NavDrawerItem navItem = new NavDrawerItem();
            navItem.setTitle(title);
            data.add(navItem);
        }
        return data;
    }

    public void setDrawerListener(FragmentDrawerListener listener) {
        this.drawerListener = listener;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        titles = Objects.requireNonNull(getActivity()).getResources().getStringArray(R.array.nav_drawer_labels);
    }

    @SuppressLint("Range")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mSharedPreferences = Objects.requireNonNull(getActivity()).getSharedPreferences(AppConstants.PREF_NAME, Context.MODE_PRIVATE);
        // Inflating view layout
        View layout = inflater.inflate(R.layout.fragment_navigation_drawer, container, false);
        RecyclerView recyclerView = layout.findViewById(R.id.drawerList);
        TextView tv_settings= layout.findViewById(R.id.tv_settings);
        TextView  tv_help_feedback= layout.findViewById(R.id.tv_help_feedback);
        TextView tv_contact_us= layout.findViewById(R.id.tv_contact_us);
        ImageView iv_logout= layout.findViewById(R.id.iv_logout);
        LinearLayout ll_change_lan= layout.findViewById(R.id.ll_change_lan);

        iv_logout.setOnClickListener(v -> {
           getActivity().finish();
        });

        ll_change_lan.setOnClickListener(v -> {
            mDrawerLayout.closeDrawers();
            Utility.movetoFragment(getActivity(),new ChangeLanguageFragment());
        });

        tv_settings.setOnClickListener(v -> {
            mDrawerLayout.closeDrawers();
            Utility.movetoFragment(getActivity(),new SettingFragment());
        });

        tv_help_feedback.setOnClickListener(v -> {
            mDrawerLayout.closeDrawers();
            Utility.movetoFragment(getActivity(),new HelpFragment());
        });

        tv_contact_us.setOnClickListener(v -> {
            mDrawerLayout.closeDrawers();
            Utility.movetoFragment(getActivity(),new ContactUsFragment());
        });

        NavigationDrawerAdapter adapter = new NavigationDrawerAdapter(getActivity(), getData());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        //  recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL));
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getActivity(), recyclerView, (view, position) -> {
            drawerListener.onDrawerItemSelected(view, position);
            mDrawerLayout.closeDrawers();
            //mDrawerLayout.closeDrawer(containerView);
        }));

        return layout;
    }

    private void movetoFragment(Fragment fragment) {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        Fragment oldFragment = fragmentManager.findFragmentByTag(fragment.getClass().getSimpleName());
        if (oldFragment != null) {
            fragmentManager.beginTransaction().remove(oldFragment).commit();
        }
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.pull_in_right, R.anim.push_out_left);
        fragmentTransaction.replace(R.id.container_body, fragment, fragment.getClass().getSimpleName()).commit();
    }

    public void setUp(int fragmentId, DrawerLayout drawerLayout, @NonNull final Toolbar toolbar) {
        containerView = Objects.requireNonNull(getActivity()).findViewById(fragmentId);
        mDrawerLayout = drawerLayout;

        mDrawerToggle = new ActionBarDrawerToggle(getActivity(), drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);

                if (AppController.getInstance().isUpdate) {
                    AppController.getInstance().isUpdate = false;
                   /* try {
                        Glide.with(getActivity()).load(mSharedPreferences.getString(AppConstants.PROFILE_PIC_URL, "")).apply(RequestOptions.circleCropTransform()).thumbnail(0.5f).into(img_profile);
                    } catch (Exception e) {
                        e.getMessage();
                    }*/
                    String name = mSharedPreferences.getString(AppConstants.FIRST_NAME, "") + " " + mSharedPreferences.getString(AppConstants.LAST_NAME, "");
                  //  txt_name.setText(name);
                }
                //  getActivity().invalidateOptionsMenu();
                Utility.hideKeyboard(getActivity());
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                //getActivity().invalidateOptionsMenu();
            }

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);
                toolbar.setAlpha(1 - slideOffset / 2);
            }
        };

        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerLayout.post(new Runnable() {
            @Override
            public void run() {
                mDrawerToggle.syncState();
            }
        });

       /* mDrawerToggle.setToolbarNavigationClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "sdf", Toast.LENGTH_SHORT).show();
            }
        });*/
    }

    interface ClickListener {
        void onClick(View view, int position);

    }

    public interface FragmentDrawerListener {
        void onDrawerItemSelected(View view, int position);
    }

    static class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {

        @Nullable
        private final GestureDetector gestureDetector;
        @Nullable
        private final ClickListener clickListener;

        RecyclerTouchListener(Context context, @NonNull final RecyclerView recyclerView, @Nullable final ClickListener clickListener) {
            this.clickListener = clickListener;
            gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }

                @Override
                public void onLongPress(@NonNull MotionEvent e) {
                   /* View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                    if (child != null && clickListener != null) {
                        clickListener.onLongClick(child, recyclerView.getChildPosition(child));
                    }*/
                }
            });
        }

        @Override
        public boolean onInterceptTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {

            View child = rv.findChildViewUnder(e.getX(), e.getY());
            if (child != null && clickListener != null && Objects.requireNonNull(gestureDetector).onTouchEvent(e)) {
                clickListener.onClick(child, rv.getChildPosition(child));
            }
            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {
        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

        }


    }
}
