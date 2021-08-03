package com.mikuac.bot.telegram;

import com.mikuac.bot.common.utils.SendMsgUtils;
import com.mikuac.bot.common.utils.TelegramUtils;
import com.mikuac.shiro.utils.Msg;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

/**
 * @author Zero
 * @date 2020/10/31 17:08
 */
@Slf4j
//@Component
public class ForwardMessage extends TelegramLongPollingBot {

    private SendMsgUtils sendMsgUtils;
    @Value("${yuri.telegram.botName}")
    private String botName;
    @Value("${yuri.telegram.botToken}")
    private String botToken;

    @Autowired
    public void setSendMsgUtils(SendMsgUtils sendMsgUtils) {
        this.sendMsgUtils = sendMsgUtils;
    }

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
                String imgUrl = TelegramUtils.getImgUrl(botToken, imgFileId);
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
                        log.info("群组列表获取失败");
                    }
                } else {
                    log.info("Telegram imgUrl获取失败");
                }
            } else {
                log.info("Telegram imgFileId获取失败");
            }
        }
    }

    @Override
    public String getBotUsername() {
        return botName;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

}