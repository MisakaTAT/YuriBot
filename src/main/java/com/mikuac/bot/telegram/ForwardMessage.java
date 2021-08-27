package com.mikuac.bot.telegram;

import com.mikuac.bot.common.utils.GetBeanUtil;
import com.mikuac.bot.common.utils.SendMsgUtils;
import com.mikuac.bot.common.utils.TelegramUtils;
import com.mikuac.bot.config.Global;
import com.mikuac.shiro.common.utils.MsgUtils;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import javax.annotation.PostConstruct;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

/**
 * @author Zero
 * @date 2020/10/31 17:08
 */
@Slf4j
@Component
public class ForwardMessage extends TelegramLongPollingBot {

    public static String downloadImg(String imgUrl) throws IOException {
        String dirPath = "temp/telegram/img/";
        File file = new File(dirPath);
        if (!file.exists() && !file.isDirectory()) {
            boolean mkdirDone = file.mkdirs();
            log.warn("Telegram img temp no exist, auto create status: {}", mkdirDone);
        }
        URL url = new URL(imgUrl);
        URLConnection urlConnection = url.openConnection();
        InputStream inputStream = urlConnection.getInputStream();
        String path = dirPath + System.currentTimeMillis() + ".webp";
        FileOutputStream out = new FileOutputStream(path);
        int j;
        while ((j = inputStream.read()) != -1) {
            out.write(j);
        }
        inputStream.close();

        BufferedImage image = ImageIO.read(new File(path));
        Thumbnails.of(image)
                .outputFormat("jpg")
                .scale(1f)
                .toFile(new File(path.replaceAll("\\.webp", "")));

        return new File(path.replaceAll("webp", "jpg")).getAbsolutePath();
    }

    @PostConstruct
    private void init() {
        if (Global.telegramForwardGroup == null || Global.telegramForwardGroup.size() <= 0) {
            return;
        }
        try {
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            DefaultBotOptions botOptions = new DefaultBotOptions();
            if (Global.telegramEnableProxy && !Global.telegramBaseUrl.isEmpty()) {
                botOptions.setBaseUrl(Global.telegramBaseUrl + "/bot");
                log.error("Telegram plugin config proxy and baseurl exist simultaneously, First using baseurl.");
            } else {
                if (Global.telegramEnableProxy) {
                    botOptions.setProxyHost(Global.telegramProxyHost);
                    botOptions.setProxyPort(Global.telegramProxyPort);
                    botOptions.setProxyType(DefaultBotOptions.ProxyType.HTTP);
                }
                if (!Global.telegramBaseUrl.isEmpty()) {
                    botOptions.setBaseUrl(Global.telegramBaseUrl + "/bot");
                }
            }
            botsApi.registerBot(new ForwardMessage(botOptions));
        } catch (TelegramApiException e) {
            log.error("Telegram plugin init exception: {}", e.getMessage());
        }
    }

    private final SendMsgUtils sendMsgUtils = GetBeanUtil.getBean(SendMsgUtils.class);

    public ForwardMessage() {
        super();
    }

    public ForwardMessage(DefaultBotOptions botOptions) {
        super(botOptions);
    }

    @SneakyThrows
    @Override
    public void onUpdateReceived(Update update) {
        boolean hasMsg = false;
        if (update.hasMessage()) {
            MsgUtils msgUtils = MsgUtils.builder();
            if (update.getMessage().hasText()) {
                msgUtils.text(update.getMessage().getText());
                hasMsg = true;
            }
            if (update.getMessage().hasPhoto()) {
                List<PhotoSize> list = update.getMessage().getPhoto();
                int size = list.size() - 1;
                String url = TelegramUtils.getImgUrl(Global.telegramBotToken, list.get(size).getFileId());
                if (url != null) {
                    msgUtils.img(url);
                }
                hasMsg = true;
            }
            if (update.getMessage().hasSticker()) {
                String url = TelegramUtils.getImgUrl(Global.telegramBotToken, update.getMessage().getSticker().getFileId());
                String path = downloadImg(url);
                if (url == null || url.isEmpty() || path.isEmpty()) {
                    return;
                }
                if (path.contains(":\\")) {
                    path = path.replaceAll("\\\\", "/");
                    msgUtils.img("file:///" + path);
                } else {
                    msgUtils.img("file://" + path);
                }
                hasMsg = true;
            }
            if (hasMsg) {
                msgUtils.text("\n发送者: " + update.getMessage().getFrom().getUserName());
                msgUtils.text("\n消息来自TG群组: " + update.getMessage().getChat().getTitle());
                if (Global.telegramForwardGroup.size() > 0) {
                    for (long groupId : Global.telegramForwardGroup) {
                        sendMsgUtils.sendGroupMsgForMsg(groupId, msgUtils);
                    }
                }
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