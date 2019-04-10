package com.liang.widget.holder;

import android.support.v7.widget.RecyclerView;
import android.view.View;

public class DataBindingViewHolder extends RecyclerView.ViewHolder {
    public DataBindingViewHolder(View itemView) {
        super(itemView);
        itemView.setOnClickListener(null);
    }
}
