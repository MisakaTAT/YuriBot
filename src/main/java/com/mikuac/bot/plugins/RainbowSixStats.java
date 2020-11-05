package com.mikuac.bot.plugins;

import com.alibaba.fastjson.JSON;
import com.mikuac.bot.bean.r6s.*;
import com.mikuac.bot.utils.CommonUtils;
import com.mikuac.bot.utils.HttpClientUtil;
import lombok.extern.slf4j.Slf4j;
import net.lz1998.pbbot.bot.Bot;
import net.lz1998.pbbot.bot.BotPlugin;
import net.lz1998.pbbot.utils.Msg;
import onebot.OnebotEvent;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

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

    @Value("${yuri.plugins.rainbow-six-stats.api}")
    private String api;
    @Value("${yuri.plugins.rainbow-six-stats.msgMatch}")
    private String msgMatch;

    /**
     * 战绩查询方法
     * @param gameUserName 游戏用户名
     */
    public void getRainbowSixStats(String gameUserName) {
        String result = HttpClientUtil.httpGetWithJson(api+gameUserName);
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
        for (StatCR statCR : r6S.getStatCR()) {
            if ("ranked".equals(statCR.getModel())) {
                msg.text("\n\n【排名战数据】")
                        .text("\n胜场:"+statCR.getWon()+"  "+"败场:"+statCR.getLost()+"  "+"胜率:"+ CommonUtils.formatDouble(((double) statCR.getWon() / (double) statCR.getLost()))+"%"+"  "+"游戏场次:"+statCR.getPlayed())
                        .text("\n击杀:"+statCR.getKills()+"  "+"阵亡:"+statCR.getDeaths()+"  "+"KD:"+ CommonUtils.formatDouble(((double) statCR.getKills() / (double) statCR.getDeaths()))+"%");
            }
            if ("casual".equals(statCR.getModel())) {
                msg.text("\n\n【常规战数据】")
                        .text("\n胜场:"+statCR.getWon()+"  "+"败场:"+statCR.getLost()+"  "+"胜率:"+ CommonUtils.formatDouble(((double) statCR.getWon() / (double) statCR.getLost()))+"%"+"  "+"游戏场次:"+statCR.getPlayed())
                        .text("\n击杀:"+statCR.getKills()+"  "+"阵亡:"+statCR.getDeaths()+"  "+"KD:"+ CommonUtils.formatDouble(((double) statCR.getKills() / (double) statCR.getDeaths()))+"%");
            }
        }
        for (StatBHS statBHS : r6S.getStatBHS()) {
            // 肃清威胁
            if ("secureareapvp".equals(statBHS.getModel())) {
                msg.text("\n\n【模式数据-肃清威胁】")
                        .text("\n胜场:"+statBHS.getWon()+"  "+"败场:"+statBHS.getLost()+"  "+"总场次:"+statBHS.getPlayed()+"  "+"最高得分:"+statBHS.getBestScore());
            }
            // 炸弹
            if ("plantbombpvp".equals(statBHS.getModel())) {
                msg.text("\n\n【模式数据-炸弹模式】")
                        .text("\n胜场:"+statBHS.getWon()+"  "+"败场:"+statBHS.getLost()+"  "+"总场次:"+statBHS.getPlayed()+"  "+"最高得分:"+statBHS.getBestScore());

            }
            // 人质
            if ("rescuehostagepvp".equals(statBHS.getModel())) {
                msg.text("\n\n【模式数据-人质模式】")
                        .text("\n胜场:"+statBHS.getWon()+"  "+"败场:"+statBHS.getLost()+"  "+"总场次:"+statBHS.getPlayed()+"  "+"最高得分:"+statBHS.getBestScore());

            }
        }
        return msg;
    }

    @Override
    public int onGroupMessage(@NotNull Bot bot, @NotNull OnebotEvent.GroupMessageEvent event) {
        String msg = event.getRawMessage();
        if (msg.matches(msgMatch)){
            long groupId = event.getGroupId();
            long userId = event.getUserId();
            String gameUserId;
            try {
                gameUserId = URLEncoder.encode(msg.replaceAll("^[查获][询取]", "").replaceAll("[战数][绩据]-[Rr彩][6六]亚服", ""), "utf-8");
            } catch (UnsupportedEncodingException e) {
                gameUserId = msg.replaceAll("^[查获][询取]", "").replaceAll("[战数][绩据]-[Rr彩][6六]亚服", "");
                log.info("彩虹六号战绩查询gameUserId URLEncoder异常 [{}]",e);
            }
            try {
                bot.sendGroupMsg(groupId,Msg.builder().at(userId).text(gameUserId+"数据查询中，请稍后~").build(),false);
                getRainbowSixStats(gameUserId);
                getDataAndBuilder();
                bot.sendGroupMsg(groupId,getDataAndBuilder().text("\n\n").at(userId).build(),false);
            } catch (Exception e) {
                bot.sendGroupMsg(groupId,Msg.builder().at(userId).text(gameUserId+"游戏数据查询失败，请稍后重试~").build(),false);
            }
        }
        return MESSAGE_IGNORE;
    }

    @Override
    public int onPrivateMessage(@NotNull Bot bot, @NotNull OnebotEvent.PrivateMessageEvent event) {
        String msg = event.getRawMessage();
        if (msg.matches(msgMatch)){
            long userId = event.getUserId();
            String gameUserId;
            try {
                gameUserId = URLEncoder.encode(msg.replaceAll("^[查获][询取]", "").replaceAll("[战数][绩据]-[Rr彩][6六]亚服", ""), "utf-8");
            } catch (UnsupportedEncodingException e) {
                gameUserId = msg.replaceAll("^[查获][询取]", "").replaceAll("[战数][绩据]-[Rr彩][6六]亚服", "");
                log.info("彩虹六号战绩查询gameUserId URLEncoder异常 [{}]",e);
            }
            try {
                bot.sendPrivateMsg(userId,Msg.builder().at(userId).text(gameUserId+"数据查询中，请稍后~").build(),false);
                getRainbowSixStats(gameUserId);
                getDataAndBuilder();
                bot.sendPrivateMsg(userId,getDataAndBuilder().build(),false);
            } catch (Exception e) {
                bot.sendPrivateMsg(userId,Msg.builder().at(userId).text(gameUserId+"游戏数据查询失败，请稍后重试~").build(),false);
            }
        }
        return MESSAGE_IGNORE;
    }

}