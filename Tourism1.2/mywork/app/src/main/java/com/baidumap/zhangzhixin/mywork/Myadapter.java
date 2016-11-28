package com.baidumap.zhangzhixin.mywork;

import android.content.Context;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.TextView;


import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMTextMessageBody;

import java.util.List;

/**
 * Created by zhangzhixin on 2016/11/22.
 */
public class Myadapter extends BaseAdapter{
    private List<EMMessage> msg;
    private Context c;
    private EditText text;
    private TextView user;

    public Myadapter(List<EMMessage> msg, Context c) {
        this.msg = msg;
        this.c = c;
    }

    @Override
    public int getCount() {
        return msg.size();
    }

    @Override
    public EMMessage getItem(int position) {
        return msg.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        EMMessage message = getItem(position);
        return message.direct() == EMMessage.Direct.RECEIVE ? 0 : 1;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        EMMessage message = getItem(position);
        int viewType=getItemViewType(position);
            if (convertView == null) {
                if (viewType == 0) {
                    convertView =LayoutInflater.from(c).inflate(R.layout.reciveitem,null);
                } else {
                    convertView = LayoutInflater.from(c).inflate(R.layout.send,null);
                }
            }
        EMTextMessageBody txtBody = (EMTextMessageBody) message.getBody();
        text=(EditText)convertView.findViewById(R.id.content);
        text.setText(txtBody.getMessage());
        user=(TextView)convertView.findViewById(R.id.username);
        String name=message.getFrom();
        user.setText(name);
        return convertView;
    }
}
