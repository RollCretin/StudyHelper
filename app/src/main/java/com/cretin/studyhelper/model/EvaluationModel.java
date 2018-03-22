package com.cretin.studyhelper.model;

import java.util.List;

import cn.bmob.v3.BmobObject;

/**
 * Created by cretin on 2018/3/22.
 * 测评的数据类型
 */

public class EvaluationModel extends BmobObject{
    //发布人
    private CusUser cusUser;
    //发布人的id
    private String userId;
    //发布的测评所属的类型id
    private String typeId;
    //发布的测评所属的类型
    private PostTypeModel typeModel;
    //试题
    private List<TestModel> tests;
    //参与用户数量
    private int joinNums;
    //参与用户的id集合
    private List<String> joinUsers;
    //测评的描述
    private String describe;

    public String getDescribe() {
        return describe;
    }

    public void setDescribe(String describe) {
        this.describe = describe;
    }

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

    public String getTypeId() {
        return typeId;
    }

    public void setTypeId(String typeId) {
        this.typeId = typeId;
    }

    public PostTypeModel getTypeModel() {
        return typeModel;
    }

    public void setTypeModel(PostTypeModel typeModel) {
        this.typeModel = typeModel;
    }

    public List<TestModel> getTests() {
        return tests;
    }

    public void setTests(List<TestModel> tests) {
        this.tests = tests;
    }

    public int getJoinNums() {
        return joinNums;
    }

    public void setJoinNums(int joinNums) {
        this.joinNums = joinNums;
    }

    public List<String> getJoinUsers() {
        return joinUsers;
    }

    public void setJoinUsers(List<String> joinUsers) {
        this.joinUsers = joinUsers;
    }
}
