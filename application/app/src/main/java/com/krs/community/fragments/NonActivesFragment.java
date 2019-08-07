package com.krs.community.fragments;

import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.krs.community.R;
import com.krs.community.model.Message;
import com.krs.community.parallaxrecyclerview.ParallaxRecyclerAdapter;
import com.krs.community.utils.FlipAnimator;
import com.krs.community.utils.Utility;
import com.nightonke.boommenu.BoomMenuButton;

import java.util.ArrayList;
import java.util.List;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

public class NonActivesFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ParallaxRecyclerAdapter<Message> adapter;
    private List<Message> messages = new ArrayList<>();
    private ActionModeCallback actionModeCallback;
    private ActionMode actionMode;
    private SparseBooleanArray selectedItems;
    private SparseBooleanArray animationItemsIndex;
    private boolean reverseAllAnimations = false;
    private int currentSelectedIndex = -1;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_nonactives, container, false);

        selectedItems = new SparseBooleanArray();
        animationItemsIndex = new SparseBooleanArray();

      //  ll_title=root.findViewById(R.id.ll_title);
        recyclerView = root.findViewById(R.id.recycler_view);
        swipeRefreshLayout = root.findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(this);

        adapter=new ParallaxRecyclerAdapter<Message>(messages) {
            @Override
            public void onBindViewHolderImpl(RecyclerView.ViewHolder viewHolder, ParallaxRecyclerAdapter<Message> adapter, int position) {

                Message message = messages.get(position);
                String name="Kunjan Shah";

                MyViewHolder holder= (MyViewHolder) viewHolder;

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
                return new MyViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_row_nonactives, viewGroup, false));
            }

            @Override
            public int getItemCountImpl(ParallaxRecyclerAdapter<Message> adapter) {
                return messages.size();
            }
        };

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        View header = LayoutInflater.from(getActivity()).inflate(R.layout.header_nonactives, container, false);
        ImageView iv_cancel = header.findViewById(R.id.iv_cancel);
        iv_cancel.setOnClickListener(v -> {
            Utility.movetoFragment(getActivity(), new DashboardFragment());
        });

        adapter.setParallaxHeader(header, recyclerView);
        recyclerView.setAdapter(adapter);

        actionModeCallback = new ActionModeCallback();
        getInbox();
        return root;
    }

    private void applyClickEvents(MyViewHolder holder, final int position) {
        holder.iconContainer.setOnClickListener(view -> onIconClicked(position));

        holder.messageContainer.setOnClickListener(view -> onMessageRowClicked(position));

        holder.messageContainer.setOnLongClickListener(view -> {

            onRowLongClicked(position);
            view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
            return true;
        });
    }

    private void applyProfilePicture(MyViewHolder holder, Message message) {
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

    private void applyIconAnimation(MyViewHolder holder, int position) {
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

    private void resetCurrentIndex() {
        currentSelectedIndex = -1;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener {
        TextView iconText,tv_name;
        ImageView imgProfile;
        LinearLayout messageContainer;
        RelativeLayout iconContainer, iconBack, iconFront;
        BoomMenuButton boomMenuButton;

        MyViewHolder(View view) {
            super(view);

            boomMenuButton= view.findViewById(R.id.boomMenuButton);
            tv_name=  view.findViewById(R.id.tv_name);
            iconText =  view.findViewById(R.id.icon_text);
            iconBack =  view.findViewById(R.id.icon_back);
            iconFront =  view.findViewById(R.id.icon_front);
            imgProfile =  view.findViewById(R.id.icon_profile);
            messageContainer =  view.findViewById(R.id.message_container);
            iconContainer =  view.findViewById(R.id.icon_container);
            view.setOnLongClickListener(this);
        }

        @Override
        public boolean onLongClick(View view) {
            enableActionMode(getAdapterPosition());
            view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
            return true;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
    }

    @Override
    public void onStop() {
        super.onStop();
        ((AppCompatActivity) getActivity()).getSupportActionBar().show();
    }

    private void resetAnimationIndex() {
        reverseAllAnimations = false;
        animationItemsIndex.clear();
    }

    private void deleteMessages() {
        resetAnimationIndex();
        List<Integer> selectedItemPositions = getSelectedItems();
        for (int i = selectedItemPositions.size() - 1; i >= 0; i--) {
            removeData(selectedItemPositions.get(i));
        }
        adapter.notifyDataSetChanged();
    }

    private void removeData(int position) {
        messages.remove(position);
        resetCurrentIndex();
    }

    @Override
    public void onRefresh() {
        // swipe refresh is performed, fetch the messages again
        getInbox();
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

    private void onIconClicked(int position) {
        if (actionMode == null) {
            actionMode = getActivity().startActionMode(actionModeCallback);
        }
        toggleSelection(position);
    }

    private List<Integer> getSelectedItems() {
        List<Integer> items =
                new ArrayList<>(selectedItems.size());
        for (int i = 0; i < selectedItems.size(); i++) {
            items.add(selectedItems.keyAt(i));
        }
        return items;
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

    private void toggleSelection(int position) {
        toggleSelected(position);
        int count = getSelectedItemCount();

        if (count == 0) {
            actionMode.finish();
           // ll_title.setVisibility(View.VISIBLE);
        } else {
           // ll_title.setVisibility(View.GONE);
            actionMode.setTitle(String.valueOf(count));
            actionMode.invalidate();
        }
    }

    private int getSelectedItemCount() {
        return selectedItems.size();
    }

    private void onMessageRowClicked(int position) {
        // verify whether action mode is enabled or not
        // if enabled, change the row state to activated
        if (getSelectedItemCount() > 0) {
            enableActionMode(position);
        } else {
            // read the message which removes bold from the row
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

    private void enableActionMode(int position) {
        if (actionMode == null) {
            actionMode = getActivity().startActionMode(actionModeCallback);
        }
        toggleSelection(position);
    }

    private class ActionModeCallback implements ActionMode.Callback {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            mode.getMenuInflater().inflate(R.menu.menu_action_mode, menu);

            // disable swipe refresh if action mode is enabled
            swipeRefreshLayout.setEnabled(false);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
           /* ViewGroup   decorView = (ViewGroup) getActivity().getWindow().getDecorView().findViewById(R.id.action_mode_bar);
            decorView.setBackgroundColor(getResources().getColor(R.color.colorBG));

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                Utility.changeStatusbarColor(getActivity(),R.color.colorBG,true);
            }*/

            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                case R.id.action_delete:
                    // delete all the selected messages
                    deleteMessages();
                    mode.finish();
                    return true;

                default:
                    return false;
            }
        }

        public void clearSelections() {
            reverseAllAnimations = true;
            selectedItems.clear();
            adapter.notifyDataSetChanged();
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            clearSelections();
            swipeRefreshLayout.setEnabled(true);
            actionMode = null;
           // ll_title.setVisibility(View.VISIBLE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                Utility.changeStatusbarColor(getActivity(),R.color.colorBG,false);
            }
            recyclerView.post(new Runnable() {
                @Override
                public void run() {
                    resetAnimationIndex();
                    // mAdapter.notifyDataSetChanged();
                }
            });
        }
    }
}
