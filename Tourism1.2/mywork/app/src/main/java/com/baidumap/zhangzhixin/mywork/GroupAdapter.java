package com.baidumap.zhangzhixin.mywork;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.hyphenate.chat.EMGroup;

import java.util.List;

/**
 * Created by zhangzhixin on 2016/11/23.
 */
public class GroupAdapter extends BaseAdapter{
    private List <EMGroup> groupList;
    private Context c;
    private TextView groupname;

    public GroupAdapter(List<EMGroup> groupList, Context c) {
        this.groupList = groupList;
        this.c = c;
    }

    @Override
    public int getCount() {
        return groupList.size();
    }

    @Override
    public EMGroup getItem(int position) {
        return groupList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(null==convertView){
            convertView= LayoutInflater.from(c).inflate(R.layout.groupitem,null);
        }
        groupname=(TextView)convertView.findViewById(R.id.groupname);
        groupname.setText(groupList.get(position).getGroupName().toString());
        return convertView;
    }
}
