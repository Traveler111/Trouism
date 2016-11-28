package com.baidumap.zhangzhixin.mywork;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMCursorResult;
import com.hyphenate.chat.EMGroup;
import com.hyphenate.chat.EMGroupInfo;
import com.hyphenate.chat.EMGroupManager;
import com.hyphenate.exceptions.HyphenateException;

import java.util.List;

/**
 * Created by zhangzhixin on 2016/11/22.
 */
public class Chatgroup extends Activity {
    private ListView lv;
    private GroupAdapter myadapter;
    private List<EMGroup> groupList;
    private ImageView add;
    private Handler gethandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            myadapter=new GroupAdapter(groupList,Chatgroup.this);
            lv.setAdapter(myadapter);
        }
    };
    private Handler myhandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Toast.makeText(Chatgroup.this,"群组创建完成",Toast.LENGTH_LONG).show();

            List<EMGroup> groupList2 =(List<EMGroup>) msg.obj;
           groupList.clear();
            groupList.addAll(groupList2);
            myadapter.notifyDataSetChanged();

        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.grouplist);
        finview();
        getgrouplist();
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String groupid=groupList.get(position).getGroupId();
                String groupname=groupList.get(position).getGroupName();
                Intent ina=new Intent(Chatgroup.this,GroupChat.class);
                Bundle bundle =new Bundle();
                bundle.putString("id",groupid);
                bundle.putString("groupname",groupname);
                ina.putExtras(bundle);
                startActivity(ina);
            }
        });
    }
    public void finview(){
        lv=(ListView)findViewById(R.id.lv);
        add=(ImageView)findViewById(R.id.add);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder adb=new AlertDialog.Builder(Chatgroup.this);
                View v1=getLayoutInflater().inflate(R.layout.alertitem,null);
                final EditText nameed=(EditText)v1.findViewById(R.id.groupname);
                final EditText groupjj=(EditText)v1.findViewById(R.id.groupjj);
                final EditText groupreason=(EditText)v1.findViewById(R.id.reason);
                adb.setView(v1);
                adb.setPositiveButton("创建群组", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                                 Thread th=new Thread(){
                                     @Override
                                     public void run() {
                                         super.run();
                                         EMGroupManager.EMGroupOptions option = new EMGroupManager.EMGroupOptions();
                                         option.maxUsers = 200;
                                         option.style = EMGroupManager.EMGroupStyle.EMGroupStylePublicJoinNeedApproval;
                                         String groupName=nameed.getText().toString();
                                         String desc=groupjj.getText().toString();
                                         String [] strs=new String []{};
                                         String reason=groupreason.getText().toString();
                                         try {
                                             EMClient.getInstance().groupManager().createGroup(groupName, desc, strs, reason, option);
                                             List<EMGroup> groupList1 = EMClient.getInstance().groupManager().getJoinedGroupsFromServer();
                                             Message msg=new Message();
                                             msg.obj=groupList1;
                                             myhandler.sendMessage(msg);
                                         } catch (HyphenateException e) {
                                             e.printStackTrace();
                                         }
                                     }
                                 };
                        th.start();
                    }
                });
                adb.setNegativeButton("我点错了",null);
                adb.create().show();
            }
        });
    }
    public void getgrouplist(){
              Thread th=new Thread(){
                  @Override
                  public void run() {
                      super.run();
                      try {
                          groupList = EMClient.getInstance().groupManager().getJoinedGroupsFromServer();
                          Log.e("getgroup","run");
                          Message msg=new Message();
                          gethandler.sendMessage(msg);
                      } catch (HyphenateException e) {
                          e.printStackTrace();
                      }
                  }
              };
              th.start();

    }
}
