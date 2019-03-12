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
import android.widget.Toast;

import com.liang.widget.LabelDecoration;
import com.liang.widget.FloatingLabelDecoration;
import com.liang.widget.LinearDividerDecoration;
import com.liang.widget.adapter.BaseAdapter;
import com.liang.widget.adapter.LabelBaseAdapter;

public class MainActivity extends AppCompatActivity implements BaseAdapter.OnItemClickListener<String>, LabelBaseAdapter.OnLabelClickListener {

    RecyclerView recyclerView;
    private MyAdapter adapter;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = findViewById(R.id.recycler);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new MyAdapter(recyclerView);
        adapter.setOnItemClickListener(this);
        recyclerView.setAdapter(adapter);
        adapter.addAll(getDummyDataSet());
        FloatingLabelDecoration decoration = new FloatingLabelDecoration(adapter);
        recyclerView.addItemDecoration(decoration);
        Drawable drawable = getDrawable(R.mipmap.ic_launcher);
//        recyclerView.addItemDecoration(new LabelDecoration(adapter));
        recyclerView.addItemDecoration(new LinearDividerDecoration(10,Color.YELLOW));
        adapter.setOnLabelClickListener(this, decoration);
    }

    private String[] getDummyDataSet() {
        return getResources().getStringArray(R.array.animals);
    }

    public void onItemClick(View view, int position) {

    }

    @Override
    public void onLabelClick(View headerView, int position, long id) {
        Toast.makeText(MainActivity.this, "onLabelClick position: " + position + ", id: " + id,
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onItemClick(String item, int position) {
        Toast.makeText(MainActivity.this, "onItemClick position: " + position + ", id: " + item,
                Toast.LENGTH_SHORT).show();
    }
}
