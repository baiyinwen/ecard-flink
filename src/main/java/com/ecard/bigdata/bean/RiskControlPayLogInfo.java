package com.ecard.bigdata.bean;

import java.io.Serializable;

/**
 * @Description 风控日志
 * @Author WangXueDong
 * @Date 2020/11/16 16:26
 * @Version 1.0
 **/
public class RiskControlPayLogInfo implements Serializable {

    private String tag;
    private String key;
    private String ak;
    private String essCardNo;
    private String userName;
    private String uniformOrderId;
    private String uniformOrderCreateTime;
    private String ministryMerchantId;
    private String tradeStatus;
    private String akc264;
    private String aaz570;
    private String payGroupType;
    private String esscAab301;
    private String transArea;
    private String bankCode;
    private String ownPayCh;
    private String cardType;
    private String serviceType;

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

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

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUniformOrderId() {
        return uniformOrderId;
    }

    public void setUniformOrderId(String uniformOrderId) {
        this.uniformOrderId = uniformOrderId;
    }

    public String getUniformOrderCreateTime() {
        return uniformOrderCreateTime;
    }

    public void setUniformOrderCreateTime(String uniformOrderCreateTime) {
        this.uniformOrderCreateTime = uniformOrderCreateTime;
    }

    public String getMinistryMerchantId() {
        return ministryMerchantId;
    }

    public void setMinistryMerchantId(String ministryMerchantId) {
        this.ministryMerchantId = ministryMerchantId;
    }

    public String getTradeStatus() {
        return tradeStatus;
    }

    public void setTradeStatus(String tradeStatus) {
        this.tradeStatus = tradeStatus;
    }

    public String getAkc264() {
        return akc264;
    }

    public void setAkc264(String akc264) {
        this.akc264 = akc264;
    }

    public String getAaz570() {
        return aaz570;
    }

    public void setAaz570(String aaz570) {
        this.aaz570 = aaz570;
    }

    public String getPayGroupType() {
        return payGroupType;
    }

    public void setPayGroupType(String payGroupType) {
        this.payGroupType = payGroupType;
    }

    public String getEsscAab301() {
        return esscAab301;
    }

    public void setEsscAab301(String esscAab301) {
        this.esscAab301 = esscAab301;
    }

    public String getTransArea() {
        return transArea;
    }

    public void setTransArea(String transArea) {
        this.transArea = transArea;
    }

    public String getBankCode() {
        return bankCode;
    }

    public void setBankCode(String bankCode) {
        this.bankCode = bankCode;
    }

    public String getOwnPayCh() {
        return ownPayCh;
    }

    public void setOwnPayCh(String ownPayCh) {
        this.ownPayCh = ownPayCh;
    }

    public String getCardType() {
        return cardType;
    }

    public void setCardType(String cardType) {
        this.cardType = cardType;
    }

    public String getServiceType() {
        return serviceType;
    }

    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }

    @Override
    public String toString() {
        return "RiskControlLogInfo{" +
                "tag='" + tag + '\'' +
                ", key='" + key + '\'' +
                ", ak='" + ak + '\'' +
                ", essCardNo='" + essCardNo + '\'' +
                ", userName='" + userName + '\'' +
                ", uniformOrderId='" + uniformOrderId + '\'' +
                ", uniformOrderCreateTime='" + uniformOrderCreateTime + '\'' +
                ", ministryMerchantId='" + ministryMerchantId + '\'' +
                ", tradeStatus='" + tradeStatus + '\'' +
                ", akc264='" + akc264 + '\'' +
                ", aaz570='" + aaz570 + '\'' +
                ", payGroupType='" + payGroupType + '\'' +
                ", esscAab301='" + esscAab301 + '\'' +
                ", transArea='" + transArea + '\'' +
                ", bankCode='" + bankCode + '\'' +
                ", ownPayCh='" + ownPayCh + '\'' +
                ", cardType='" + cardType + '\'' +
                ", serviceType='" + serviceType + '\'' +
                '}';
    }
}
