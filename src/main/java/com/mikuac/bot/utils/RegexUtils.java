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

    /**
     * 获取r6战绩查询ID
     * @param msg
     * @return
     */
    public static String getR6Id(String msg) {
        final String replaceRegex = "^[Rr彩][6六][战数][绩据][查获][询取]-";
        return msg.replaceAll(replaceRegex,"");
    }

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

    /**
     * 获取全群推送内容
     * @param msg
     * @return
     */
    public static String getSendAllGroupMsg(String msg) {
        final String replaceRegex = "^(SendAllGroup-)";
        return msg.replaceAll(replaceRegex,"");
    }

}
