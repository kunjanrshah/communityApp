package com.krs.vastipatrak.adapter;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.chauthai.swipereveallayout.SwipeRevealLayout;
import com.chauthai.swipereveallayout.ViewBinderHelper;
import com.krs.vastipatrak.R;
import com.krs.vastipatrak.utils.Utility;

import java.util.List;

public class ProfileAdapter extends RecyclerView.Adapter{

    private List<String> mDataSet;
    private LayoutInflater mInflater;
    private Context mContext;
    private final ViewBinderHelper binderHelper = new ViewBinderHelper();
    RecyclerView lstProfile;

    public ProfileAdapter(Context context,RecyclerView lstProfile, List<String> dataSet) {
        mContext = context;
        mDataSet = dataSet;
        mInflater = LayoutInflater.from(context);
        this.lstProfile=lstProfile;
        // uncomment if you want to open only one row at a time
        // binderHelper.setOpenOnlyOne(true);
    }

    private class ViewHolder extends RecyclerView.ViewHolder {
        private SwipeRevealLayout swipeLayout;
        private View frontLayout;
        private View deleteLayout;
        private TextView textView;

        ViewHolder(View itemView) {
            super(itemView);
            swipeLayout = (SwipeRevealLayout) itemView.findViewById(R.id.swipe_layout);
            frontLayout = itemView.findViewById(R.id.front_layout);
            deleteLayout = itemView.findViewById(R.id.delete_layout);
            textView = (TextView) itemView.findViewById(R.id.text);
        }

        void bind(final String data) {
            deleteLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mDataSet.remove(getAdapterPosition());
                    notifyItemRemoved(getAdapterPosition());
                }
            });

           // textView.setText(data);

            frontLayout.setOnClickListener(view -> {

                FragmentTransaction fragmentTransaction=initFragmentTransaction(view);
                String displayText = "" + data + " clicked";
                Toast.makeText(mContext, displayText, Toast.LENGTH_SHORT).show();
                Log.d("RecyclerAdapter", displayText);
            });
        }
    }



    private FragmentTransaction initFragmentTransaction(View view)
    {
        float toY = view.getResources().getDimensionPixelOffset(R.dimen.details_toolbar_container_height) - view.getHeight() / 2f;
        float positions[] = new float[3];
        positions[0] = view.getX();
        positions[1] = view.getY() + Utility.getToolbarHeight(mContext);
        positions[2] = toY;
        int adapterPosition = lstProfile.getChildAdapterPosition(view);

    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.row_list, parent, false);
        return new ViewHolder(view);
    }

    /**
     * Only if you need to restore open/close state when the orientation is changed.
     * Call this method in {@link //android.app.Activity#onSaveInstanceState(Bundle)}
     */
    public void saveStates(Bundle outState) {
        binderHelper.saveStates(outState);
    }

    /**
     * Only if you need to restore open/close state when the orientation is changed.
     * Call this method in {@link //android.app.Activity#onRestoreInstanceState(Bundle)}
     */
    public void restoreStates(Bundle inState) {
        binderHelper.restoreStates(inState);
    }


    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder h, int position) {
        final ViewHolder holder = (ViewHolder) h;

        if (mDataSet != null && 0 <= position && position < mDataSet.size()) {
            final String data = mDataSet.get(position);

            // Use ViewBindHelper to restore and save the open/close state of the SwipeRevealView
            // put an unique string id as value, can be any string which uniquely define the data
            binderHelper.bind(holder.swipeLayout, data);

            // Bind your data here
            holder.bind(data);
        }
    }

    @Override
    public int getItemCount() {
        if (mDataSet == null)
            return 0;
        return mDataSet.size();
    }
}
