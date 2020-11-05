package com.mikuac.bot.bean.r6s;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.springframework.stereotype.Component;

/**
 * 模式数据
 * @author Zero
 * @date 2020/11/4 23:00
 */
@Data
@Component
public class StatBHS {

    /**
     * 最高分
     */
    @JsonProperty("bestscore")
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