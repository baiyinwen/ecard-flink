package com.ecard.bigdata.bean;

import java.io.Serializable;

/**
 * @Description
 * @Author WangXueDong
 * @Date 2020/12/1 16:44
 * @Version 1.0
 **/
public class CreditScoreLogInfo implements Serializable {

    private String time;
    private String event;
    private String logId;
    private String appKey;
    private String cardNo;
    private String enterpriseName;
    private String score;
    private String resultCode;
    private String resultMsg;
    private String timestamp;
    private String origLog;

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public String getLogId() {
        return logId;
    }

    public void setLogId(String logId) {
        this.logId = logId;
    }

    public String getAppKey() {
        return appKey;
    }

    public void setAppKey(String appKey) {
        this.appKey = appKey;
    }

    public String getCardNo() {
        return cardNo;
    }

    public void setCardNo(String cardNo) {
        this.cardNo = cardNo;
    }

    public String getEnterpriseName() {
        return enterpriseName;
    }

    public void setEnterpriseName(String enterpriseName) {
        this.enterpriseName = enterpriseName;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public String getResultCode() {
        return resultCode;
    }

    public void setResultCode(String resultCode) {
        this.resultCode = resultCode;
    }

    public String getResultMsg() {
        return resultMsg;
    }

    public void setResultMsg(String resultMsg) {
        this.resultMsg = resultMsg;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getOrigLog() {
        return origLog;
    }

    public void setOrigLog(String origLog) {
        this.origLog = origLog;
    }

    @Override
    public String toString() {
        return "CreditScoreLogInfo{" +
                "time='" + time + '\'' +
                ", event='" + event + '\'' +
                ", logId='" + logId + '\'' +
                ", appKey='" + appKey + '\'' +
                ", cardNo='" + cardNo + '\'' +
                ", enterpriseName='" + enterpriseName + '\'' +
                ", score='" + score + '\'' +
                ", resultCode='" + resultCode + '\'' +
                ", resultMsg='" + resultMsg + '\'' +
                ", timestamp='" + timestamp + '\'' +
                '}';
    }
}
