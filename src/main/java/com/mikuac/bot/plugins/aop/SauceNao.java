package com.mikuac.bot.plugins.aop;

import com.alibaba.fastjson.JSON;
import com.mikuac.bot.bean.SearchBean;
import com.mikuac.bot.bean.saucenao.Results;
import com.mikuac.bot.bean.saucenao.SauceNaoBean;
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
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.Instant;
import java.util.Map;

/**
 * SauceNao图片检索
 *
 * @author Zero
 * @date 2020/12/3 11:45
 */
@Slf4j
@Component
public class SauceNao extends BotPlugin {

    @Resource
    private SauceNaoBean sauceNaoBean;

    @Resource
    private BanUtils banUtils;

    public void searchResult(String picUrl) {
        String param = "api_key=" + Global.SAUCENAO_API_KEY + "&output_type=2&numres=3&db=999&url=" + picUrl;
        String result = HttpClientUtils.httpGetWithJson(ApiConst.SAUCENAO_API + param, false);
        sauceNaoBean = JSON.parseObject(result, SauceNaoBean.class);
    }

    public Boolean apiCheck(@NotNull Bot bot, long groupId, long userId) {
        // 检查24小时内剩余搜索额度
        if (sauceNaoBean.getHeader().getLongRemaining() <= 0) {
            if (groupId != 0L) {
                bot.sendGroupMsg(groupId, MsgUtils.builder().at(userId).text("Saucenao 24小时内搜索配额已耗尽，明天再来吧~").build(), false);
                return true;
            }
            bot.sendPrivateMsg(userId, "Saucenao 24小时内搜索配额已耗尽，明天再来吧~", false);
            return true;
        }
        // 检查30秒内剩余搜索额度
        if (sauceNaoBean.getHeader().getShortRemaining() <= 0) {
            if (groupId != 0L) {
                bot.sendGroupMsg(groupId, MsgUtils.builder().at(userId).text("Saucenao 30秒内搜索配额已耗尽，请稍后再试~").build(), false);
                return true;
            }
            bot.sendPrivateMsg(userId, "Saucenao 30秒内搜索配额已耗尽，请稍后再试~", false);
            return true;
        }
        // 检查是否有返回结果
        if (sauceNaoBean.getResults().size() <= 0) {
            if (groupId != 0L) {
                bot.sendGroupMsg(groupId, MsgUtils.builder().at(userId).text("抱歉，未检索到您发送的图片内容，请更换图片再次尝试~").build(), false);
                return true;
            }
            bot.sendPrivateMsg(userId, "抱歉，未检索到您发送的图片内容，请更换图片再次尝试~", false);
            return true;
        }
        return false;
    }

    public Boolean cmdCheck(@NotNull Bot bot, long groupId, long userId, String msg, Map<Long, SearchBean> map, long key) {
        // 开始搜图
        if (msg.matches(RegexConst.SAUCE_NAO)) {
            // 检查是否触发滥用规则被封禁
            if (banUtils.isBanned(userId)) {
                if (groupId != 0L) {
                    bot.sendGroupMsg(groupId, MsgUtils.builder().at(userId).text("您因触发滥用规则已被封禁~").build(), false);
                    return false;
                }
                bot.sendPrivateMsg(userId, "您因触发滥用规则已被封禁~", false);
                return false;
            }
            // 防止重复执行
            if (map.get(key) != null) {
                if (groupId != 0L) {
                    bot.sendGroupMsg(groupId, MsgUtils.builder().at(userId).text("您已经处于搜(图/本)模式啦，请直接发送图片让我来帮您检索~").build(), false);
                    return false;
                }
                bot.sendPrivateMsg(userId, "您已经处于搜(图/本)模式啦，请直接发送图片让我来帮您检索~", false);
                return false;
            }
            // 检查是否处于搜番模式
            if (map.get(key - 1) != null) {
                if (groupId != 0L) {
                    bot.sendGroupMsg(groupId, MsgUtils.builder().at(userId).text("您已经处于搜番模式，请先退出此模式后再次尝试~").build(), false);
                    return false;
                }
                bot.sendPrivateMsg(userId, "您已经处于搜番模式，请先退出此模式后再次尝试~", false);
                return false;
            }
            if (groupId != 0L) {
                SearchModeUtils.setMap(key, groupId, userId, "group");
                bot.sendGroupMsg(groupId, MsgUtils.builder().at(userId).text("您已进入搜(图/本)模式，请发送图片来帮您检索~ \n(注意：" + Global.BAN_UTILS_LIMIT_TIME + "秒内发送超过" + Global.BAN_UTILS_LIMIT_COUNT + "张图片将会触发滥用规则被封禁)").build(), false);
                return false;
            }
            SearchModeUtils.setMap(key, userId, "private");
            bot.sendPrivateMsg(userId, "您已进入搜(图/本)模式，请发送图片来帮您检索~ \n(注意：" + Global.BAN_UTILS_LIMIT_TIME + "秒内发送超过" + Global.BAN_UTILS_LIMIT_COUNT + "张图片将会触发滥用规则被封禁)", false);
            return false;
        }
        // 退出搜图
        if (msg.matches(RegexConst.SAUCE_NAO_QUIT)) {
            if (groupId != 0L) {
                if (map.get(key) != null) {
                    SearchModeUtils.quitSearch(key);
                    bot.sendGroupMsg(groupId, MsgUtils.builder().at(userId).text("已为您退出搜(图/本)模式~").build(), false);
                    return false;
                }
                bot.sendGroupMsg(groupId, MsgUtils.builder().at(userId).text("您尚未进入搜(图/本)模式~").build(), false);
                return false;
            }
            if (map.get(key) != null) {
                SearchModeUtils.quitSearch(key);
                bot.sendPrivateMsg(userId, "已为您退出搜(图/本)模式~", false);
                return false;
            }
            bot.sendPrivateMsg(userId, "您尚未进入搜(图/本)模式~", false);
            return false;
        }
        return true;
    }

    public void msgBuilder(@NotNull Bot bot, long groupId, long userId, Results r, int db) {
        if (r == null || db == -1) {
            if (groupId != 0L) {
                bot.sendGroupMsg(groupId, MsgUtils.builder().at(userId).text("未检索到您发送的内容，请更换图片后重新尝试~").build(), false);
                return;
            }
            bot.sendPrivateMsg(userId, "未检索到您发送的内容，请更换图片后重新尝试~", false);
            return;
        }

        MsgUtils sendMsg = MsgUtils.builder();
        // 如果群号不为0L则为群组消息
        if (groupId != 0L) {
            sendMsg.at(userId);
        }
        sendMsg.img(r.getResultHeader().getThumbnail());
        sendMsg.text("\n相似度: " + r.getResultHeader().getSimilarity() + "%");
        switch (db) {
            case 0 -> {
                sendMsg.text("\n标题: " + r.getResultData().getTitle());
                sendMsg.text("\n画师: " + r.getResultData().getMemberName());
                sendMsg.text("\nPixiv: https://pixiv.net/i/" + r.getResultData().getPixivId());
                sendMsg.text("\nAuthor: https://pixiv.net/u/" + r.getResultData().getMemberId());
                sendMsg.text("\nProxy: " + PixivProxyUtils.imgProxy(r.getResultData().getPixivId()));
                sendMsg.text("\n剩余搜索配额: " + sauceNaoBean.getHeader().getLongRemaining());
                sendMsg.text("\n数据来源: SauceNao (Pixiv)");
            }
            case 1 -> {
                sendMsg.text("\n链接: " + r.getResultData().getExtUrls().get(0));
                sendMsg.text("\n用户: " + "https://twitter.com/" + r.getResultData().getTwitterUserHandle());
                sendMsg.text("\n剩余搜索配额: " + sauceNaoBean.getHeader().getLongRemaining());
                sendMsg.text("\n数据来源: SauceNao (Twitter)");
            }
            case 2 -> {
                sendMsg.text("\n来源: " + r.getResultData().getSource());
                sendMsg.text("\n日文名: " + r.getResultData().getJpName());
                sendMsg.text("\n英文名: " + r.getResultData().getEngName());
                sendMsg.text("\n剩余搜索配额: " + sauceNaoBean.getHeader().getLongRemaining());
                sendMsg.text("\n数据来源: SauceNao (H-Misc)");
            }
            default -> {
            }
        }
        // 如果群号不为0L则为群组消息
        if (groupId != 0L) {
            bot.sendGroupMsg(groupId, sendMsg.build(), false);
        } else {
            bot.sendPrivateMsg(userId, sendMsg.build(), false);
        }
    }

    private boolean resultMatch(@NotNull Bot bot, long groupId, long userId) {
        // Index #41: Twitter
        // Index #5: pixiv Images
        // Index #18, #38: H-Misc
        for (Results r : sauceNaoBean.getResults()) {
            int indexId = r.getResultHeader().getIndexId();
            if (indexId == 5) {
                msgBuilder(bot, groupId, userId, r, 0);
                return true;
            }
            if (indexId == 41) {
                msgBuilder(bot, groupId, userId, r, 1);
                return true;
            }
            if (indexId == 38 || indexId == 18) {
                msgBuilder(bot, groupId, userId, r, 2);
                return true;
            }
        }
        msgBuilder(bot, groupId, userId, null, -1);
        return false;
    }

    @Override
    public int onPrivateMessage(@NotNull Bot bot, PrivateMessageEvent event) {
        String msg = event.getMessage();
        long userId = event.getUserId();
        // key加2以区分其它搜图模式
        long key = userId + 2;

        Map<Long, SearchBean> map = SearchModeUtils.getMap();

        if (!cmdCheck(bot, 0L, userId, msg, map, key)) {
            return MESSAGE_IGNORE;
        }

        if (map.get(key) != null && map.get(key).getEnable()) {
            String picUrl = RegexUtils.regex(RegexConst.GET_MSG_PIC_URL, msg);
            if (picUrl == null || picUrl.isEmpty()) {
                return MESSAGE_IGNORE;
            }
            // 如有操作重新设置TTL
            map.get(key).setStartTime(Instant.now().getEpochSecond());
            // 检查滥用
            banUtils.setBan(userId);
            bot.sendPrivateMsg(userId, "图片搜索中，请稍后~", false);
            try {
                searchResult(picUrl);
                if (apiCheck(bot, 0L, userId) || resultMatch(bot, 0L, userId
                )) {
                    return MESSAGE_IGNORE;
                }
            } catch (Exception e) {
                bot.sendPrivateMsg(userId, "SauceNao检索服务出现异常，请稍后重试~", false);
                log.error("SauceNao插件检索异常: {}", e.getMessage());
            }
        }
        return MESSAGE_IGNORE;
    }

    @Override
    public int onGroupMessage(@NotNull Bot bot, @NotNull GroupMessageEvent event) {
        String msg = event.getMessage();
        long userId = event.getUserId();
        long groupId = event.getGroupId();
        // key加2以区分其它搜图模式
        long key = userId + groupId + 2;

        Map<Long, SearchBean> map = SearchModeUtils.getMap();

        if (!cmdCheck(bot, groupId, userId, msg, map, key)) {
            return MESSAGE_IGNORE;
        }

        if (map.get(key) != null && map.get(key).getEnable()) {
            String picUrl = RegexUtils.regex(RegexConst.GET_MSG_PIC_URL, msg);
            if (picUrl == null || picUrl.isEmpty()) {
                return MESSAGE_IGNORE;
            }
            // 如有操作重新设置TTL
            map.get(key).setStartTime(Instant.now().getEpochSecond());
            // 检查滥用
            banUtils.setBan(userId);
            bot.sendGroupMsg(groupId, MsgUtils.builder().at(userId).text("图片搜索中，请稍后~").build(), false);
            try {
                searchResult(picUrl);
                if (apiCheck(bot, groupId, userId) || resultMatch(bot, groupId, userId)) {
                    return MESSAGE_IGNORE;
                }
            } catch (Exception e) {
                bot.sendGroupMsg(groupId, MsgUtils.builder().at(userId).text("SauceNao检索服务出现异常，请稍后重试~").build(), false);
                log.error("SauceNao插件检索异常: {}", e.getMessage());
            }
        }
        return MESSAGE_IGNORE;
    }

}

