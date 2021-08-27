package com.mikuac.bot.bean;

import lombok.Data;

import java.util.List;

/**
 * Created on 2021/5/24.
 *
 * @author Zero
 */
@Data
public class ConfigBean {

    private Server server;
    private Bot bot;
    private Telegram telegram;
    private Prefix prefix;
    private Maintenance maintenance;
    private Repeat repeat;
    private Hitokoto hitokoto;
    private Setu setu;
    private SauceNao sauceNao;
    private BanUtils banUtils;

    @Data
    public static class Server {
        private String address;
        private int port;
    }

    @Data
    public static class Bot {
        private String botName;
        private long adminId;
        private long selfId;
    }

    @Data
    public static class Telegram {
        private boolean enableProxy;
        private String baseUrl;
        private String proxyHost;
        private int proxyPort;
        private String botName;
        private String botToken;
        private List<Long> forwardGroup;
    }

    @Data
    public static class Prefix {
        private String prefix;
    }

    @Data
    public static class Maintenance {
        private boolean enable;
        private String alertMsg;
    }

    @Data
    public static class Repeat {
        private int randomCountSize;
    }

    @Data
    public static class Hitokoto {
        private int cdTime;
    }

    @Data
    public static class Setu {
        private String apiKey;
        private int cdTime;
        private int delTime;
        private int maxGet;
    }

    @Data
    public static class SauceNao {
        private String apiKey;
    }

    @Data
    public static class BanUtils {
        private int limitTime;
        private int limitCount;
    }

}
