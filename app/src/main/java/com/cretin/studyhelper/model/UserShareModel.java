package com.cretin.studyhelper.model;

import com.chad.library.adapter.base.entity.MultiItemEntity;

import java.util.List;

import cn.bmob.v3.BmobObject;

/**
 * Created by cretin on 2018/3/23.
 * 用户分享到首页的数据模型
 */

public class UserShareModel extends BmobObject implements MultiItemEntity {
    public static final int TYPE_PLAN = 0;
    public static final int TYPE_DAILY = 1;
    //分享的用户
    private CusUser cusUser;
    //分享的用户id
    private String userId;
    //分享的内容 计划
    private PlansModel plansModel;
    //分享的内容 日报
    private DailyModel dailyModel;
    //分享的内容id
    private String contentId;
    //分享的类型 0 计划 1 日报
    private int type;
    //用户的点赞数
    private int likeCount;
    //点赞的用户id集合
    private List<String> likeUserIds;

    public CusUser getCusUser() {
        return cusUser;
    }

    public void setCusUser(CusUser cusUser) {
        this.cusUser = cusUser;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public PlansModel getPlansModel() {
        return plansModel;
    }

    public void setPlansModel(PlansModel plansModel) {
        this.plansModel = plansModel;
    }

    public DailyModel getDailyModel() {
        return dailyModel;
    }

    public void setDailyModel(DailyModel dailyModel) {
        this.dailyModel = dailyModel;
    }

    public String getContentId() {
        return contentId;
    }

    public void setContentId(String contentId) {
        this.contentId = contentId;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(int likeCount) {
        this.likeCount = likeCount;
    }

    public List<String> getLikeUserIds() {
        return likeUserIds;
    }

    public void setLikeUserIds(List<String> likeUserIds) {
        this.likeUserIds = likeUserIds;
    }

    @Override
    public int getItemType() {
        return getType();
    }
}
