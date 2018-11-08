package com.huaneng.zhgd.bean;

import java.io.Serializable;
import java.util.List;

/**
 * 考试结果
 */
public class ExamResult implements Serializable {

    public int time;
    public String score;
    public List<Answer> data;
    public int correctCount;
    public int wrongCount;

    public void calcuCount() {
        for (Answer answer: data) {
            if (answer.isCorrect()) {
                correctCount++;
            } else {
                wrongCount++;
            }
        }
    }
}
