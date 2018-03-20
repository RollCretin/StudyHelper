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
    //签名
    private String signature;
    //性别
    private String sex;
    //生日
    private String birthday;
    //QQ
    private String qq;
    //微信
    private String weixin;
    //微博
    private String weibo;

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getQq() {
        return qq;
    }

    public void setQq(String qq) {
        this.qq = qq;
    }

    public String getWeixin() {
        return weixin;
    }

    public void setWeixin(String weixin) {
        this.weixin = weixin;
    }

    public String getWeibo() {
        return weibo;
    }

    public void setWeibo(String weibo) {
        this.weibo = weibo;
    }

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
