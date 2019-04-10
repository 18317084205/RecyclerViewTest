package com.liang;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

public interface IDecorationAdapter<VH extends RecyclerView.ViewHolder> {
    long getDecorationId(int position);

    int getDecorationType(int position);

    VH onCreateDecorationHolder(ViewGroup parent, int decorationType);

    void onBindDecorationHolder(VH holder, int position);

    void registerAdapterDataObserver(RecyclerView.AdapterDataObserver adapterDataObserver);

    int getItemCount();
}
