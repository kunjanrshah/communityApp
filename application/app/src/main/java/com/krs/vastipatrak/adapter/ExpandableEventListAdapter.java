package com.krs.vastipatrak.adapter;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.krs.vastipatrak.R;
import com.krs.vastipatrak.app.AppController;
import com.krs.vastipatrak.model.ListEventChildData;
import com.krs.vastipatrak.model.ListEventParentData;
import com.krs.vastipatrak.utils.Common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.realm.Realm;

public class ExpandableEventListAdapter extends BaseExpandableListAdapter {

    Realm realm = AppController.getInstance().realm;
    private Context _context;
    private ArrayList<ListEventParentData> _listDataHeader = null;
    private HashMap<ListEventParentData, List<ListEventChildData>> _listDataChild = null;
    private String TAG = "ExpandableEventListAdapter";
    private SharedPreferences mSharedPreferences = null;
    private SharedPreferences.Editor mEditor = null;
    private ChildViewHolder childViewHolder;
    private GroupViewHolder groupViewHolder;

    public ExpandableEventListAdapter(Context context, ArrayList<ListEventParentData> listDataHeader, HashMap<ListEventParentData, List<ListEventChildData>> listDataChild) {
        this._context = context;
        this._listDataHeader = listDataHeader;
        this._listDataChild = listDataChild;
        mSharedPreferences = _context.getSharedPreferences(Common.Constant_Class.PREF_NAME, Context.MODE_PRIVATE);
        mEditor = mSharedPreferences.edit();
    }


    private void openImageDialog(String name, String url) {
        Dialog dialog = new Dialog(_context);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setContentView(R.layout.image_dialog);
        dialog.setTitle(name);
        ImageView image = dialog.findViewById(R.id.img_dialog);
        Glide.with(_context).load(url).apply(RequestOptions.circleCropTransform()).thumbnail(1f).into(image);
        dialog.show();
    }

    @Override
    public Object getChild(int groupPosition, int childPosititon) {
        return this._listDataChild.get(this._listDataHeader.get(groupPosition)).get(childPosititon);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

        ListEventChildData mListEventChildData = (ListEventChildData) getChild(groupPosition, childPosition);

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.list_event_item, null);
            childViewHolder = new ChildViewHolder();
            childViewHolder.recyclerView = convertView.findViewById(R.id.recycler_view);


            convertView.setTag(childViewHolder);
        } else {
            childViewHolder = (ChildViewHolder) convertView.getTag();
        }

        URLsAdapter mAdapter=new URLsAdapter(mListEventChildData.getImageUrls(), mListEventChildData.getYoutubeUrls());

        childViewHolder.recyclerView.setAdapter(mAdapter);

        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return this._listDataChild.get(this._listDataHeader.get(groupPosition)).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this._listDataHeader.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return this._listDataHeader.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        final ListEventParentData mListEventParentData = (ListEventParentData) getGroup(groupPosition);

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.list_event_group, null);
            groupViewHolder = new GroupViewHolder();
            groupViewHolder.tvEventTitle = convertView.findViewById(R.id.tvEventTitle);
            groupViewHolder.tvEventDesc = convertView.findViewById(R.id.tvEventDesc);
            groupViewHolder.tvEventLocation = convertView.findViewById(R.id.tvEventLocation);
            groupViewHolder.tvEventDate = convertView.findViewById(R.id.tvEventDate);

            convertView.setTag(groupViewHolder);
        } else {
            groupViewHolder = (GroupViewHolder) convertView.getTag();
        }

        String eventId = mListEventParentData.getEventId();
        String eventTitle = mListEventParentData.getEventTitle();
        String eventDesc = mListEventParentData.getEventDesc();
        String eventLocation = mListEventParentData.getEventLocation();
        String eventDate = mListEventParentData.getEventDate();

        groupViewHolder.tvEventTitle.setText(Common.camelCase(eventTitle));
        groupViewHolder.tvEventDesc.setText(Common.camelCase(eventDesc));
        groupViewHolder.tvEventLocation.setText(Common.camelCase(eventLocation));
        groupViewHolder.tvEventDate.setText(Common.camelCase(eventDate));

        return convertView;
    }


    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    private class ChildViewHolder {
        RecyclerView recyclerView;
    }

    private class URLsAdapter extends RecyclerView.Adapter<URLsAdapter.MyViewHolder> {
        private ArrayList<String> urlList;

        public URLsAdapter(List<String> imgList, List<String> videoList) {
            urlList = new ArrayList<>();
            this.urlList.addAll(imgList);
            this.urlList.addAll(videoList);
        }

        @Override
        public URLsAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.url_list_row, parent, false);

            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(URLsAdapter.MyViewHolder holder, final int position) {

            if (urlList.get(position).contains("youtube")) {
                holder.rl_ImgEvent.setVisibility(View.GONE);
                holder.rl_VideoEvent.setVisibility(View.VISIBLE);
            } else {
                holder.rl_ImgEvent.setVisibility(View.VISIBLE);
                holder.rl_VideoEvent.setVisibility(View.GONE);
                Glide.with(_context).load(urlList.get(position)).thumbnail(0.5f).into(holder.imgEvent);
            }

            holder.imgEvent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    openImageDialog("", urlList.get(position));
                }
            });
        }

        @Override
        public int getItemCount() {
            return urlList.size();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {
            public ImageView imgEvent;
            public VideoView videoViewEvent;
            public RelativeLayout rl_ImgEvent, rl_VideoEvent;

            public MyViewHolder(View view) {
                super(view);
                imgEvent = (ImageView) view.findViewById(R.id.imgEvent);
                videoViewEvent = (VideoView) view.findViewById(R.id.videoViewEvent);
                rl_ImgEvent = (RelativeLayout) view.findViewById(R.id.rl_ImgEvent);
                rl_VideoEvent = (RelativeLayout) view.findViewById(R.id.rl_VideoEvent);
            }
        }
    }

    private class GroupViewHolder {
        TextView tvEventTitle;
        TextView tvEventDesc;
        TextView tvEventLocation;
        TextView tvEventDate;
    }
}
