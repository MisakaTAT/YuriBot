package com.mikuac.bot.utils;

import org.springframework.stereotype.Component;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 正则工具类
 * @author Zero
 * @date 2020/11/20 13:39
 */
@Component
public class RegexUtils {

    public final static String GET_MSG_PIC_URL = "(http|https)://[\\w\\-_]+(\\.[\\w\\-_]+)+([\\w\\-.,@?^=%&:/~+#]*[\\w\\-@?^=%&/~+#])?";

    public final static String GET_R6_ID = "(?<=询-).*";

    public final static String GET_SEND_ALL_GROUP_MSG = "(?<=SendAllGroup-).*";

    public static String regex (String regex,String msg) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher =  pattern.matcher(msg);
        if(matcher.find()){
            return matcher.group();
        }
        return null;
    }

}
