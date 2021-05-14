package com.mikuac.bot.config;

import org.springframework.stereotype.Component;

/**
 * api常量
 * @author Zero
 * @date 2020/12/3 12:04
 */
@Component
public class ApiConst {

    public final static String HITOKOTO_API = "https://v1.hitokoto.cn?c=";

    public final static String R6S_API = "https://www.r6s.cn/Stats?username=";

    public final static String SETU_API = "https://api.lolicon.app/setu/?apikey=";

    public final static String WHAT_ANIME_BASIC_API = "https://trace.moe/api/search";

    // public final static String WHAT_ANIME_BASIC_API = "https://api.trace.moe/search?cutBorders&url=";

    public final static String WHAT_ANIME_INFO_API = "https://graphql.anilist.co";

    public final static String SAUCENAO_API = "https://saucenao.com/search.php?";

    public final static String BILI_VIDEO_INFO_API = "https://api.bilibili.com/x/web-interface/view?bvid=";

    public final static String BIANCE_USDT_API = "https://api.binancezh.co/api/v3/ticker/price?symbol=";

    public final static String BIANCE_MARKET_API = "https://otc-api.huobi.be/v1/data/market/detail";

    public final static String STEAM_REP_API2 = "https://steamid.facheme.com/lookup";

}
