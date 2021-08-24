package com.mikuac.bot.telegram;

import com.mikuac.bot.common.utils.SendMsgUtils;
import com.mikuac.bot.common.utils.TelegramUtils;
import com.mikuac.bot.config.Global;
import com.mikuac.shiro.utils.Msg;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author Zero
 * @date 2020/10/31 17:08
 */
@Slf4j
// @Component
public class ForwardMessage extends TelegramLongPollingBot {

    @Resource
    private SendMsgUtils sendMsgUtils;

    @SneakyThrows
    @Override
    public void onUpdateReceived(Update update) {
        boolean isChannelMessage = update.getChannelPost().isChannelMessage();
        if (isChannelMessage) {
            String groupName = update.getChannelPost().getChat().getTitle();
            String forwardFrom = update.getChannelPost().getForwardFromChat().getTitle();
            String imgFileId = "";
            int photoListSize = update.getChannelPost().getPhoto().size();
            for (int i = 0; i < photoListSize; i++) {
                imgFileId = update.getChannelPost().getPhoto().get(i).getFileId();
            }
            if (imgFileId != null && !imgFileId.isEmpty()) {
                String imgUrl = TelegramUtils.getImgUrl(Global.telegramBotToken, imgFileId);
                if (imgUrl != null && !imgUrl.isEmpty()) {
                    List<Long> groupIdList = sendMsgUtils.getGroupList();
                    if (groupIdList != null && !groupIdList.isEmpty()) {
                        for (long groupId : groupIdList) {
                            Msg msg = Msg.builder()
                                    .img(imgUrl)
                                    .text("\nFrom Telegram：" + (forwardFrom != null ? forwardFrom : groupName));
                            sendMsgUtils.sendGroupMsgForMsg(groupId, msg);
                        }
                    } else {
                        log.error("群组列表获取失败");
                    }
                } else {
                    log.error("Telegram imgUrl获取失败");
                }
            } else {
                log.error("Telegram imgFileId获取失败");
            }
        }
    }

    @Override
    public String getBotUsername() {
        return Global.telegramBotName;
    }

    @Override
    public String getBotToken() {
        return Global.telegramBotToken;
    }

}