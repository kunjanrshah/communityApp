package com.krs.community.fragments;

import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.krs.community.R;
import com.krs.community.adapter.MessagesAdapter;
import com.krs.community.model.Message;
import com.krs.community.utils.Utility;

import java.util.ArrayList;
import java.util.List;

public class NonActivesFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, MessagesAdapter.MessageAdapterListener {

    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private MessagesAdapter mAdapter;
    private List<Message> messages = new ArrayList<>();
    private ActionModeCallback actionModeCallback;
    private ActionMode actionMode;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        View root = inflater.inflate(R.layout.fragment_nonactives, container, false);

        FloatingActionButton fab = (FloatingActionButton) root.findViewById(R.id.fab);
        fab.setOnClickListener(view -> Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG).setAction("Action", null).show());

        recyclerView = (RecyclerView) root.findViewById(R.id.recycler_view);
        swipeRefreshLayout = (SwipeRefreshLayout) root.findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(this);

        mAdapter = new MessagesAdapter(getContext(), messages, this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

        actionModeCallback = new ActionModeCallback();

        return root;
    }

    // deleting the messages from recycler view
    private void deleteMessages() {
        mAdapter.resetAnimationIndex();
        List<Integer> selectedItemPositions = mAdapter.getSelectedItems();
        for (int i = selectedItemPositions.size() - 1; i >= 0; i--) {
            mAdapter.removeData(selectedItemPositions.get(i));
        }
        mAdapter.notifyDataSetChanged();
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
            message.setColor(getRandomMaterialColor("400"));
            messages.add(message);
        }

        mAdapter.notifyDataSetChanged();
        swipeRefreshLayout.setRefreshing(false);
    }

    /**
     * chooses a random color from array.xml
     */
    private int getRandomMaterialColor(String typeColor) {
        int returnColor = Color.GRAY;
        int arrayId = getResources().getIdentifier("mdcolor_" + typeColor, "array", getActivity().getPackageName());

        if (arrayId != 0) {
            TypedArray colors = getResources().obtainTypedArray(arrayId);
            int index = (int) (Math.random() * colors.length());
            returnColor = colors.getColor(index, Color.GRAY);
            colors.recycle();
        }
        return returnColor;
    }

    @Override
    public void onIconClicked(int position) {
        if (actionMode == null) {
            actionMode = getActivity().startActionMode(actionModeCallback);
        }
        toggleSelection(position);
    }

    private void toggleSelection(int position) {
        mAdapter.toggleSelection(position);
        int count = mAdapter.getSelectedItemCount();

        if (count == 0) {
            actionMode.finish();
            ((AppCompatActivity) getActivity()).getSupportActionBar().show();
        } else {
            ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
            actionMode.setTitle(String.valueOf(count));
            actionMode.invalidate();
        }
    }

    @Override
    public void onIconImportantClicked(int position) {
        // Star icon is clicked,
        // mark the message as important
        Message message = messages.get(position);
        message.setImportant(!message.isImportant());
        messages.set(position, message);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onMessageRowClicked(int position) {
        // verify whether action mode is enabled or not
        // if enabled, change the row state to activated
        if (mAdapter.getSelectedItemCount() > 0) {
            enableActionMode(position);
        } else {
            // read the message which removes bold from the row
            Message message = messages.get(position);
            message.setRead(true);
            messages.set(position, message);
            mAdapter.notifyDataSetChanged();

            Toast.makeText(getActivity(), "Read: " + message.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRowLongClicked(int position) {
        // long press is performed, enable action mode
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

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            mAdapter.clearSelections();
            swipeRefreshLayout.setEnabled(true);
            actionMode = null;
            ((AppCompatActivity) getActivity()).getSupportActionBar().show();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                Utility.changeStatusbarColor(getActivity(),R.color.colorBG,false);
            }
            recyclerView.post(new Runnable() {
                @Override
                public void run() {
                    mAdapter.resetAnimationIndex();
                    // mAdapter.notifyDataSetChanged();
                }
            });
        }
    }

}
