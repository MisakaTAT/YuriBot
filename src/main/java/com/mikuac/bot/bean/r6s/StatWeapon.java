package com.mikuac.bot.bean.r6s;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.springframework.stereotype.Component;

/**
 * 武器数据
 * @author Zero
 * @date 2020/11/5 24:00
 */
@Data
@Component
public class StatWeapon {

    /**
     * 击杀
     */
    private int kills;

    /**
     * 爆头击杀
     */
    @JsonProperty("headshot")
    private int headShot;

    /**
     * 子弹命中次数
     */
    @JsonProperty("bullethit")
    private int bulletHit;

    /**
     * 武器名
     */
    private String type;

    /**
     * 开火次数
     */
    @JsonProperty("bulletfired")
    private int bulletFired;

}