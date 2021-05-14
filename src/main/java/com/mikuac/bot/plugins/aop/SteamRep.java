package com.mikuac.bot.plugins.aop;

import com.alibaba.fastjson.JSONObject;
import com.mikuac.bot.bean.SteamRepBean;
import com.mikuac.bot.config.ApiConst;
import com.mikuac.bot.config.RegexConst;
import com.mikuac.bot.utils.HttpClientUtils;
import com.mikuac.bot.utils.RegexUtils;
import lombok.extern.slf4j.Slf4j;
import net.lz1998.pbbot.bot.Bot;
import net.lz1998.pbbot.bot.BotPlugin;
import net.lz1998.pbbot.utils.Msg;
import onebot.OnebotEvent;
import org.jetbrains.annotations.NotNull;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Component;

import java.io.IOException;


/**
 * @author Zero
 */
@Slf4j
@Component
public class SteamRep extends BotPlugin {

    public String getDom(String id) {
        String url = "https://steamrepcn.com/profiles/" + id + "/content";
        String result = HttpClientUtils.httpGetWithJson(url, false);
        JSONObject jsonObject = JSONObject.parseObject(result);
        return jsonObject.getString("html");
    }

    public SteamRepBean parseDom(String id) {
        SteamRepBean steamRepBean = new SteamRepBean();
        try {
            Connection.Response response =
                    Jsoup.connect(ApiConst.STEAM_REP_API2)
                            .timeout(5 * 1000)
                            .method(Connection.Method.POST)
                            .data("input", id)
                            .followRedirects(true)
                            .execute();

            Document doc = response.parse();
            // 获取全尺寸头像
            steamRepBean.setAvatar(doc.select(".img-rounded").attr("src").replaceAll("medium", "full"));
            // 获取64位ID
            steamRepBean.setSteam64Id(response.url().toString().replaceAll(ApiConst.STEAM_REP_API2, "").replaceAll("/", ""));
        } catch (IOException e) {
            log.error("SteamRep Plugin getDom2 Exception ", e);
        }

        // 解析Dom
        Document doc = Jsoup.parse(getDom(steamRepBean.getSteam64Id()));

        // Steam 名称
        String steamNickName = ".persona_name > p > b";
        steamRepBean.setSteamNickName(doc.select(steamNickName).text());

        // VAC 作弊系统检测
        String vacCheckQuery = ".profile_detail > div:nth-child(2) > div:nth-child(1) > p:nth-child(2)";
        steamRepBean.setVacCheck(doc.select(vacCheckQuery).text());

        // VAC 被 BAN 次数
        String vacBannedCountQuery = ".profile_detail > div:nth-child(2) > div:nth-child(2) > p:nth-child(2)";
        steamRepBean.setVacBannedCount(doc.select(vacBannedCountQuery).text());

        // 游戏内被 BAN 次数
        String gameBannedCountQuery = ".profile_detail > div:nth-child(2) > div:nth-child(3) > p:nth-child(2)";
        steamRepBean.setGameBannedCount(doc.select(gameBannedCountQuery).text());

        // Steam 交易状态
        String steamMarketStatusQuery = ".profile_detail > div:nth-child(2) > div:nth-child(4) > p:nth-child(2)";
        steamRepBean.setSteamMarketStatus(doc.select(steamMarketStatusQuery).text());

        // Steam 社区状态
        String steamCommunityStatusQuery = ".profile_detail > div:nth-child(3) > div:nth-child(1) > p:nth-child(2)";
        steamRepBean.setSteamCommunityStatus(doc.select(steamCommunityStatusQuery).text());

        // Steam 页面透明度
        String steamInfoStatusQuery = ".profile_detail > div:nth-child(3) > div:nth-child(2) > p:nth-child(2)";
        steamRepBean.setSteamInfoStatus(doc.select(steamInfoStatusQuery).text());

        // Steam 32位 ID
        String steam32IdQuery = ".profile_detail > div:nth-child(3) > div:nth-child(3) > p:nth-child(2)";
        steamRepBean.setSteam32Id(doc.select(steam32IdQuery).text());

        // SteamID3
        String steamId3Query = ".profile_detail > div:nth-child(3) > div:nth-child(4) > p:nth-child(2)";
        steamRepBean.setSteamId3(doc.select(steamId3Query).text());

        // Steam 注册时间
        String steamRegisterTimeQuery = ".profile_detail > div:nth-child(4) > div:nth-child(1) > p:nth-child(2)";
        steamRepBean.setSteamRegisterTime(doc.select(steamRegisterTimeQuery).text());

        // 地理位置
        String steamLocationQuery = ".profile_detail > div:nth-child(4) > div:nth-child(2) > p:nth-child(2)";
        steamRepBean.setSteamLocation(doc.select(steamLocationQuery).text());

        // 真实姓名
        String steamRealNameQuery = ".profile_detail > div:nth-child(4) > div:nth-child(3) > p:nth-child(2)";
        steamRepBean.setSteamRealName(doc.select(steamRealNameQuery).text());

        // Steam 游戏数
        String steamGameCountQuery = ".profile_detail > div:nth-child(4) > div:nth-child(4) > p:nth-child(2)";
        steamRepBean.setSteamGameCount(doc.select(steamGameCountQuery).text());

        // 信誉状况查询
        String steamRepStatusQuery1 = ".profile_rep > .rep_text > p:nth-child(1)";
        String steamRepStatusQuery2 = ".profile_rep > .rep_text > p:nth-child(2)";
        String steamRepStatus = doc.select(steamRepStatusQuery1).text() + "/" + doc.select(steamRepStatusQuery2).text();
        steamRepBean.setSteamRepStatus(steamRepStatus);

        return steamRepBean;
    }

    public Msg buildMsg(Boolean isGroupMsg, long userId, SteamRepBean steamRepBean) {
        Msg sendMsg = Msg.builder();
        if (isGroupMsg) {
            sendMsg.at(userId).text("\n");
        }
        sendMsg.image(steamRepBean.getAvatar()
        );
        sendMsg.text("\nSteam 名称: " + steamRepBean.getSteamNickName());
        sendMsg.text("\nVAC 作弊系统检测: " + steamRepBean.getVacCheck());
        sendMsg.text("\nVAC 被 BAN 次数: " + steamRepBean.getVacBannedCount());
        sendMsg.text("\n游戏内被 BAN 次数: " + steamRepBean.getGameBannedCount());
        sendMsg.text("\nSteam 交易状态: " + steamRepBean.getSteamMarketStatus());
        sendMsg.text("\nSteam 社区状态: " + steamRepBean.getSteamCommunityStatus());
        sendMsg.text("\nSteam 页面透明度: " + steamRepBean.getSteamInfoStatus());
        sendMsg.text("\nSteamID3: " + steamRepBean.getSteamId3());
        sendMsg.text("\nSteam 32位 ID: " + steamRepBean.getSteam32Id());
        sendMsg.text("\nSteam 64位 ID: " + steamRepBean.getSteam64Id());
        sendMsg.text("\nSteam 注册时间: " + steamRepBean.getSteamRegisterTime());
        sendMsg.text("\n地理位置: " + steamRepBean.getSteamLocation());
        sendMsg.text("\n真实姓名: " + steamRepBean.getSteamRealName());
        sendMsg.text("\nSteam 游戏数: " + steamRepBean.getSteamGameCount());
        sendMsg.text("\n" + steamRepBean.getSteamRepStatus());
        return sendMsg;
    }

    @Override
    public int onPrivateMessage(@NotNull Bot bot, @NotNull OnebotEvent.PrivateMessageEvent event) {
        String msg = event.getRawMessage();
        long userId = event.getUserId();

        if (msg.matches(RegexConst.STEAM_REP)) {
            String id = RegexUtils.regexGroup(RegexConst.STEAM_REP, msg, 1);
            try {
                bot.sendPrivateMsg(userId, "查询中，请稍后～", false);
                bot.sendPrivateMsg(userId, buildMsg(false, userId, parseDom(id)).build(), false);
            } catch (Exception e) {
                bot.sendPrivateMsg(userId, "查询失败，请检查SteamID是否正确或使用64位SteamID重试～", false);
            }
        }

        return MESSAGE_IGNORE;
    }

    @Override
    public int onGroupMessage(@NotNull Bot bot, @NotNull OnebotEvent.GroupMessageEvent event) {
        String msg = event.getRawMessage();
        long groupId = event.getGroupId();
        long userId = event.getUserId();

        if (msg.matches(RegexConst.STEAM_REP)) {
            String id = RegexUtils.regexGroup(RegexConst.STEAM_REP, msg, 1);
            try {
                bot.sendGroupMsg(groupId, Msg.builder().at(userId).text("查询中，请稍后～").build(), false);
                bot.sendGroupMsg(groupId, buildMsg(true, userId, parseDom(id)).build(), false);
            } catch (Exception e) {
                bot.sendGroupMsg(groupId, Msg.builder().at(userId).text("查询失败，请检查SteamID是否正确或使用64位SteamID重试～").build(), false);
            }
        }

        return MESSAGE_IGNORE;
    }

}
