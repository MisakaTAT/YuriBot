package com.mikuac.bot.bean.r6s;

import lombok.Data;

/**
 * 常规战 & 排名战总数据
 * @author Zero
 * @date 2020/11/3 23:00
 */
@Data
public class StatCR {

    /**
     * 击杀数
     */
    private int kills;

    /**
     * 游玩时长
     */
    private int timePlayed;

    /**
     * 败场
     */
    private int lost;

    /**
     * 胜场
     */
    private int won;

    /**
     * 游戏模式（casual，ranked）
     */
    private String model;

    /**
     * 总场次
     */
    private int played;

    /**
     * 死亡次数
     */
    private int deaths;

}