package com.krs.community.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.chauthai.swipereveallayout.ViewBinderHelper;
import com.krs.community.R;
import com.krs.community.utils.Utility;
import com.nightonke.boommenu.BoomMenuButton;

import java.util.List;

public class FilterResultAdapter extends RecyclerView.Adapter {

    private List<String> mDataSet;
    private LayoutInflater mInflater;
    private Context mContext;
    private final ViewBinderHelper binderHelper = new ViewBinderHelper();

    public FilterResultAdapter(Context context, List<String> dataSet) {
        mContext = context;
        mDataSet = dataSet;
        mInflater = LayoutInflater.from(context);

        // uncomment if you want to open only one row at a time
        // binderHelper.setOpenOnlyOne(true);
    }
    private class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView iv_profile;
        private BoomMenuButton boomMenuButton;
        private TextView tv_name;
        private TextView tv_area;
        private TextView tv_role;
        private TextView tv_mobile;
        private TextView tv_email;

        ViewHolder(View itemView) {
            super(itemView);
            iv_profile= itemView.findViewById(R.id.iv_profile);
            boomMenuButton = itemView.findViewById(R.id.bmb1);
            tv_name = itemView.findViewById(R.id.tv_name);
            tv_area = itemView.findViewById(R.id.tv_area);
            tv_role = itemView.findViewById(R.id.tv_role);
            tv_mobile= itemView.findViewById(R.id.tv_mobile);
            tv_email = itemView.findViewById(R.id.tv_email);
        }
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.filter_result_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder h, int position) {
        final ViewHolder holder = (ViewHolder) h;


        if (mDataSet != null && 0 <= position && position < mDataSet.size()) {
            final String data = mDataSet.get(position);
            holder.tv_name.setText("Kunjan Shah");
            holder.boomMenuButton.clearBuilders();
            for(int i=0; i<holder.boomMenuButton.getPiecePlaceEnum().pieceNumber(); i++)
            {
                holder.boomMenuButton.addBuilder(Utility.getTextInsideCircleButtonBuilder());
            }
            holder.boomMenuButton.setOnClickListener(v -> {
                holder.boomMenuButton.boom();
            });
        }
    }

    @Override
    public int getItemCount() {
        if (mDataSet == null)
            return 0;
        return mDataSet.size();
    }
}
