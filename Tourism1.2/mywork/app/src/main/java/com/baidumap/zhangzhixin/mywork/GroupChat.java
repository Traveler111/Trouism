package com.baidumap.zhangzhixin.mywork;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.hyphenate.EMGroupChangeListener;
import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMGroup;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.exceptions.HyphenateException;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by zhangzhixin on 2016/11/24.
 */
public class GroupChat extends Activity {
    private String id;
    private String name;
    private List<String> members;
    private TextView groupname;
    private ListView lv;
    private ListView lvmember;
    private EditText nr;
    private Button send;
    private Groupmember memeberadapter;
    private Handler recivermsg=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            List<EMMessage> listmsg=(List<EMMessage>)msg.obj;
            msgList.addAll(listmsg);
            adapter.notifyDataSetChanged();
        }
    };
    private Handler invitehandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.what==1){
                invationclass invite1=(invationclass) msg.obj;
                AlertDialog.Builder adb=new AlertDialog.Builder(GroupChat.this);
                View v1=getLayoutInflater().inflate(R.layout.receiveinvite,null);
                final TextView groupid = (TextView) v1.findViewById(R.id.inviteid);
                groupid.setText(invite1.getGroupid().toString());
                TextView groupname1 = (TextView) v1.findViewById(R.id.invitename);
                groupname1.setText(invite1.getGroupname().toString());
                final TextView inviter = (TextView) v1.findViewById(R.id.inviter);
                inviter.setText(invite1.getInviter().toString());
                TextView reason1 = (TextView) v1.findViewById(R.id.reason);
                reason1.setText(invite1.getInvitereason().toString());
                adb.setView(v1);
                adb.setPositiveButton("加入", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                           Thread th=new Thread(){
                               @Override
                               public void run() {
                                   super.run();
                                   try {
                                       EMClient.getInstance().groupManager().acceptInvitation(groupid.getText().toString(),inviter.getText().toString());
                                       Message mgs=new Message();
                                       mgs.what=2;
                                       invitehandler.sendMessage(mgs);
                                   } catch (HyphenateException e) {
                                       e.printStackTrace();
                                   }
                                   Log.e(inviter.getText().toString()+"的邀请","同意");
                               }
                           };
                           th.start();

                    }
                });
                adb.setNegativeButton("拒绝",null);
                adb.create().show();
            }
            else if(msg.what==2){
                members.clear();
                showmember();
            }
        }
    };
    private Handler memberhandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            memeberadapter=new Groupmember(members,GroupChat.this);
            lvmember.setAdapter(memeberadapter);
        }
    };
    private Button invite;
    private EMGroup group;
    private View v1;
    private EditText invitename;
    private EMConversation conversation;
    private int pagesize=20;
    private EMConversation.EMConversationType chatType= EMConversation.EMConversationType.GroupChat;
    private List<EMMessage> msgList;
    private Myadapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chatchatchat);
        finview();
        Bundle bundle=getIntent().getExtras();
         id=bundle.getString("id");
         name=bundle.getString("groupname");
        getAllgroupmsg();
        msgList=conversation.getAllMessages();
        adapter=new Myadapter(msgList,GroupChat.this);
        lv.setAdapter(adapter);
        setgroupchatlistener();
        setlistener();
        showname();
        showmember();
    }
    public void finview(){
        groupname= (TextView)findViewById(R.id.groupname);
        lv=(ListView)findViewById(R.id.lv);
        lvmember=(ListView)findViewById(R.id.lvmeber);
        nr=(EditText)findViewById(R.id.chatnr);
        send=(Button)findViewById(R.id.send);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                     sendgroupmsg(nr.getText().toString().trim());
            }
        });
        invite=(Button)findViewById(R.id.invite);
        invite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    invitefriend();
            }
        });
    }
    public void showname(){
        groupname.setText(name);
    }
    public void showmember(){
        Thread th=new Thread(){
            @Override
            public void run() {
                super.run();
                try {
                    group = EMClient.getInstance().groupManager().getGroupFromServer(id);
                    members=group.getMembers();
                    Message msg=new Message();
                    memberhandler.sendMessage(msg);
                } catch (HyphenateException e) {
                    e.printStackTrace();
                }
            }
        };
        th.start();
    }
    public void invitefriend(){
        final String str=EMClient.getInstance().getCurrentUser();
        final String name=group.getOwner();
        AlertDialog.Builder adb=new AlertDialog.Builder(GroupChat.this);
          v1= getLayoutInflater().inflate(R.layout.sendinviteitem,null);
        adb.setView(v1);
        adb.setPositiveButton("发送", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                 invitename = (EditText) v1.findViewById(R.id.invitename);
                final String strname=invitename.getText().toString();
                if(str.equals(name)){
                    Thread th=new Thread(){
                        @Override
                        public void run() {
                            super.run();
                            try {
                                EMClient.getInstance().groupManager().addUsersToGroup(id, new String[]{strname});
                                Log.e("invite","send success");
                            } catch (HyphenateException e) {
                                e.printStackTrace();
                                Log.e("invite","send failed");
                            }
                        }
                    };
                    th.start();
                }
                else {
                    String strname1=invitename.getText().toString();
                    try {
                        Log.e("not owner invite","send success");
                        EMClient.getInstance().groupManager().inviteUser(id, new String[]{strname1}, null);//需异步处理
                    } catch (HyphenateException e) {
                        e.printStackTrace();
                        Log.e("not owner invite","send failed");
                    }
                }
            }
        });
        adb.setNegativeButton("取消",null);
        adb.create().show();
    }
    public void setlistener(){
        EMClient.getInstance().groupManager().addGroupChangeListener(new EMGroupChangeListener() {
            @Override
            public void onInvitationReceived(String s, String s1, String s2, String s3) {
                     Message msg=new Message();
                     msg.what=1;
                try {
                    EMGroup group=EMClient.getInstance().groupManager().getGroupFromServer(s);
                    String groupname=group.getGroupName();
                    invationclass invite1=new invationclass(s,groupname,s2,s3);
                    msg.obj=invite1;
                    invitehandler.sendMessage(msg);
                    Log.e("recive invation","from"+groupname);
                } catch (HyphenateException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onApplicationReceived(String s, String s1, String s2, String s3) {

            }

            @Override
            public void onApplicationAccept(String s, String s1, String s2) {

            }

            @Override
            public void onApplicationDeclined(String s, String s1, String s2, String s3) {

            }

            @Override
            public void onInvitationAccepted(String s, String s1, String s2) {

            }

            @Override
            public void onInvitationDeclined(String s, String s1, String s2) {

            }

            @Override
            public void onUserRemoved(String s, String s1) {

            }

            @Override
            public void onGroupDestroyed(String s, String s1) {

            }

            @Override
            public void onAutoAcceptInvitationFromGroup(String s, String s1, String s2) {

            }
        });
    }
    public void sendgroupmsg(String content){
        // 创建一条文本消息，content为消息文字内容，toChatUsername为对方用户或者群聊的id，后文皆是如此
        EMMessage message = EMMessage.createTxtSendMessage(content, id);
        // 如果是群聊，设置chattype，默认是单聊
        if (chatType == EMConversation.EMConversationType.GroupChat)
            message.setChatType(EMMessage.ChatType.GroupChat);
        // 发送消息
        EMClient.getInstance().chatManager().sendMessage(message);
        msgList.add(message);

        adapter.notifyDataSetChanged();
        if (msgList.size() > 0) {
            lv.setSelection(lv.getCount() - 1);
        }
        nr.setText("");
        nr.clearFocus();
    }
    public void getAllgroupmsg() {
        conversation = EMClient.getInstance().chatManager().getConversation(id,
                EMConversation.EMConversationType.GroupChat, true);
        // 把此会话的未读数置为0
        conversation.markAllMessagesAsRead();
        // 初始化db时，每个conversation加载数目是getChatOptions().getNumberOfMessagesLoaded
        // 这个数目如果比用户期望进入会话界面时显示的个数不一样，就多加载一些
        final List<EMMessage> msgs = conversation.getAllMessages();
        int msgCount = msgs != null ? msgs.size() : 0;
        if (msgCount < conversation.getAllMsgCount() && msgCount < pagesize) {
            String msgId = null;
            if (msgs != null && msgs.size() > 0) {
                msgId = msgs.get(0).getMsgId();
            }
            conversation.loadMoreMsgFromDB(msgId, pagesize - msgCount);
        }
    }
    public void setgroupchatlistener(){
        EMMessageListener msgListener = new EMMessageListener() {

            @Override
            public void onMessageReceived(List<EMMessage> messages) {
                Log.e("receive","success");

                for (EMMessage message : messages) {
                    String username = null;
                    // 群组消息
                    if (message.getChatType() == EMMessage.ChatType.GroupChat || message.getChatType() == EMMessage.ChatType.ChatRoom) {
                        username = message.getTo();
                    } else {
                        // 单聊消息
                        username = message.getFrom();
                    }
                    // 如果是当前会话的消息，刷新聊天页面

                        Message msgcd=new Message();
                        msgcd.obj=messages;
                        recivermsg.sendMessage(msgcd);

                }

                // 收到消息
            }

            @Override
            public void onCmdMessageReceived(List<EMMessage> messages) {
                // 收到透传消息
            }

            @Override
            public void onMessageReadAckReceived(List<EMMessage> messages) {
                // 收到已读回执
            }

            @Override
            public void onMessageDeliveryAckReceived(List<EMMessage> message) {
                // 收到已送达回执
            }

            @Override
            public void onMessageChanged(EMMessage message, Object change) {
                // 消息状态变动
            }
        };
    }
}
