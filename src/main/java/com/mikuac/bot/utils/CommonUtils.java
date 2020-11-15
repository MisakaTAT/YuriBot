package com.mikuac.bot.utils;

import org.springframework.stereotype.Component;

/**
 * 常用工具类
 * @author Zero
 * @date 2020/11/3 12:13
 */
@Component
public class CommonUtils {

    /**
     * 精确到小数点后两位，返回String
     * @param value
     * @return
     */
    public static String formatDouble(double value) {
        return String.valueOf(Double.valueOf(String.format("%.2f", value)));
    }

}