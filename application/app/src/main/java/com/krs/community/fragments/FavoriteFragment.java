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
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

public class FavoriteFragment extends Fragment {

    private ActionModeCallback actionModeCallback;
    private ActionMode actionMode;
    private SparseBooleanArray selectedItems;
    private SparseBooleanArray animationItemsIndex;
    private boolean reverseAllAnimations = false;
    private static int currentSelectedIndex = -1;
    private RecyclerView rv_favorite;
    private List<Message> messages = new ArrayList<>();
    ParallaxRecyclerAdapter<Message> adapter = null;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Utility.changeStatusbarColor(getActivity(), R.color.colorBG, false);
        }

        View root = inflater.inflate(R.layout.fragmnet_favorite, container, false);

        selectedItems = new SparseBooleanArray();
        animationItemsIndex = new SparseBooleanArray();
        actionModeCallback = new ActionModeCallback();
        adapter = new ParallaxRecyclerAdapter<Message>(messages) {
            @Override
            public void onBindViewHolderImpl(RecyclerView.ViewHolder viewHolder, ParallaxRecyclerAdapter<Message> adapter, int position) {
                Message message = messages.get(position);
                String name = "Kunjan Shah";
                ListViewHolder holder = (ListViewHolder) viewHolder;
                holder.tv_name.setText(name);
                holder.boomMenuButton.clearBuilders();

                for (int i = 0; i < holder.boomMenuButton.getPiecePlaceEnum().pieceNumber(); i++) {
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
                return new ListViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.favorite_list_item, viewGroup, false));
            }

            @Override
            public int getItemCountImpl(ParallaxRecyclerAdapter<Message> adapter) {
                return messages.size();
            }
        };

        LinearLayoutManager MyLayoutManager = new LinearLayoutManager(getActivity());
        rv_favorite = root.findViewById(R.id.rv_favorite);
        rv_favorite.setLayoutManager(MyLayoutManager);
        rv_favorite.setItemAnimator(new DefaultItemAnimator());
        rv_favorite.setHasFixedSize(true);
        View header = LayoutInflater.from(getActivity()).inflate(R.layout.header_favorite, container, false);
        ImageView iv_cancel = header.findViewById(R.id.iv_cancel);
        iv_cancel.setOnClickListener(v -> {
            Utility.movetoFragment(getActivity(), new DashboardFragment());
        });

        adapter.setParallaxHeader(header, rv_favorite);
        rv_favorite.setAdapter(adapter);

        getInbox();
        return root;
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

    private void getInbox() {
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
            message.setColor(Utility.getRandomMaterialColor(getActivity(), "400"));
            messages.add(message);
        }
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
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

    private class ListViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener {

        BoomMenuButton boomMenuButton;
        RelativeLayout iconContainer, iconBack, iconFront;
        TextView iconText, tv_name;
        ImageView imgProfile;
        LinearLayout messageContainer;

        ListViewHolder(View v) {
            super(v);
            tv_name = v.findViewById(R.id.tv_name);
            boomMenuButton = v.findViewById(R.id.boomMenuButton);
            iconText = v.findViewById(R.id.icon_text);
            iconBack = v.findViewById(R.id.icon_back);
            iconFront = v.findViewById(R.id.icon_front);
            imgProfile = v.findViewById(R.id.icon_profile);
            messageContainer = v.findViewById(R.id.message_container);
            iconContainer = v.findViewById(R.id.icon_container);
            v.setOnLongClickListener(this);
        }

        @Override
        public boolean onLongClick(View v) {
            enableActionMode(getAdapterPosition());
            v.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
            return true;
        }
    }

    private class ActionModeCallback implements ActionMode.Callback {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            mode.getMenuInflater().inflate(R.menu.fav_action_mode, menu);

            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {

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

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            clearSelections();

            actionMode = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                Utility.changeStatusbarColor(getActivity(), R.color.colorBG, false);
            }
            rv_favorite.post((Runnable) () -> {
                resetAnimationIndex();
                 adapter.notifyDataSetChanged();
            });
        }
    }

    private void applyClickEvents(ListViewHolder holder, final int position) {
        holder.iconContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (actionMode == null) {
                    actionMode = getActivity().startActionMode(actionModeCallback);
                }
                toggleSelection(position);
            }
        });


        holder.messageContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
        });

        holder.messageContainer.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                enableActionMode(position);
                view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
                return true;
            }
        });
    }

    private void applyProfilePicture(ListViewHolder holder, Message message) {
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

    private void applyIconAnimation(ListViewHolder holder, int position) {
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

    private void resetCurrentIndex() {
        currentSelectedIndex = -1;
    }

    private void resetAnimationIndex() {
        reverseAllAnimations = false;
        animationItemsIndex.clear();
    }

    private void resetIconYAxis(View view) {
        if (view.getRotationY() != 0) {
            view.setRotationY(0);
        }
    }

    private void enableActionMode(int position) {
        if (actionMode == null) {
            actionMode = getActivity().startActionMode(actionModeCallback);
        }
        toggleSelection(position);
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

    private void clearSelections() {
        reverseAllAnimations = true;
        selectedItems.clear();
        adapter.notifyDataSetChanged();
    }

    private void removeData(int position) {
        messages.remove(position);
        resetCurrentIndex();
    }

    private void deleteMessages() {
        resetAnimationIndex();
        List<Integer> selectedItemPositions = getSelectedItems();
        for (int i = selectedItemPositions.size() - 1; i >= 0; i--) {
            removeData(selectedItemPositions.get(i));
        }
        adapter.notifyDataSetChanged();
    }

    private List<Integer> getSelectedItems() {
        List<Integer> items =
                new ArrayList<>(selectedItems.size());
        for (int i = 0; i < selectedItems.size(); i++) {
            items.add(selectedItems.keyAt(i));
        }
        return items;
    }

    private int getSelectedItemCount() {
        return selectedItems.size();
    }

}
