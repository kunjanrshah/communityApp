package com.krs.community.awareviewpager;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.krs.community.R;
import com.krs.community.activity.FamilyTreeDetailActivity;
import com.leinardi.android.speeddial.FabWithLabelView;
import com.leinardi.android.speeddial.SpeedDialActionItem;
import com.leinardi.android.speeddial.SpeedDialOverlayLayout;
import com.leinardi.android.speeddial.SpeedDialView;

/**
 * Handles keeping track of its child fragment recyclerView and it's scroll position in relation to other fragments in the viewPager
 */

public class ViewPagerFragmentBase extends Fragment {

    public static final String LESSON_TO_MODULE_BROADCAST = "lesson_to_module_broadcast";
    public static final String BROADCAST_TYPE = "key_lesson_broadcast_type";
    public static final String BROADCAST_TYPE_UPDATE_SCROLL_POSITION = "broadcast_lesson_update_module_scroll_position";
    public static final String BROADCAST_KEY_SCROLL_POSITION = "key_scroll_position";
    public static final String BROADCAST_KEY_OFFSET_POSITION = "key_offset_position";
    protected static final int ADD_ACTION_POSITION = 4;
    private static final String TAG = ViewPagerFragmentBase.class.getSimpleName();
    protected ObservableRecyclerView mRecyclerView;
    protected LinearLayoutManager mLinearLayoutManager;
    protected int scrollCumulator = 0;
    /**
     * Used to track the scroll position of all module fragments
     * recyclerViews and send the data to the lesson activity. The lesson
     * activity will then translate the appropriate views in sync with the scrolling
     * of the recyclerview.
     */
    protected RecyclerView.OnScrollListener mRecyclerScrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
            if (getActivity() instanceof FragmentListener) {
                ((FragmentListener) getActivity()).onFragmentScrollStateChanged(newState);
            }
        }

        //Scroll calculation with a recyclerView is based on position and offset.  position is the first child visible of the
        //recycler view.  Offset is the amount of that child view which is visible.  For example,
        //if position 0 is visible with offset of mHeaderHeight, that means the entire header is visible.
        //if position 0 is visible with offset of half mHeaderHeight, that means half of the header has been scrolled off screen and half is still visible


        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);

            //If the header is still in view (firstVisibleItemPosition == 0) we want to have the lesson activity's
            //contents (header image, titlebox, tabs, fab) scrolled.  The scrollCumulator will act as an offset watcher for position 0 and will be
            //passed to the lesson activity to tell all dependant views how much to be translated by.
            if (mLinearLayoutManager.findFirstVisibleItemPosition() == 0) {
                scrollCumulator = scrollCumulator + dy;
                if (getActivity() instanceof FragmentListener) {
                    ((FragmentListener) getActivity()).onFragmentHeaderChanged(0, scrollCumulator, dx, dy);
                }
            } else if (mLinearLayoutManager.findFirstVisibleItemPosition() >= 1) {

                //If the firstVisibleItemPosition is no longer 0, we know we have completely scrolled the
                //head view off of the screen.  There is no need at this point to keep translating the dependant views
                //in the lesson activity.  Notify the lesson activity of the firstVisibleItemPosition so it can make sure
                //all views are completely scrolled out of the way

                if (getActivity() instanceof FragmentListener) {
                    ((FragmentListener) getActivity()).onFragmentHeaderChanged(mLinearLayoutManager.findFirstVisibleItemPosition(), 0, dx, dy);
                }
            }
        }
    };
    private SpeedDialOverlayLayout overlay;
    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            String broadcastType = intent.getStringExtra(BROADCAST_TYPE);
            switch (broadcastType) {
                case BROADCAST_TYPE_UPDATE_SCROLL_POSITION:
                    int position = intent.getIntExtra(BROADCAST_KEY_SCROLL_POSITION, 0);
                    int offset = intent.getIntExtra(BROADCAST_KEY_OFFSET_POSITION, 0);
                    updateScrollPosition(position, offset);
                    break;
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    void setOverlay(SpeedDialOverlayLayout overlay) {
        this.overlay = overlay;
    }

    @Override
    public void onResume() {
        super.onResume();
        initiateScrollPosition();

        //view pager events and scroll position updates are sent to fragments through a local broadcast. Register the receiver
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mBroadcastReceiver,
                new IntentFilter(LESSON_TO_MODULE_BROADCAST));
    }

    @Override
    public void onStop() {
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mBroadcastReceiver);

        super.onStop();
    }

    protected int getHeaderHeight() {
        return ((FamilyTreeDetailActivity) getActivity()).getHeaderHeight();
    }

    protected void setupRecyclerView(RecyclerView recyclerView) {
        mRecyclerView = (ObservableRecyclerView) recyclerView;
        mRecyclerView.setOnScrollListener(mRecyclerScrollListener);
        mLinearLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLinearLayoutManager);

    }

    protected void initiateScrollPosition() {
        int scroll = ((FamilyTreeDetailActivity) getActivity()).getScrollPositionWatcherInt();
        int offset = ((FamilyTreeDetailActivity) getActivity()).getOffsetPositionWatcherInt();

        updateScrollPosition(scroll, offset);
    }

    /**
     * Called by the base activity whenever a user starts to swipe horizontally between module fragments.
     * This assures that the adjacent fragments have reflected any scroll change that occurred in the fragment being scrolled from
     * and makes a seamless transition from one module to the next.
     */
    public void updateScrollPosition(int position, int offset) {

        if (mLinearLayoutManager != null) {

            // if the current fragment is at its starting point(position==0 && offset==0), put the adjacent fragments at their starting point as well - fully scrolled down.
            if (position == 0 && offset == 0) {
                mLinearLayoutManager.scrollToPositionWithOffset(0, 0);

                //If the current fragment has been scrolled but the current scroll position
                //is not enough to hideOverlay the header, update the adjacent fragments to
                //the proper value
            } else if (position == 0) {
                mLinearLayoutManager.scrollToPositionWithOffset(1, getHeaderHeight() - offset);

                //If the current fragments header has been completely scrolled off the screen, update the adjacent fragments
                //to have item 1 (which is the first item after the header. also the first cell containing real content) to be
                //at the top of the screen and with no header showing.
            } else if (position >= 1) {
                //Only scroll to position 1 if the first position is 0.  If the first position is 1 or greater, we know the
                //fragments recyclerView is already taking up the whole screen.  This acts as a scroll position holder.
                if (mLinearLayoutManager.findFirstVisibleItemPosition() < 1) {
                    //TODO: the sticky header will be on top of the content.  Fix this?
                    mLinearLayoutManager.scrollToPositionWithOffset(1, 0);
                }
            }
        }
    }

    protected void initSpeedDial(SpeedDialView speedDialView) {


        speedDialView.addActionItem(new SpeedDialActionItem.Builder(R.id.fab_no_label, R.drawable.ic_link_white_24dp).create());

        Drawable drawable = AppCompatResources.getDrawable(getActivity(), R.drawable.ic_custom_color);
        FabWithLabelView fabWithLabelView = speedDialView.addActionItem(new SpeedDialActionItem.Builder(R.id
                .fab_custom_color, drawable)
                .setFabImageTintColor(ResourcesCompat.getColor(getResources(), R.color.colorPrimary, getActivity().getTheme()))
                .setLabel(R.string.label_custom_color)
                .setLabelColor(Color.WHITE)
                .setLabelBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.colorPrimary, getActivity().getTheme()))
                .create());

        if (fabWithLabelView != null) {
            fabWithLabelView.setSpeedDialActionItem(fabWithLabelView.getSpeedDialActionItemBuilder().setFabBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.material_white_1000, getActivity().getTheme()))
                    .create());
        }

        speedDialView.addActionItem(new SpeedDialActionItem.Builder(R.id.fab_long_label, R.drawable.ic_lorem_ipsum)
                .setLabel("Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor " +
                        "incididunt ut labore et dolore magna aliqua.")
                .create());

        drawable = AppCompatResources.getDrawable(getActivity(), R.drawable.ic_add_white_24dp);
        speedDialView.addActionItem(new SpeedDialActionItem.Builder(R.id.fab_add_action, drawable)
                .setFabBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.material_green_500, getActivity().getTheme()))
                .setLabel(R.string.label_add_action)
                .setLabelBackgroundColor(Color.TRANSPARENT)
                .create());

        speedDialView.addActionItem(new SpeedDialActionItem.Builder(R.id.fab_custom_theme, R.drawable.ic_theme_white_24dp)
                .setLabel(getString(R.string.label_custom_theme))
                .setTheme(R.style.AppTheme_Purple)
                .create());


        speedDialView.setOnChangeListener(new SpeedDialView.OnChangeListener() {
            @Override
            public boolean onMainActionSelected() {
                //showToast("Main action clicked!");
                Toast.makeText(getActivity(), "Main action clicked!", Toast.LENGTH_SHORT).show();
                return false; // True to keep the Speed Dial open
            }

            @Override
            public void onToggleChanged(boolean isOpen) {
                if (isOpen) {
                    overlay.show(true);
                } else {
                    overlay.hide(true);
                }
                Log.d(TAG, "Speed dial toggle state changed. Open = " + isOpen);
            }
        });

        speedDialView.setOnActionSelectedListener(new SpeedDialView.OnActionSelectedListener() {
            @Override
            public boolean onActionSelected(SpeedDialActionItem actionItem) {
                switch (actionItem.getId()) {
                    case R.id.fab_no_label:
                        Toast.makeText(getActivity(), "No label action clicked!\nClosing with animation", Toast.LENGTH_SHORT).show();
                        speedDialView.close(); // To close the Speed Dial with animation
                        return true; // false will close it without animation
                    case R.id.fab_long_label:
                        //showSnackbar(actionItem.getLabel(getActivity()) + " clicked!");
                        Toast.makeText(getActivity(), actionItem.getLabel(getActivity()) + " clicked!", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.fab_custom_color:
                        //showToast(actionItem.getLabel(getActivity()) + " clicked!\nClosing without animation.");
                        Toast.makeText(getActivity(), actionItem.getLabel(getActivity()) + " clicked!\nClosing without animation.", Toast.LENGTH_SHORT).show();
                        return false; // closes without animation (same as speedDialView.close(false); return false;)
                    case R.id.fab_custom_theme:
                        Toast.makeText(getActivity(), actionItem.getLabel(getActivity()) + " clicked!", Toast.LENGTH_SHORT).show();
                        //showToast(actionItem.getLabel(getActivity()) + " clicked!");
                        break;
                    case R.id.fab_add_action:
                        speedDialView.addActionItem(new SpeedDialActionItem.Builder(R.id.fab_replace_action,
                                R.drawable.ic_replace_white_24dp)
                                .setFabBackgroundColor(ResourcesCompat.getColor(getResources(), R.color
                                                .material_orange_500,
                                        getActivity().getTheme()))
                                .setLabel(getString(R.string.label_replace_action))
                                .create(), ADD_ACTION_POSITION);
                        break;
                    case R.id.fab_replace_action:
                        speedDialView.replaceActionItem(new SpeedDialActionItem.Builder(R.id
                                .fab_remove_action,
                                R.drawable.ic_delete_white_24dp)
                                .setLabel(getString(R.string.label_remove_action))
                                .setFabBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.colorAccent,
                                        getActivity().getTheme()))
                                .create(), ADD_ACTION_POSITION);
                        break;
                    case R.id.fab_remove_action:
                        speedDialView.removeActionItemById(R.id.fab_remove_action);
                        break;
                    default:
                        break;
                }
                return true; // To keep the Speed Dial open
            }
        });

    }


    //interface for sending scroll events back to the base activity
    public interface FragmentListener {
        void onFragmentHeaderChanged(int position, int offset, int dx, int dy);

        void onFragmentScrollStateChanged(int newState);
    }

}
