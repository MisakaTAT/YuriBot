package com.mikuac.bot.plugins.aop;

import com.alibaba.fastjson.JSON;
import com.mikuac.bot.bean.setu.Data;
import com.mikuac.bot.bean.setu.SetuBean;
import com.mikuac.bot.common.task.AsyncTask;
import com.mikuac.bot.common.utils.HttpClientUtils;
import com.mikuac.bot.config.ApiConst;
import com.mikuac.bot.config.Global;
import com.mikuac.bot.config.RegexConst;
import com.mikuac.bot.repository.PluginSwitchRepository;
import lombok.extern.slf4j.Slf4j;
import net.lz1998.pbbot.bot.Bot;
import net.lz1998.pbbot.bot.BotContainer;
import net.lz1998.pbbot.bot.BotPlugin;
import net.lz1998.pbbot.utils.Msg;
import onebot.OnebotApi;
import onebot.OnebotEvent;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Zero
 * @date 2020/11/9 14:19
 */
@Slf4j
@Component
public class SeTu extends BotPlugin {

    private SetuBean seTuBean;

    @Autowired
    public void setSeTuBean(SetuBean seTuBean) {
        this.seTuBean = seTuBean;
    }

    private BotContainer botContainer;

    @Autowired
    public void setBotContainer(BotContainer botContainer) {
        this.botContainer = botContainer;
    }

    private PluginSwitchRepository pluginSwitchRepository;

    @Autowired
    public void setPluginSwitchRepository(PluginSwitchRepository pluginSwitchRepository) {
        this.pluginSwitchRepository = pluginSwitchRepository;
    }

    private AsyncTask asyncTask;

    @Autowired
    public void setAsyncTask(AsyncTask asyncTask) {
        this.asyncTask = asyncTask;
    }

    private String picUrl;

    Map<Long, Long> lastGetTimeMap = new ConcurrentHashMap<>();

    Map<Long, Integer> getCountMap = new ConcurrentHashMap<>();

    @Scheduled(cron = "0 0 00 * * ?")
    public void clearMaxGetCount() {
        getCountMap.clear();
        log.info("色图每日上限已清除");
    }

    public void getData(String r18) {
        String result = HttpClientUtils.httpGetWithJson(ApiConst.SETU_API + Global.setu_apiKey + r18, false);
        seTuBean = JSON.parseObject(result, SetuBean.class);
    }


    @Override
    public int onPrivateMessage(@NotNull Bot bot, @NotNull OnebotEvent.PrivateMessageEvent event) {
        String msg = event.getRawMessage();
        // 私聊消息处理
        if (msg.matches(RegexConst.SETU)) {
            long userId = event.getUserId();
            Boolean isPrivateDisable = !pluginSwitchRepository.isPrivateDisable("SeTu");
            Boolean isGlobalDisable = !pluginSwitchRepository.isGlobalDisable("SeTu");
            if (isPrivateDisable && isGlobalDisable) {
                long getNowTime = Instant.now().getEpochSecond();
                long lastGetTime = lastGetTimeMap.getOrDefault(userId, 0L);
                long rCd = Math.abs((getNowTime - lastGetTime) - Global.setu_cdTime);
                // 逻辑处理
                if (getNowTime >= lastGetTime + Global.setu_cdTime) {
                    bot.sendPrivateMsg(userId, "少女祈祷中~", false);
                    try {
                        getData(msg.matches("(.*?)[rR]18(.*)") ? "&r18=1" : "&r18=0");
                        lastGetTimeMap.put(userId, Instant.now().getEpochSecond());
                        Msg stInfoMsg = Msg.builder();
                        for (Data data : seTuBean.getData()) {
                            stInfoMsg.text("标题：" + data.getTitle());
                            stInfoMsg.text("\nPID：" + data.getPid());
                            stInfoMsg.text("\n作者：" + data.getAuthor());
                            stInfoMsg.text("\n链接：" + "https://www.pixiv.net/artworks/" + data.getPid());
                            stInfoMsg.text("\n反代链接：" + data.getUrl());
                            picUrl = data.getUrl();
                        }
                        bot.sendPrivateMsg(userId, stInfoMsg.build(), false);
                        // 构建闪照图片消息
                        Msg flashPic = Msg.builder().flash(picUrl);
                        OnebotApi.SendPrivateMsgResp picMsg = bot.sendPrivateMsg(userId, flashPic.build(), false);
                        if (picMsg != null) {
                            asyncTask.deleteMsg(picMsg.getMessageId(), botContainer.getBots().get(Global.bot_selfId));
                        }
                    } catch (Exception e) {
                        lastGetTimeMap.put(userId, 0L);
                        bot.sendPrivateMsg(userId, "图片获取失败，请稍后重试~", false);
                        log.info("色图私聊发送异常", e);
                    }
                } else {
                    bot.sendPrivateMsg(userId, "请求过于频繁~ 剩余CD时间为" + rCd + "秒", false);
                }
            } else {
                bot.sendPrivateMsg(userId, "此模块被停用", false);
            }
        }
        return MESSAGE_IGNORE;
    }

    @Override
    public int onGroupMessage(@NotNull Bot bot, @NotNull OnebotEvent.GroupMessageEvent event) {
        String msg = event.getRawMessage();
        // 私聊消息处理
        if (msg.matches(RegexConst.SETU)) {
            long userId = event.getUserId();
            long groupId = event.getGroupId();
            Boolean isGroupDisable = !pluginSwitchRepository.isGroupDisable("SeTu");
            Boolean isGlobalDisable = !pluginSwitchRepository.isGlobalDisable("SeTu");
            if (isGroupDisable && isGlobalDisable) {
                long getNowTime = Instant.now().getEpochSecond();
                long lastGetTime = lastGetTimeMap.getOrDefault(userId + groupId, 0L);
                long rCd = Math.abs((getNowTime - lastGetTime) - Global.setu_cdTime);
                // 逻辑处理
                int count = getCountMap.get(userId) == null ? 0 : getCountMap.get(userId);
                if (getNowTime >= lastGetTime + Global.setu_cdTime && count < Global.setu_maxGet) {
                    bot.sendGroupMsg(groupId, Msg.builder().at(userId).text("少女祈祷中~").build(), false);
                    try {
                        getData(msg.matches("(.*?)[rR]18(.*)") ? "&r18=1" : "&r18=0");
                        getCountMap.put(userId, count + 1);
                        lastGetTimeMap.put(userId + groupId, Instant.now().getEpochSecond());
                        Msg stInfoMsg = Msg.builder();
                        for (Data data : seTuBean.getData()) {
                            stInfoMsg.at(userId);
                            stInfoMsg.text("\n标题：" + data.getTitle());
                            stInfoMsg.text("\nPID：" + data.getPid());
                            stInfoMsg.text("\n作者：" + data.getAuthor());
                            stInfoMsg.text("\n链接：" + "https://www.pixiv.net/artworks/" + data.getPid());
                            stInfoMsg.text("\n反代链接：" + data.getUrl());
                            stInfoMsg.text("\n今日剩余次数：" + (Global.setu_maxGet - getCountMap.get(userId)));
                            picUrl = data.getUrl();
                        }
                        bot.sendGroupMsg(groupId, stInfoMsg.build(), false);
                        // 构建闪照图片消息
                        Msg flashPic = Msg.builder().flash(picUrl);
                        OnebotApi.SendGroupMsgResp picMsg = bot.sendGroupMsg(groupId, flashPic.build(), false);
                        if (picMsg != null) {
                            asyncTask.deleteMsg(picMsg.getMessageId(), botContainer.getBots().get(Global.bot_selfId));
                        }
                    } catch (Exception e) {
                        getCountMap.put(userId, getCountMap.get(userId) - 1);
                        lastGetTimeMap.put(userId + groupId, 0L);
                        bot.sendGroupMsg(groupId, Msg.builder().at(userId).text("图片获取失败，请稍后重试~").build(), false);
                        log.info("色图私聊发送异常", e);
                    }
                } else if (count == Global.setu_maxGet) {
                    bot.sendGroupMsg(groupId, Msg.builder().at(userId).text("今日获取次数已达上限，每晚24点重置~").build(), false);
                } else {
                    bot.sendGroupMsg(groupId, Msg.builder().at(userId).text("请求过于频繁~ 剩余CD时间为" + rCd + "秒").build(), false);
                }
            } else {
                bot.sendGroupMsg(groupId, Msg.builder().at(userId).text("此模块被停用").build(), false);
            }
        }
        return MESSAGE_IGNORE;
    }

}