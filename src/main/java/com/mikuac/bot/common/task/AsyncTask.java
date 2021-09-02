package com.mikuac.bot.common.task;

import com.mikuac.bot.BotApplication;
import com.mikuac.bot.config.Config;
import com.mikuac.shiro.core.Bot;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * Created on 2021/5/25.
 *
 * @author Zero
 */
@Slf4j
@Component
public class AsyncTask {

    @Async("taskExecutor")
    public void rebootBot() {
        BotApplication.reboot();
    }

    @Async("taskExecutor")
    public void deleteMsg(int msgId, Bot bot) {
        if (msgId != 0) {
            try {
                Thread.sleep(Config.SETU_DELETE_TIME * 1000L);
                bot.deleteMsg(msgId);
                log.info("色图撤回成功，消息ID：[{}]", msgId);
            } catch (InterruptedException e) {
                log.error("色图撤回异常: {}", e.getMessage());
            }
        }
    }

}
