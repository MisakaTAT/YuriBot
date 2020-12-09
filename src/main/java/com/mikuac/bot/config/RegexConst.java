package com.mikuac.bot.config;

import org.springframework.stereotype.Component;

/**
 * 消息正则常量定义类
 * @author Zero
 * @date 2020/12/3 9:37
 */
@Component
public class RegexConst {

    public final static String HITOKOTO = "^hitokoto(.*)$|^一言(.*)$";

    public final static String RAINBOW_SIX_STATS = "^[Rr彩][6六][战数][绩据][查获][询取]-(.*)$";

    public final static String SETU = "^[来來发發给給]([1一])?[张張个個幅点點份]([Rr]18的?)?[色瑟][图圖]$|^setu(-[Rr]18)?$|^[色瑟][图圖](-[Rr]18)?$";

    public final static String WHATANIME = "^(搜番(模式)?)$";

    public final static String WHATANIME_QUIT = "^(退出搜番(模式)?)$";

    public final static String SAUCE_NAO = "^(搜[图本](子)?(模式)?)$";

    public final static String SAUCE_NAO_QUIT = "^(退出搜[图本](子)?(模式)?)$";

    public final static String SEND_ALL_GROUP = "^(SendAllGroup)-(.*)$";

    public final static String SEND_HELP = "^(?i)help|帮助";

    public final static String GET_MSG_PIC_URL = "(http|https)://[\\w\\-_]+(\\.[\\w\\-_]+)+([\\w\\-.,@?^=%&:/~+#]*[\\w\\-@?^=%&/~+#])?";

    public final static String GET_R6_ID = "(?<=询-).*";

    public final static String GET_SEND_ALL_GROUP_MSG = "(?<=SendAllGroup-).*";

    public final static String AT_ME = "(?<=<at qq=\").*?(?=\"/>)";

    public final static String GET_QQ_DOC_URL = "(?<=\"qqdocurl\":\").*?(?=\\?share_medium)";

    public final static String GET_URL_BVID = "(?<=video/).*?(?=\\?p=)";

    public final static String ANTI_BILI_MINI_APP = "^(.*?)1109937557(.*)";

    public final static String GET_SYS_INFO = "^(?i)status|(获取)?(运行)?(服务)?状态";

}
