package com.mikuac.bot.telegram;

import com.mikuac.bot.utils.SendMsgUtils;
import com.mikuac.bot.utils.TelegramUtils;
import lombok.extern.slf4j.Slf4j;
import net.lz1998.pbbot.utils.Msg;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Update;

/**
 * @author Zero
 * @date 2020/10/31 17:08
 */
@Slf4j
@Component
public class ForwardMessage extends TelegramLongPollingBot {

    @Autowired
    private SendMsgUtils sendMsgUtils;

    @Value("${yuri.telegram.enable}")
    private Boolean enable;
    @Value("${yuri.telegram.botName}")
    private String botName;
    @Value("${yuri.telegram.botToken}")
    private String botToken;

    @Override
    public void onUpdateReceived(Update update) {
        if (enable) {
            Boolean isGroupChat = update.getMessage().getChat().isGroupChat();
            if (isGroupChat) {
                String groupName = update.getMessage().getChat().getTitle();
                String forwardFrom = update.getMessage().getForwardFromChat().getTitle();
                String imgFileId = update.getMessage().getPhoto().get(2).getFileId();
                if (imgFileId != null) {
                    String imgUrl = TelegramUtils.getImgUrl(botToken, imgFileId);
                    if (imgUrl != null) {
                        Msg msg = Msg.builder()
                                .image(imgUrl)
                                .text("消息来自Telegram群组：" + (forwardFrom != null ? forwardFrom : groupName));
                        sendMsgUtils.sendGroupMsg(msg);
                    }
                }
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