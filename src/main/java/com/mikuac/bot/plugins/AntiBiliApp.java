package com.mikuac.bot.plugins;

import com.alibaba.fastjson.JSON;
import com.mikuac.bot.bean.antibili.AntiBiliBean;
import com.mikuac.bot.bean.antibili.AntiBiliData;
import com.mikuac.bot.bean.antibili.AntiBiliStat;
import com.mikuac.bot.config.ApiConst;
import com.mikuac.bot.config.RegexConst;
import com.mikuac.bot.utils.HttpClientUtils;
import com.mikuac.bot.utils.RegexUtils;
import lombok.SneakyThrows;
import net.lz1998.pbbot.bot.Bot;
import net.lz1998.pbbot.bot.BotPlugin;
import net.lz1998.pbbot.utils.Msg;
import onebot.OnebotEvent;
import org.apache.commons.text.StringEscapeUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * 解析哔哩哔哩小程序
 * @author Zero
 * @date 2020/12/7 14:12
 */
@Component
public class AntiBiliApp extends BotPlugin {

    private AntiBiliBean antiBiliBean;

    @Autowired
    public void setAntiBiliBean(AntiBiliBean antiBiliBean) {
        this.antiBiliBean = antiBiliBean;
    }

    public String getRedirectUrlBv (String url) throws IOException {
        int resCode = 403;
        HttpURLConnection h = (HttpURLConnection) new URL(url).openConnection();
        h.setRequestMethod("GET");
        h.connect();
        if (h.getResponseCode() == resCode) {
            String bvId = RegexUtils.regex(RegexConst.GET_URL_BVID,String.valueOf(h.getURL()));
            h.disconnect();
            return bvId;
        } else {
            h.disconnect();
            return null;
        }
    }

    public void getVideoInfo (String bvId) {
        String result = HttpClientUtils.httpGetWithJson(ApiConst.BILI_VIDEO_INFO_API + bvId,false);
        antiBiliBean = JSON.parseObject(result, AntiBiliBean.class);
    }

    @SneakyThrows
    @Override
    public int onPrivateMessage(@NotNull Bot bot, @NotNull OnebotEvent.PrivateMessageEvent event) {
        String videoUrl = "https://www.bilibili.com/video/";
        String msg = StringEscapeUtils.unescapeHtml4(event.getRawMessage()).replaceAll("\\\\","");
        long userId = event.getUserId();

        if (msg.matches(RegexConst.ANTI_BILI_MINI_APP)) {
            // 获取qqDocUrl字段的值
            String redirectUrl = RegexUtils.regex(RegexConst.GET_QQ_DOC_URL,msg);
            // 如果不为空继续302跳转取到bvId
            if (redirectUrl != null) {
                String bvId = getRedirectUrlBv(redirectUrl);
                if (bvId != null && !"".equals(bvId)) {
                    getVideoInfo(bvId);
                    AntiBiliData data = antiBiliBean.getData();
                    AntiBiliStat stat = antiBiliBean.getData().getStat();
                    Msg sendMsg = Msg.builder()
                            .image(data.getPic())
                            .text("\n" + data.getTitle())
                            .text("\n【UP】" + data.getOwner().getName())
                            .text("\n【播放】" + stat.getView() + "【弹幕】" + stat.getDanmaku() + "【投币】" + stat.getCoin())
                            .text("\n【评论】" + stat.getReply() + "【分享】" + stat.getShare() + "【点赞】" + stat.getLike())
                            .text("\n" + videoUrl + data.getAid())
                            .text("\n" + videoUrl + data.getBvid());
                    bot.sendPrivateMsg(userId,sendMsg.build(),false);
                }
            }
        }

        return MESSAGE_IGNORE;
    }

    @SneakyThrows
    @Override
    public int onGroupMessage(@NotNull Bot bot, @NotNull OnebotEvent.GroupMessageEvent event) {
        String videoUrl = "https://www.bilibili.com/video/";
        String msg = StringEscapeUtils.unescapeHtml4(event.getRawMessage()).replaceAll("\\\\","");
        long userId = event.getUserId();
        long groupId = event.getGroupId();

        if (msg.matches(RegexConst.ANTI_BILI_MINI_APP)) {
            // 获取qqDocUrl字段的值
            String redirectUrl = RegexUtils.regex(RegexConst.GET_QQ_DOC_URL,msg);
            // 如果不为空继续302跳转取到bvId
            if (redirectUrl != null) {
                String bvId = getRedirectUrlBv(redirectUrl);
                if (bvId != null && !"".equals(bvId)) {
                    getVideoInfo(bvId);
                    AntiBiliData data = antiBiliBean.getData();
                    AntiBiliStat stat = antiBiliBean.getData().getStat();
                    Msg sendMsg = Msg.builder()
                            .at(userId)
                            .image(data.getPic())
                            .text("\n" + data.getTitle())
                            .text("\n【UP】" + data.getOwner().getName())
                            .text("\n【播放】" + stat.getView() + "【弹幕】" + stat.getDanmaku() + "【投币】" + stat.getCoin())
                            .text("\n【评论】" + stat.getReply() + "【分享】" + stat.getShare() + "【点赞】" + stat.getLike())
                            .text("\n" + videoUrl + data.getAid())
                            .text("\n" + videoUrl + data.getBvid());
                    bot.sendGroupMsg(groupId,sendMsg.build(),false);
                }
            }
        }

        return MESSAGE_IGNORE;
    }

}
