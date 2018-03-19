package com.cretin.studyhelper.model;

import cn.bmob.v3.BmobObject;

/**
 * Created by cretin on 2018/3/18.
 * 学科 model
 */

public class StudyDataModel extends BmobObject {
    //学科
    private String subject;
    //学科类型
    private int subjectValue;

    /**
     * 计算机网络
     微型计算机技术
     操作系统
     数字图像处理
     计算机图形学
     形势与政策
     多媒体技术及安全
     算法分析与设计
     信息安全原理与实践
     编译原理
     信息系统项目管理
     数据库原理与安全
     计算机科学导论
     线性代数
     * @return
     */

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
}
