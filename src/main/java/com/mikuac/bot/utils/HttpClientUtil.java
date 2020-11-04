package com.mikuac.bot.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Http请求工具类
 * @author Zero
 * @date  2020/10/23 22:50
 */
@Slf4j
@Component
public class HttpClientUtil {

    /**
     * Get请求并返回Json数据
     * @param url api地址
     * @return
     * @throws Exception
     */
    public static String httpGetWithJson(String url) {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        RequestConfig requestConfig = RequestConfig.custom()
                // 设置连接超时时间
                .setConnectTimeout(5000)
                // 设置请求超时时间
                .setConnectionRequestTimeout(5000)
                .setSocketTimeout(5000)
                // 默认允许自动重定向
                .setRedirectsEnabled(true)
                .build();
        HttpGet httpGet = new HttpGet(url);
        // 设置请求头
        httpGet.setHeader("referer","no-referer");
        httpGet.setConfig(requestConfig);
        try {
            HttpResponse httpResponse = httpClient.execute(httpGet);
            // 获得返回的结果
            return EntityUtils.toString(httpResponse.getEntity(),"UTF-8");
        } catch (IOException e) {
            log.info("HttpGetWithJson请求异常：[{}]", e);
        }finally {
            try {
                httpClient.close();
            } catch (IOException e) {
                log.info("HttpGetWithJson HttpClient Close异常：[{}]", e);
            }
        }
        return null;
    }

}