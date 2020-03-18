package com.krs.community.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.widget.AppCompatButton;

import com.krs.community.R;

public class UploadDialogAdapter extends BaseAdapter {

    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private UploadFileListner uploadListner = null;

    public UploadDialogAdapter(Context context) {
        mContext = context;
        mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void setListener(UploadFileListner listener) {
        uploadListner = listener;
    }

    @Override
    public int getCount() {
        return 1;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = mLayoutInflater.inflate(R.layout.upload_file_bottom_sheet, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.btnUpload.setOnClickListener(v -> {
            String str = viewHolder.edtName.getText().toString().trim();
            if (!str.isEmpty()) {
                uploadListner.cancelDialog();
                uploadListner.upload(str);
            } else {
                Toast.makeText(mContext, R.string.pleasename, Toast.LENGTH_SHORT).show();
            }
        });

        viewHolder.ivCancel.setOnClickListener(v -> {
            uploadListner.cancelDialog();
        });

        return convertView;
    }

    public interface UploadFileListner {
        void upload(String name);

        void cancelDialog();
    }

    static class ViewHolder {

        AppCompatButton btnUpload;
        EditText edtName;
        ImageView ivCancel;

        ViewHolder(View view) {
            edtName = view.findViewById(R.id.edt_name);
            btnUpload = view.findViewById(R.id.btn_upload);
            ivCancel = view.findViewById(R.id.iv_cancel);
        }
    }
}
