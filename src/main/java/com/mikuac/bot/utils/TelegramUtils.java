package com.mikuac.bot.utils;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Telegram工具类
 * @author Zero
 * @date 2020/11/2 13:19
 */
@Slf4j
@Component
public class TelegramUtils {

    /**
     * 获取图片链接
     * @param botToken
     * @param fileId
     * @return
     * @throws Exception
     */
    public static String getImgUrl(String botToken, String fileId) {
        String api = "https://api.telegram.org/bot" + botToken + "/getFile?file_id=" + fileId;
        String result = null;
        try {
            result = HttpClientUtil.httpGetWithJson(api,false);
        } catch (Exception e) {
            log.info("Telegram图片链接获取异常 [{}]", e);
        }
        if (result != null) {
            JSONObject jsonObject = JSONObject.parseObject(result);
            String filePath = (String) jsonObject.getJSONObject("result").get("file_path");
            return "https://api.telegram.org/file/bot" + botToken + "/" + filePath;
        }else {
            return null;
        }
    }

}
