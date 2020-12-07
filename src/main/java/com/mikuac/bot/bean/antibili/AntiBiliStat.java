package com.mikuac.bot.bean.antibili;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

/**
 * @author Zero
 * @date 2020/12/7 15:36
 */
@Data
public class AntiBiliStat {

    private int aid;

    private int view;

    private int danmaku;

    private int reply;

    private int favorite;

    private int coin;

    private int share;

    @JSONField(name = "now_rank")
    private int nowRank;

    @JSONField(name = "his_rank")
    private int hisRank;

    private int like;

}
