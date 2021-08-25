package com.mikuac.bot.plugins;

import com.mikuac.bot.bean.MsgCountCacheBean;
import com.mikuac.bot.common.utils.SendMsgUtils;
import com.mikuac.bot.entity.MsgCountEntity;
import com.mikuac.bot.repository.MsgCountRepository;
import com.mikuac.shiro.common.utils.MsgUtils;
import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.core.BotPlugin;
import com.mikuac.shiro.dto.event.message.GroupMessageEvent;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 发言统计
 *
 * @author Zero
 * @date 2020/11/16 19:03
 */
@Slf4j
@Component
public class GroupMsgCount extends BotPlugin {

    Map<Long, MsgCountCacheBean> cache = new ConcurrentHashMap<>();
    @Resource
    private SendMsgUtils sendMsgUtils;
    @Resource
    private MsgCountRepository msgCountRepository;

    @Scheduled(cron = "0 0 00 * * ?", zone = "Asia/Shanghai")
    private void sendMsg() throws InterruptedException {
        List<Long> groupIdList = sendMsgUtils.getGroupList();
        if (groupIdList != null && !groupIdList.isEmpty()) {
            for (long groupId : groupIdList) {
                Optional<MsgCountEntity> msgCount = msgCountRepository.findTodayMaxCount(groupId);
                if (msgCount.isPresent() && msgCount.get().getTodayMsgCount() > 0) {
                    MsgUtils msg = MsgUtils.builder()
                            .at(msgCount.get().getUserId())
                            .text("\n恭喜获得今日群龙王称号~")
                            .text("\n今日发言次数：" + msgCount.get().getTodayMsgCount())
                            .text("\n历史统计次数：" + msgCount.get().getAllMsgCount());
                    sendMsgUtils.sendGroupMsgForMsg(groupId, msg);
                } else {
                    log.info("群组[{}]发言次数获取异常或者无人发言", groupId);
                    MsgUtils msg = MsgUtils.builder()
                            .text("今日群内无人发言，暂无龙王诞生~");
                    sendMsgUtils.sendGroupMsgForMsg(groupId, msg);
                }
            }
            //重置每日统计次数为0
            msgCountRepository.setDefaultTodayMsgCount();
        }
    }

    /**
     * 一分钟持久化一次，值设置为50错开12点整的统计定时任务，防止sqlite同时读写出现locked
     */
    @Scheduled(cron = "50 * * * * ?", zone = "Asia/Shanghai")
    private void cachePersistent() {
        try {
            // 遍历Map
            for (Map.Entry<Long, MsgCountCacheBean> c : cache.entrySet()) {
                MsgCountCacheBean msgCountCacheBean = c.getValue();
                long groupId = msgCountCacheBean.getGroupId();
                long userId = msgCountCacheBean.getUserId();
                // 写入数据库
                Optional<MsgCountEntity> msgCount = msgCountRepository.findByGroupAndUserId(groupId, userId);
                if (msgCount.isPresent()) {
                    // 今日计数，数据库今日计数 + 当前未持久化的次数
                    int todayMsgCount = msgCount.get().getTodayMsgCount() + msgCountCacheBean.getCount();
                    // 总计数，数据库当前计数 + 当前未持久化的次数
                    int allMsgCount = msgCount.get().getAllMsgCount() + msgCountCacheBean.getCount();
                    msgCountRepository.update(groupId, userId, todayMsgCount, allMsgCount);
                } else {
                    MsgCountEntity msgCountEntity = new MsgCountEntity();
                    msgCountEntity.setGroupId(groupId);
                    msgCountEntity.setUserId(userId);
                    msgCountEntity.setTodayMsgCount(msgCountCacheBean.getCount());
                    msgCountEntity.setAllMsgCount(msgCountCacheBean.getCount());
                    msgCountRepository.save(msgCountEntity);
                }
            }
            cache.clear();
        } catch (Exception e) {
            log.error("发言统计缓存持久化失败: {}", e.getMessage());
        }
    }

    @Override
    public int onGroupMessage(@NotNull Bot bot, @NotNull GroupMessageEvent event) {
        long userId = event.getUserId();
        long groupId = event.getGroupId();
        MsgCountCacheBean mb = cache.getOrDefault(userId + groupId, null);
        if (mb != null) {
            int nowCount = mb.getCount();
            mb.setCount(++nowCount);
            cache.put(userId + groupId, mb);
        }
        if (mb == null) {
            mb = new MsgCountCacheBean();
            mb.setUserId(userId);
            mb.setGroupId(groupId);
            mb.setCount(1);
            cache.put(userId + groupId, mb);
        }
        return MESSAGE_IGNORE;
    }

}
