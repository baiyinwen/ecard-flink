package com.ecard.bigdata.model;

import java.io.Serializable;

/**
 * @Description 日志一级结构
 * @Author WangXuedong
 * @Date 2019/9/19 15:28
 * @Version 1.0
 **/
public class JsonLog<I, O> implements Serializable {

    private String channelNo;
    private String costTime;
    private String event;
    private I input;
    private O output;
    private String time;
    private String type;
    private String version;

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

    public I getInput() {
        return input;
    }

    public void setInput(I input) {
        this.input = input;
    }

    public O getOutput() {
        return output;
    }

    public void setOutput(O output) {
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

    @Override
    public String toString() {
        return "JsonLog{" +
                "channelNo='" + channelNo + '\'' +
                ", costTime='" + costTime + '\'' +
                ", event='" + event + '\'' +
                ", input=" + input +
                ", output=" + output +
                ", time='" + time + '\'' +
                ", type='" + type + '\'' +
                ", version='" + version + '\'' +
                '}';
    }
}
