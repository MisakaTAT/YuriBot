package com.mikuac.bot.common.utils;

import com.mikuac.bot.bean.SearchBean;
import com.mikuac.shiro.utils.Msg;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Zero
 * @date 2020/12/1 17:11
 */
@Component
public class SearchModeUtils {

    private static final Map<Long, SearchBean> SEARCH_MODE = new ConcurrentHashMap<>();
    private SendMsgUtils sendMsgUtils;

    public static void setMap(long key, long groupId, long userId, String msgType) {
        SearchBean searchBean = new SearchBean();
        searchBean.setKey(key);
        searchBean.setGroupId(groupId);
        searchBean.setUserId(userId);
        searchBean.setEnable(true);
        searchBean.setStartTime(Instant.now().getEpochSecond());
        searchBean.setMsgType(msgType);
        SEARCH_MODE.put(key, searchBean);
    }

    public static void setMap(long key, long userId, String msgType) {
        SearchBean searchBean = new SearchBean();
        searchBean.setKey(key);
        searchBean.setUserId(userId);
        searchBean.setEnable(true);
        searchBean.setStartTime(Instant.now().getEpochSecond());
        searchBean.setMsgType(msgType);
        SEARCH_MODE.put(key, searchBean);
    }

    public static Map<Long, SearchBean> getMap() {
        return SEARCH_MODE;
    }

    public static void quitSearch(long key) {
        SEARCH_MODE.remove(key);
    }

    @Autowired
    public void setSendMsgUtils(SendMsgUtils sendMsgUtils) {
        this.sendMsgUtils = sendMsgUtils;
    }

    @Scheduled(cron = "0/5 * * * * ?", zone = "Asia/Shanghai")
    public void timeOutRemove() throws InterruptedException {
        // 迭代出Map中所有的Key
        for (long key : SEARCH_MODE.keySet()) {
            String msgType = SEARCH_MODE.get(key).getMsgType();
            if ("group".equals(msgType) && SEARCH_MODE.get(key) != null) {
                long groupId = SEARCH_MODE.get(key).getGroupId();
                long userId = SEARCH_MODE.get(key).getUserId();
                int ttl = 30;
                long startTime = SEARCH_MODE.get(key).getStartTime();
                long nowTime = Instant.now().getEpochSecond();
                // 超时删除
                if (nowTime - startTime >= ttl) {
                    SEARCH_MODE.remove(key);
                    sendMsgUtils.sendGroupMsgForMsg(groupId, Msg.builder().at(userId).text("您已超过" + ttl + "秒未发送图片，已为您退出搜(番/图/本)模式~"));
                }
            } else if ("private".equals(msgType) && SEARCH_MODE.get(key) != null) {
                long userId = SEARCH_MODE.get(key).getUserId();
                int ttl = 30;
                long startTime = SEARCH_MODE.get(key).getStartTime();
                long nowTime = Instant.now().getEpochSecond();
                // 超时删除
                if (nowTime - startTime >= ttl) {
                    SEARCH_MODE.remove(key);
                    sendMsgUtils.sendPrivateMsgForMsg(userId, Msg.builder().text("您已超过" + ttl + "秒未发送图片，已为您退出搜(番/图/本)模式~"));
                }
            }
        }
    }

}
