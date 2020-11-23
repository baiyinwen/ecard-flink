package com.ecard.bigdata.model;

/**
 * @Description
 * @Author WangXueDong
 * @Date 2020/11/19 17:02
 * @Version 1.0
 **/
public class RiskControlPayResult {

    private String ak;
    private String essCardNo;
    private String uniformOrderId;

    public String getAk() {
        return ak;
    }

    public void setAk(String ak) {
        this.ak = ak;
    }

    public String getEssCardNo() {
        return essCardNo;
    }

    public void setEssCardNo(String essCardNo) {
        this.essCardNo = essCardNo;
    }

    public String getUniformOrderId() {
        return uniformOrderId;
    }

    public void setUniformOrderId(String uniformOrderId) {
        this.uniformOrderId = uniformOrderId;
    }
}
