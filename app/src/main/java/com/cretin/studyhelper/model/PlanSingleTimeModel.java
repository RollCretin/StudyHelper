package com.cretin.studyhelper.model;

/**
 * Created by cretin on 2018/3/19.
 * 目标计划将被划分成一个一个的小时间段 每一个时间段由PlanSingleTimeModel提现
 */

public class PlanSingleTimeModel {
    //本次经过的时间 单位秒
    private int times;

    public int getTimes() {
        return times;
    }

    public void setTimes(int times) {
        this.times = times;
    }
}
