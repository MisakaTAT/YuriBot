package com.mikuac.bot.plugins;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.mikuac.bot.bean.whatanime.BasicData;
import com.mikuac.bot.bean.whatanime.Docs;
import com.mikuac.bot.bean.whatanime.InfoData;
import com.mikuac.bot.utils.CommonUtils;
import com.mikuac.bot.utils.HttpClientUtil;
import com.mikuac.bot.utils.RegexUtils;
import lombok.extern.slf4j.Slf4j;
import net.lz1998.pbbot.bot.Bot;
import net.lz1998.pbbot.bot.BotPlugin;
import net.lz1998.pbbot.utils.Msg;
import onebot.OnebotEvent;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * WhatAnime搜番
 * @author Zero
 * @date 2020/12/1 9:36
 */
@Slf4j
@Component
public class WhatAnime extends BotPlugin {

    private BasicData basicData;

    @Autowired
    public void setBasicData(BasicData basicData) {
        this.basicData = basicData;
    }

    private InfoData infoData;

    @Autowired
    public void setInfoData(InfoData infoData) {
        this.infoData = infoData;
    }

    @Value("${yuri.plugins.what-anime.basicApi}")
    private String basicApi;
    @Value("${yuri.plugins.what-anime.infoApi}")
    private String infoApi;
    @Value("${yuri.plugins.what-anime.msgRegex}")
    private String msgRegex;
    @Value("${yuri.plugins.what-anime.token}")
    private String token;

    public void getBasicData (String picUrl) {
        String result = HttpClientUtil.httpGetWithJson(basicApi + "?token=" + token + "&url=" + picUrl,false);
        basicData = JSON.parseObject(result, BasicData.class);
        // 取得基本信息后调用getDetailedData方法取得详细信息
        // api docs返回结果按相似性排序，从最相似到最不相似，所以取list第一个即可
        if (basicData != null && !basicData.getDocs().isEmpty()) {
            int aniListId = basicData.getDocs().get(0).getAniListId();
            getDetailedData(aniListId);
            log.info("getDetailedData方法调用成功，AniListId为[{}]",aniListId);
        } else {
            log.info("WhatAnime调用getDetailedData方法失败，可能基本信息获取失败或者docs为空");
        }
    }

    public void getDetailedData (int aniListId) {
        String result = HttpClientUtil.httpGetWithJson(infoApi + aniListId,false);
        JSONArray jsonArray = JSON.parseArray(result);
        if (jsonArray != null) {
            infoData = JSON.parseObject(jsonArray.getJSONObject(0).toString(), InfoData.class);
        } else {
            log.info("WhatAnime infoApi result null");
        }
    }

    @Override
    public int onGroupMessage(@NotNull Bot bot, @NotNull OnebotEvent.GroupMessageEvent event) {
        long groupId = event.getGroupId();
        long userId = event.getUserId();
        String msg = event.getRawMessage();

        if (msg.matches(msgRegex)) {
            String picUrl = RegexUtils.getMsgPicUrl(msg);
            if (picUrl != null) {
                bot.sendGroupMsg(groupId,Msg.builder().at(userId).text("番剧搜索中，请稍后~").build(),false);
                try {
                    getBasicData(picUrl);
                    Docs docs = basicData.getDocs().get(0);
                    String startTime = infoData.getStartDate().getYear()+"年"+infoData.getStartDate().getMonth()+"月"+infoData.getStartDate().getDay()+"日";
                    String endTime = infoData.getEndDate().getYear()+"年"+infoData.getEndDate().getMonth()+"月"+infoData.getEndDate().getDay()+"日";
                    Msg msgSend = Msg.builder()
                            .at(userId)
                            .image(infoData.getCoverImage().getLarge())
                            .text("\n该截图出自番剧"+docs.getTitleChinese()+"第"+docs.getEpisode()+"集")
                            .text("\n截图位于"+ CommonUtils.sFormat(docs.getFrom()) +"至"+CommonUtils.sFormat(docs.getTo())+"附近")
                            .text("\n精确位置大约位于："+CommonUtils.sFormat(docs.getAt()))
                            .text("\n番剧类型："+infoData.getType()+"-"+infoData.getFormat())
                            .text("\n状态："+infoData.getStatus())
                            .text("\n总集数："+infoData.getEpisodes())
                            .text("\n开播季节："+infoData.getSeason())
                            .text("\n开播时间："+startTime)
                            .text("\n完结时间："+endTime)
                            .text("\n在"+basicData.getLimitTtl()+"秒内剩余"+basicData.getLimit()+"次搜索次数")
                            .text("\n今日配额剩余："+basicData.getQuota());
                    bot.sendGroupMsg(groupId,msgSend.build(),false);
                } catch (Exception e) {
                    bot.sendGroupMsg(groupId,Msg.builder().at(userId).text("WhatAnime番剧检索失败，请稍后重试~").build(),false);
                    log.info("WhatAnime插件检索异常",e);
                }
            } else {
                bot.sendGroupMsg(groupId,Msg.builder().at(userId).text("消息中匹配图片链接失败").build(),false);
                log.info("WhatAnime图片链接匹配失败");
            }

        }

        return MESSAGE_IGNORE;
    }

    @Override
    public int onPrivateMessage(@NotNull Bot bot, @NotNull OnebotEvent.PrivateMessageEvent event) {
        long userId = event.getUserId();
        String msg = event.getRawMessage();

        if (msg.matches(msgRegex)) {
            String picUrl = RegexUtils.getMsgPicUrl(msg);
            if (picUrl != null) {
                bot.sendPrivateMsg(userId,"番剧搜索中，请稍后~",false);
                try {
                    getBasicData(picUrl);
                    Docs docs = basicData.getDocs().get(0);
                    String startTime = infoData.getStartDate().getYear()+"年"+infoData.getStartDate().getMonth()+"月"+infoData.getStartDate().getDay()+"日";
                    String endTime = infoData.getEndDate().getYear()+"年"+infoData.getEndDate().getMonth()+"月"+infoData.getEndDate().getDay()+"日";
                    Msg msgSend = Msg.builder()
                            .image(infoData.getCoverImage().getLarge())
                            .text("该截图出自番剧"+docs.getTitleChinese()+"第"+docs.getEpisode()+"集")
                            .text("\n截图位于"+ CommonUtils.sFormat(docs.getFrom()) +"至"+CommonUtils.sFormat(docs.getTo())+"附近")
                            .text("\n精确位置大约位于："+CommonUtils.sFormat(docs.getAt()))
                            .text("\n番剧类型："+infoData.getType()+"-"+infoData.getFormat())
                            .text("\n状态："+infoData.getStatus())
                            .text("\n总集数："+infoData.getEpisodes())
                            .text("\n开播季节："+infoData.getSeason())
                            .text("\n开播时间："+startTime)
                            .text("\n完结时间："+endTime)
                            .text("\n在"+basicData.getLimitTtl()+"秒内剩余"+basicData.getLimit()+"次搜索次数")
                            .text("\n今日配额剩余："+basicData.getQuota());
                    bot.sendPrivateMsg(userId,msgSend.build(),false);
                } catch (Exception e) {
                    bot.sendPrivateMsg(userId,"WhatAnime番剧检索失败，请稍后重试~",false);
                    log.info("WhatAnime插件检索异常",e);
                }
            } else {
                bot.sendPrivateMsg(userId,"消息中匹配图片链接失败",false);
                log.info("WhatAnime图片链接匹配失败");
            }

        }

        return MESSAGE_IGNORE;
    }

}