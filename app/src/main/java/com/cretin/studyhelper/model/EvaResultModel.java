package com.cretin.studyhelper.model;

import java.util.List;

import cn.bmob.v3.BmobObject;

/**
 * Created by cretin on 2018/3/22.
 * 用户测评结果
 */

public class EvaResultModel extends BmobObject {
    //测评用户
    private CusUser cusUser;
    //测评用户id
    private String userId;
    //测评正确的个数
    private int correctNums;
    //测评错误的个数
    private int errorNums;
    //所属的测评id
    private String evaluatuinId;
    //所属的测评
    private EvaluationModel evaluationModel;
    //用户提价的答案
    private List<Integer> answerIndex;

    public List<Integer> getAnswerIndex() {
        return answerIndex;
    }

    public void setAnswerIndex(List<Integer> answerIndex) {
        this.answerIndex = answerIndex;
    }

    public EvaluationModel getEvaluationModel() {
        return evaluationModel;
    }

    public void setEvaluationModel(EvaluationModel evaluationModel) {
        this.evaluationModel = evaluationModel;
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

    public int getCorrectNums() {
        return correctNums;
    }

    public void setCorrectNums(int correctNums) {
        this.correctNums = correctNums;
    }

    public int getErrorNums() {
        return errorNums;
    }

    public void setErrorNums(int errorNums) {
        this.errorNums = errorNums;
    }

    public String getEvaluatuinId() {
        return evaluatuinId;
    }

    public void setEvaluatuinId(String evaluatuinId) {
        this.evaluatuinId = evaluatuinId;
    }
}
