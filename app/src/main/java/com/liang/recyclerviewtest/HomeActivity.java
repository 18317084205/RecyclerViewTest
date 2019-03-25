package com.liang.recyclerviewtest;

import android.content.Intent;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.liang.widget.RefreshLayout;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener {

    private RefreshLayout jRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        jRefreshLayout = findViewById(R.id.jRefreshLayout);
        jRefreshLayout.setRefreshEnabled(true);
        jRefreshLayout.setLoadEnabled(true);
//        CircleImageView circleImageView = new CircleImageView(this, Color.BLUE);
//        jRefreshLayout.addHeaderView(circleImageView);
        findViewById(R.id.sideBar_view).setOnClickListener(this);
        findViewById(R.id.sideTab_left).setOnClickListener(this);
        findViewById(R.id.sideTab_top).setOnClickListener(this);
        new SwipeRefreshLayout(this);
        jRefreshLayout.setOnRefreshListener(new RefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshFinish();
            }
        });
        jRefreshLayout.setOnLoadListener(new RefreshLayout.OnLoadListener() {
            @Override
            public void onLoad() {
                refreshFinish();
            }
        });

    }

    private void refreshFinish() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                jRefreshLayout.finish();
            }
        }, 3000);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sideBar_view:
                startActivity(new Intent(HomeActivity.this, SideBarActivity.class));
                break;
            case R.id.sideTab_left:
                startActivity(new Intent(HomeActivity.this, SideTabLeftActivity.class));
                break;
            case R.id.sideTab_top:
                startActivity(new Intent(HomeActivity.this, SideTabTopActivity.class));

                break;
        }
    }
}
