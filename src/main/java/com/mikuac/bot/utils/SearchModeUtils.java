package com.mikuac.bot.utils;

import com.mikuac.bot.bean.SearchObj;
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

    static Map<Long, SearchObj> searchMode = new ConcurrentHashMap<>();

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
                    sendMsgUtils.sendGroupMsg(groupId, Msg.builder().at(userId).text("您已超过30秒未发送图片，已为您退出搜番模式~"));
                }
            }else if("private".equals(msgType) && searchMode.get(key) != null){
                long userId = searchMode.get(key).getUserId();
                int ttl = 30;
                long startTime = searchMode.get(key).getStartTime();
                long nowTime = Instant.now().getEpochSecond();
                // 超时删除
                if (nowTime - startTime >= ttl) {
                    searchMode.remove(key);
                    sendMsgUtils.sendPrivateMsg(userId, Msg.builder().text("您已超过30秒未发送图片，已为您退出搜番模式~"));
                }
            }
        }
    }

    public static void setMap (long key,long groupId,long userId,String msgType) {
        SearchObj searchObj = new SearchObj();
        searchObj.setKey(key);
        searchObj.setGroupId(groupId);
        searchObj.setUserId(userId);
        searchObj.setEnable(true);
        searchObj.setStartTime(Instant.now().getEpochSecond());
        searchObj.setMsgType(msgType);
        searchMode.put(key,searchObj);
    }

    public static void setMap (long key,long userId,String msgType) {
        SearchObj searchObj = new SearchObj();
        searchObj.setKey(key);
        searchObj.setUserId(userId);
        searchObj.setEnable(true);
        searchObj.setStartTime(Instant.now().getEpochSecond());
        searchObj.setMsgType(msgType);
        searchMode.put(key,searchObj);
    }

    public static Map<Long, SearchObj> getMap () {
        return searchMode;
    }

    public static void quitSearch (long key) {
        searchMode.remove(key);
    }

}
