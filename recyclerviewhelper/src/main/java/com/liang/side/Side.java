package com.liang.side;

import com.liang.listener.OnSideCheckedListener;

import java.util.LinkedHashSet;

public interface Side {
    void setTags(LinkedHashSet<String> tags);
    void setChecked(String tag);
    void OnSideCheckedListener(OnSideCheckedListener onSideCheckedListener);
}
