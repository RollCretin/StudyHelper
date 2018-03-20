package com.cretin.studyhelper.model;

import java.util.List;

import cn.bmob.v3.BmobObject;

/**
 * Created by cretin on 2018/3/19.
 * 计划的数据类型
 */

public class PlansModel extends BmobObject{
    public static final int PLAN_TYPE_NORMAL = 0;
    public static final int PLAN_TYPE_AIM = 1;
    public static final int PLAN_TYPE_HABIT = 2;
    public static final int NORMAL_TYPE_DJS = 0;
    public static final int NORMAL_TYPE_ZJS = 1;
    public static final int NORMAL_TYPE_BJS = 2;
    public static final int TIME_TYPE_HOUR = 0;
    public static final int TIME_TYPE_MINUTE = 1;
    public static final int HABIT_TYPE_DAY = 0;
    public static final int HABIT_TYPE_WEEK = 1;
    public static final int HABIT_TYPE_MONTH = 2;
    //计划的类型 0 普通计划 1 目标计划 2 习惯计划
    private int planType;
    //计划名称
    private String planName;
    //普通计划的计时类型 0 倒计时 1 正计时 2 不计时
    private int normalType;
    //普通计划的时长
    private int normalTime;
    //目标计划最后期限
    private String aimEndTime;
    //目标类型的时长
    private int aimTime;
    //目标类型的市场类型 0 小时 1 分钟
    private int aimTimeType;
    //习惯类型的类型 0 每天 1 每周 2 每月
    private int habitType;
    //习惯类型的时长
    private int habitTime;
    //习惯类型的时长类型
    private int habitTimeType;
    //计划的备注 非必须
    private String remark;
    //所属用户
    private String userId;
    //当前状态 0 进行中 1 已完成
    private int currFlag;
    //存储目标计划类型下的单次执行时间
    private List<PlanSingleTimeModel> singleTimeModelList;

    public List<PlanSingleTimeModel> getSingleTimeModelList() {
        return singleTimeModelList;
    }

    public void setSingleTimeModelList(List<PlanSingleTimeModel> singleTimeModelList) {
        this.singleTimeModelList = singleTimeModelList;
    }


    public int getCurrFlag() {
        return currFlag;
    }

    public void setCurrFlag(int currFlag) {
        this.currFlag = currFlag;
    }

    public int getPlanType() {
        return planType;
    }

    public void setPlanType(int planType) {
        this.planType = planType;
    }

    public String getPlanName() {
        return planName;
    }

    public void setPlanName(String planName) {
        this.planName = planName;
    }

    public int getNormalType() {
        return normalType;
    }

    public void setNormalType(int normalType) {
        this.normalType = normalType;
    }

    public int getNormalTime() {
        return normalTime;
    }

    public void setNormalTime(int normalTime) {
        this.normalTime = normalTime;
    }

    public String getAimEndTime() {
        return aimEndTime;
    }

    public void setAimEndTime(String aimEndTime) {
        this.aimEndTime = aimEndTime;
    }

    public int getAimTime() {
        return aimTime;
    }

    public void setAimTime(int aimTime) {
        this.aimTime = aimTime;
    }

    public int getAimTimeType() {
        return aimTimeType;
    }

    public void setAimTimeType(int aimTimeType) {
        this.aimTimeType = aimTimeType;
    }

    public int getHabitType() {
        return habitType;
    }

    public void setHabitType(int habitType) {
        this.habitType = habitType;
    }

    public int getHabitTime() {
        return habitTime;
    }

    public void setHabitTime(int habitTime) {
        this.habitTime = habitTime;
    }

    public int getHabitTimeType() {
        return habitTimeType;
    }

    public void setHabitTimeType(int habitTimeType) {
        this.habitTimeType = habitTimeType;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
