package com.baidumap.zhangzhixin.mywork;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by zhangzhixin on 2016/11/26.
 */
public class Groupmember extends BaseAdapter {
    private List <String> members;
    private Context c;
    private TextView member;

    public Groupmember(List<String> members, Context c) {
        this.members = members;
        this.c = c;
    }

    @Override
    public int getCount() {
        return members.size();
    }

    @Override
    public String getItem(int position) {
        return members.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(null==convertView){
            convertView= LayoutInflater.from(c).inflate(R.layout.memberitem,null);
        }
        member=(TextView)convertView.findViewById(R.id.groupname);
        member.setText(members.get(position).toString());
        return convertView;
    }
}
