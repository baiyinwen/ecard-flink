package com.ecard.bigdata.utils;

import com.alibaba.fastjson.JSON;
import com.ecard.bigdata.constants.CONFIGS;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;

/**
 * @Description push数据到open-falcon
 * @Author WangXueDong
 * @Date 2019/11/22 15:19
 * @Version 1.0
 **/
public class PushToFalconUtils {

    private static Logger logger = LoggerFactory.getLogger(PushToFalconUtils.class);

    private CloseableHttpClient httpClient;

    private final Object syncLock = new Object();

    public PushToFalconUtils() {

        httpClient = createHttpClient();
    }

    private CloseableHttpClient createHttpClient() {

        PoolingHttpClientConnectionManager manager = new PoolingHttpClientConnectionManager();
        manager.setMaxTotal(10);
        return HttpClients.custom().setConnectionManager(manager).build();
    }

    private void getHttpClient() {

        if (null == httpClient) {
            synchronized(syncLock) {
                if (null == httpClient) {
                    httpClient = createHttpClient();
                    logger.info("get http client --- ");
                }
            }
        }
    }

    public void sendInfoToFalcon(String endpoint, String metric, long timestamp, int step, float value, String counterType, String tags) {

        HashMap<String, Object> sendInfo = new HashMap<>();
        sendInfo.put("endpoint", endpoint);
        sendInfo.put("metric", metric);
        sendInfo.put("timestamp", timestamp);
        sendInfo.put("step", step);
        sendInfo.put("value", value);
        sendInfo.put("counterType", counterType);
        sendInfo.put("tags", tags);

        HttpPost post = new HttpPost(ConfigUtils.getString(CONFIGS.OPEN_FALCON_PUSH_URL));
        CloseableHttpResponse response = null;
        try {
            StringEntity stringEntity = new StringEntity("["+ JSON.toJSONString(sendInfo)+"]");
            stringEntity.setContentEncoding("UTF-8");
            stringEntity.setContentType("application/json");
            post.setEntity(stringEntity);
            getHttpClient();
            response = httpClient.execute(post);
            if(response.getStatusLine().getStatusCode() == 200) {
                logger.info("push to open-falcon successfully --- " + sendInfo.toString());
            } else {
                logger.info("push to open-falcon failed --- " + sendInfo.toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (response != null) {
                try {
                    response.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
