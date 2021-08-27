package com.mikuac.bot.common.utils;

import com.mikuac.bot.config.Global;
import com.mikuac.bot.entity.BanEntity;
import com.mikuac.bot.repository.BanRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 封禁工具类
 *
 * @author Zero
 * @date 2020/12/4 11:19
 */
@Slf4j
@Component
public class BanUtils {

    Map<Long, Integer> msgCountMap = new ConcurrentHashMap<>();
    Map<Long, Long> startTimeMap = new ConcurrentHashMap<>();
    private BanRepository banRepository;

    @Autowired
    public void setBanRepository(BanRepository banRepository) {
        this.banRepository = banRepository;
    }

    public void setBan(long userId) {
        startTimeMap.put(userId, Instant.now().getEpochSecond());
        int msgCount = msgCountMap.getOrDefault(userId, 0);
        msgCountMap.put(userId, ++msgCount);
        long startTime = startTimeMap.getOrDefault(userId, 0L);
        long nowTime = Instant.now().getEpochSecond();

        // 判断是否还在限制时间内
        if (startTime + Global.BAN_UTILS_LIMIT_TIME >= nowTime && userId != Global.BOT_ADMIN_ID) {
            int getMsgCount = msgCountMap.getOrDefault(userId, 0);
            // 如果在限制时间内发送的消息次数大于限制次数则封禁
            if (getMsgCount > Global.BAN_UTILS_LIMIT_COUNT) {
                log.info("用户：[{}]已触发滥用规则被封禁", userId);
                BanEntity banEntity = new BanEntity();
                banEntity.setUserId(userId);
                banEntity.setIsBanned(true);
                banRepository.save(banEntity);
            }
        } else {
            // 如果不在限制时间内则重置搜索次数
            msgCountMap.clear();
        }
    }

    public Boolean isBanned(long userId) {
        Optional<BanEntity> optional = banRepository.findByUserId(userId);
        Boolean isBanned = false;
        if (optional.isPresent()) {
            isBanned = optional.get().getIsBanned();
        }
        return isBanned;
    }

}
