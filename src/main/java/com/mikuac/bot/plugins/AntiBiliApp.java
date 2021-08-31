package com.mikuac.bot.plugins;

import cn.hutool.http.HttpStatus;
import com.alibaba.fastjson.JSON;
import com.mikuac.bot.bean.antibili.AntiBiliBean;
import com.mikuac.bot.bean.antibili.AntiBiliData;
import com.mikuac.bot.bean.antibili.AntiBiliStat;
import com.mikuac.bot.common.utils.RegexUtils;
import com.mikuac.bot.common.utils.RequestUtils;
import com.mikuac.bot.config.ApiConst;
import com.mikuac.bot.config.RegexConst;
import com.mikuac.shiro.common.utils.MsgUtils;
import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.core.BotPlugin;
import com.mikuac.shiro.dto.event.message.GroupMessageEvent;
import com.mikuac.shiro.dto.event.message.PrivateMessageEvent;
import lombok.SneakyThrows;
import org.apache.commons.text.StringEscapeUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * 解析哔哩哔哩小程序
 *
 * @author Zero
 * @date 2020/12/7 14:12
 */
@Component
public class AntiBiliApp extends BotPlugin {

    @Resource
    private AntiBiliBean antiBiliBean;

    private String getRedirectUrlBv(String url) throws IOException {
        HttpURLConnection h = (HttpURLConnection) new URL(url).openConnection();
        h.setRequestMethod("GET");
        h.connect();
        if (h.getResponseCode() == HttpStatus.HTTP_FORBIDDEN) {
            String bvId = RegexUtils.regex(RegexConst.GET_URL_BVID, String.valueOf(h.getURL()));
            h.disconnect();
            return bvId;
        } else {
            h.disconnect();
            return null;
        }
    }

    private void getVideoInfo(String bvId) {
        String result = RequestUtils.get(ApiConst.BILI_VIDEO_INFO_API + bvId, false);
        antiBiliBean = JSON.parseObject(result, AntiBiliBean.class);
    }

    private void sendMsg(@NotNull Bot bot, long groupId, long userId, AntiBiliData data, AntiBiliStat stat) {
        String videoUrl = "https://www.bilibili.com/video/";
        MsgUtils sendMsg = MsgUtils.builder()
                .img(data.getPic())
                .text("\n" + data.getTitle())
                .text("\nUP：" + data.getOwner().getName())
                .text("\n播放：" + stat.getView() + "  弹幕：" + stat.getDanmaku())
                .text("\n投币：" + stat.getCoin() + "  点赞：" + stat.getLike())
                .text("\n评论：" + stat.getReply() + "  分享：" + stat.getShare())
                .text("\n" + videoUrl + "av" + data.getAid())
                .text("\n" + videoUrl + data.getBvid());
        if (groupId != 0L) {
            bot.sendGroupMsg(groupId, sendMsg.build(), false);
            return;
        }
        bot.sendPrivateMsg(userId, sendMsg.build(), false);
    }

    @SneakyThrows
    @Override
    public int onPrivateMessage(@NotNull Bot bot, PrivateMessageEvent event) {
        String msg = StringEscapeUtils.unescapeHtml4(event.getMessage()).replaceAll("\\\\", "");
        long userId = event.getUserId();
        if (msg.matches(RegexConst.ANTI_BILI_MINI_APP)) {
            // 获取qqDocUrl字段的值
            String redirectUrl = RegexUtils.regex(RegexConst.GET_QQ_DOC_URL, msg);
            // 如果不为空继续302跳转取到bvId
            if (redirectUrl != null) {
                String bvId = getRedirectUrlBv(redirectUrl);
                if (bvId != null && !bvId.isEmpty()) {
                    getVideoInfo(bvId);
                    AntiBiliData data = antiBiliBean.getData();
                    AntiBiliStat stat = antiBiliBean.getData().getStat();
                    sendMsg(bot, 0L, userId, data, stat);
                }
            }
        }
        return MESSAGE_IGNORE;
    }

    @SneakyThrows
    @Override
    public int onGroupMessage(@NotNull Bot bot, @NotNull GroupMessageEvent event) {
        String msg = StringEscapeUtils.unescapeHtml4(event.getMessage()).replaceAll("\\\\", "");
        long userId = event.getUserId();
        long groupId = event.getGroupId();
        if (msg.matches(RegexConst.ANTI_BILI_MINI_APP)) {
            // 获取qqDocUrl字段的值
            String redirectUrl = RegexUtils.regex(RegexConst.GET_QQ_DOC_URL, msg);
            // 如果不为空继续302跳转取到bvId
            if (redirectUrl != null) {
                String bvId = getRedirectUrlBv(redirectUrl);
                if (bvId != null && !bvId.isEmpty()) {
                    getVideoInfo(bvId);
                    AntiBiliData data = antiBiliBean.getData();
                    AntiBiliStat stat = antiBiliBean.getData().getStat();
                    sendMsg(bot, groupId, userId, data, stat);
                }
            }
        }
        return MESSAGE_IGNORE;
    }

}
