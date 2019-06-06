package com.liang.recyclerviewtest.adapter;

import android.databinding.ViewDataBinding;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.liang.recyclerviewtest.R;
import com.liang.recyclerviewtest.bean.SortModel;
import com.liang.recyclerviewtest.databinding.ItemCountryListBindingBinding;
import com.liang.widget.adapter.DataBindingAdapter;

public class TestAdapter extends DataBindingAdapter<SortModel> {

    public TestAdapter(@NonNull RecyclerView recyclerView) {
        super(recyclerView);
    }

    @Override
    protected int getItemLayout(int viewType) {
        return R.layout.item_country_list_binding;
    }

    @Override
    protected void onBindViewHolder(ViewDataBinding viewDataBinding, SortModel item, int position) {
        Log.e("BindViewHolder", "onBindViewHolder: ..." + position);
        if (viewDataBinding instanceof ItemCountryListBindingBinding) {
            ((ItemCountryListBindingBinding) viewDataBinding).setModel(item);
            viewDataBinding.executePendingBindings();
        }
    }
}
