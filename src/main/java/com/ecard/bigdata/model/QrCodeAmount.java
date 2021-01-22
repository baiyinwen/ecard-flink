package com.ecard.bigdata.model;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * @Description
 * @Author WangXuedong
 * @Date 2021/1/21 15:41
 * @Version 1.0
 **/
public class QrCodeAmount implements Serializable {

    private int id;
    private Timestamp collectTime;
    private String event;
    private Integer transferTimes;
    private Timestamp createTime;
    private int status = 1;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Timestamp getCollectTime() {
        return collectTime;
    }

    public void setCollectTime(Timestamp collectTime) {
        this.collectTime = collectTime;
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public Integer getTransferTimes() {
        return transferTimes;
    }

    public void setTransferTimes(Integer transferTimes) {
        this.transferTimes = transferTimes;
    }

    public Timestamp getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Timestamp createTime) {
        this.createTime = createTime;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "QrCodeAmount{" +
                "id=" + id +
                ", collectTime=" + collectTime +
                ", event='" + event + '\'' +
                ", transferTimes=" + transferTimes +
                ", createTime=" + createTime +
                ", status=" + status +
                '}';
    }
}
