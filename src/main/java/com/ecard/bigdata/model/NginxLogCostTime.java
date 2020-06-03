package com.ecard.bigdata.model;

import java.io.Serializable;

/**
 * @Description
 * @Author WangXueDong
 * @Date 2020/2/19 11:04
 * @Version 1.0
 **/
public class NginxLogCostTime implements Serializable {

    private String ip;
    private String event;
    private float costTime;
    private long time;

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public float getCostTime() {
        return costTime;
    }

    public void setCostTime(float costTime) {
        this.costTime = costTime;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return "NginxLogCostTime{" +
                "ip='" + ip + '\'' +
                ", event='" + event + '\'' +
                ", costTime=" + costTime +
                ", time=" + time +
                '}';
    }
}
