package com.mikuac.bot.utils;

import com.mikuac.bot.bean.SearchBean;
import net.lz1998.pbbot.utils.Msg;
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

    private SendMsgUtils sendMsgUtils;

    @Autowired
    public void setSendMsgUtils(SendMsgUtils sendMsgUtils) {
        this.sendMsgUtils = sendMsgUtils;
    }

    private static Map<Long, SearchBean> searchMode = new ConcurrentHashMap<>();

    @Scheduled(cron = "0/5 * * * * ?",zone = "Asia/Shanghai")
    public void timeOutRemove() throws InterruptedException {
        // 迭代出Map中所有的Key
        for (long key : searchMode.keySet()) {
            String msgType = searchMode.get(key).getMsgType();
            if ("group".equals(msgType) && searchMode.get(key) != null) {
                long groupId = searchMode.get(key).getGroupId();
                long userId = searchMode.get(key).getUserId();
                int ttl = 30;
                long startTime = searchMode.get(key).getStartTime();
                long nowTime = Instant.now().getEpochSecond();
                // 超时删除
                if (nowTime - startTime >= ttl) {
                    searchMode.remove(key);
                    sendMsgUtils.sendGroupMsg(groupId, Msg.builder().at(userId).text("您已超过"+ttl+"秒未发送图片，已为您退出搜(番/图/本)模式~"));
                }
            }else if("private".equals(msgType) && searchMode.get(key) != null){
                long userId = searchMode.get(key).getUserId();
                int ttl = 30;
                long startTime = searchMode.get(key).getStartTime();
                long nowTime = Instant.now().getEpochSecond();
                // 超时删除
                if (nowTime - startTime >= ttl) {
                    searchMode.remove(key);
                    sendMsgUtils.sendPrivateMsg(userId, Msg.builder().text("您已超过"+ttl+"秒未发送图片，已为您退出搜(番/图/本)模式~"));
                }
            }
        }
    }

    public static void setMap (long key,long groupId,long userId,String msgType) {
        SearchBean searchBean = new SearchBean();
        searchBean.setKey(key);
        searchBean.setGroupId(groupId);
        searchBean.setUserId(userId);
        searchBean.setEnable(true);
        searchBean.setStartTime(Instant.now().getEpochSecond());
        searchBean.setMsgType(msgType);
        searchMode.put(key, searchBean);
    }

    public static void setMap (long key,long userId,String msgType) {
        SearchBean searchBean = new SearchBean();
        searchBean.setKey(key);
        searchBean.setUserId(userId);
        searchBean.setEnable(true);
        searchBean.setStartTime(Instant.now().getEpochSecond());
        searchBean.setMsgType(msgType);
        searchMode.put(key, searchBean);
    }

    public static Map<Long, SearchBean> getMap () {
        return searchMode;
    }

    public static void quitSearch (long key) {
        searchMode.remove(key);
    }

}
