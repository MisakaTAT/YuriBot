package com.mikuac.bot.plugins;

import com.alibaba.fastjson.JSON;
import com.mikuac.bot.bean.SearchObj;
import com.mikuac.bot.bean.saucenao.SauceNaoBean;
import com.mikuac.bot.config.ApiConst;
import com.mikuac.bot.config.MsgRegexConst;
import com.mikuac.bot.utils.HttpClientUtil;
import com.mikuac.bot.utils.RegexUtils;
import com.mikuac.bot.utils.SearchModeUtils;
import lombok.extern.slf4j.Slf4j;
import net.lz1998.pbbot.bot.Bot;
import net.lz1998.pbbot.bot.BotPlugin;
import net.lz1998.pbbot.utils.Msg;
import onebot.OnebotEvent;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.time.Instant;
import java.util.Map;

/**
 * SauceNao图片检索
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

    @Value("${yuri.plugins.sauceNao.apiKey}")
    private String apiKey;

    public void searchResult (String picUrl) {
        String param = "api_key="+apiKey+"&output_type=2&numres=1&db=999&url="+picUrl;
        String result = HttpClientUtil.httpGetWithJson(ApiConst.SAUCENAO_API + param,false);
        sauceNaoBean = JSON.parseObject(result, SauceNaoBean.class);
    }

    @Override
    public int onPrivateMessage(@NotNull Bot bot, @NotNull OnebotEvent.PrivateMessageEvent event) {
        String msg = event.getRawMessage();
        long userId = event.getUserId();
        // key加2以区分其它搜图模式
        long key = userId + 2;

        Map<Long, SearchObj> map = SearchModeUtils.getMap();

        if (msg.matches(MsgRegexConst.SAUCE_NAO)) {
            // 防止重复执行
            if (map.get(key) != null) {
                bot.sendPrivateMsg(userId,"您已经处于搜(图/本)模式啦，请直接发送图片让我来帮您检索~",false);
                return MESSAGE_IGNORE;
            }
            // 检查是否处于搜番模式
            if (map.get(key-1) != null) {
                bot.sendPrivateMsg(userId,"您已经处于搜番模式，请先退出此模式后再次尝试~",false);
                return MESSAGE_IGNORE;
            }
            SearchModeUtils.setMap(key,userId,"private");
            bot.sendPrivateMsg(userId,"您已进入搜(图/本)模式，请发送图片来帮您检索~ (滥用此功能将被封禁)",false);
            return MESSAGE_IGNORE;
        }

        if (msg.matches(MsgRegexConst.SAUCE_NAO_QUIT)) {
            if (map.get(key) != null) {
                SearchModeUtils.quitSearch(key);
                bot.sendPrivateMsg(userId,"已为您退出搜(图/本)模式~",false);
                return MESSAGE_IGNORE;
            }
            bot.sendPrivateMsg(userId,"您尚未进入搜(图/本)模式~",false);
            return MESSAGE_IGNORE;
        }

        if (map.get(key) != null && map.get(key).getEnable()) {
            String picUrl = RegexUtils.regex(RegexUtils.GET_MSG_PIC_URL,msg);
            if (picUrl != null) {
                // 如有操作重新设置TTL
                map.get(key).setStartTime(Instant.now().getEpochSecond());
                bot.sendPrivateMsg(userId,"图片搜索中，请稍后~",false);
                try {
                    searchResult(picUrl);
                    // 检查24小时内剩余搜索额度
                    if (sauceNaoBean.getHeader().getLongRemaining() <= 0) {
                        bot.sendPrivateMsg(userId,"Saucenao 24小时内搜索配额已耗尽，明天再来吧~",false);
                        return MESSAGE_IGNORE;
                    }
                    // 检查30秒内剩余搜索额度
                    if (sauceNaoBean.getHeader().getShortRemaining() <= 0) {
                        bot.sendPrivateMsg(userId,"Saucenao 30秒内搜索配额已耗尽，请稍后再试~",false);
                        return MESSAGE_IGNORE;
                    }
                    // 检查是否有返回结果
                    if (sauceNaoBean.getResults().size() <= 0) {
                        bot.sendPrivateMsg(userId,"抱歉，未检索到您发送的图片内容，请更换图片再次尝试~",false);
                        return MESSAGE_IGNORE;
                    }
                    // 发送消息
                    sauceNaoBean.getResults().forEach(k->{
                        if (k.getResultHeader().getIndexName().matches("(.*)Pixiv(.*)")) {
                            Msg sendMsg = Msg.builder()
                                    .image(k.getResultHeader().getThumbnail())
                                    .text("\n相似度："+k.getResultHeader().getSimilarity()+"%")
                                    .text("\n标题："+k.getResultData().getTitle())
                                    .text("\n画师："+k.getResultData().getMemberName())
                                    .text("\nPixiv：https://pixiv.net/i/"+k.getResultData().getPixivId())
                                    .text("\nAuthor：https://pixiv.net/u/"+k.getResultData().getMemberId())
                                    .text("\n剩余搜索配额："+sauceNaoBean.getHeader().getLongRemaining())
                                    .text("\n数据来源：SauceNao (Pixiv)");
                            bot.sendPrivateMsg(userId,sendMsg.build(),false);
                        } else if (k.getResultHeader().getIndexName().matches("(.*)E-Hentai(.*)")) {
                            Msg sendMsg = Msg.builder()
                                    .image(k.getResultHeader().getThumbnail())
                                    .text("\n相似度："+k.getResultHeader().getSimilarity()+"%")
                                    .text("\n来源："+k.getResultData().getSource())
                                    .text("\n\n日文名："+k.getResultData().getJpName())
                                    .text("\n\n英文名："+k.getResultData().getEngName())
                                    .text("\n\n剩余搜索配额："+sauceNaoBean.getHeader().getLongRemaining())
                                    .text("\n数据来源：SauceNao (E-Hentai)");
                            bot.sendPrivateMsg(userId,sendMsg.build(),false);
                        } else {
                            bot.sendPrivateMsg(userId,"未检索到您发送的内容，请更换图片后重新尝试~",false);
                        }
                    });
                } catch (Exception e) {
                    bot.sendPrivateMsg(userId,"SauceNao检索服务出现异常，请稍后重试~",false);
                    log.info("SauceNao插件检索异常",e);
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

        Map<Long, SearchObj> map = SearchModeUtils.getMap();

        if (msg.matches(MsgRegexConst.SAUCE_NAO)) {
            // 防止重复执行
            if (map.get(key) != null) {
                bot.sendGroupMsg(groupId,Msg.builder().at(userId).text("您已经处于搜(图/本)模式啦，请直接发送图片让我来帮您检索~").build(),false);
                return MESSAGE_IGNORE;
            }
            // 检查是否处于搜番模式
            if (map.get(key-1) != null) {
                bot.sendGroupMsg(groupId,Msg.builder().at(userId).text("您已经处于搜番模式，请先退出此模式后再次尝试~").build(),false);
                return MESSAGE_IGNORE;
            }
            SearchModeUtils.setMap(key,groupId,userId,"group");
            bot.sendGroupMsg(groupId,Msg.builder().at(userId).text("您已进入搜(图/本)模式，请发送图片来帮您检索~ (滥用此功能将被封禁)").build(),false);
            return MESSAGE_IGNORE;
        }

        if (msg.matches(MsgRegexConst.SAUCE_NAO_QUIT)) {
            if (map.get(key) != null) {
                SearchModeUtils.quitSearch(key);
                bot.sendGroupMsg(groupId,Msg.builder().at(userId).text("已为您退出搜(图/本)模式~").build(),false);
                return MESSAGE_IGNORE;
            }
            bot.sendGroupMsg(groupId,Msg.builder().at(userId).text("您尚未进入搜(图/本)模式~").build(),false);
            return MESSAGE_IGNORE;
        }

        if (map.get(key) != null && map.get(key).getEnable()) {
            String picUrl = RegexUtils.regex(RegexUtils.GET_MSG_PIC_URL,msg);
            if (picUrl != null) {
                // 如有操作重新设置TTL
                map.get(key).setStartTime(Instant.now().getEpochSecond());
                bot.sendGroupMsg(groupId,Msg.builder().at(userId).text("图片搜索中，请稍后~").build(),false);
                try {
                    searchResult(picUrl);
                    // 检查24小时内剩余搜索额度
                    if (sauceNaoBean.getHeader().getLongRemaining() <= 0) {
                        bot.sendGroupMsg(groupId,Msg.builder().at(userId).text("Saucenao 24小时内搜索配额已耗尽，明天再来吧~").build(),false);
                        return MESSAGE_IGNORE;
                    }
                    // 检查30秒内剩余搜索额度
                    if (sauceNaoBean.getHeader().getShortRemaining() <= 0) {
                        bot.sendGroupMsg(groupId,Msg.builder().at(userId).text("Saucenao 30秒内搜索配额已耗尽，请稍后再试~").build(),false);
                        return MESSAGE_IGNORE;
                    }
                    // 检查是否有返回结果
                    if (sauceNaoBean.getResults().size() <= 0) {
                        bot.sendGroupMsg(groupId,Msg.builder().at(userId).text("抱歉，未检索到您发送的图片内容，请更换图片再次尝试~").build(),false);
                        return MESSAGE_IGNORE;
                    }
                    // 发送消息
                    sauceNaoBean.getResults().forEach(k->{
                        if (k.getResultHeader().getIndexName().matches("(.*)Pixiv(.*)")) {
                            Msg sendMsg = Msg.builder()
                                    .at(userId)
                                    .image(k.getResultHeader().getThumbnail())
                                    .text("\n相似度："+k.getResultHeader().getSimilarity()+"%")
                                    .text("\n标题："+k.getResultData().getTitle())
                                    .text("\n画师："+k.getResultData().getMemberName())
                                    .text("\nPixiv：https://pixiv.net/i/"+k.getResultData().getPixivId())
                                    .text("\nAuthor：https://pixiv.net/u/"+k.getResultData().getMemberId())
                                    .text("\n剩余搜索配额："+sauceNaoBean.getHeader().getLongRemaining())
                                    .text("\n数据来源：SauceNao (Pixiv)");
                            bot.sendGroupMsg(groupId,sendMsg.build(),false);
                        } else if (k.getResultHeader().getIndexName().matches("(.*)E-Hentai(.*)")) {
                            Msg sendMsg = Msg.builder()
                                    .at(userId)
                                    .image(k.getResultHeader().getThumbnail())
                                    .text("\n相似度："+k.getResultHeader().getSimilarity()+"%")
                                    .text("\n来源："+k.getResultData().getSource())
                                    .text("\n\n日文名："+k.getResultData().getJpName())
                                    .text("\n\n英文名："+k.getResultData().getEngName())
                                    .text("\n\n剩余搜索配额："+sauceNaoBean.getHeader().getLongRemaining())
                                    .text("\n数据来源：SauceNao (E-Hentai)");
                            bot.sendGroupMsg(groupId,sendMsg.build(),false);
                        } else {
                            bot.sendGroupMsg(groupId,Msg.builder().at(userId).text("未检索到您发送的内容，请更换图片后重新尝试~").build(),false);
                        }
                    });
                } catch (Exception e) {
                    bot.sendGroupMsg(groupId,Msg.builder().at(userId).text("SauceNao检索服务出现异常，请稍后重试~").build(),false);
                    log.info("SauceNao插件检索异常",e);
                }
            }
        }

        return MESSAGE_IGNORE;
    }

}
