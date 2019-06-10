package com.krs.vastipatrak.activity;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.krs.vastipatrak.R;
import com.krs.vastipatrak.adapter.RecyclerAdapter;
import com.krs.vastipatrak.model.DataProvider;
import com.krs.vastipatrak.parallaxrecyclerview.HeaderLayoutManagerFixed;
import com.krs.vastipatrak.parallaxrecyclerview.ParallaxRecyclerAdapter;
import com.krs.vastipatrak.utils.Utility;

import java.util.ArrayList;
import java.util.List;

public class NearByLocationActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    public RecyclerAdapter<DataProvider.DataProvider1.Card> recyclerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_near_by);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        createCardAdapter(recyclerView);
        //recyclerAdapter = new RecyclerAdapter<DataProvider.DataProvider1.Card>(DataProvider.DataProvider1.getCardData(),null, null);
        //recyclerView.setAdapter(recyclerAdapter);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView textView;

        public ViewHolder(View itemView) {
            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.textView);
        }
    }

    private void createCardAdapter(RecyclerView recyclerView) {
        final List<String> content = new ArrayList<>();
        for (int i = 0; i < 50; i++) {
            content.add("item " + i);
        }

        final ParallaxRecyclerAdapter<String> adapter = new ParallaxRecyclerAdapter<String>(content) {
            @Override
            public void onBindViewHolderImpl(RecyclerView.ViewHolder viewHolder, ParallaxRecyclerAdapter<String> adapter, int i) {
                ((ViewHolder) viewHolder).textView.setText(adapter.getData().get(i));
            }

            @Override
            public RecyclerView.ViewHolder onCreateViewHolderImpl(ViewGroup viewGroup, final ParallaxRecyclerAdapter<String> adapter, int i) {
                return new ViewHolder(getLayoutInflater().inflate(R.layout.row_recyclerview_cards, viewGroup, false));
            }

            @Override
            public int getItemCountImpl(ParallaxRecyclerAdapter<String> adapter) {
                return content.size();
            }
        };

        adapter.setOnClickEvent(new ParallaxRecyclerAdapter.OnClickEvent() {
            @Override
            public void onClick(View v, int position) {
                Toast.makeText(NearByLocationActivity.this, "You clicked '" + position + "'", Toast.LENGTH_SHORT).show();
            }
        });

        HeaderLayoutManagerFixed layoutManagerFixed = new HeaderLayoutManagerFixed(this);
        recyclerView.setLayoutManager(layoutManagerFixed);
        View header = getLayoutInflater().inflate(R.layout.header_nearby, recyclerView, false);
        layoutManagerFixed.setHeaderIncrementFixer(header);
        adapter.setShouldClipView(false);
        adapter.setParallaxHeader(header, recyclerView);
        adapter.setData(content);
        recyclerView.setAdapter(adapter);
    }
}
