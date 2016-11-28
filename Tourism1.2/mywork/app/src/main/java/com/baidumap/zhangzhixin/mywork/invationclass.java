package com.baidumap.zhangzhixin.mywork;

/**
 * Created by zhangzhixin on 2016/11/26.
 */
public class invationclass  {
    private String groupid;
    private String groupname;
    private String inviter;
    private String invitereason;

    public String getGroupid() {
        return groupid;
    }

    public void setGroupid(String groupid) {
        this.groupid = groupid;
    }

    public String getGroupname() {
        return groupname;
    }

    public void setGroupname(String groupname) {
        this.groupname = groupname;
    }

    public String getInviter() {
        return inviter;
    }

    public void setInviter(String inviter) {
        this.inviter = inviter;
    }

    public String getInvitereason() {
        return invitereason;
    }

    public void setInvitereason(String invitereason) {
        this.invitereason = invitereason;
    }

    public invationclass(String groupid, String groupname, String inviter, String invitereason) {

        this.groupid = groupid;
        this.groupname = groupname;
        this.inviter = inviter;
        this.invitereason = invitereason;
    }
}
