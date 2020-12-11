package com.ecard.bigdata.sink;

import com.ecard.bigdata.bean.RiskControlPayLogInfo;
import com.ecard.bigdata.constants.CONFIGS;
import com.ecard.bigdata.utils.ConfigUtils;
import com.ecard.bigdata.utils.HBaseUtils;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;
import org.apache.hadoop.hbase.client.Put;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Description
 * @Author WangXueDong
 * @Date 2020/11/18 18:11
 * @Version 1.0
 **/
public class RiskControlPaySaveSink extends RichSinkFunction<RiskControlPayLogInfo> {

    private static Logger logger = LoggerFactory.getLogger(RiskControlPaySaveSink.class);
    private static HBaseUtils hBaseUtils;

    private static String FAMILY = "data";
    private static String Ak = "ak";
    private static String EssCardNo = "essCardNo";
    private static String UserName = "userName";
    private static String UniformOrderId = "uniformOrderId";
    private static String UniformOrderCreateTime = "uniformOrderCreateTime";
    private static String MinistryMerchantId = "ministryMerchantId";
    private static String TradeStatus = "tradeStatus";
    private static String Akc264 = "akc264";
    private static String Aaz570 = "aaz570";
    private static String PayGroupType = "payGroupType";
    private static String EsscAab301 = "esscAab301";
    private static String TransArea = "transArea";
    private static String BankCode = "bankCode";
    private static String OwnPayCh = "ownPayCh";
    private static String CardType = "cardType";
    private static String ServiceType = "serviceType";

    @Override
    public void open(Configuration parameters) throws Exception {

        logger.info("调用open --- ");
        super.open(parameters);
        String tableName = ConfigUtils.getString(CONFIGS.RISK_CONTROL_PAY_HBASE_TABLE);
        hBaseUtils = new HBaseUtils(tableName);
    }

    @Override
    public void close() throws Exception {

        logger.info("调用close --- ");
        super.close();
        hBaseUtils.close();
    }

    @Override
    public void invoke(RiskControlPayLogInfo riskControlPayLogInfo, Context context) {

        try {
            hBaseUtils.putData(makeSavePut(riskControlPayLogInfo));
            hBaseUtils.flush();
        } catch (Exception e) {
            logger.error(riskControlPayLogInfo.toString());
            e.printStackTrace();
        }
    }

    private Put makeSavePut(RiskControlPayLogInfo riskControlPayLogInfo){
        String ak = riskControlPayLogInfo.getAk() != null ? riskControlPayLogInfo.getAk() : "";
        String essCardNo = riskControlPayLogInfo.getEssCardNo() != null ? riskControlPayLogInfo.getEssCardNo() : "";
        String userName = riskControlPayLogInfo.getUserName() != null ? riskControlPayLogInfo.getUserName() : "";
        String uniformOrderId = riskControlPayLogInfo.getUniformOrderId() != null ? riskControlPayLogInfo.getUniformOrderId() : "";
        String uniformOrderCreateTime = riskControlPayLogInfo.getUniformOrderCreateTime() != null ? riskControlPayLogInfo.getUniformOrderCreateTime() : "";
        String ministryMerchantId = riskControlPayLogInfo.getMinistryMerchantId() != null ? riskControlPayLogInfo.getMinistryMerchantId() : "";
        String tradeStatus = riskControlPayLogInfo.getTradeStatus() != null ? riskControlPayLogInfo.getTradeStatus() : "";
        String akc264 = riskControlPayLogInfo.getAkc264() != null ? riskControlPayLogInfo.getAkc264() : "";
        String aaz570 = riskControlPayLogInfo.getAaz570() != null ? riskControlPayLogInfo.getAaz570() : "";
        String payGroupType = riskControlPayLogInfo.getPayGroupType() != null ? riskControlPayLogInfo.getPayGroupType() : "";
        String esscAab301 = riskControlPayLogInfo.getEsscAab301() != null ? riskControlPayLogInfo.getEsscAab301() : "";
        String transArea = riskControlPayLogInfo.getTransArea() != null ? riskControlPayLogInfo.getTransArea() : "";
        String bankCode = riskControlPayLogInfo.getBankCode() != null ? riskControlPayLogInfo.getBankCode() : "";
        String ownPayCh = riskControlPayLogInfo.getOwnPayCh() != null ? riskControlPayLogInfo.getOwnPayCh() : "";
        String cardType = riskControlPayLogInfo.getCardType() != null ? riskControlPayLogInfo.getCardType() : "";
        String serviceType = riskControlPayLogInfo.getServiceType() != null ? riskControlPayLogInfo.getServiceType() : "";

        Put put = new Put(riskControlPayLogInfo.getUniformOrderId().getBytes());
        put.addColumn(FAMILY.getBytes(), Ak.getBytes(), ak.getBytes());
        put.addColumn(FAMILY.getBytes(), EssCardNo.getBytes(), essCardNo.getBytes());
        put.addColumn(FAMILY.getBytes(), UserName.getBytes(), userName.getBytes());
        put.addColumn(FAMILY.getBytes(), UniformOrderId.getBytes(), uniformOrderId.getBytes());
        put.addColumn(FAMILY.getBytes(), UniformOrderCreateTime.getBytes(), uniformOrderCreateTime.getBytes());
        put.addColumn(FAMILY.getBytes(), MinistryMerchantId.getBytes(), ministryMerchantId.getBytes());
        put.addColumn(FAMILY.getBytes(), TradeStatus.getBytes(), tradeStatus.getBytes());
        put.addColumn(FAMILY.getBytes(), Akc264.getBytes(), akc264.getBytes());
        put.addColumn(FAMILY.getBytes(), Aaz570.getBytes(), aaz570.getBytes());
        put.addColumn(FAMILY.getBytes(), PayGroupType.getBytes(), payGroupType.getBytes());
        put.addColumn(FAMILY.getBytes(), EsscAab301.getBytes(), esscAab301.getBytes());
        put.addColumn(FAMILY.getBytes(), TransArea.getBytes(), transArea.getBytes());
        put.addColumn(FAMILY.getBytes(), BankCode.getBytes(), bankCode.getBytes());
        put.addColumn(FAMILY.getBytes(), OwnPayCh.getBytes(), ownPayCh.getBytes());
        put.addColumn(FAMILY.getBytes(), CardType.getBytes(), cardType.getBytes());
        put.addColumn(FAMILY.getBytes(), ServiceType.getBytes(), serviceType.getBytes());
        return put;
    }

}
