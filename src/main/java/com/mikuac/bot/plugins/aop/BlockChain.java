package com.mikuac.bot.plugins.aop;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.mikuac.bot.config.ApiConst;
import com.mikuac.bot.config.RegexConst;
import com.mikuac.bot.utils.CommonUtils;
import com.mikuac.bot.utils.HttpClientUtils;
import com.mikuac.bot.utils.RegexUtils;
import net.lz1998.pbbot.bot.Bot;
import net.lz1998.pbbot.bot.BotPlugin;
import net.lz1998.pbbot.utils.Msg;
import onebot.OnebotEvent;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

/**
 * @author Zero
 * @date 2021/1/27 12:34
 */
@Component
public class BlockChain extends BotPlugin {

    private final static String TO_TYPE = "USDT";

    String symbol;
    double price;
    double buy;
    double sell;
    String coinName;
    String bcType;
    String buyPrice;
    String sellPrice;

    public void getPrice(String type) {
        String searchType = type.toUpperCase() + TO_TYPE;
        String result = HttpClientUtils.httpGetWithJson(ApiConst.BIANCE_USDT_API + searchType, false);
        JSONObject jsonObject = JSONObject.parseObject(result);
        symbol = jsonObject.getString("symbol");
        price = Double.parseDouble(jsonObject.getString("price").replaceAll("0+?$", ""));
    }

    public void toCny() {
        String result = HttpClientUtils.httpGetWithJson(ApiConst.BIANCE_MARKET_API, false);
        JSONObject jsonObject = JSONObject.parseObject(result);
        JSONArray jsonArray = jsonObject.getJSONObject("data").getJSONArray("detail");
        for (Object obj : jsonArray) {
            JSONObject jb = JSONObject.parseObject(String.valueOf(obj));
            String cname = jb.getString("coinName");
            if (TO_TYPE.equals(cname)) {
                coinName = cname;
                buy = Double.parseDouble(jb.getString("buy"));
                sell = Double.parseDouble(jb.getString("sell"));
                break;
            }
        }
    }

    public void exchange(String number) {
        double n = Double.parseDouble(number);
        buyPrice = CommonUtils.formatDouble(n * price * buy);
        sellPrice = CommonUtils.formatDouble(n * price * sell);
    }

    public Msg builderMsg(Boolean exIsNull, Boolean isGroupMsg, long uerId) {
        Msg msg = Msg.builder();
        if (isGroupMsg) {
            msg.at(uerId);
            msg.text("\n");
        }
        if (exIsNull) {
            msg.text("Symbol：" + symbol);
            msg.text("\nPrice：" + price);
            msg.text("\n" + coinName + " Buy：" + buy);
            msg.text("\n" + coinName + " Sell：" + sell);
            String toCnyPrice = CommonUtils.formatDouble(price * ((buy + sell) / 2));
            msg.text("\n" + bcType.toUpperCase() + " To CNY：" + toCnyPrice);
        } else {
            msg.text("Buy：" + buyPrice);
            msg.text("\nSell：" + sellPrice);
        }
        return msg;
    }

    @Override
    public int onGroupMessage(@NotNull Bot bot, @NotNull OnebotEvent.GroupMessageEvent event) {
        String msg = event.getRawMessage();
        if (msg.matches(RegexConst.BIANCE_PRICE)) {
            long groupId = event.getGroupId();
            long userId = event.getUserId();
            bot.sendGroupMsg(groupId, Msg.builder().at(userId).text("币价查询中，请稍后~").build(), false);
            bcType = RegexUtils.regexGroup(RegexConst.BIANCE_PRICE, msg, 1);
            String number = RegexUtils.regexGroup(RegexConst.BIANCE_PRICE, msg, 2);
            if (bcType != null && !bcType.isEmpty()) {
                try {
                    getPrice(bcType);
                    toCny();
                    if (number != null && !number.isEmpty()) {
                        exchange(number);
                        bot.sendGroupMsg(groupId, builderMsg(false, true, userId).build(), false);
                    } else {
                        bot.sendGroupMsg(groupId, builderMsg(true, true, userId).build(), false);
                    }
                } catch (Exception e) {
                    bot.sendGroupMsg(groupId, Msg.builder().at(userId).text("查询失败，可能是货币类型输入错误，请检查后重试~").build(), false);
                }
            }
        }
        return MESSAGE_IGNORE;
    }

    @Override
    public int onPrivateMessage(@NotNull Bot bot, @NotNull OnebotEvent.PrivateMessageEvent event) {
        String msg = event.getRawMessage();
        if (msg.matches(RegexConst.BIANCE_PRICE)) {
            long userId = event.getUserId();
            bot.sendGroupMsg(userId, "币价查询中，请稍后~", false);
            String bcType = RegexUtils.regexGroup(RegexConst.BIANCE_PRICE, msg, 1);
            String number = RegexUtils.regexGroup(RegexConst.BIANCE_PRICE, msg, 2);
            if (bcType != null && !bcType.isEmpty()) {
                try {
                    getPrice(bcType);
                    toCny();
                    if (number != null && !number.isEmpty()) {
                        exchange(number);
                        bot.sendPrivateMsg(userId, builderMsg(false, false, 0L).build(), false);
                    } else {
                        bot.sendPrivateMsg(userId, builderMsg(true, false, 0L).build(), false);
                    }
                } catch (Exception e) {
                    bot.sendPrivateMsg(userId, "查询失败，可能是货币类型输入错误，请检查后重试~", false);
                }
            }
        }
        return MESSAGE_IGNORE;
    }

}
