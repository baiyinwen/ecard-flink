package com.ecard.bigdata.bean;

import com.alibaba.fastjson.JSONObject;

import java.io.Serializable;

/**
 * @Description 日志一级结构
 * @Author WangXuedong
 * @Date 2019/9/19 15:28
 * @Version 1.0
 **/
public class JsonLogInfo implements Serializable {
    private String channelNo;
    private String appKey;
    private String costTime;
    private String event;
    private JSONObject input;
    private JSONObject output;
    private String time;
    private String type;
    private String version;
    private String origLog;

    public String getChannelNo() {
        return channelNo;
    }

    public void setChannelNo(String channelNo) {
        this.channelNo = channelNo;
    }

    public String getCostTime() {
        return costTime;
    }

    public void setCostTime(String costTime) {
        this.costTime = costTime;
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public JSONObject getInput() {
        return input;
    }

    public void setInput(JSONObject input) {
        this.input = input;
    }

    public JSONObject getOutput() {
        return output;
    }

    public void setOutput(JSONObject output) {
        this.output = output;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getOrigLog() {
        return origLog;
    }

    public void setOrigLog(String origLog) {
        this.origLog = origLog;
    }

    public String getAppKey() {
        return appKey;
    }

    public void setAppKey(String appKey) {
        this.appKey = appKey;
    }

    @Override
    public String toString() {
        return "JsonLogInfo{" +
                "channelNo='" + channelNo + '\'' +
                ", appKey='" + appKey + '\'' +
                ", costTime='" + costTime + '\'' +
                ", event='" + event + '\'' +
                ", input=" + input +
                ", output=" + output +
                ", time='" + time + '\'' +
                ", type='" + type + '\'' +
                ", version='" + version + '\'' +
                ", origLog='" + origLog + '\'' +
                '}';
    }
}
