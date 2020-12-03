package com.mikuac.bot.bean.r6s;

import lombok.Data;

/**
 * 综合数据
 * @author Zero
 * @date 2020/11/4 23:00
 */
@Data
public class StatGeneral {

    /**
     * 助攻
     */
    private int killAssists;

    /**
     * 总击杀
     */
    private int kills;

    /**
     * 近战击杀
     */
    private int meleeKills;

    /**
     * 总开火次数
     */
    private int bulletsFired;

    /**
     * 穿透击杀
     */
    private int penetrationKills;

    /**
     * 总游戏场次
     */
    private int played;

    /**
     * 救助
     */
    private int revives;

    /**
     * 爆头击杀
     */
    private int headShot;

    /**
     * 总败场
     */
    private int lost;

    /**
     * 子弹命中次数
     */
    private int bulletsHit;

    /**
     * 总胜场
     */
    private int won;

    /**
     * 死亡次数
     */
    private int deaths;

}