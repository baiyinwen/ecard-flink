package com.ecard.bigdata.bean;

import java.io.Serializable;

/**
 * @Description
 * @Author WangXueDong
 * @Date 2020/2/19 10:21
 * @Version 1.0
 **/
public class NginxLogInfo implements Serializable {

    /**Nginx日志服务器IP**/
    private String ip;
    /**日志对应接口**/
    private String event;
    /**日志对应接口请求时长**/
    private float costTime;
    /**日志对应接口生成时间**/
    private long time;
    /**返回码**/
    private String code;

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

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Override
    public String toString() {
        return "NginxLogInfo{" +
                "ip='" + ip + '\'' +
                ", event='" + event + '\'' +
                ", costTime=" + costTime +
                ", time=" + time +
                ", code='" + code + '\'' +
                '}';
    }
}
