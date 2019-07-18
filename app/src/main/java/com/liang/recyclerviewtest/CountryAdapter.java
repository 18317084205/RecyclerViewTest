package com.liang.recyclerviewtest;

import android.databinding.ViewDataBinding;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.liang.recyclerviewtest.bean.SortModel;
import com.liang.recyclerviewtest.databinding.ItemCountryListBindingBinding;
import com.liang.recyclerviewtest.databinding.ViewHeaderBindingBinding;
import com.liang.recyclerviewtest.utils.CharacterParser;
import com.liang.recyclerviewtest.utils.PinyinComparator;
import com.liang.side.Side;
import com.liang.widget.SideBar;
import com.liang.widget.adapter.DataBindingRelateSideAdapter;
import com.liang.widget.adapter.RelateSideAdapter;
import com.liang.widget.holder.DataBindingViewHolder;

import java.security.SecureRandom;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;

public class CountryAdapter extends DataBindingRelateSideAdapter<SortModel> {

    private PinyinComparator mPinyinComparator;

    public CountryAdapter(RecyclerView recyclerView, Side sideBar) {
        super(recyclerView, sideBar);
        mPinyinComparator = new PinyinComparator();
    }

    @Override
    public void add(SortModel object) {
        super.add(object);
        initSideBar();
    }

    @Override
    public void add(int index, SortModel object) {
        super.add(index, object);
        initSideBar();
    }

    @Override
    public void addAll(SortModel... items) {
        super.addAll(items);
        initSideBar();
    }

    @Override
    public void addAll(Collection<SortModel> collection) {
        super.addAll(collection);
        formatSortModel();
        initSideBar();
    }

    private void initSideBar() {
        LinkedHashSet<String> letters = new LinkedHashSet<>();
        for (SortModel model : getItems()) {
            letters.add(model.getSortLetters());
        }
        addTags(letters);
    }

    private void formatSortModel() {
        for (SortModel model : getItems()) {
            //汉字转换成拼音
            String pinyin = CharacterParser.getInstance().getSelling(model.getName());
            String sortString = pinyin.substring(0, 1).toUpperCase();
            // 正则表达式，判断首字母是否是英文字母
            if (sortString.matches("[A-Z]")) {
                model.setSortLetters(sortString.toUpperCase());
            } else {
                model.setSortLetters("#");
            }
        }

        Collections.sort(getItems(), mPinyinComparator);
    }

    @Override
    protected int getItemLayout(int viewType) {
        return R.layout.item_country_list_binding;
    }

    @Override
    public long getDecorationId(int position) {
        return getItem(position).getSortLetters().charAt(0);
    }

    @Override
    public int getDecorationType(int position) {
        return 0;
    }

    @Override
    protected int getItemDecorationLayout(int decorationType) {
        return R.layout.view_header_binding;
    }

    @Override
    protected void onBindDecorationHolder(ViewDataBinding viewDataBinding, SortModel item) {
        if (viewDataBinding instanceof ViewHeaderBindingBinding) {
            ((ViewHeaderBindingBinding) viewDataBinding).setModel(item);
            viewDataBinding.getRoot().setBackgroundColor(getRandomColor());
            viewDataBinding.executePendingBindings();
        }
    }

    @Override
    protected void onBindViewHolder(ViewDataBinding viewDataBinding, SortModel item, int position) {
        if (viewDataBinding instanceof ItemCountryListBindingBinding) {
            ((ItemCountryListBindingBinding) viewDataBinding).setModel(item);
            ((ItemCountryListBindingBinding) viewDataBinding).setPosition(position);
            ((ItemCountryListBindingBinding) viewDataBinding).setOnItemClickListener(getOnItemClickListener());
            viewDataBinding.executePendingBindings();
        }
    }

    private int getRandomColor() {
        SecureRandom secureRandom = new SecureRandom();
        return Color.HSVToColor(150, new float[]{
                secureRandom.nextInt(359), 1, 1
        });
    }

    @Override
    protected int getPositionForSection(String tag) {
        for (int i = 0; i < getItemCount(); i++) {
            String sortStr = getItem(i).getSortLetters();
            char firstChar = sortStr.toUpperCase().charAt(0);
            if (firstChar == tag.charAt(0)) {
                return i;
            }
        }
        return -1;
    }

    @Override
    protected void onRecyclerViewScrolled(int position) {
        super.onRecyclerViewScrolled(position);
        getSide().setChecked(getItem(position).getSortLetters());
    }

}
