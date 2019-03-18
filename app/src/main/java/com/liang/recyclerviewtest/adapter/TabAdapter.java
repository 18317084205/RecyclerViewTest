package com.liang.recyclerviewtest.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.liang.recyclerviewtest.R;
import com.liang.widget.adapter.BaseAdapter;

public class TabAdapter extends BaseAdapter<String, RecyclerView.ViewHolder> {

    private int checkedPosition = -1;

    public TabAdapter(RecyclerView recyclerView) {
        super(recyclerView);
    }

    public void setCheckedPosition(int checkedPosition) {
        if (this.checkedPosition == checkedPosition) {
            return;
        }
        this.checkedPosition = checkedPosition;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.view_header, parent, false);
        return new RecyclerView.ViewHolder(view) {
        };
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        TextView textView = (TextView) holder.itemView;
        textView.setText(getItem(position));
        textView.setPressed(checkedPosition == position);
    }
}
