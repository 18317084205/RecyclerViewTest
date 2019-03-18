package com.liang.recyclerviewtest;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.liang.jtab.view.TabView;
import com.liang.widget.BadgeView;

public class TabMenu extends TabView {

    private View tabView;

    public TabMenu(@NonNull Context context) {
        super(context);
    }

    @Override
    protected View initTabView() {
        tabView = LayoutInflater.from(getContext()).inflate(R.layout.tab_menul, null, true);
        return tabView;
    }

    @Override
    protected TextView setTabTitleView() {
        TextView title = (TextView) tabView;
        return title;
    }

    @Override
    protected ImageView setTabIconView() {
        return null;
    }

    @Override
    protected BadgeView setTabBadgeView() {
        return null;
    }
}
