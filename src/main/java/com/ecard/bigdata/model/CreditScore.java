package com.ecard.bigdata.model;

import java.io.Serializable;

/**
 * @Description
 * @Author WangXueDong
 * @Date 2020/8/3 9:21
 * @Version 1.0
 **/
public class CreditScore implements Serializable {

    private String topic;
    private String key;
    private String creditID;
    private String score;
    private String time;

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getCreditID() {
        return creditID;
    }

    public void setCreditID(String creditID) {
        this.creditID = creditID;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return topic + "," + key + ","+ creditID + "," + score + ","+ time;
    }

}
