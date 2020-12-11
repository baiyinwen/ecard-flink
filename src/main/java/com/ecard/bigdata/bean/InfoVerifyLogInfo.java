package com.ecard.bigdata.bean;

import java.io.Serializable;

/**
 * @Description
 * @Author WangXueDong
 * @Date 2020/12/03 15:24
 * @Version 1.0
 **/
public class InfoVerifyLogInfo implements Serializable {

    private String vefFlag;
    private String appKey;
    private String event;
    private String logId;
    private String resultCode;
    private String msg;
    private String time;
    private String origLog;

    public String getVefFlag() {
        return vefFlag;
    }

    public void setVefFlag(String vefFlag) {
        this.vefFlag = vefFlag;
    }

    public String getAppKey() {
        return appKey;
    }

    public void setAppKey(String appKey) {
        this.appKey = appKey;
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

    public String getResultCode() {
        return resultCode;
    }

    public void setResultCode(String resultCode) {
        this.resultCode = resultCode;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getOrigLog() {
        return origLog;
    }

    public void setOrigLog(String origLog) {
        this.origLog = origLog;
    }

    @Override
    public String toString() {
        return "InfoVerifyLogInfo{" +
                "vefFlag='" + vefFlag + '\'' +
                ", appKey='" + appKey + '\'' +
                ", event='" + event + '\'' +
                ", logId='" + logId + '\'' +
                ", resultCode='" + resultCode + '\'' +
                ", msg='" + msg + '\'' +
                ", time='" + time + '\'' +
                '}';
    }

}
