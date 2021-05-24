package com.mikuac.bot.config;

import com.mikuac.bot.bean.ConfigBean;
import com.mikuac.bot.common.utils.ConfigUtils;
import org.springframework.stereotype.Component;

/**
 * Created on 2021/5/24.
 *
 * @author Zero
 */
@Component
public class Global {

    public static ConfigBean config = ConfigUtils.init(false);

    public static String server_address;
    public static int server_port;

    public static String bot_botName;
    public static long bot_adminId;
    public static long bot_selfId;

    public static String prefix_prefix;

    public static int hitokoto_cdTime;

    public static String setu_apiKey;
    public static int setu_cdTime;
    public static int setu_delTime;
    public static int setu_maxGet;

    public static int repeat_randomCountSize;

    public static String telegram_proxyHost;
    public static int telegram_proxyPort;
    public static String telegram_botName;
    public static String telegram_botToken;

    public static int banUtils_limitTime;
    public static int banUtils_limitCount;

    public static boolean maintenance_enable;
    public static String maintenance_alertMsg;

    public static String sauceNao_apiKey;

    public static void set() {
        server_address = config.getServer().getAddress();
        server_port = config.getServer().getPort();
        bot_botName = config.getBot().getBotName();
        bot_adminId = config.getBot().getAdminId();
        bot_selfId = config.getBot().getSelfId();
        prefix_prefix = config.getPrefix().getPrefix();
        hitokoto_cdTime = config.getHitokoto().getCdTime();
        setu_apiKey = config.getSetu().getApiKey();
        setu_cdTime = config.getSetu().getCdTime();
        setu_delTime = config.getSetu().getDelTime();
        setu_maxGet = config.getSetu().getMaxGet();
        repeat_randomCountSize = config.getRepeat().getRandomCountSize();
        telegram_proxyHost = config.getTelegram().getProxyHost();
        telegram_proxyPort = config.getTelegram().getProxyPort();
        telegram_botName = config.getTelegram().getBotName();
        telegram_botToken = config.getTelegram().getBotToken();
        banUtils_limitTime = config.getBanUtils().getLimitTime();
        banUtils_limitCount = config.getBanUtils().getLimitCount();
        maintenance_enable = config.getMaintenance().isEnable();
        maintenance_alertMsg = config.getMaintenance().getAlertMsg();
        sauceNao_apiKey = config.getSauceNao().getApiKey();
    }

}
