package com.ecard.bigdata.model;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * @Description 签发数据分析分钟
 * @Author WangXuedong
 * @Date 2019/9/20 11:41
 * @Version 1.0
 **/
public class SignAmount implements Serializable {

    private int id;
    private Timestamp collectTime;
    private String channelNo;
    private String cardRegionCode;
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
        return "SignAmount{" +
                "id=" + id +
                ", collectTime=" + collectTime +
                ", channelNo='" + channelNo + '\'' +
                ", cardRegionCode='" + cardRegionCode + '\'' +
                ", transferTimes=" + transferTimes +
                ", createTime=" + createTime +
                ", status=" + status +
                '}';
    }
}
