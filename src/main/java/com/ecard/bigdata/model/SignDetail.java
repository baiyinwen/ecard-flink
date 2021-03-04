package com.ecard.bigdata.model;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * @Description 签发数据分析分钟
 * @Author Wanghongdong
 * @Date 2020/02/05 11:41
 * @Version 1.0
 **/
public class SignDetail implements Serializable {

    private int id;
    private Timestamp collectTime;
    private String channelNo;
    private String cardRegionCode;
    private String aac002;
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

    public String getChannelNo() {
        return channelNo;
    }

    public void setChannelNo(String channelNo) {
        this.channelNo = channelNo;
    }

    public String getCardRegionCode() {
        return cardRegionCode;
    }

    public void setCardRegionCode(String cardRegionCode) {
        this.cardRegionCode = cardRegionCode;
    }

    public String getAac002() {
        return aac002;
    }

    public void setAac002(String aac002) {
        this.aac002 = aac002;
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
        return "SignAmount{" +
                "id=" + id +
                ", collectTime=" + collectTime +
                ", channelNo='" + channelNo + '\'' +
                ", aac002='" + aac002 + '\'' +
                ", cardRegionCode='" + cardRegionCode + '\'' +
                ", createTime=" + createTime +
                ", status=" + status +
                '}';
    }
}
