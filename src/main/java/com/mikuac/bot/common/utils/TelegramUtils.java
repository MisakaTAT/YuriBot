package com.mikuac.bot.common.utils;

import com.alibaba.fastjson.JSONObject;
import com.mikuac.bot.config.Global;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.stereotype.Component;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.Objects;

/**
 * Telegram工具类
 *
 * @author Zero
 * @date 2020/11/2 13:19
 */
@Slf4j
@Component
public class TelegramUtils {

    /**
     * 获取图片链接
     */
    public static String getImgUrl(String botToken, String fileId) {

        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        String baseUrl = "https://api.telegram.org";
        String proxyHost;
        int proxyPort;

        if (Global.telegramEnableProxy && !Global.telegramBaseUrl.isEmpty()) {
            baseUrl = Global.telegramBaseUrl;
        } else {
            if (Global.telegramEnableProxy) {
                proxyHost = Global.telegramProxyHost;
                proxyPort = Global.telegramProxyPort;
                Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyHost, proxyPort));
                builder = new OkHttpClient.Builder().proxy(proxy);
            }
            if (!Global.telegramBaseUrl.isEmpty()) {
                baseUrl = Global.telegramBaseUrl;
            }
        }
        String api = baseUrl + "/bot" + botToken + "/getFile?file_id=" + fileId;
        String result = null;
        OkHttpClient client = builder.build();
        Request request = new Request.Builder()
                .url(api)
                .build();
        Call call = client.newCall(request);
        try {
            Response response = call.execute();
            if (response.body() == null) {
                return null;
            }
            result = Objects.requireNonNull(response.body()).string();
        } catch (Exception e) {
            log.error("Telegram 图片链接获取异常: {}", e.getMessage());
        }
        if (result != null) {
            JSONObject jsonObject = JSONObject.parseObject(result);
            String filePath = (String) jsonObject.getJSONObject("result").get("file_path");
            return baseUrl + "/file/bot" + botToken + "/" + filePath;
        } else {
            return null;
        }
    }

}
