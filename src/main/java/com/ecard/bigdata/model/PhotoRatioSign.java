package com.ecard.bigdata.model;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * @Description
 * @Author WangXuedong
 * @Date 2021/3/17 10:00
 * @Version 1.0
 **/
public class PhotoRatioSign implements Serializable {

    private int id;
    private Timestamp collectTime;
    private Integer photoCount = 0;
    private Integer signCount = 0;
    private float result;
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

    public Integer getPhotoCount() {
        return photoCount;
    }

    public void setPhotoCount(Integer photoCount) {
        this.photoCount = photoCount;
    }

    public Integer getSignCount() {
        return signCount;
    }

    public void setSignCount(Integer signCount) {
        this.signCount = signCount;
    }

    public float getResult() {
        return result;
    }

    public void setResult(float result) {
        this.result = result;
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
        return "PhotoRatioSign{" +
                "id=" + id +
                ", collectTime=" + collectTime +
                ", photoCount=" + photoCount +
                ", signCount=" + signCount +
                ", result=" + result +
                ", createTime=" + createTime +
                ", status=" + status +
                '}';
    }
}
