package com.cretin.studyhelper.model;

import cn.bmob.v3.BmobObject;

/**
 * Created by cretin on 2018/3/22.
 * 用户收藏的数据模型
 */

public class CollectModel extends BmobObject {
    public static final int TYPE_POST = 0;
    public static final int TYPE_DAILY = 1;
    //收藏此内容的用户
    private CusUser cusUser;
    //收藏的内容 帖子
    private PostModel postModel;
    //收藏的内容 日报
    private DailyModel dailyModel;
    //收藏的类型 0 帖子 1 日报  目前只支持帖子
    private int type;
    //收藏的用户id
    private String userId;
    //收藏的内容id
    private String contentId;

    public String getContentId() {
        return contentId;
    }

    public void setContentId(String contentId) {
        this.contentId = contentId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public CusUser getCusUser() {
        return cusUser;
    }

    public void setCusUser(CusUser cusUser) {
        this.cusUser = cusUser;
    }

    public PostModel getPostModel() {
        return postModel;
    }

    public void setPostModel(PostModel postModel) {
        this.postModel = postModel;
    }

    public DailyModel getDailyModel() {
        return dailyModel;
    }

    public void setDailyModel(DailyModel dailyModel) {
        this.dailyModel = dailyModel;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
