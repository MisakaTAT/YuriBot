package com.mikuac.bot.bean.r6s;

import lombok.Data;

/**
 * 干员数据
 * @author Zero
 * @date 2020/11/4 23:00
 */
@Data
public class StatOperator {

    /**
     * 击杀数
     */
    private int kills;

    /**
     * 败场
     */
    private int lost;

    /**
     * 胜场
     */
    private int won;

    /**
     * 干员名
     */
    private String name;

    /**
     * 死亡数
     */
    private int deaths;

}