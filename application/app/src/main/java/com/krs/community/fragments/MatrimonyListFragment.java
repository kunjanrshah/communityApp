package com.krs.community.fragments;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.krs.community.R;
import com.krs.community.activity.DashboardActivity;
import com.krs.community.parallaxrecyclerview.ParallaxRecyclerAdapter;
import com.krs.community.utils.Utility;
import com.nightonke.boommenu.BoomMenuButton;
import com.nightonke.boommenu.Util;

import org.json.JSONObject;

import java.util.ArrayList;

public class MatrimonyListFragment extends Fragment {


    ArrayList<JSONObject> lstMatrimony;

    @SuppressLint("ClickableViewAccessibility")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View root=inflater.inflate(R.layout.fragment_matrimonylist,container,false);

        ParallaxRecyclerAdapter<JSONObject> adapter = new ParallaxRecyclerAdapter<JSONObject>(lstMatrimony) {
            @Override
            public void onBindViewHolderImpl(RecyclerView.ViewHolder viewHolder, ParallaxRecyclerAdapter<JSONObject> adapter, int position) {
                ListViewHolder holder= (ListViewHolder) viewHolder;
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

            @Override
            public RecyclerView.ViewHolder onCreateViewHolderImpl(ViewGroup viewGroup, ParallaxRecyclerAdapter<JSONObject> adapter, int i) {
                return new ListViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.matrimony_profile, viewGroup, false));
            }

            @Override
            public int getItemCountImpl(ParallaxRecyclerAdapter<JSONObject> adapter) {
                return 10;
            }
        };

        RecyclerView listMatrimony=root.findViewById(R.id.listMatrimony);
        LinearLayoutManager MyLayoutManager = new LinearLayoutManager(getActivity());
        listMatrimony.setLayoutManager(MyLayoutManager);
        listMatrimony.setItemAnimator(new DefaultItemAnimator());
        listMatrimony.setHasFixedSize(true);
        View header = LayoutInflater.from(getActivity()).inflate(R.layout.header_matrimony, container, false);
        ImageView iv_cancel = header.findViewById(R.id.iv_cancel);
        iv_cancel.setOnClickListener(v -> {
            Utility.movetoFragment(getActivity(), new DashboardFragment());
        });

        EditText edtSearch=header.findViewById(R.id.edtSearch);
        edtSearch.setOnTouchListener((v, event) -> {
            final int DRAWABLE_RIGHT = 2;
            if(event.getAction() == MotionEvent.ACTION_UP) {
                if(event.getRawX() >= (edtSearch.getRight() - edtSearch.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                    openFilter();
                    return true;
                }
            }
            return false;
        });
        adapter.setParallaxHeader(header, listMatrimony);
        listMatrimony.setAdapter(adapter);

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


    private void openFilter()
    {
        // custom dialog
        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.filter_matrimony);
        Button btnmale=dialog.findViewById(R.id.btnmale);
        Button btnfemale=dialog.findViewById(R.id.btnfemale);

        btnmale.setOnClickListener(v -> {
            btnmale.setBackground(getResources().getDrawable(R.drawable.round_corner_primary));
            btnmale.setTextColor(getResources().getColor(R.color.white));
            btnfemale.setBackground(getResources().getDrawable(R.drawable.round_corner_white));
            btnfemale.setTextColor(getResources().getColor(R.color.black));
        });

        btnfemale.setOnClickListener(v -> {
            btnmale.setBackground(getResources().getDrawable(R.drawable.round_corner_white));
            btnmale.setTextColor(getResources().getColor(R.color.black));
            btnfemale.setBackground(getResources().getDrawable(R.drawable.round_corner_primary));
            btnfemale.setTextColor(getResources().getColor(R.color.white));
        });

        ImageView iv_cancel=dialog.findViewById(R.id.iv_cancel);
        iv_cancel.setOnClickListener(v -> {
            dialog.dismiss();
        });

        Button btn_search=dialog.findViewById(R.id.btn_search);
        btn_search.setOnClickListener(v -> {
            dialog.dismiss();
        });

        dialog.show();

    }


    /*public class ListMatrimonyAdapter extends RecyclerView.Adapter<ListViewHolder>
    {
        @NonNull
        @Override
        public ListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.matrimony_profile, parent, false);
            return new ListViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ListViewHolder holder, int position) {
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

        @Override
        public int getItemCount() {
            return 10;
        }
    }*/


    class ListViewHolder extends RecyclerView.ViewHolder {

       TextView tv_name;
        BoomMenuButton boomMenuButton;
        ListViewHolder(View v) {
            super(v);
            tv_name = v.findViewById(R.id.tv_name);
            boomMenuButton= v.findViewById(R.id.boomMenuButton);
        }
    }

}
