package com.krs.community.fragments;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.Gravity;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.krs.community.R;
import com.krs.community.activity.DashboardActivity;
import com.krs.community.adapter.AtoZBottomAdapter;
import com.krs.community.model.Message;
import com.krs.community.parallaxrecyclerview.ParallaxRecyclerAdapter;
import com.krs.community.utils.FlipAnimator;
import com.krs.community.utils.Utility;
import com.nightonke.boommenu.BoomMenuButton;
import com.orhanobut.dialogplus.DialogPlus;

import java.util.ArrayList;
import java.util.List;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

public class SmartFilterResult extends Fragment implements SwipeRefreshLayout.OnRefreshListener{

    private RecyclerView rv_filters;
    private ShimmerFrameLayout mShimmerViewContainer;
    //private FilterResultAdapter mAdapter;
    private List<Message> messages = new ArrayList<>();
    private ActionModeCallback actionModeCallback;
    private ActionMode actionMode;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ParallaxRecyclerAdapter<Message> adapter;
    private SparseBooleanArray selectedItems;
    private SparseBooleanArray animationItemsIndex;
    private boolean reverseAllAnimations = false;
    private int currentSelectedIndex = -1;

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_filter_result, container, false);
        selectedItems = new SparseBooleanArray();
        animationItemsIndex = new SparseBooleanArray();

        mShimmerViewContainer = rootView.findViewById(R.id.shimmer_view_container);
        rv_filters =rootView.findViewById(R.id.lstFilter);
        swipeRefreshLayout = rootView.findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(this);
        actionModeCallback = new ActionModeCallback();

        adapter=new ParallaxRecyclerAdapter<Message>(messages) {
            @Override
            public void onBindViewHolderImpl(RecyclerView.ViewHolder viewHolder, ParallaxRecyclerAdapter<Message> adapter, int position) {

                Message message = messages.get(position);
                String name="Kunjan Shah";

                ViewHolder holder= (ViewHolder) viewHolder;

                holder.tv_name.setText(name);
                holder.boomMenuButton.clearBuilders();

                for(int i=0; i<holder.boomMenuButton.getPiecePlaceEnum().pieceNumber(); i++)
                {
                    holder.boomMenuButton.addBuilder(Utility.getTextInsideCircleButtonBuilder());
                }
                holder.boomMenuButton.setOnClickListener(v -> {
                    holder.boomMenuButton.boom();
                });

                holder.iconText.setText(name.substring(0, 1));
                holder.itemView.setActivated(selectedItems.get(position, false));
                applyIconAnimation(holder, position);
                applyProfilePicture(holder, message);
                applyClickEvents(holder, position);
            }

            @Override
            public RecyclerView.ViewHolder onCreateViewHolderImpl(ViewGroup viewGroup, ParallaxRecyclerAdapter<Message> adapter, int i) {
                return new ViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.filter_result_list, viewGroup, false));
            }

            @Override
            public int getItemCountImpl(ParallaxRecyclerAdapter<Message> adapter) {
                if (messages == null)
                    return 0;
                return messages.size();
            }
        };

        View header = LayoutInflater.from(getActivity()).inflate(R.layout.header_smart_filter, container, false);
        ImageView iv_cancel = header.findViewById(R.id.iv_cancel);
        iv_cancel.setOnClickListener(v -> {
            Utility.movetoFragment(getActivity(), new ExpandableFilterListFragment());
        });

        ImageView iv_export=header.findViewById(R.id.iv_export);
        ImageView iv_atoz=header.findViewById(R.id.iv_atoz);
        iv_atoz.setOnClickListener(v -> {
            AtoZBottomAdapter adapter=new AtoZBottomAdapter(getContext());
            DialogPlus dialog = DialogPlus.newDialog(getContext())
                    .setAdapter(adapter)
                    .setGravity(Gravity.BOTTOM)
                    .setCancelable(true)
                    .setExpanded(true)
                    .setContentBackgroundResource(R.drawable.popup_top_corner)
                    .create();
            dialog.show();
        });

        adapter.setParallaxHeader(header, rv_filters);

        setupList();
        getInbox();
        return rootView;
    }

    private void setupList() {
        rv_filters.setLayoutManager(new LinearLayoutManager(getActivity()));
        // mAdapter = new FilterResultAdapter(getActivity(), messages, this);
        rv_filters.setAdapter(adapter);

        new Handler().postDelayed(() -> {
            mShimmerViewContainer.stopShimmerAnimation();
            mShimmerViewContainer.setVisibility(View.GONE);
        },2000);
    }

    private void getInbox() {
        swipeRefreshLayout.setRefreshing(true);
        messages.clear();

        for (int i = 0; i < 20; i++) {
            Message message = new Message();
            message.setId(1);
            message.setImportant(false);
            message.setMessage("Now android supports multiple voice recogonization");
            message.setPicture("https://api.androidhive.info/json/google.png");
            message.setRead(false);
            message.setTimestamp("10:30 AM");
            message.setFrom("Google Alerts");
            message.setSubject("Google Alert - android");
            message.setColor(Utility.getRandomMaterialColor(getActivity(),"400"));
            messages.add(message);
        }

        adapter.notifyDataSetChanged();
        swipeRefreshLayout.setRefreshing(false);
    }

    private void applyClickEvents(ViewHolder holder, final int position) {
        holder.iconContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onIconClicked(position);
            }
        });


        holder.messageContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onMessageRowClicked(position);
            }
        });

        holder.messageContainer.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {

                onRowLongClicked(position);
                view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
                return true;
            }
        });
    }

    private void applyProfilePicture(ViewHolder holder, Message message) {
        if (!TextUtils.isEmpty(message.getPicture())) {
            Glide.with(getActivity()).load(message.getPicture())
                    .thumbnail(0.5f)
                    .transition(withCrossFade())
                    .apply(RequestOptions.circleCropTransform())
                    .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.ALL))
                    .into(holder.imgProfile);
            holder.imgProfile.setColorFilter(null);
            holder.iconText.setVisibility(View.GONE);
        } else {
            holder.imgProfile.setImageResource(R.drawable.bg_circle);
            holder.imgProfile.setColorFilter(message.getColor());
            holder.iconText.setVisibility(View.VISIBLE);
        }
    }

    private void applyIconAnimation(ViewHolder holder, int position) {
        if (selectedItems.get(position, false)) {
            holder.iconFront.setVisibility(View.GONE);
            resetIconYAxis(holder.iconBack);
            holder.iconBack.setVisibility(View.VISIBLE);
            holder.iconBack.setAlpha(1);
            if (currentSelectedIndex == position) {
                FlipAnimator.flipView(getActivity(), holder.iconBack, holder.iconFront, true);
                resetCurrentIndex();
            }
        } else {
            holder.iconBack.setVisibility(View.GONE);
            resetIconYAxis(holder.iconFront);
            holder.iconFront.setVisibility(View.VISIBLE);
            holder.iconFront.setAlpha(1);
            if ((reverseAllAnimations && animationItemsIndex.get(position, false)) || currentSelectedIndex == position) {
                FlipAnimator.flipView(getActivity(), holder.iconBack, holder.iconFront, false);
                resetCurrentIndex();
            }
        }
    }

    private void resetIconYAxis(View view) {
        if (view.getRotationY() != 0) {
            view.setRotationY(0);
        }
    }

    private void resetAnimationIndex() {
        reverseAllAnimations = false;
        animationItemsIndex.clear();
    }

    private void toggleSelected(int pos) {
        currentSelectedIndex = pos;
        if (selectedItems.get(pos, false)) {
            selectedItems.delete(pos);
            animationItemsIndex.delete(pos);
        } else {
            selectedItems.put(pos, true);
            animationItemsIndex.put(pos, true);
        }
        adapter.notifyItemChanged(pos);
    }

    private void clearSelections() {
        reverseAllAnimations = true;
        selectedItems.clear();
        adapter.notifyDataSetChanged();
    }

    private int getSelectedItemCount() {
        return selectedItems.size();
    }

    private List<Integer> getSelectedItems() {
        List<Integer> items =
                new ArrayList<>(selectedItems.size());
        for (int i = 0; i < selectedItems.size(); i++) {
            items.add(selectedItems.keyAt(i));
        }
        return items;
    }

    private void removeData(int position) {
        messages.remove(position);
        resetCurrentIndex();
    }

    private void resetCurrentIndex() {
        currentSelectedIndex = -1;
    }


    private class ViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener{

        private ImageView iv_profile;
        private BoomMenuButton boomMenuButton;
        private TextView tv_area;
        private TextView tv_role;
        private TextView tv_mobile;
        private TextView tv_email;

        RelativeLayout iconContainer, iconBack, iconFront;
        TextView iconText,tv_name;
        ImageView imgProfile;
        LinearLayout messageContainer;


        ViewHolder(View itemView) {
            super(itemView);
            iv_profile= itemView.findViewById(R.id.iv_profile);
            boomMenuButton = itemView.findViewById(R.id.bmb1);
            tv_name = itemView.findViewById(R.id.tv_name);
            tv_area = itemView.findViewById(R.id.tv_area);
            tv_role = itemView.findViewById(R.id.tv_role);
            tv_mobile= itemView.findViewById(R.id.tv_mobile);
            tv_email = itemView.findViewById(R.id.tv_email);

            tv_name = itemView.findViewById(R.id.tv_name);
            iconText =  itemView.findViewById(R.id.icon_text);
            iconBack =  itemView.findViewById(R.id.icon_back);
            iconFront =  itemView.findViewById(R.id.icon_front);
            imgProfile =  itemView.findViewById(R.id.icon_profile);
            messageContainer =  itemView.findViewById(R.id.message_container);
            iconContainer =  itemView.findViewById(R.id.icon_container);
            itemView.setOnLongClickListener(this);

        }

        @Override
        public boolean onLongClick(View v) {
            onRowLongClicked(getAdapterPosition());
            v.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
            return true;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mShimmerViewContainer.startShimmerAnimation();
        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
        DashboardActivity.spaceNavigationView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onStop() {
        super.onStop();
        ((AppCompatActivity) getActivity()).getSupportActionBar().show();
        mShimmerViewContainer.stopShimmerAnimation();
    }

    private void deleteMessages() {
        resetAnimationIndex();
        List<Integer> selectedItemPositions = getSelectedItems();
        for (int i = selectedItemPositions.size() - 1; i >= 0; i--) {
            removeData(selectedItemPositions.get(i));
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onRefresh() {
        getInbox();
    }

    private class ActionModeCallback implements ActionMode.Callback {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            mode.getMenuInflater().inflate(R.menu.menu_action_mode, menu);

            swipeRefreshLayout.setEnabled(false);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {


            return true;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                case R.id.action_delete:
                    deleteMessages();
                    mode.finish();
                    return true;

                default:
                    return false;
            }
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            clearSelections();
            swipeRefreshLayout.setEnabled(true);
            actionMode = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                Utility.changeStatusbarColor(getActivity(),R.color.colorBG,false);
            }
            rv_filters.post(new Runnable() {
                @Override
                public void run() {
                    resetAnimationIndex();
                }
            });
        }
    }

    private void toggleSelection(int position) {
        toggleSelected(position);
        int count = getSelectedItemCount();

        if (count == 0) {
            actionMode.finish();
        } else {
            actionMode.setTitle(String.valueOf(count));
            actionMode.invalidate();
        }
    }

    private void enableActionMode(int position) {
        if (actionMode == null) {
            actionMode = getActivity().startActionMode(actionModeCallback);
        }
        toggleSelection(position);
    }


    private void onIconClicked(int position) {
        if (actionMode == null) {
            actionMode = getActivity().startActionMode(actionModeCallback);
        }
        toggleSelection(position);
    }


    private void onIconImportantClicked(int position) {
        Message message = messages.get(position);
        message.setImportant(!message.isImportant());
        messages.set(position, message);
        adapter.notifyDataSetChanged();
    }

    private void onMessageRowClicked(int position) {
        if (getSelectedItemCount() > 0) {
            enableActionMode(position);
        } else {
            Message message = messages.get(position);
            message.setRead(true);
            messages.set(position, message);
            adapter.notifyDataSetChanged();

            Toast.makeText(getActivity(), "Read: " + message.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void onRowLongClicked(int position) {
        enableActionMode(position);
    }
}
