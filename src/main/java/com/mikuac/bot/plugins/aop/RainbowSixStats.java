package com.mikuac.bot.plugins.aop;

import com.alibaba.fastjson.JSON;
import com.mikuac.bot.bean.r6s.*;
import com.mikuac.bot.config.ApiConst;
import com.mikuac.bot.utils.CommonUtils;
import com.mikuac.bot.utils.HttpClientUtils;
import com.mikuac.bot.config.RegexConst;
import com.mikuac.bot.utils.RegexUtils;
import lombok.extern.slf4j.Slf4j;
import net.lz1998.pbbot.bot.Bot;
import net.lz1998.pbbot.bot.BotPlugin;
import net.lz1998.pbbot.utils.Msg;
import onebot.OnebotEvent;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * 彩虹六号战绩查询
 * @author Zero
 * @date 2020/11/4 22:01
 */
@Slf4j
@Component
public class RainbowSixStats extends BotPlugin {

    private R6S r6S;

    @Autowired
    public void setR6S(R6S r6S) {
        this.r6S = r6S;
    }

    /**
     * 战绩查询方法
     * @param gameUserName 游戏用户名
     */
    public void getRainbowSixStats(String gameUserName) {
        String result = HttpClientUtils.httpGetWithJson(ApiConst.R6S_API +gameUserName,true);
        r6S = JSON.parseObject(result, R6S.class);
    }

    /**
     * 数据处理
     */
    public Msg getDataAndBuilder() {
        Msg msg = Msg.builder();
        for (BasicStat basicStat : r6S.getBasicStat()) {
            if ("apac".equals(basicStat.getRegion())) {
                msg.text("【基本信息】")
                        .text("\n查询状态码:"+r6S.getStatus()+"  "+"区服:"+basicStat.getRegion())
                        .text("\n数据更新时间:"+basicStat.getUpdatedAt())
                        .text("\n用户名:"+r6S.getUsername()+"  "+"游戏等级:"+basicStat.getLevel())
                        .text("\n\n【当前赛季排位信息】")
                        .text("\n胜场:"+basicStat.getWins()+"  "+"败场:"+basicStat.getLosses()+"  "+"弃赛:"+basicStat.getAbandons())
                        .text("\n当前MMR:"+basicStat.getMmr()+"  "+"最高MMR:"+basicStat.getMaxMmr()+"  "+"赛季:"+basicStat.getSeason())
                        .text("\n当前段位:"+basicStat.getRank()+"  "+"最高段位:"+basicStat.getMaxRank()+"  "+"平台:"+basicStat.getPlatform());
            }
        }
        for (StatGeneral statGeneral : r6S.getStatGeneral()){
            msg.text("\n\n【综合数据】")
                    .text("\n总胜场:"+statGeneral.getWon()+"  "+"总败场:"+statGeneral.getLost()+"  "+"胜率:"+ CommonUtils.formatDouble(((double) statGeneral.getWon() / (double) statGeneral.getLost()))+"%")
                    .text("\n总击杀:"+statGeneral.getKills()+"  "+"总死亡:"+statGeneral.getDeaths()+"  "+"KD:"+ CommonUtils.formatDouble(((double) statGeneral.getKills() / (double) statGeneral.getDeaths()))+"%")
                    .text("\n总开火次数:"+statGeneral.getBulletsFired()+"  "+"总命中次数:"+statGeneral.getBulletsHit())
                    .text("\n助攻:"+statGeneral.getKillAssists()+"  "+"近战:"+statGeneral.getMeleeKills()+"  "+"救助:"+statGeneral.getRevives()+"  "+"爆头:"+statGeneral.getHeadShot())
                    .text("\n穿透击杀:"+statGeneral.getPenetrationKills()+"  "+"爆头率:"+CommonUtils.formatDouble(((double)statGeneral.getHeadShot()/(double)statGeneral.getKills())*100)+"%");
        }
        for (StatCR statcr : r6S.getStatCR()) {
            if ("ranked".equals(statcr.getModel())) {
                msg.text("\n\n【排名战数据】")
                        .text("\n胜场:"+statcr.getWon()+"  "+"败场:"+statcr.getLost()+"  "+"胜率:"+ CommonUtils.formatDouble(((double) statcr.getWon() / (double) statcr.getLost()))+"%"+"  "+"游戏场次:"+statcr.getPlayed())
                        .text("\n击杀:"+statcr.getKills()+"  "+"阵亡:"+statcr.getDeaths()+"  "+"KD:"+ CommonUtils.formatDouble(((double) statcr.getKills() / (double) statcr.getDeaths()))+"%");
            }
            if ("casual".equals(statcr.getModel())) {
                msg.text("\n\n【常规战数据】")
                        .text("\n胜场:"+statcr.getWon()+"  "+"败场:"+statcr.getLost()+"  "+"胜率:"+ CommonUtils.formatDouble(((double) statcr.getWon() / (double) statcr.getLost()))+"%"+"  "+"游戏场次:"+statcr.getPlayed())
                        .text("\n击杀:"+statcr.getKills()+"  "+"阵亡:"+statcr.getDeaths()+"  "+"KD:"+ CommonUtils.formatDouble(((double) statcr.getKills() / (double) statcr.getDeaths()))+"%");
            }
        }
        for (StatBHS statbhs : r6S.getStatBHS()) {
            // 肃清威胁
            if ("secureareapvp".equals(statbhs.getModel())) {
                msg.text("\n\n【模式数据-肃清威胁】")
                        .text("\n胜场:"+statbhs.getWon()+"  "+"败场:"+statbhs.getLost()+"  "+"总场次:"+statbhs.getPlayed()+"  "+"最高得分:"+statbhs.getBestScore());
            }
            // 炸弹
            if ("plantbombpvp".equals(statbhs.getModel())) {
                msg.text("\n\n【模式数据-炸弹模式】")
                        .text("\n胜场:"+statbhs.getWon()+"  "+"败场:"+statbhs.getLost()+"  "+"总场次:"+statbhs.getPlayed()+"  "+"最高得分:"+statbhs.getBestScore());

            }
            // 人质
            if ("rescuehostagepvp".equals(statbhs.getModel())) {
                msg.text("\n\n【模式数据-人质模式】")
                        .text("\n胜场:"+statbhs.getWon()+"  "+"败场:"+statbhs.getLost()+"  "+"总场次:"+statbhs.getPlayed()+"  "+"最高得分:"+statbhs.getBestScore());

            }
        }
        return msg;
    }

    @Override
    public int onGroupMessage(@NotNull Bot bot, @NotNull OnebotEvent.GroupMessageEvent event) {
        String msg = event.getRawMessage();
        if (msg.matches(RegexConst.RAINBOW_SIX_STATS)){
            long groupId = event.getGroupId();
            long userId = event.getUserId();
            String gameUserId = RegexUtils.regex(RegexConst.GET_R6_ID,msg);
            if (gameUserId != null) {
                gameUserId = URLEncoder.encode(gameUserId, StandardCharsets.UTF_8);
                bot.sendGroupMsg(groupId,Msg.builder().at(userId).text(gameUserId+"数据查询中，请稍后~").build(),false);
                try {
                    getRainbowSixStats(gameUserId);
                    getDataAndBuilder();
                    bot.sendGroupMsg(groupId,getDataAndBuilder().text("\n\n").at(userId).build(),false);
                } catch (Exception e) {
                    bot.sendGroupMsg(groupId,Msg.builder().at(userId).text(gameUserId+"游戏数据查询失败，请稍后重试~").build(),false);
                }
            } else {
                bot.sendGroupMsg(groupId,Msg.builder().at(userId).text("游戏ID匹配失败，请重试~").build(),false);
            }

        }
        return MESSAGE_IGNORE;
    }

    @Override
    public int onPrivateMessage(@NotNull Bot bot, @NotNull OnebotEvent.PrivateMessageEvent event) {
        String msg = event.getRawMessage();
        if (msg.matches(RegexConst.RAINBOW_SIX_STATS)){
            long userId = event.getUserId();
            String gameUserId = RegexUtils.regex(RegexConst.GET_R6_ID,msg);
            if (gameUserId != null) {
                gameUserId = URLEncoder.encode(gameUserId, StandardCharsets.UTF_8);
                bot.sendPrivateMsg(userId,gameUserId+"数据查询中，请稍后~",false);
                try {
                    getRainbowSixStats(gameUserId);
                    getDataAndBuilder();
                    bot.sendPrivateMsg(userId,getDataAndBuilder().build(),false);
                } catch (Exception e) {
                    bot.sendPrivateMsg(userId,gameUserId+"游戏数据查询失败，请稍后重试~",false);
                }
            } else {
                bot.sendPrivateMsg(userId,"游戏ID匹配失败，请重试~",false);
            }
        }
        return MESSAGE_IGNORE;
    }

}
