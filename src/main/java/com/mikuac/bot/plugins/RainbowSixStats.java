package com.mikuac.bot.plugins;

import com.alibaba.fastjson.JSON;
import com.mikuac.bot.bean.r6s.BasicStat;
import com.mikuac.bot.bean.r6s.R6S;
import com.mikuac.bot.utils.HttpClientUtil;
import lombok.extern.slf4j.Slf4j;
import net.lz1998.pbbot.bot.Bot;
import net.lz1998.pbbot.bot.BotPlugin;
import net.lz1998.pbbot.utils.Msg;
import onebot.OnebotEvent;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 彩虹六号战绩查询
 * @author Zero
 * @date 2020/11/4 22:01
 */
@Slf4j
@Component
public class RainbowSixStats extends BotPlugin {

    @Value("${yuri.plugins.rainbow-six-stats.api}")
    private String api;
    @Value("${yuri.plugins.rainbow-six-stats.msgMatch}")
    private String msgMatch;

    private int rankImg;
    /**
     * 战绩查询方法
     * @param gameUserName 游戏用户名
     */
    public void getRainbowSixStats(String gameUserName) {
        String result = HttpClientUtil.httpGetWithJson(api+gameUserName);
        System.out.println(result);
        R6S r6s = JSON.parseObject(result, R6S.class);
        for (BasicStat basicstat : r6s.getBasicStat()) {
            if ("apac".equals(basicstat.getRegion())) {
                rankImg = basicstat.getRank();
                System.out.println(rankImg);
            }
        }
    }

    @Override
    public int onGroupMessage(@NotNull Bot bot, @NotNull OnebotEvent.GroupMessageEvent event) {
        String msg = event.getRawMessage();
        long groupId = event.getGroupId();
        long userId = event.getUserId();

        if ("GET".equals(msg)){
            getRainbowSixStats("MisakaTAT");
            Msg msgBuilder = Msg.builder()
                    .at(userId)
                    .image("https://s1.ax1x.com/2020/11/05/B23QW8.png");
            System.out.println("https://www.r6s.cn/NewRanksSvg/r"+rankImg+".svg");
            bot.sendGroupMsg(groupId,msgBuilder.build(),false);
        }

        return MESSAGE_IGNORE;
    }

}
