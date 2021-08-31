package com.mikuac.bot.common.utils;

import cn.hutool.http.HttpRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 请求工具类
 *
 * @author Zero
 * @date 2020/10/23 22:50
 */
@Slf4j
@Component
public class RequestUtils {

    public static String get(String url, Boolean noReferer) {
        try {
            HttpRequest httpRequest = new HttpRequest(url);
            if (noReferer) {
                httpRequest.header("referer", "no-referer");
            }
            return httpRequest.execute().body();
        } catch (Exception e) {
            log.error("RequestUtils Get Exception: {}", e.getMessage());
        }
        return null;
    }

    public static String post(String url, String json) {
        try {
            return HttpRequest.post(url)
                    .body(json)
                    .execute().body();
        } catch (Exception e) {
            log.error("RequestUtils Post Exception: {}", e.getMessage());
        }
        return null;
    }

}