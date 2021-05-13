package com.mikuac.bot.plugins.aop;

import com.alibaba.fastjson.JSON;
import com.mikuac.bot.bean.SearchBean;
import com.mikuac.bot.bean.whatanime.AnimeInfo;
import com.mikuac.bot.bean.whatanime.BasicData;
import com.mikuac.bot.bean.whatanime.Docs;
import com.mikuac.bot.config.ApiConst;
import com.mikuac.bot.config.RegexConst;
import com.mikuac.bot.utils.*;
import lombok.extern.slf4j.Slf4j;
import net.lz1998.pbbot.bot.Bot;
import net.lz1998.pbbot.bot.BotPlugin;
import net.lz1998.pbbot.utils.Msg;
import onebot.OnebotEvent;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Map;

/**
 * WhatAnime搜番
 *
 * @author Zero
 * @date 2020/12/1 9:36
 */
@Slf4j
@Component
public class WhatAnime extends BotPlugin {

    private BasicData basicData;

    private AnimeInfo animeInfo;

    private BanUtils banUtils;

    @Autowired
    public void setBanUtils(BanUtils banUtils) {
        this.banUtils = banUtils;
    }

    @Value("${yuri.plugins.banUtils.limitTime}")
    private int limitTime;
    @Value("${yuri.plugins.banUtils.limitCount}")
    private int limitCount;
    @Value("${yuri.plugins.whatAnime.apiKey}")
    private String apiKey;

    String graphqlQuery = "query ($id: Int) { Media (id: $id, type: ANIME) " +
            "{ type format status episodes season startDate { year month day } endDate { year month day } " +
            "coverImage { large } } }";

    public void getBasicData(String picUrl) throws IOException {
        String result = HttpClientUtils.httpGetWithJson(ApiConst.WHAT_ANIME_BASIC_API + "?token=" + apiKey + "&url=" + picUrl, false);
        basicData = JSON.parseObject(result, BasicData.class);
        // 取得基本信息后调用getDetailedData方法取得详细信息
        // api docs返回结果按相似性排序，从最相似到最不相似，所以取list第一个即可
        if (basicData != null && !basicData.getDocs().isEmpty()) {
            int aniListId = basicData.getDocs().get(0).getAniListId();
            doSearch(aniListId);
            log.info("getDetailedData方法调用成功，AniListId为[{}]", aniListId);
        } else {
            log.info("WhatAnime调用getDetailedData方法失败，可能基本信息获取失败或者docs为空");
        }
    }

    public void doSearch(long animeId) throws IOException {
        String json = "{\"query\": \"" + graphqlQuery + "\", \"variables\": {\"id\":" + animeId + "}}";
        String result = HttpUtils.post(ApiConst.WHAT_ANIME_INFO_API, json);
        animeInfo = JSON.parseObject(result, AnimeInfo.class);
    }

    public String videoUrl(int aniListId, String fileName, double at, String token) {
        return "https://media.trace.moe/video/" + aniListId + "" +
                "/" + URLEncoder.encode(fileName, StandardCharsets.UTF_8) + "?t=" + at + "&token=" + token + "&size=l";
    }

    @Override
    public int onGroupMessage(@NotNull Bot bot, @NotNull OnebotEvent.GroupMessageEvent event) {
        long groupId = event.getGroupId();
        long userId = event.getUserId();
        String msg = event.getRawMessage();
        // key加1以区分其它搜图模式
        long key = groupId + userId + 1;

        Map<Long, SearchBean> map = SearchModeUtils.getMap();

        if (msg.matches(RegexConst.WHATANIME)) {
            if (banUtils.isBanned(userId)) {
                bot.sendGroupMsg(groupId, Msg.builder().at(userId).text("您因触发滥用规则已被永久封禁~").build(), false);
                return MESSAGE_IGNORE;
            }
            // 防止重复执行
            if (map.get(key) != null) {
                bot.sendGroupMsg(groupId, Msg.builder().at(userId).text("您已经处于搜番模式啦，请直接发送图片让我来帮您检索~").build(), false);
                return MESSAGE_IGNORE;
            }
            // 检查是否处于搜(图/本)模式
            if (map.get(key + 1) != null) {
                bot.sendGroupMsg(groupId, Msg.builder().at(userId).text("您已经处于搜(图/本)模式，请先退出此模式后再次尝试~"), false);
                return MESSAGE_IGNORE;
            }
            SearchModeUtils.setMap(key, groupId, userId, "group");
            bot.sendGroupMsg(groupId, Msg.builder().at(userId).text("您已进入搜番模式，请发送番剧截图来帮您检索~ (注意：" + limitTime + "秒内发送超过" + limitCount + "张图片将会触发滥用规则被封禁)").build(), false);
            return MESSAGE_IGNORE;
        }

        if (msg.matches(RegexConst.WHATANIME_QUIT)) {
            if (map.get(key) != null) {
                SearchModeUtils.quitSearch(key);
                bot.sendGroupMsg(groupId, Msg.builder().at(userId).text("已为您退出搜番模式~").build(), false);
                return MESSAGE_IGNORE;
            }
            bot.sendGroupMsg(groupId, Msg.builder().at(userId).text("您尚未进入搜番模式~").build(), false);
            return MESSAGE_IGNORE;
        }

        if (map.get(key) != null && map.get(key).getEnable() && map.get(key).getGroupId() == groupId) {
            String picUrl = RegexUtils.regex(RegexConst.GET_MSG_PIC_URL, msg);
            // 判断是否为图片消息
            if (picUrl != null) {
                // 如有操作重新设置TTL
                map.get(key).setStartTime(Instant.now().getEpochSecond());
                // 检查滥用
                banUtils.setBan(userId);
                bot.sendGroupMsg(groupId, Msg.builder().at(userId).text("番剧检索中，请稍后~").build(), false);
                try {
                    getBasicData(picUrl);
                    Docs docs = basicData.getDocs().get(0);
                    // 获取详细信息
                    AnimeInfo.Media data = animeInfo.getGetData().getMedia();
                    String startTime = data.getStartDate().getYear() + "年" + data.getStartDate().getMonth() + "月" + data.getStartDate().getDay() + "日";
                    String endTime = data.getEndDate().getYear() + "年" + data.getEndDate().getMonth() + "月" + data.getEndDate().getDay() + "日";
                    Msg msgSend = Msg.builder()
                            .at(userId)
                            .image(data.getCoverImage().getLarge())
                            .text("\n该截图出自番剧" + docs.getTitleChinese() + "第" + docs.getEpisode() + "集")
                            .text("\n截图位于" + CommonUtils.sFormat(docs.getFrom()) + "至" + CommonUtils.sFormat(docs.getTo()) + "附近")
                            .text("\n精确位置大约位于：" + CommonUtils.sFormat(docs.getAt()))
                            .text("\n番剧类型：" + data.getType() + "-" + data.getFormat())
                            .text("\n状态：" + data.getStatus())
                            .text("\n总集数：" + data.getEpisodes())
                            .text("\n开播季节：" + data.getSeason())
                            .text("\n开播时间：" + startTime)
                            .text("\n完结时间：" + endTime)
                            .text("\n在" + basicData.getLimitTtl() + "秒内剩余" + basicData.getLimit() + "次搜索次数")
                            .text("\n今日配额剩余：" + basicData.getQuota())
                            .text("\n数据来源：WhatAnime");
                    String videoUrl = videoUrl(docs.getAniListId(), docs.getFilename(), docs.getAt(), docs.getTokenThumb());
                    bot.sendGroupMsg(groupId, Msg.builder().video(videoUrl, picUrl, false).build(), false);
                    bot.sendGroupMsg(groupId, msgSend.build(), false);
                } catch (Exception e) {
                    bot.sendGroupMsg(groupId, Msg.builder().at(userId).text("WhatAnime番剧检索失败，请稍后重试~").build(), false);
                    log.info("WhatAnime插件检索异常", e);
                }
            }
        }

        return MESSAGE_IGNORE;
    }

    @Override
    public int onPrivateMessage(@NotNull Bot bot, @NotNull OnebotEvent.PrivateMessageEvent event) {
        long userId = event.getUserId();
        String msg = event.getRawMessage();
        // key加1以区分其它搜图模式
        long key = userId + 1;
        Map<Long, SearchBean> map = SearchModeUtils.getMap();

        if (msg.matches(RegexConst.WHATANIME)) {
            if (banUtils.isBanned(userId)) {
                bot.sendPrivateMsg(userId, "您因触发滥用规则已被永久封禁~", false);
                return MESSAGE_IGNORE;
            }
            // 防止重复执行
            if (map.get(key) != null) {
                bot.sendPrivateMsg(userId, "您已经处于搜番模式啦，请直接发送图片让我来帮您检索~", false);
                return MESSAGE_IGNORE;
            }
            // 检查是否处于搜(图/本)模式
            if (map.get(key + 1) != null) {
                bot.sendPrivateMsg(userId, "您已经处于搜(图/本)模式，请先退出此模式后再次尝试~", false);
                return MESSAGE_IGNORE;
            }
            SearchModeUtils.setMap(key, userId, "private");
            bot.sendPrivateMsg(userId, "您已进入搜番模式，请发送番剧截图来帮您检索~ (注意：" + limitTime + "秒内发送超过" + limitCount + "张图片将会触发滥用规则被封禁)", false);
            return MESSAGE_IGNORE;
        }

        if (msg.matches(RegexConst.WHATANIME_QUIT)) {
            if (map.get(key) != null) {
                SearchModeUtils.quitSearch(key);
                bot.sendPrivateMsg(userId, "已为您退出搜番模式~", false);
                return MESSAGE_IGNORE;
            }
            bot.sendPrivateMsg(userId, "您尚未进入搜番模式~", false);
            return MESSAGE_IGNORE;
        }

        if (map.get(key) != null && map.get(key).getEnable()) {
            String picUrl = RegexUtils.regex(RegexConst.GET_MSG_PIC_URL, msg);
            if (picUrl != null) {
                // 如有操作重新设置TTL
                map.get(key).setStartTime(Instant.now().getEpochSecond());
                // 检查滥用
                banUtils.setBan(userId);
                bot.sendPrivateMsg(userId, "番剧搜索中，请稍后~", false);
                try {
                    getBasicData(picUrl);
                    Docs docs = basicData.getDocs().get(0);
                    // 获取详细信息
                    AnimeInfo.Media data = animeInfo.getGetData().getMedia();
                    String startTime = data.getStartDate().getYear() + "年" + data.getStartDate().getMonth() + "月" + data.getStartDate().getDay() + "日";
                    String endTime = data.getEndDate().getYear() + "年" + data.getEndDate().getMonth() + "月" + data.getEndDate().getDay() + "日";
                    Msg msgSend = Msg.builder()
                            .image(data.getCoverImage().getLarge())
                            .text("\n该截图出自番剧" + docs.getTitleChinese() + "第" + docs.getEpisode() + "集")
                            .text("\n截图位于" + CommonUtils.sFormat(docs.getFrom()) + "至" + CommonUtils.sFormat(docs.getTo()) + "附近")
                            .text("\n番剧类型：" + data.getType() + "-" + data.getFormat())
                            .text("\n状态：" + data.getStatus())
                            .text("\n总集数：" + data.getEpisodes())
                            .text("\n开播季节：" + data.getSeason())
                            .text("\n开播时间：" + startTime)
                            .text("\n完结时间：" + endTime)
                            .text("\n在" + basicData.getLimitTtl() + "秒内剩余" + basicData.getLimit() + "次搜索次数")
                            .text("\n今日配额剩余：" + basicData.getQuota())
                            .text("\n数据来源：WhatAnime");
                    String videoUrl = videoUrl(docs.getAniListId(), docs.getFilename(), docs.getAt(), docs.getTokenThumb());
                    bot.sendPrivateMsg(userId, msgSend.build(), false);
                    bot.sendPrivateMsg(userId, Msg.builder().video(videoUrl, picUrl, false).build(), false);
                } catch (Exception e) {
                    bot.sendPrivateMsg(userId, "WhatAnime检索服务出现异常，请稍后重试~", false);
                    log.info("WhatAnime插件检索异常", e);
                }
            }
        }
        return MESSAGE_IGNORE;
    }

}
