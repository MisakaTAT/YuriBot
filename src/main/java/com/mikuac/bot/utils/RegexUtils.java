package com.mikuac.bot.utils;

import org.springframework.stereotype.Component;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Zero
 * @date 2020/11/20 13:39
 */
@Component
public class RegexUtils {

//    public static String getR6Id(String msg) {
//        final String REGEX = "(?<=询).*?(?=战)";
//        Pattern pattern = Pattern.compile(REGEX);
//        Matcher matcher =  pattern.matcher(msg);
//        if(matcher.find()){
//            return matcher.group();
//        }
//        return null;
//    }

    /**
     * 传入消息字符串，匹配其中的图片链接
     * @param msg
     */
    public static String getMsgPicUrl(String msg) {
        final String regex = "(http|https)://[\\w\\-_]+(\\.[\\w\\-_]+)+([\\w\\-.,@?^=%&:/~+#]*[\\w\\-@?^=%&/~+#])?";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher =  pattern.matcher(msg);
        if(matcher.find()){
            return matcher.group();
        }
        return null;
    }

}