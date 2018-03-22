package com.cretin.studyhelper.model;

import java.util.List;

import cn.bmob.v3.BmobObject;

/**
 * Created by cretin on 2018/3/21.
 * 帖子信息
 */

public class PostModel extends BmobObject {
    //发布帖子的用户
    private CusUser cusUser;
    //所属分类的id
    private String typeId;
    //帖子内容
    private String content;
    //帖子图片路径
    private String picUrl;
    //帖子所属的分类
    private PostTypeModel typeModel;
    //收藏的数量
    private int likeCount;
    //点赞的人员的id
    private List<String> likeUsers;

    public List<String> getLikeUsers() {
        return likeUsers;
    }

    public void setLikeUsers(List<String> likeUsers) {
        this.likeUsers = likeUsers;
    }

    public int getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(int likeCount) {
        this.likeCount = likeCount;
    }

    public CusUser getCusUser() {
        return cusUser;
    }

    public void setCusUser(CusUser cusUser) {
        this.cusUser = cusUser;
    }

    public String getTypeId() {
        return typeId;
    }

    public void setTypeId(String typeId) {
        this.typeId = typeId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getPicUrl() {
        return picUrl;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }

    public PostTypeModel getTypeModel() {
        return typeModel;
    }

    public void setTypeModel(PostTypeModel typeModel) {
        this.typeModel = typeModel;
    }
}
