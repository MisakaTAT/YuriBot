package com.mikuac.bot.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import java.util.UUID;

/**
 * 常用工具类
 * @author Zero
 * @date 2020/11/3 12:13
 */
@Component
public class CommonUtils {

    private Environment environment;

    @Autowired
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    public String getHostAndPort(){
        return "http://127.0.0.1:" + environment.getProperty("local.server.port");
    }

    /**
     * 精确到小数点后两位，返回String
     * @param value
     * @return
     */
    public static String formatDouble(double value) {
        return String.valueOf(Double.valueOf(String.format("%.2f", value)));
    }

    /**
     * 秒转分秒
     * @param value
     * @return
     */
    public static String sFormat (double value) {
        long minute = Math.round(value) / 60;
        long seconds = Math.round(value) % 60;
        int m  = Math.round(minute);
        int s = Math.round(seconds);
        return m + "分" + s + "秒";
    }

    /**
     * 生成UUID
     * @return
     */
    public static String getUUID() {
        return UUID.randomUUID().toString();
    }

}