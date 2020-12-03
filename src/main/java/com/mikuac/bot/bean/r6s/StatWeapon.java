package com.mikuac.bot.bean.r6s;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

/**
 * 武器数据
 * @author Zero
 * @date 2020/11/5 24:00
 */
@Data
public class StatWeapon {

    /**
     * 击杀
     */
    private int kills;

    /**
     * 爆头击杀
     */
    @JSONField(name = "headshot")
    private int headShot;

    /**
     * 子弹命中次数
     */
    @JSONField(name = "bullethit")
    private int bulletHit;

    /**
     * 武器名
     */
    private String type;

    /**
     * 开火次数
     */
    @JSONField(name = "bulletfired")
    private int bulletFired;

}