package com.mikuac.bot.plugins.aop;

import com.alibaba.fastjson.JSON;
import com.mikuac.bot.bean.SearchBean;
import com.mikuac.bot.bean.saucenao.Results;
import com.mikuac.bot.bean.saucenao.SauceNaoBean;
import com.mikuac.bot.common.utils.*;
import com.mikuac.bot.config.ApiConst;
import com.mikuac.bot.config.Global;
import com.mikuac.bot.config.RegexConst;
import lombok.extern.slf4j.Slf4j;
import net.lz1998.pbbot.bot.Bot;
import net.lz1998.pbbot.bot.BotPlugin;
import net.lz1998.pbbot.utils.Msg;
import onebot.OnebotEvent;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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

    private SauceNaoBean sauceNaoBean;

    @Autowired
    public void setSauceNaoBean(SauceNaoBean sauceNaoBean) {
        this.sauceNaoBean = sauceNaoBean;
    }

    private BanUtils banUtils;

    @Autowired
    public void setBanUtils(BanUtils banUtils) {
        this.banUtils = banUtils;
    }

    int limitTime = Global.config.getBanUtils().getLimitTime();
    int limitCount = Global.config.getBanUtils().getLimitCount();
    String apiKey = Global.config.getSauceNao().getApiKey();

    public void searchResult(String picUrl) {
        String param = "api_key=" + apiKey + "&output_type=2&numres=3&db=999&url=" + picUrl;
        String result = HttpClientUtils.httpGetWithJson(ApiConst.SAUCENAO_API + param, false);
        sauceNaoBean = JSON.parseObject(result, SauceNaoBean.class);
    }

    public Boolean apiCheckForPrivate(@NotNull Bot bot, long userId) {
        // 检查24小时内剩余搜索额度
        if (sauceNaoBean.getHeader().getLongRemaining() <= 0) {
            bot.sendPrivateMsg(userId, "Saucenao 24小时内搜索配额已耗尽，明天再来吧~", false);
            return true;
        }
        // 检查30秒内剩余搜索额度
        if (sauceNaoBean.getHeader().getShortRemaining() <= 0) {
            bot.sendPrivateMsg(userId, "Saucenao 30秒内搜索配额已耗尽，请稍后再试~", false);
            return true;
        }
        // 检查是否有返回结果
        if (sauceNaoBean.getResults().size() <= 0) {
            bot.sendPrivateMsg(userId, "抱歉，未检索到您发送的图片内容，请更换图片再次尝试~", false);
            return true;
        }
        return false;
    }

    public Boolean apiCheckForGroup(@NotNull Bot bot, long groupId, long userId) {
        // 检查24小时内剩余搜索额度
        if (sauceNaoBean.getHeader().getLongRemaining() <= 0) {
            bot.sendGroupMsg(groupId, Msg.builder().at(userId).text("Saucenao 24小时内搜索配额已耗尽，明天再来吧~").build(), false);
            return true;
        }
        // 检查30秒内剩余搜索额度
        if (sauceNaoBean.getHeader().getShortRemaining() <= 0) {
            bot.sendGroupMsg(groupId, Msg.builder().at(userId).text("Saucenao 30秒内搜索配额已耗尽，请稍后再试~").build(), false);
            return true;
        }
        // 检查是否有返回结果
        if (sauceNaoBean.getResults().size() <= 0) {
            bot.sendGroupMsg(groupId, Msg.builder().at(userId).text("抱歉，未检索到您发送的图片内容，请更换图片再次尝试~").build(), false);
            return true;
        }
        return false;
    }

    public Boolean privateMsgBuilder(@NotNull Bot bot, long userId, Results r, int db) {
        if (0 == db) {
            Msg sendMsg = Msg.builder()
                    .image(r.getResultHeader().getThumbnail())
                    .text("\n相似度：" + r.getResultHeader().getSimilarity() + "%")
                    .text("\n标题：" + r.getResultData().getTitle())
                    .text("\n画师：" + r.getResultData().getMemberName())
                    .text("\nPixiv：https://pixiv.net/i/" + r.getResultData().getPixivId())
                    .text("\nAuthor：https://pixiv.net/u/" + r.getResultData().getMemberId())
                    .text("\nProxy：" + PixivProxyUtils.imgProxy(r.getResultData().getPixivId()))
                    .text("\n剩余搜索配额：" + sauceNaoBean.getHeader().getLongRemaining())
                    .text("\n数据来源：SauceNao (Pixiv)");
            bot.sendPrivateMsg(userId, sendMsg.build(), false);
            return true;
        }
        if (1 == db) {
            Msg sendMsg = Msg.builder()
                    .image(r.getResultHeader().getThumbnail())
                    .text("\n相似度：" + r.getResultHeader().getSimilarity() + "%")
                    .text("\n来源：" + r.getResultData().getSource())
                    .text("\n\n日文名：" + r.getResultData().getJpName())
                    .text("\n\n英文名：" + r.getResultData().getEngName())
                    .text("\n\n剩余搜索配额：" + sauceNaoBean.getHeader().getLongRemaining())
                    .text("\n数据来源：SauceNao (E-Hentai)");
            bot.sendPrivateMsg(userId, sendMsg.build(), false);
            return true;
        }
        return false;
    }

    public Boolean groupMsgBuilder(@NotNull Bot bot, long groupId, long userId, Results r, int db) {
        if (db == 0) {
            Msg sendMsg = Msg.builder()
                    .at(userId)
                    .image(r.getResultHeader().getThumbnail())
                    .text("\n相似度：" + r.getResultHeader().getSimilarity() + "%")
                    .text("\n标题：" + r.getResultData().getTitle())
                    .text("\n画师：" + r.getResultData().getMemberName())
                    .text("\nPixiv：https://pixiv.net/i/" + r.getResultData().getPixivId())
                    .text("\nAuthor：https://pixiv.net/u/" + r.getResultData().getMemberId())
                    .text("\nProxy：" + PixivProxyUtils.imgProxy(r.getResultData().getPixivId()))
                    .text("\n剩余搜索配额：" + sauceNaoBean.getHeader().getLongRemaining())
                    .text("\n数据来源：SauceNao (Pixiv)");
            bot.sendGroupMsg(groupId, sendMsg.build(), false);
            return true;
        }
        if (db == 1) {
            Msg sendMsg = Msg.builder()
                    .at(userId)
                    .image(r.getResultHeader().getThumbnail())
                    .text("\n相似度：" + r.getResultHeader().getSimilarity() + "%")
                    .text("\n来源：" + r.getResultData().getSource())
                    .text("\n\n日文名：" + r.getResultData().getJpName())
                    .text("\n\n英文名：" + r.getResultData().getEngName())
                    .text("\n\n剩余搜索配额：" + sauceNaoBean.getHeader().getLongRemaining())
                    .text("\n数据来源：SauceNao (E-Hentai)");
            bot.sendGroupMsg(groupId, sendMsg.build(), false);
            return true;
        }
        return false;
    }

    @Override
    public int onPrivateMessage(@NotNull Bot bot, @NotNull OnebotEvent.PrivateMessageEvent event) {
        String msg = event.getRawMessage();
        long userId = event.getUserId();
        // key加2以区分其它搜图模式
        long key = userId + 2;

        Map<Long, SearchBean> map = SearchModeUtils.getMap();

        if (msg.matches(RegexConst.SAUCE_NAO)) {
            if (banUtils.isBanned(userId)) {
                bot.sendPrivateMsg(userId, "您因触发滥用规则已被封禁~", false);
                return MESSAGE_IGNORE;
            }
            // 防止重复执行
            if (map.get(key) != null) {
                bot.sendPrivateMsg(userId, "您已经处于搜(图/本)模式啦，请直接发送图片让我来帮您检索~", false);
                return MESSAGE_IGNORE;
            }
            // 检查是否处于搜番模式
            if (map.get(key - 1) != null) {
                bot.sendPrivateMsg(userId, "您已经处于搜番模式，请先退出此模式后再次尝试~", false);
                return MESSAGE_IGNORE;
            }
            SearchModeUtils.setMap(key, userId, "private");
            bot.sendPrivateMsg(userId, "您已进入搜(图/本)模式，请发送图片来帮您检索~ \n(注意：" + limitTime + "秒内发送超过" + limitCount + "张图片将会触发滥用规则被封禁)", false);
            return MESSAGE_IGNORE;
        }

        if (msg.matches(RegexConst.SAUCE_NAO_QUIT)) {
            if (map.get(key) != null) {
                SearchModeUtils.quitSearch(key);
                bot.sendPrivateMsg(userId, "已为您退出搜(图/本)模式~", false);
                return MESSAGE_IGNORE;
            }
            bot.sendPrivateMsg(userId, "您尚未进入搜(图/本)模式~", false);
            return MESSAGE_IGNORE;
        }

        if (map.get(key) != null && map.get(key).getEnable()) {
            String picUrl = RegexUtils.regex(RegexConst.GET_MSG_PIC_URL, msg);
            if (picUrl != null) {
                // 如有操作重新设置TTL
                map.get(key).setStartTime(Instant.now().getEpochSecond());
                // 检查滥用
                banUtils.setBan(userId);
                bot.sendPrivateMsg(userId, "图片搜索中，请稍后~", false);
                try {
                    searchResult(picUrl);
                    if (apiCheckForPrivate(bot, userId)) {
                        return MESSAGE_IGNORE;
                    }
                    // 构建消息 匹配到P站图片db返回0，匹配到E站返回1
                    for (Results r : sauceNaoBean.getResults()) {
                        if (r.getResultHeader().getIndexName().matches("(.*)Pixiv(.*)")) {
                            if (privateMsgBuilder(bot, userId, r, 0)) {
                                return MESSAGE_IGNORE;
                            }
                        }
                        if (r.getResultHeader().getIndexName().matches("(.*)E-Hentai(.*)")) {
                            if (privateMsgBuilder(bot, userId, r, 1)) {
                                return MESSAGE_IGNORE;
                            }
                        }
                    }
                    bot.sendPrivateMsg(userId, "未检索到您发送的内容，请更换图片后重新尝试~", false);
                } catch (Exception e) {
                    bot.sendPrivateMsg(userId, "SauceNao检索服务出现异常，请稍后重试~", false);
                    log.info("SauceNao插件检索异常", e);
                }
            }
        }

        return MESSAGE_IGNORE;
    }

    @Override
    public int onGroupMessage(@NotNull Bot bot, @NotNull OnebotEvent.GroupMessageEvent event) {
        String msg = event.getRawMessage();
        long userId = event.getUserId();
        long groupId = event.getGroupId();
        // key加2以区分其它搜图模式
        long key = userId + groupId + 2;

        Map<Long, SearchBean> map = SearchModeUtils.getMap();

        if (msg.matches(RegexConst.SAUCE_NAO)) {
            if (banUtils.isBanned(userId)) {
                bot.sendGroupMsg(groupId, Msg.builder().at(userId).text("您因触发滥用规则已被封禁~").build(), false);
                return MESSAGE_IGNORE;
            }
            // 防止重复执行
            if (map.get(key) != null) {
                bot.sendGroupMsg(groupId, Msg.builder().at(userId).text("您已经处于搜(图/本)模式啦，请直接发送图片让我来帮您检索~").build(), false);
                return MESSAGE_IGNORE;
            }
            // 检查是否处于搜番模式
            if (map.get(key - 1) != null) {
                bot.sendGroupMsg(groupId, Msg.builder().at(userId).text("您已经处于搜番模式，请先退出此模式后再次尝试~").build(), false);
                return MESSAGE_IGNORE;
            }
            SearchModeUtils.setMap(key, groupId, userId, "group");
            bot.sendGroupMsg(groupId, Msg.builder().at(userId).text("您已进入搜(图/本)模式，请发送图片来帮您检索~ \n(注意：" + limitTime + "秒内发送超过" + limitCount + "张图片将会触发滥用规则被封禁)").build(), false);
            return MESSAGE_IGNORE;
        }

        if (msg.matches(RegexConst.SAUCE_NAO_QUIT)) {
            if (map.get(key) != null) {
                SearchModeUtils.quitSearch(key);
                bot.sendGroupMsg(groupId, Msg.builder().at(userId).text("已为您退出搜(图/本)模式~").build(), false);
                return MESSAGE_IGNORE;
            }
            bot.sendGroupMsg(groupId, Msg.builder().at(userId).text("您尚未进入搜(图/本)模式~").build(), false);
            return MESSAGE_IGNORE;
        }

        if (map.get(key) != null && map.get(key).getEnable()) {
            String picUrl = RegexUtils.regex(RegexConst.GET_MSG_PIC_URL, msg);
            if (picUrl != null) {
                // 如有操作重新设置TTL
                map.get(key).setStartTime(Instant.now().getEpochSecond());
                // 检查滥用
                banUtils.setBan(userId);
                bot.sendGroupMsg(groupId, Msg.builder().at(userId).text("图片搜索中，请稍后~").build(), false);
                try {
                    searchResult(picUrl);
                    if (apiCheckForGroup(bot, groupId, userId)) {
                        return MESSAGE_IGNORE;
                    }
                    // 构建消息 匹配到P站图片db返回0，匹配到E站返回1
                    for (Results r : sauceNaoBean.getResults()) {
                        if (r.getResultHeader().getIndexName().matches("(.*)Pixiv(.*)")) {
                            if (groupMsgBuilder(bot, groupId, userId, r, 0)) {
                                return MESSAGE_IGNORE;
                            }
                        }
                        if (r.getResultHeader().getIndexName().matches("(.*)E-Hentai(.*)")) {
                            if (groupMsgBuilder(bot, groupId, userId, r, 1)) {
                                return MESSAGE_IGNORE;
                            }
                        }
                    }
                    bot.sendGroupMsg(groupId, Msg.builder().at(userId).text("未检索到您发送的内容，请更换图片后重新尝试~").build(), false);
                } catch (Exception e) {
                    bot.sendGroupMsg(groupId, Msg.builder().at(userId).text("SauceNao检索服务出现异常，请稍后重试~").build(), false);
                    log.info("SauceNao插件检索异常", e);
                }
            }
        }

        return MESSAGE_IGNORE;
    }

}
