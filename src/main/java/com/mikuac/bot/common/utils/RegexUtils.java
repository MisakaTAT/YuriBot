package com.mikuac.bot.common.utils;

import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 正则工具类
 *
 * @author Zero
 * @date 2020/11/20 13:39
 */
@Component
public class RegexUtils {

    public static String regex(String regex, String msg) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(msg);
        if (matcher.find()) {
            return matcher.group();
        }
        return null;
    }

    public static String regexGroup(String regex, String msg, int groupId) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(msg);
        if (matcher.find()) {
            return matcher.group(groupId);
        }
        return null;
    }

}
