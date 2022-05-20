package com.ecard.bigdata.main;

import akka.protobuf.Internal;
import com.alibaba.fastjson.JSONObject;
import com.ecard.bigdata.bean.JsonLogInfo;
import com.ecard.bigdata.constants.CONFIGS;
import com.ecard.bigdata.schemas.JsonLogSchema;
import com.ecard.bigdata.utils.ConfigUtils;
import com.ecard.bigdata.utils.ExecutionEnvUtils;
import com.ecard.bigdata.utils.KafkaConfigUtils;
import com.ecard.bigdata.utils.ParameterUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.flink.api.common.functions.RuntimeContext;
import org.apache.flink.streaming.connectors.elasticsearch.ElasticsearchSinkFunction;
import org.apache.flink.streaming.connectors.elasticsearch.RequestIndexer;
import org.apache.flink.streaming.connectors.elasticsearch.util.RetryRejectedExecutionFailureHandler;
import org.apache.flink.streaming.connectors.elasticsearch6.ElasticsearchSink;
import org.apache.flink.streaming.connectors.elasticsearch6.RestClientFactory;
import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer010;
import org.apache.http.message.BasicHeader;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.Requests;
import org.mortbay.util.ajax.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class EcardAppData2EsStream {
    private static Logger logger = LoggerFactory.getLogger(EcardAppData2EsStream.class);
    private static final String ClassName = EcardAppData2EsStream.class.getSimpleName();

    public static void main(String[] args) throws Exception {

        logger.info("start " + ClassName);
        final ParameterTool parameterTool = ParameterUtils.createParameterTool();
        String topic  = parameterTool.get(CONFIGS.ECARDAPP_KAFKA_TOPIC);
        logger.info("topic is " + topic);
        String KafkaGroupId = topic + "_" + ClassName;
        Properties props = KafkaConfigUtils.createKafkaProps(parameterTool, KafkaGroupId);

        StreamExecutionEnvironment env = ExecutionEnvUtils.prepare(parameterTool);
        env.setStreamTimeCharacteristic(TimeCharacteristic.ProcessingTime);

        JsonLogSchema jsonLogSchema = new JsonLogSchema();
        FlinkKafkaConsumer010<JsonLogInfo> consumer = new FlinkKafkaConsumer010<>(topic, jsonLogSchema, props);
        DataStream<JsonLogInfo> data = env.addSource(consumer);

        String eshosts  = parameterTool.get(CONFIGS.ECARDAPP_ES_HOSTS);
        logger.info("eshost is " + eshosts);
        String[] hostsArray = eshosts.split(",");
        List<HttpHost> elsearchHosts = new ArrayList<>();
        for (int i =0;i<hostsArray.length;i++) {
            String ips = hostsArray[i];
            elsearchHosts.add(new HttpHost(ips.split(":")[0].toString(), Integer.valueOf(ips.split(":")[1]), "https"));
        }
        String eventLoginLog = ConfigUtils.getString(CONFIGS.ECARDAPP_LOGIN_LOG_EVENTS);
        ObjectMapper mapper = new ObjectMapper(); // jaskson ObjectMapper
        ElasticsearchSink.Builder<JsonLogInfo> esSinkBuilder = new ElasticsearchSink.Builder<>(
                elsearchHosts,
                new ElasticsearchSinkFunction<JsonLogInfo>() {
                    private static final long serialVersionUID = -6797861015704600807L;
                    public IndexRequest createIndexRequest(JsonLogInfo appLoginlog){
                        String event = appLoginlog.getEvent();
                        if (event.equals(eventLoginLog)) {
                            Map<String, String> jsonMap = (Map)JSON.parse(JSONObject.toJSONString(appLoginlog));
                            return Requests.indexRequest()
                                    .index("ecardapp_index") // 设置Index(库)
                                    .type("ecardapp_loginlog_type")//设置类型(表)
                                    //.id("ecardapp-loginlog-flink-id") // 设置文档ID
                                    .source(jsonMap);
                        }else {
                            return null;
                        }
                    }
                    @Override
                    public void process(JsonLogInfo element, RuntimeContext runtimeContext, RequestIndexer requestIndexer) {
                        requestIndexer.add(createIndexRequest(element));
                    }
                }
        );

        esSinkBuilder.setBulkFlushMaxActions(1);
        esSinkBuilder.setFailureHandler(new RetryRejectedExecutionFailureHandler());

        esSinkBuilder.setRestClientFactory((RestClientFactory) restClientBuilder -> {
            Header[] headers = new BasicHeader[]{new BasicHeader("Content-Type", "application/json")};
            restClientBuilder.setDefaultHeaders(headers);
        });

        data.addSink((ElasticsearchSink)esSinkBuilder.build());
        env.execute(ClassName);
    }
}
