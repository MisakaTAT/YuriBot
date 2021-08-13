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

    public static String serverAddress;

    public static int serverPort;
    public static String botBotName;
    public static long botAdminId;
    public static long botSelfId;
    public static String prefixPrefix;
    public static int hitokotoCdTime;
    public static String setuApiKey;
    public static int setuCdTime;
    public static int setuDelTime;
    public static int setuMaxGet;
    public static int repeatRandomCountSize;
    public static String telegramProxyHost;
    public static int telegramProxyPort;
    public static String telegramBotName;
    public static String telegramBotToken;
    public static int banUtilsLimitTime;
    public static int banUtilsLimitCount;
    public static boolean maintenanceEnable;
    public static String maintenanceAlertMsg;
    public static String sauceNaoApiKey;
    public static ConfigBean config = ConfigUtils.init(false);

    public static void set() {
        serverAddress = config.getServer().getAddress();
        serverPort = config.getServer().getPort();
        botBotName = config.getBot().getBotName();
        botAdminId = config.getBot().getAdminId();
        botSelfId = config.getBot().getSelfId();
        prefixPrefix = config.getPrefix().getPrefix();
        hitokotoCdTime = config.getHitokoto().getCdTime();
        setuApiKey = config.getSetu().getApiKey();
        setuCdTime = config.getSetu().getCdTime();
        setuDelTime = config.getSetu().getDelTime();
        setuMaxGet = config.getSetu().getMaxGet();
        repeatRandomCountSize = config.getRepeat().getRandomCountSize();
        telegramProxyHost = config.getTelegram().getProxyHost();
        telegramProxyPort = config.getTelegram().getProxyPort();
        telegramBotName = config.getTelegram().getBotName();
        telegramBotToken = config.getTelegram().getBotToken();
        banUtilsLimitTime = config.getBanUtils().getLimitTime();
        banUtilsLimitCount = config.getBanUtils().getLimitCount();
        maintenanceEnable = config.getMaintenance().isEnable();
        maintenanceAlertMsg = config.getMaintenance().getAlertMsg();
        sauceNaoApiKey = config.getSauceNao().getApiKey();
    }

}
