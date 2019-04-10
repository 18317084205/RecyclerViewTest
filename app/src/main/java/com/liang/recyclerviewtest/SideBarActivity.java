package com.liang.recyclerviewtest;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.liang.recyclerviewtest.bean.SortModel;
import com.liang.widget.LabelDecoration;
import com.liang.widget.FloatingLabelDecoration;
import com.liang.widget.LinearDividerDecoration;
import com.liang.widget.SideBar;
import com.liang.widget.adapter.LabelRecyclerAdapter;
import com.liang.widget.listener.OnItemClickListener;

import java.util.ArrayList;
import java.util.List;

public class SideBarActivity extends AppCompatActivity implements OnItemClickListener<SortModel>
        , LabelRecyclerAdapter.OnLabelClickListener, SideBar.OnTextDialogListener {

    private RecyclerView recyclerView;
    private CountryAdapter adapter;
    private TextView textDialog;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = findViewById(R.id.recycler);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        textDialog = findViewById(R.id.textDialog);
//        adapter = new MyAdapter(recyclerView);
        MySideBar sidebar = findViewById(R.id.sidebar);
        sidebar.setOnTextDialogListener(this);
        adapter = new CountryAdapter(recyclerView, sidebar);
        adapter.setOnItemClickListener(this);
        recyclerView.setAdapter(adapter);
//        adapter.addAll(getDummyDataSet());
        FloatingLabelDecoration decoration = new FloatingLabelDecoration(adapter);
//        recyclerView.addItemDecoration(decoration);
        Drawable drawable = getDrawable(R.mipmap.ic_launcher);
        recyclerView.addItemDecoration(new LabelDecoration(adapter));
        recyclerView.addItemDecoration(new LinearDividerDecoration(1, Color.GRAY));
//        adapter.setOnLabelClickListener(this, decoration);
        initCountryList();
    }

    private String[] getDummyDataSet() {
        return getResources().getStringArray(R.array.animals);
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
    public void onLabelClick(View headerView, int position, long id) {
        Toast.makeText(SideBarActivity.this, "onLabelClick position: " + position + ", id: " + id,
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onItemClick(SortModel item, int position) {
        Toast.makeText(SideBarActivity.this, "onItemClick position: " + position + ", id: " + item,
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void dismiss() {
        textDialog.setVisibility(View.GONE);
    }

    @Override
    public void show(String s, int y) {
        textDialog.setVisibility(View.VISIBLE);
        textDialog.setText(s);
    }
}
