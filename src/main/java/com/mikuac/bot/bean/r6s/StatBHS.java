package com.mikuac.bot.bean.r6s;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

/**
 * 模式数据
 * @author Zero
 * @date 2020/11/4 23:00
 */
@Data
public class StatBHS {

    /**
     * 最高分
     */
    @JSONField(name = "bestscore")
    private int bestScore;

    /**
     * 败场
     */
    private int lost;

    /**
     * 胜场
     */
    private int won;

    /**
     * 模式
     */
    private String model;

    /**
     * 总场数
     */
    private int played;

}