package com.mikuac.bot.common.utils;

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
 *
 * @author Zero
 * @date 2020/10/23 22:50
 */
@Slf4j
@Component
public class HttpClientUtils {

    /**
     * Get请求并返回Json数据
     *
     * @param url api地址
     * @return
     * @throws Exception
     */
    public static String httpGetWithJson(String url, Boolean noReferer) {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        RequestConfig requestConfig = RequestConfig.custom()
                // 设置连接超时时间
                .setConnectTimeout(10000)
                // 设置请求超时时间
                .setConnectionRequestTimeout(10000)
                .setSocketTimeout(10000)
                // 默认允许自动重定向
                .setRedirectsEnabled(true)
                .build();
        HttpGet httpGet = new HttpGet(url);
        // 设置请求头
        if (noReferer) {
            httpGet.setHeader("referer", "no-referer");
        }
        httpGet.setConfig(requestConfig);
        try {
            HttpResponse httpResponse = httpClient.execute(httpGet);
            // 获得返回的结果
            return EntityUtils.toString(httpResponse.getEntity(), "UTF-8");
        } catch (IOException e) {
            log.error("HttpGetWithJson Request Exception: {}", e.getMessage());
        } finally {
            try {
                httpClient.close();
            } catch (IOException e) {
                log.error("HttpGetWithJson HttpClient Close Exception: {}", e.getMessage());
            }
        }
        return null;
    }

}