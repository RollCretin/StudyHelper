package com.cretin.studyhelper.model;

import cn.bmob.v3.BmobObject;

/**
 * Created by cretin on 2018/3/15.
 * 计划model
 */

public class PlanModel extends BmobObject {
    //计划标题
    private String title;
    //计划的备注
    private String remark;
    //开始时间
    private String startTime;
    private String startTimeValue;
    //结束时间
    private String endTime;//标准格式 yyyy-MM-dd HH:mm
    private String endTimeValue;//显示模式 yy/MM/dd HH:mm
    //提醒模式 0 不提醒 1 提前5分钟 2 提前10分钟 3 提前半小时 4 提前一小时
    private int remindFlag;
    //当前状态 0 进行中 1 已超时 2 已完成
    private int currFlag;
    //创建此计划的用户
    private String userId;

    public String getStartTimeValue() {
        return startTimeValue;
    }

    public void setStartTimeValue(String startTimeValue) {
        this.startTimeValue = startTimeValue;
    }

    public String getEndTimeValue() {
        return endTimeValue;
    }

    public void setEndTimeValue(String endTimeValue) {
        this.endTimeValue = endTimeValue;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public int getRemindFlag() {
        return remindFlag;
    }

    public void setRemindFlag(int remindFlag) {
        this.remindFlag = remindFlag;
    }

    public int getCurrFlag() {
        return currFlag;
    }

    public void setCurrFlag(int currFlag) {
        this.currFlag = currFlag;
    }
}

