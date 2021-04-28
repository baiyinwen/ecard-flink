package com.ecard.bigdata.model;

import java.io.Serializable;

/**
 * @Description
 * @Author WangXueDong
 * @Date 2020/8/3 9:21
 * @Version 1.0
 **/
public class CreditScore implements Serializable {

    private String creditID = "";
    private String score = "";
    private String time = "";

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
        return creditID + "," + score + ","+ time;
    }

}
