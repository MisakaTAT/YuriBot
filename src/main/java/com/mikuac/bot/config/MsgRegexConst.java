package com.mikuac.bot.config;

import org.springframework.stereotype.Component;

/**
 * 消息正则常量定义类
 * @author Zero
 * @date 2020/12/3 9:37
 */
@Component
public class MsgRegexConst {

    public final static String HITOKOTO = "^hitokoto(.*)$|^一言(.*)$";

    public final static String RAINBOW_SIX_STATS = "^[Rr彩][6六][战数][绩据][查获][询取]-(.*)$";

    public final static String SETU = "^[来來发發给給]([1一])?[张張个個幅点點份]([Rr]18的?)?[色瑟][图圖]$|^setu(-[Rr]18)?$|^[色瑟][图圖](-[Rr]18)?$";

    public final static String WHATANIME = "^(搜番模式)$";

    public final static String WHATANIME_QUIT = "^(退出搜番模式)$";

    public final static String SEND_ALL_GROUP = "^(SendAllGroup)-(.*)$";

    public final static String SEND_HELP = "^(?i)help|帮助";

}
