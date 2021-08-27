package com.mikuac.bot.config;

import com.mikuac.bot.bean.ConfigBean;
import com.mikuac.bot.common.utils.ConfigUtils;

/**
 * Created on 2021/5/24.
 *
 * @author Zero
 */
public class Global {

    public static String SERVER_ADDRESS;
    public static int SERVER_PORT;
    public static String BOT_NAME;
    public static long BOT_ADMIN_ID;
    public static long BOT_SELF_ID;
    public static String CMD_PREFIX;
    public static int HITOKOTO_CD_TIME;
    public static String SETU_API_KEY;
    public static int SETU_CD_TIME;
    public static int SETU_DELETE_TIME;
    public static int SETU_ONEDAY_MAX_GET;
    public static int REPEAT_RANDOM_COUNT_SIZE;
    public static int BAN_UTILS_LIMIT_TIME;
    public static int BAN_UTILS_LIMIT_COUNT;
    public static String SAUCENAO_API_KEY;
    public static ConfigBean config = ConfigUtils.init(false);

    public static void set() {
        SERVER_ADDRESS = config.getServer().getAddress();
        SERVER_PORT = config.getServer().getPort();
        BOT_NAME = config.getBot().getBotName();
        BOT_ADMIN_ID = config.getBot().getAdminId();
        BOT_SELF_ID = config.getBot().getSelfId();
        CMD_PREFIX = config.getPrefix().getPrefix();
        HITOKOTO_CD_TIME = config.getHitokoto().getCdTime();
        SETU_API_KEY = config.getSetu().getApiKey();
        SETU_CD_TIME = config.getSetu().getCdTime();
        SETU_DELETE_TIME = config.getSetu().getDelTime();
        SETU_ONEDAY_MAX_GET = config.getSetu().getMaxGet();
        REPEAT_RANDOM_COUNT_SIZE = config.getRepeat().getRandomCountSize();
        BAN_UTILS_LIMIT_TIME = config.getBanUtils().getLimitTime();
        BAN_UTILS_LIMIT_COUNT = config.getBanUtils().getLimitCount();
        SAUCENAO_API_KEY = config.getSauceNao().getApiKey();
    }

}
