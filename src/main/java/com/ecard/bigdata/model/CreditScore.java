package com.ecard.bigdata.model;

import java.io.Serializable;

/**
 * @Description
 * @Author WangXueDong
 * @Date 2020/8/3 9:21
 * @Version 1.0
 **/
public class CreditScore implements Serializable {

    private String creditID;
    private String time;
    private String score;

    public String getCreditID() {
        return creditID;
    }

    public void setCreditID(String creditID) {
        this.creditID = creditID;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    @Override
    public String toString() {
        return creditID + "," + time + ","+ score;
    }
}
