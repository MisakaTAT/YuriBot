package com.mikuac.bot.plugins.aop;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mikuac.bot.bean.SearchBean;
import com.mikuac.bot.bean.whatanime.AnimeInfo;
import com.mikuac.bot.bean.whatanime.BasicInfo;
import com.mikuac.bot.common.utils.*;
import com.mikuac.bot.config.ApiConst;
import com.mikuac.bot.config.Global;
import com.mikuac.bot.config.RegexConst;
import com.mikuac.shiro.common.utils.MsgUtils;
import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.core.BotPlugin;
import com.mikuac.shiro.dto.event.message.GroupMessageEvent;
import com.mikuac.shiro.dto.event.message.PrivateMessageEvent;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
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

    String graphqlQuery = """
                query ($id: Int) {
                  Media (id: $id, type: ANIME) {
                    id
                    type
                    format
                    status
                    episodes
                    season
                    synonyms
                    title {
                      native
                      romaji
                      english
                    }
                    startDate {
                      year
                      month
                      day
                    }
                    endDate {
                      year
                      month
                      day
                    }
                    coverImage {
                      large
                    }
                  }
                }
            """;
    private BasicInfo basicInfo;
    private AnimeInfo animeInfo;
    private BanUtils banUtils;

    @Autowired
    public void setBanUtils(BanUtils banUtils) {
        this.banUtils = banUtils;
    }

    public void getBasicInfo(String picUrl) throws IOException {
        String result = RequestUtils.get(ApiConst.WHAT_ANIME_BASIC_API + picUrl, false);
        basicInfo = JSON.parseObject(result, BasicInfo.class);
        // 取得基本信息后调用getDetailedData方法取得详细信息
        // api docs返回结果按相似性排序，从最相似到最不相似，所以取list第一个即可
        if (basicInfo != null && !basicInfo.getResult().isEmpty()) {
            long aniListId = basicInfo.getResult().get(0).getAnilist();
            doSearch(aniListId);
        } else {
            log.error("WhatAnime doSearch Failed，可能基本信息获取失败或者aniListId为空");
        }
    }

    public void doSearch(long animeId) throws IOException {
        JSONObject variables = new JSONObject();
        variables.put("id", animeId);
        JSONObject json = new JSONObject();
        json.put("query", graphqlQuery);
        json.put("variables", variables);
        String result = RequestUtils.post(ApiConst.WHAT_ANIME_INFO_API, json.toJSONString());
        animeInfo = JSON.parseObject(result, AnimeInfo.class);
    }

    public MsgUtils buildMsg(Boolean isGroupMsg, long userId, AnimeInfo.Media data, BasicInfo.Result result) {
        String startTime = data.getStartDate().getYear() + "年" + data.getStartDate().getMonth() + "月" + data.getStartDate().getDay() + "日";
        String endTime = data.getEndDate().getYear() + "年" + data.getEndDate().getMonth() + "月" + data.getEndDate().getDay() + "日";
        // 构建Msg
        MsgUtils sendMsg = MsgUtils.builder();
        if (isGroupMsg) {
            sendMsg.at(userId).text("\n");
        }
        sendMsg.img(data.getCoverImage().getLarge());
        String animeName = data.getTitle().getChinese();
        if ("".equals(animeName)) {
            animeName = data.getTitle().getNativeName();
        }
        sendMsg.text("\n该截图出自番剧" + animeName + "第" + result.getEpisode() + "集");
        sendMsg.text("\n截图位于" + CommonUtils.sFormat(result.getFrom()) + "至" + CommonUtils.sFormat(result.getTo()) + "附近");
        sendMsg.text("\n番剧类型：" + data.getType() + "-" + data.getFormat());
        sendMsg.text("\n状态：" + data.getStatus());
        sendMsg.text("\n总集数：" + data.getEpisodes());
        sendMsg.text("\n开播季节：" + data.getSeason());
        sendMsg.text("\n开播时间：" + startTime);
        sendMsg.text("\n完结时间：" + endTime);
        sendMsg.text("\n数据来源：WhatAnime");
        return sendMsg;
    }

    @Override
    public int onGroupMessage(@NotNull Bot bot, @NotNull GroupMessageEvent event) {
        long groupId = event.getGroupId();
        long userId = event.getUserId();
        String msg = event.getMessage();
        // key加1以区分其它搜图模式
        long key = groupId + userId + 1;

        Map<Long, SearchBean> map = SearchModeUtils.getMap();

        if (msg.matches(RegexConst.WHATANIME)) {
            if (banUtils.isBanned(userId)) {
                bot.sendGroupMsg(groupId, MsgUtils.builder().at(userId).text("您因触发滥用规则已被永久封禁~").build(), false);
                return MESSAGE_IGNORE;
            }
            // 防止重复执行
            if (map.get(key) != null) {
                bot.sendGroupMsg(groupId, MsgUtils.builder().at(userId).text("您已经处于搜番模式啦，请直接发送图片让我来帮您检索~").build(), false);
                return MESSAGE_IGNORE;
            }
            // 检查是否处于搜(图/本)模式
            if (map.get(key + 1) != null) {
                bot.sendGroupMsg(groupId, MsgUtils.builder().at(userId).text("您已经处于搜(图/本)模式，请先退出此模式后再次尝试~").build(), false);
                return MESSAGE_IGNORE;
            }
            SearchModeUtils.setMap(key, groupId, userId, "group");
            bot.sendGroupMsg(groupId, MsgUtils.builder().at(userId).text("您已进入搜番模式，请发送番剧截图来帮您检索~ (注意：" + Global.BAN_UTILS_LIMIT_TIME + "秒内发送超过" + Global.BAN_UTILS_LIMIT_COUNT + "张图片将会触发滥用规则被封禁)").build(), false);
            return MESSAGE_IGNORE;
        }

        if (msg.matches(RegexConst.WHATANIME_QUIT)) {
            if (map.get(key) != null) {
                SearchModeUtils.quitSearch(key);
                bot.sendGroupMsg(groupId, MsgUtils.builder().at(userId).text("已为您退出搜番模式~").build(), false);
                return MESSAGE_IGNORE;
            }
            bot.sendGroupMsg(groupId, MsgUtils.builder().at(userId).text("您尚未进入搜番模式~").build(), false);
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
                bot.sendGroupMsg(groupId, MsgUtils.builder().at(userId).text("番剧检索中，请稍后~").build(), false);
                try {
                    getBasicInfo(picUrl);
                    BasicInfo.Result result = basicInfo.getResult().get(0);
                    // 获取详细信息
                    AnimeInfo.Media data = animeInfo.getGetData().getMedia();
                    // 构建消息
                    MsgUtils send = buildMsg(true, userId, data, result);
                    // 发送视频
                    bot.sendGroupMsg(groupId, send.build(), false);
                    bot.sendGroupMsg(groupId, MsgUtils.builder().video(result.getVideo(), picUrl).build(), false);
                } catch (Exception e) {
                    bot.sendGroupMsg(groupId, MsgUtils.builder().at(userId).text("WhatAnime番剧检索失败，请更换图片或稍后重试~").build(), false);
                    log.error("WhatAnime插件检索异常: {}", e.getMessage());
                }
            }
        }

        return MESSAGE_IGNORE;
    }

    @Override
    public int onPrivateMessage(@NotNull Bot bot, PrivateMessageEvent event) {
        long userId = event.getUserId();
        String msg = event.getMessage();
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
            bot.sendPrivateMsg(userId, "您已进入搜番模式，请发送番剧截图来帮您检索~ (注意：" + Global.BAN_UTILS_LIMIT_TIME + "秒内发送超过" + Global.BAN_UTILS_LIMIT_COUNT + "张图片将会触发滥用规则被封禁)", false);
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
                    getBasicInfo(picUrl);
                    BasicInfo.Result result = basicInfo.getResult().get(0);
                    // 获取详细信息
                    AnimeInfo.Media data = animeInfo.getGetData().getMedia();
                    // 构建消息
                    MsgUtils send = buildMsg(false, userId, data, result);
                    bot.sendPrivateMsg(userId, send.build(), false);
                    // 发送视频
                    bot.sendPrivateMsg(userId, MsgUtils.builder().video(result.getVideo(), picUrl).build(), false);
                } catch (Exception e) {
                    bot.sendPrivateMsg(userId, "WhatAnime番剧检索失败，请更换图片或稍后重试~", false);
                    log.error("WhatAnime插件检索异常: {}", e.getMessage());
                }
            }
        }

        return MESSAGE_IGNORE;
    }

}
