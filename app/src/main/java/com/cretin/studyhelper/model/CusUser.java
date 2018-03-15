package com.cretin.studyhelper.model;

import cn.bmob.v3.BmobUser;

/**
 * Created by cretin on 2018/3/15.
 */

public class CusUser extends BmobUser {
    //昵称
    private String nickname;
    //用户头像
    private String avatar;

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }
}
