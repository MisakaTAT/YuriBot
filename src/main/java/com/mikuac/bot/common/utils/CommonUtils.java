package com.mikuac.bot.common.utils;

import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.UUID;

/**
 * 常用工具类
 *
 * @author Zero
 * @date 2020/11/3 12:13
 */
@Component
public class CommonUtils {

    @Resource
    private Environment environment;

    /**
     * 精确到小数点后两位，返回String
     *
     * @param value 值
     * @return 格式化后的字符串
     */
    public static String formatDouble(double value) {
        return String.valueOf(Double.valueOf(String.format("%.2f", value)));
    }

    /**
     * 秒转分秒
     *
     * @param value 秒值
     * @return 格式化后的字符串
     */
    public static String sFormat(double value) {
        long minute = Math.round(value) / 60;
        long seconds = Math.round(value) % 60;
        int m = Math.round(minute);
        int s = Math.round(seconds);
        return m + "分" + s + "秒";
    }

    /**
     * 生成UUID
     *
     * @return uuid
     */
    public static String getUuid() {
        return UUID.randomUUID().toString();
    }

    public String getHostAndPort() {
        return "http://127.0.0.1:" + environment.getProperty("local.server.port");
    }

    public static String secondFormat(long s) {
        String dateTimes = null;
        long days = s / (60 * 60 * 24);
        long hours = (s % (60 * 60 * 24)) / (60 * 60);
        long minutes = (s % (60 * 60)) / 60;
        long seconds = s % 60;
        if (days > 0) {
            dateTimes = days + " 天 " + hours + " 小时 " + minutes + " 分钟 " + seconds + " 秒 ";
        } else if (hours > 0) {
            dateTimes = hours + " 小时 " + minutes + " 分钟 " + seconds + " 秒 ";
        } else if (minutes > 0) {
            dateTimes = minutes + " 分钟 " + seconds + " 秒 ";
        } else {
            dateTimes = seconds + " 秒 ";
        }
        return dateTimes;
    }

}