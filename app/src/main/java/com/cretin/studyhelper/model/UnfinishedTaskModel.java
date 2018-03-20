package com.cretin.studyhelper.model;

import java.io.Serializable;

/**
 * Created by cretin on 2018/3/20.
 * 记录未完成任务的信息  本地保存就好
 */

public class UnfinishedTaskModel implements Serializable{
    //状态 0 已完成 1 未完成 2 从计划页面过来的 不理会
    private int state = 0;
    //操作的PlansModel
    private PlansModel aimItem;
    //已经过的时间
    private long passedTime;

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public PlansModel getAimItem() {
        return aimItem;
    }

    public void setAimItem(PlansModel aimItem) {
        this.aimItem = aimItem;
    }

    public long getPassedTime() {
        return passedTime;
    }

    public void setPassedTime(long passedTime) {
        this.passedTime = passedTime;
    }
}
