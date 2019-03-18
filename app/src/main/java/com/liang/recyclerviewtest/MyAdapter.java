package com.liang.recyclerviewtest;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.liang.widget.adapter.LabelBaseAdapter;

import java.security.SecureRandom;

public class MyAdapter extends LabelBaseAdapter<String, RecyclerView.ViewHolder> {
    public MyAdapter(RecyclerView recyclerView) {
        super(recyclerView);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.view_item, parent, false);
        return new RecyclerView.ViewHolder(view) {
        };
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        TextView textView = (TextView) holder.itemView;
        textView.setText(getItem(position));
    }

    @Override
    public long getDecorationId(int position) {
        return getItem(position).charAt(0);
    }

    @Override
    public int getDecorationType(int position) {
        return 0;
    }

    @Override
    public RecyclerView.ViewHolder onCreateDecorationHolder(ViewGroup parent, int decorationType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.view_header, parent, false);
        return new RecyclerView.ViewHolder(view) {
        };
    }

    @Override
    public void onBindDecorationHolder(RecyclerView.ViewHolder holder, int position) {
        TextView textView = (TextView) holder.itemView;
        textView.setText(String.valueOf(getItem(position).charAt(0)));
        holder.itemView.setBackgroundColor(getRandomColor());
    }

    private int getRandomColor() {
        SecureRandom secureRandom = new SecureRandom();
        return Color.HSVToColor(150, new float[]{
                secureRandom.nextInt(359), 1, 1
        });
    }
}
