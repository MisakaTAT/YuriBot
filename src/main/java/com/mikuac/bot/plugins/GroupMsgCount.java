package com.mikuac.bot.plugins;

import com.alibaba.fastjson.JSONObject;
import com.mikuac.bot.bean.MsgCountCacheBean;
import com.mikuac.bot.common.utils.SendMsgUtils;
import com.mikuac.bot.entity.MsgCountEntity;
import com.mikuac.bot.repository.MsgCountRepository;
import lombok.extern.slf4j.Slf4j;
import net.lz1998.pbbot.bot.Bot;
import net.lz1998.pbbot.bot.BotPlugin;
import net.lz1998.pbbot.utils.Msg;
import onebot.OnebotEvent;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * 发言统计
 *
 * @author Zero
 * @date 2020/11/16 19:03
 */
@Slf4j
@Component
public class GroupMsgCount extends BotPlugin {

    private SendMsgUtils sendMsgUtils;

    private MsgCountCacheBean msgCountCacheBean;

    @Autowired
    public void setSendMsgUtils(SendMsgUtils sendMsgUtils) {
        this.sendMsgUtils = sendMsgUtils;
    }

    private MsgCountRepository msgCountRepository;

    @Autowired
    public void setMsgCountRepository(MsgCountRepository msgCountRepository) {
        this.msgCountRepository = msgCountRepository;
    }

    @Scheduled(cron = "0 0 00 * * ?", zone = "Asia/Shanghai")
    public void sendMsg() throws InterruptedException {
        cacheToDataBase();
        List<Long> groupIdList = sendMsgUtils.getGroupList();
        if (groupIdList != null && !groupIdList.isEmpty()) {
            for (long groupId : groupIdList) {
                Optional<MsgCountEntity> msgCount = msgCountRepository.findTodayMaxCount(groupId);
                if (msgCount.isPresent() && msgCount.get().getTodayMsgCount() > 0) {
                    Msg msg = Msg.builder()
                            .at(msgCount.get().getUserId())
                            .text("\n恭喜获得今日群龙王称号~")
                            .text("\n今日发言次数：" + msgCount.get().getTodayMsgCount())
                            .text("\n历史统计次数：" + msgCount.get().getAllMsgCount());
                    sendMsgUtils.sendGroupMsg(groupId, msg);
                } else {
                    log.info("群组[{}]发言次数获取异常或者无人发言", groupId);
                    Msg msg = Msg.builder()
                            .text("今日群内无人发言，暂无龙王诞生~");
                    sendMsgUtils.sendGroupMsg(groupId, msg);
                }
            }
            //重置每日统计次数为0
            msgCountRepository.setDefaultTodayMsgCount();
        }
    }

    public void writeJsonCache(long groupId, long userId) throws IOException {
        try {
            // 读取现有Cache File
            msgCountCacheBean = JSONObject.parseObject(readJsonCache(), MsgCountCacheBean.class);
            List<MsgCountCacheBean.CacheData> dataList = msgCountCacheBean.getCacheData();
            boolean hasCache = false;
            for (MsgCountCacheBean.CacheData data : dataList) {
                long getGroupId = Long.parseLong(data.getGroupId());
                long getUserId = Long.parseLong(data.getUserId());
                // 如果缓存中已存在数据
                if (groupId == getGroupId && userId == getUserId) {
                    int getCount = data.getCount();
                    data.setCount(++getCount);
                    hasCache = true;
                    break;
                }
            }
            // 如果不存在缓存数据
            if (!hasCache) {
                MsgCountCacheBean.CacheData cacheData = new MsgCountCacheBean.CacheData();
                cacheData.setGroupId(String.valueOf(groupId));
                cacheData.setUserId(String.valueOf(userId));
                cacheData.setCount(1);
                // 追加数据
                dataList.add(cacheData);
            }
            // Obj转为json
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("data", dataList);
            // 写入Cache File
            OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream("GroupMsgCountCache.json"), StandardCharsets.UTF_8);
            osw.write(jsonObject.toString());
            osw.flush();
            osw.close();
        } catch (Exception e) {
            File file = new File("GroupMsgCountCache.json");
            if (file.isFile() && file.exists()) {
                log.warn("发言统计缓存文件解析异常，即将删除缓存文件: {}", e.getMessage());
                deleteCache();
            }
            log.warn("发言统计缓存文件不存在或缓存文件为空，即将创建缓存文件");
            List<MsgCountCacheBean.CacheData> dataList = new ArrayList<>();
            // 创建CacheData
            MsgCountCacheBean.CacheData cacheData = new MsgCountCacheBean.CacheData();
            cacheData.setGroupId(String.valueOf(groupId));
            cacheData.setUserId(String.valueOf(userId));
            cacheData.setCount(1);
            dataList.add(cacheData);
            // Obj转为json
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("data", dataList);
            // 写入Cache File
            OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream("GroupMsgCountCache.json"), StandardCharsets.UTF_8);
            osw.write(jsonObject.toString());
            osw.flush();
            osw.close();
        }
    }

    public String readJsonCache() throws IOException {
        InputStreamReader isr = new InputStreamReader(new FileInputStream("GroupMsgCountCache.json"), StandardCharsets.UTF_8);
        int ch = 0;
        StringBuilder sb = new StringBuilder();
        while ((ch = isr.read()) != -1) {
            sb.append((char) ch);
        }
        isr.close();
        return sb.toString();
    }

    public void deleteCache() {
        File file = new File("GroupMsgCountCache.json");
        if (file.isFile() && file.exists()) {
            boolean flag = file.delete();
            log.info("发言统计缓存文件删除成功，Flag = {}", flag);
        } else {
            boolean flag = file.delete();
            int tryCount = 0;
            while (!flag && tryCount++ < 5) {
                // 回收资源
                System.gc();
                flag = file.delete();
                log.error("发言统计缓存文件删除失败，当前重试次数[{}]", tryCount);
                if (flag) {
                    log.info("发言统计缓存文件删除成功，Flag = {}", true);
                    return;
                }
            }
            log.error("发言统计缓存文件删除失败，文件可能不存在或被占用");
        }
    }

    public void cacheToDataBase() {
        // 读取Cache File
        try {
            msgCountCacheBean = JSONObject.parseObject(readJsonCache(), MsgCountCacheBean.class);
            // 从Cache统计发言次数
            for (MsgCountCacheBean.CacheData cacheData : msgCountCacheBean.getCacheData()) {
                long groupId = Long.parseLong(cacheData.getGroupId());
                long userId = Long.parseLong(cacheData.getUserId());
                // 写入数据库
                Optional<MsgCountEntity> msgCount = msgCountRepository.findByGroupAndUserId(groupId, userId);
                if (msgCount.isPresent()) {
                    int todayMsgCount = cacheData.getCount();
                    int allMsgCount = msgCount.get().getAllMsgCount() + todayMsgCount;
                    msgCountRepository.update(groupId, userId, todayMsgCount, allMsgCount);
                }
            }
            // 删除缓存
            deleteCache();
        } catch (Exception e) {
            log.warn("发言统计缓存数据写入数据库失败，可能是缓存不存在或缓存文件为空: {}", e.getMessage());
        }
    }

    @Override
    public int onGroupMessage(@NotNull Bot bot, @NotNull OnebotEvent.GroupMessageEvent event) {
        long userId = event.getUserId();
        long groupId = event.getGroupId();
        try {
            writeJsonCache(groupId, userId);
        } catch (Exception e) {
            log.error("群组发言统计Json Cache写入异常: {}", e.getMessage());
        }
        return MESSAGE_IGNORE;
    }

}
