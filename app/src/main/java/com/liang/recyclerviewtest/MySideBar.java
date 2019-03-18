package com.liang.recyclerviewtest;

import android.content.Context;
import android.util.AttributeSet;

import com.liang.listener.OnSideCheckedListener;
import com.liang.side.Side;
import com.liang.widget.SideBar;

import java.util.ArrayList;
import java.util.LinkedHashSet;

public class MySideBar extends SideBar implements Side, SideBar.OnTouchingChangedListener {
    private OnSideCheckedListener onSideCheckedListener;
    private OnMYSideCheckedListener onMYSideCheckedListener;

    public void setOnMYSideCheckedListener(OnMYSideCheckedListener onMYSideCheckedListener) {
        this.onMYSideCheckedListener = onMYSideCheckedListener;
    }

    public MySideBar(Context context) {
        this(context, null);
    }

    public MySideBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MySideBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setOnTouchingChangedListener(this);
    }

    @Override
    public void setTags(LinkedHashSet<String> tags) {
        setArrayTag(new ArrayList<>(tags));
    }

    @Override
    public void setChecked(String tag) {
        super.setChecked(tag);
    }

    @Override
    public void OnSideCheckedListener(OnSideCheckedListener onSideCheckedListener) {
        this.onSideCheckedListener = onSideCheckedListener;
    }

    @Override
    public void onTouchingChanged(String s) {
        if (onSideCheckedListener != null) {
            onSideCheckedListener.onItemChecked(s);
        }
        if (onMYSideCheckedListener != null) {
            onMYSideCheckedListener.onSideBarChecked(s);
        }
    }


    public interface OnMYSideCheckedListener {
        void onSideBarChecked(String tag);
    }


}
