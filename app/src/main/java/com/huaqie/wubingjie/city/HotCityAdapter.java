package com.huaqie.wubingjie.city;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.huaqie.wubingjie.R;
import com.huaqie.wubingjie.model.city.CityInfo;

import java.util.List;


/**
 * Created by AINANA-RD-X on 2016/7/5.
 * 城市列表里热门城市列表适配器
 */
public class HotCityAdapter extends BaseAdapter {

    private List<CityInfo> mList;
    private Context mContext;

    public HotCityAdapter(List<CityInfo> list, Context context) {
        this.mList = list;
        this.mContext = context;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int i) {
        return mList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {

        ViewHolde holder;
        if (view == null) {
            view = LayoutInflater.from(mContext).inflate(R.layout.item_city_hot, null);
            holder = new ViewHolde();
            holder.btn = (TextView) view;
            view.setTag(holder);
        } else {
            holder = (ViewHolde) view.getTag();
        }
        holder.btn.setText(mList.get(i).getName());

        return view;
    }

    class ViewHolde {
        private TextView btn;
    }
}
