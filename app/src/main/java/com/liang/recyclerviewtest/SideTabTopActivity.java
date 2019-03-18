package com.liang.recyclerviewtest;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import com.liang.jtab.indicator.JIndicator;
import com.liang.jtab.listener.OnTabSelectedListener;
import com.liang.listener.OnSideCheckedListener;
import com.liang.recyclerviewtest.adapter.TabAdapter;
import com.liang.recyclerviewtest.bean.SortModel;
import com.liang.side.Side;
import com.liang.widget.FloatingLabelDecoration;
import com.liang.widget.JTabLayout;
import com.liang.widget.LinearDividerDecoration;
import com.liang.widget.adapter.BaseAdapter;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

public class SideTabTopActivity extends AppCompatActivity implements Side, OnTabSelectedListener {
    private JTabLayout tabView;
    private RecyclerView contentView;
    private CountryAdapter adapter;
    private TabAdapter tabAdapter;
    private OnSideCheckedListener onSideCheckedListener;
    private ArrayList<String> tags = new ArrayList<>();
    private int selectedPosition = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_side_tab_top);
        tabView = findViewById(R.id.tab_view);
        contentView = findViewById(R.id.content_view);
        initTabView();
        initContentView();
    }

    private void initTabView() {
        JIndicator indicator = new JIndicator();
        indicator.setColor(getResources().getColor(android.R.color.holo_orange_dark));
        indicator.setHeight(10);
        indicator.setTransitionScroll(true);
        indicator.setRadius(5);
        tabView.setIndicator(indicator);
        tabView.addOnTabSelectedListener(this);
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
        this.tags.addAll(tags);
        for (String tag : tags) {
            tabView.addTab(new TabMenu(this).setTitle(tag));
        }
    }

    @Override
    public void setChecked(String tag) {
        int position = tags.indexOf(tag);
        Log.e("SideTabTopActivity", "setChecked: position" + position);
        if (selectedPosition == position) {
            return;
        }
        selectedPosition = position;
        tabView.setCurrentItem(position, false);
    }

    @Override
    public void OnSideCheckedListener(OnSideCheckedListener onSideCheckedListener) {
        this.onSideCheckedListener = onSideCheckedListener;
    }

    @Override
    public void onTabSelected(int position) {
        if (onSideCheckedListener != null) {
            onSideCheckedListener.onItemChecked(tags.get(position));
        }
    }

    @Override
    public void onTabReselected(int position) {

    }

    private class ContentOnItemClickListener implements BaseAdapter.OnItemClickListener<SortModel> {

        @Override
        public void onItemClick(SortModel item, int position) {
            Toast.makeText(SideTabTopActivity.this, "onItemClick position: " + position + ", name: " + item.getName(),
                    Toast.LENGTH_SHORT).show();
        }
    }
}
