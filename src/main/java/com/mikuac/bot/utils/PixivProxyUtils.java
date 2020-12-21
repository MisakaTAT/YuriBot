package com.mikuac.bot.utils;

/**
 * @author Zero
 * @date 2020/12/21 9:58
 */
public class PixivProxyUtils {

    final static String PROXY_SERVER = "https://i.loli.best/";

    public static String imgProxy(String pid) {
        return PROXY_SERVER + pid;
    }

}
