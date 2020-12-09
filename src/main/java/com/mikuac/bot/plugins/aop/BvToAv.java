package com.mikuac.bot.plugins.aop;

import net.lz1998.pbbot.bot.Bot;
import net.lz1998.pbbot.bot.BotPlugin;
import onebot.OnebotEvent;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 算法实现av bv互转
 * @author Zero
 * @date 2020/12/9 11:46
 */
@Component
public class BvToAv extends BotPlugin {

    // 算法来自知乎mcfx的回答
    // https://www.zhihu.com/question/381784377/answer/1099438784

    String table = "fZodR9XQDSUm21yCkr6zBqiveYah8bt4xsWpHnJE7jL5VG3guMTKNPAwcF";

    int[] s = {11, 10, 3, 8, 4, 6};
    long xor = 177451812L;
    long add = 8728348608L;

    static Map<String,Integer> bv2avMap = new ConcurrentHashMap<>();
    static Map<Integer,String> av2bvMap = new ConcurrentHashMap<>();

    public String bv2av (String bvId) {
        long r = 0;
        for (int i = 0; i < 58; i++) {
            bv2avMap.put(String.valueOf(table.charAt(i)),i);
        }
        for (int i = 0; i < 6; i++) {
            r += bv2avMap.get(bvId.substring(s[i], s[i] + 1)) * Math.pow(58,i);
        }
        return "av" + ((r - add) ^ xor);
    }

    public String av2bv (String avId) {
        long aid = Long.parseLong(avId.split("av")[1]);
        StringBuilder stringBuilder = new StringBuilder("BV1  4 1 7  ");
        aid = (aid ^ xor) + add;
        for (int i = 0; i < 58; i++) {
            av2bvMap.put(i,String.valueOf(table.charAt(i)));
        }
        for (int i = 0; i < 6; i++) {
            String r = av2bvMap.get((int)(aid / Math.pow(58,i) % 58));
            stringBuilder.replace(s[i], s[i] + 1, r);
        }
        return stringBuilder.toString();
    }

}
