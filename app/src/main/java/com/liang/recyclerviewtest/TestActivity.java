package com.liang.recyclerviewtest;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.widget.EditText;

import com.liang.recyclerviewtest.adapter.TestAdapter;
import com.liang.recyclerviewtest.bean.SortModel;
import com.liang.widget.IntervalDecoration;

import java.util.ArrayList;
import java.util.List;

public class TestActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    TestAdapter adapter;
    List<SortModel> sortModels = new ArrayList<>();
    private EditText edit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        recyclerView = findViewById(R.id.recyclerView);
        edit = findViewById(R.id.edit_num);
        adapter = new TestAdapter(recyclerView);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(3, RecyclerView.VERTICAL));
        recyclerView.addItemDecoration(new IntervalDecoration(3, 20));
        recyclerView.setAdapter(adapter);

        initData();
    }

    private void initData() {
        int i = 0;
        do {
            SortModel sortModel = new SortModel();
            sortModel.setName("test:" + i);
            sortModels.add(sortModel);
            i++;
        } while (i < 10);

        adapter.reset(sortModels);
    }

    public void add(View view) {
        SortModel sortModel = new SortModel();
        sortModel.setName("test: add");
        sortModels.add(sortModel);
        adapter.reset(sortModels);
    }

    public void remove(View view) {
//        if (sortModels.size() > 0) {
//            sortModels.remove(0);
//        }
//        List<SortModel> models = new ArrayList<>();
//        models.addAll(sortModels);
        if (!sortModels.isEmpty() && !edit.getText().toString().isEmpty()) {
            SortModel remove = sortModels.remove(Integer.parseInt(edit.getText().toString()));
            adapter.remove(remove);
        }

    }

    public void move(View view) {
        adapter.clear();
    }
}
