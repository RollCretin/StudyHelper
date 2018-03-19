package com.cretin.studyhelper.model;

import cn.bmob.v3.BmobObject;

/**
 * Created by cretin on 2018/3/18.
 * 这个是 日报 的数据模型
 */

public class DailyModel extends BmobObject {
    //日报日期
    private String date;
    //学习科目
    private String subject;
    //学习科目 value
    private int subjectValue;
    //完成任务
    private String finish;
    //未完成任务
    private String unfinish;
    //学习时长
    private int time;
    //备注
    private String remark;
    //所属用户
    private String userId;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public int getSubjectValue() {
        return subjectValue;
    }

    public void setSubjectValue(int subjectValue) {
        this.subjectValue = subjectValue;
    }

    public String getFinish() {
        return finish;
    }

    public void setFinish(String finish) {
        this.finish = finish;
    }

    public String getUnfinish() {
        return unfinish;
    }

    public void setUnfinish(String unfinish) {
        this.unfinish = unfinish;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
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
