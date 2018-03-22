package com.cretin.studyhelper.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by cretin on 2018/3/22.
 * 测试试题数据类型
 */

public class TestModel implements Serializable{
    //题干
    private String title;
    //答案
    private List<String> answers;
    //正确答案的索引
    private int rightAnswerIndex;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<String> getAnswers() {
        return answers;
    }

    public void setAnswers(List<String> answers) {
        this.answers = answers;
    }

    public int getRightAnswerIndex() {
        return rightAnswerIndex;
    }

    public void setRightAnswerIndex(int rightAnswerIndex) {
        this.rightAnswerIndex = rightAnswerIndex;
    }
}
