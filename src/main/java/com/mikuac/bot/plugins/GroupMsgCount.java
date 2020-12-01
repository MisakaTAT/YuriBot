package com.mikuac.bot.plugins;

import com.mikuac.bot.entity.MsgCountEntity;
import com.mikuac.bot.repository.MsgCountRepository;
import com.mikuac.bot.utils.SendMsgUtils;
import lombok.extern.slf4j.Slf4j;
import net.lz1998.pbbot.bot.Bot;
import net.lz1998.pbbot.bot.BotPlugin;
import net.lz1998.pbbot.utils.Msg;
import onebot.OnebotEvent;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.Optional;

/**
 * 发言统计
 * @author Zero
 * @date 2020/11/16 19:03
 */
@Slf4j
@Component
public class GroupMsgCount extends BotPlugin {

    private SendMsgUtils sendMsgUtils;

    @Autowired
    public void setSendMsgUtils(SendMsgUtils sendMsgUtils) {
        this.sendMsgUtils = sendMsgUtils;
    }

    private MsgCountRepository msgCountRepository;

    @Autowired
    public void setMsgCountRepository(MsgCountRepository msgCountRepository) {
        this.msgCountRepository = msgCountRepository;
    }

    @Value("${yuri.bot.adminId}")
    private long adminId;

    @Scheduled(cron = "0 0 00 * * ?",zone = "Asia/Shanghai")
    public void sendMsg() throws InterruptedException {
        List<Long> groupIdList = sendMsgUtils.getGroupList();
        if (groupIdList != null && !groupIdList.isEmpty()) {
            for (long groupId :groupIdList) {
                Optional<MsgCountEntity> msgCount = msgCountRepository.findTodayMaxCount(groupId);
                if (msgCount.isPresent() && msgCount.get().getTodayMsgCount() > 0) {
                    Msg msg = Msg.builder()
                            .at(msgCount.get().getUserId())
                            .text("\n恭喜获得今日群龙王称号~")
                            .text("\n今日发言次数：" + msgCount.get().getTodayMsgCount())
                            .text("\n历史统计次数：" + msgCount.get().getAllMsgCount());
                    sendMsgUtils.sendGroupMsg(groupId,msg);
                } else {
                    log.info("群组[{}]发言次数获取异常或者无人发言",groupId);
                    Msg msg = Msg.builder()
                            .at(adminId)
                            .text("今日无人发言，无群龙王诞生~");
                    sendMsgUtils.sendGroupMsg(groupId,msg);
                }
            }
            //重置每日统计次数为0
            msgCountRepository.setDefaultTodayMsgCount();
        }
    }

    public void add(long groupId,long userId,int todayMsgCount,int allMsgCount){
        MsgCountEntity msgCountEntity = new MsgCountEntity();
        msgCountEntity.setGroupId(groupId);
        msgCountEntity.setUserId(userId);
        msgCountEntity.setTodayMsgCount(todayMsgCount);
        msgCountEntity.setAllMsgCount(allMsgCount);
        msgCountRepository.save(msgCountEntity);
    }

    @Override
    public int onGroupMessage(@NotNull Bot bot, @NotNull OnebotEvent.GroupMessageEvent event) {
        long userId = event.getUserId();
        long groupId = event.getGroupId();
        Optional<MsgCountEntity> msgCount = msgCountRepository.findByGroupAndUserId(groupId,userId);
        if (msgCount.isPresent()) {
            int todayMsgCount = msgCount.get().getTodayMsgCount();
            int allMsgCount = msgCount.get().getAllMsgCount();
            msgCountRepository.update(groupId, userId, ++todayMsgCount, ++allMsgCount);
        } else {
            log.info("群组[{}]内的用户[{}]从未进行消息统计，即将创建该群组字段",groupId,userId);
            add(groupId,userId,1,1);
        }
        return MESSAGE_IGNORE;
    }

}