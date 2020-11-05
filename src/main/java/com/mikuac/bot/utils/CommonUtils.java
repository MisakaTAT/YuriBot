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
        double format = Math.round(value * 100) * 0.01d;
        return String.valueOf(format);
    }

}
