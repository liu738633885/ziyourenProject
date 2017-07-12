package com.hyphenate.chatuidemo.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseIntArray;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.hyphenate.easeui.adapter.EaseContactAdapter;
import com.hyphenate.easeui.domain.EaseUser;
import com.hyphenate.easeui.utils.EaseUserUtils;

import java.util.List;

/**
 * Created by Administrator on 2016/7/19.
 */
public class ContactAdapter extends EaseContactAdapter {

    protected LayoutInflater layoutInflater;
    protected SparseIntArray positionOfSection;
    protected SparseIntArray sectionOfPosition;
    protected int res;
    protected MyFilter myFilter;
    protected boolean notiyfyByFilter;

    public ContactAdapter(Context context, int resource, List<EaseUser> objects) {
        super(context, resource, objects);
    }

    protected static class ViewHolder {
        ImageView avatar;
        TextView nameView;
        TextView headerView;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if(convertView == null){
            holder = new ViewHolder();
            if(res == 0)
                convertView = layoutInflater.inflate(com.hyphenate.easeui.R.layout.ease_row_contact, null);
            else
                convertView = layoutInflater.inflate(res, null);
            holder.avatar = (ImageView) convertView.findViewById(com.hyphenate.easeui.R.id.avatar);
            holder.nameView = (TextView) convertView.findViewById(com.hyphenate.easeui.R.id.name);
            holder.headerView = (TextView) convertView.findViewById(com.hyphenate.easeui.R.id.header);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }

        EaseUser user = getItem(position);
        if(user == null)
            Log.d("ContactAdapter", position + "");
        String username = user.getNick();
        String header = user.getInitialLetter();

        if (position == 0 || header != null && !header.equals(getItem(position - 1).getInitialLetter())) {
            if (TextUtils.isEmpty(header)) {
                holder.headerView.setVisibility(View.GONE);
            } else {
                holder.headerView.setVisibility(View.VISIBLE);
                holder.headerView.setText(header);
            }
        } else {
            holder.headerView.setVisibility(View.GONE);
        }

        EaseUserUtils.setUserNick(username, holder.nameView);
        EaseUserUtils.setUserAvatar(getContext(), username, holder.avatar);

        if(primaryColor != 0)
            holder.nameView.setTextColor(primaryColor);
        if(primarySize != 0)
            holder.nameView.setTextSize(TypedValue.COMPLEX_UNIT_PX, primarySize);
        if(initialLetterBg != null)
            holder.headerView.setBackgroundDrawable(initialLetterBg);
        if(initialLetterColor != 0)
            holder.headerView.setTextColor(initialLetterColor);

        return convertView;
    }
}
