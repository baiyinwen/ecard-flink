package com.ecard.bigdata.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @Description 日志一级结构
 * @Author WangXuedong
 * @Date 2019/9/19 15:28
 * @Version 1.0
 **/
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class JsonLogInfo<I, O> implements Serializable {

    private String channelNo;
    private String costTime;
    private String event;
    private I input;
    private O output;
    private String time;
    private String type;
    private String version;

}
