package com.ecard.bigdata.bean;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * @Description 签发数据分析分钟
 * @Author WangXuedong
 * @Date 2019/9/20 11:41
 * @Version 1.0
 **/
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DataAnalysisSignMin implements Serializable {

    private int id;
    private Timestamp collectTime;
    private Integer transferTimes;
    private Timestamp createTime;
    private String status;

}
