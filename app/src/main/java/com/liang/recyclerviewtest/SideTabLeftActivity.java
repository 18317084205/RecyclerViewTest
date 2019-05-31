package com.liang.recyclerviewtest;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.liang.listener.OnSideCheckedListener;
import com.liang.recyclerviewtest.adapter.TabAdapter;
import com.liang.recyclerviewtest.bean.SortModel;
import com.liang.side.Side;
import com.liang.widget.FloatingLabelDecoration;
import com.liang.widget.LinearDividerDecoration;
import com.liang.widget.adapter.BaseAdapter;
import com.liang.widget.listener.OnItemClickListener;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

public class SideTabLeftActivity extends AppCompatActivity implements Side {

    private RecyclerView tabView;
    private RecyclerView contentView;
    private CountryAdapter adapter;
    private TabAdapter tabAdapter;
    private OnSideCheckedListener onSideCheckedListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_side_tab_left);
        tabView = findViewById(R.id.tab_view);
        contentView = findViewById(R.id.content_view);
        initTabView();
        initContentView();
    }
    public void onClick(View v) {
        String[] country = getResources().getStringArray(R.array.country);
        String[] countryCode = getResources().getStringArray(R.array.country_code);
        List<SortModel> dataList = new ArrayList<>();
        if (country.length != countryCode.length) {
            finish();
            return;
        }
        for (int i = 0; i < country.length; i++) {
            SortModel sortModel = new SortModel();
            sortModel.setName(country[i]);
            sortModel.setData(countryCode[i]);
            dataList.add(sortModel);
        }
        adapter.reset(dataList);
    }
    private void initTabView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        tabView.setLayoutManager(layoutManager);
        tabAdapter = new TabAdapter(tabView);
        tabAdapter.setOnItemClickListener(new TabOnItemClickListener());
        tabView.setAdapter(tabAdapter);
        tabView.addItemDecoration(new LinearDividerDecoration(1, Color.GRAY));
    }

    private void initContentView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        contentView.setLayoutManager(layoutManager);
        adapter = new CountryAdapter(contentView, this);
        adapter.setOnItemClickListener(new ContentOnItemClickListener());
        contentView.setAdapter(adapter);
        FloatingLabelDecoration decoration = new FloatingLabelDecoration(adapter);
        contentView.addItemDecoration(decoration);
        contentView.addItemDecoration(new LinearDividerDecoration(1, Color.GRAY));
        initCountryList();
    }

    public void initCountryList() {
        String[] country = getResources().getStringArray(R.array.country);
        String[] countryCode = getResources().getStringArray(R.array.country_code);
        List<SortModel> dataList = new ArrayList<>();
        if (country.length != countryCode.length) {
            finish();
            return;
        }
        for (int i = 0; i < country.length; i++) {
            SortModel sortModel = new SortModel();
            sortModel.setName(country[i]);
            sortModel.setData(countryCode[i]);
            dataList.add(sortModel);
        }
        adapter.addAll(dataList);
    }


    @Override
    public void setTags(LinkedHashSet<String> tags) {
        tabAdapter.addAll(tags);
    }

    @Override
    public void setChecked(String tag) {
        int position = tabAdapter.getItems().indexOf(tag);
        tabAdapter.setCheckedPosition(position);
//        tabView.smoothScrollToPosition(position);
    }

    @Override
    public void OnSideCheckedListener(OnSideCheckedListener onSideCheckedListener) {
        this.onSideCheckedListener = onSideCheckedListener;
    }


    private class ContentOnItemClickListener implements OnItemClickListener<SortModel> {

        @Override
        public void onItemClick(SortModel item, int position) {
            Toast.makeText(SideTabLeftActivity.this, "onItemClick position: " + position + ", name: " + item.getName(),
                    Toast.LENGTH_SHORT).show();
        }
    }

    private class TabOnItemClickListener implements OnItemClickListener<String> {

        @Override
        public void onItemClick(String item, int position) {
            Log.e("SideTabLeftActivity", "onItemClick position: " + position);
            tabAdapter.setCheckedPosition(position);
            if (onSideCheckedListener != null) {
                onSideCheckedListener.onItemChecked(item);
            }
        }
    }

}
