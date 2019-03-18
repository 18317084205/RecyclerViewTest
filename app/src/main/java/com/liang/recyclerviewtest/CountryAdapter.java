package com.liang.recyclerviewtest;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.liang.recyclerviewtest.bean.SortModel;
import com.liang.recyclerviewtest.utils.CharacterParser;
import com.liang.recyclerviewtest.utils.PinyinComparator;
import com.liang.side.Side;
import com.liang.widget.SideBar;
import com.liang.widget.adapter.RelateSideAdapter;

import java.security.SecureRandom;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;

public class CountryAdapter extends RelateSideAdapter<SortModel> {

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
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_country_list, parent, false);
        return new CountryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        CountryViewHolder viewHolder = (CountryViewHolder) holder;
        viewHolder.countryName.setText(getItem(position).getName());
        viewHolder.countryCode.setText("+" + getItem(position).getData());
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
    public RecyclerView.ViewHolder onCreateDecorationHolder(ViewGroup parent, int decorationType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.view_header, parent, false);
        return new RecyclerView.ViewHolder(view) {
        };
    }

    @Override
    public void onBindDecorationHolder(RecyclerView.ViewHolder holder, int position) {
        TextView textView = (TextView) holder.itemView;
        textView.setText(getItem(position).getSortLetters());
        holder.itemView.setBackgroundColor(getRandomColor());
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

    class CountryViewHolder extends RecyclerView.ViewHolder {

        public TextView countryName;
        public TextView countryCode;

        public CountryViewHolder(View itemView) {
            super(itemView);
            countryName = itemView.findViewById(R.id.countryName);
            countryCode = itemView.findViewById(R.id.countryCode);
        }
    }
}
