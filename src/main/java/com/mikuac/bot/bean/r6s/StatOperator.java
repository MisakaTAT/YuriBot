package com.mikuac.bot.bean.r6s;

import lombok.Data;
import org.springframework.stereotype.Component;

/**
 * 干员数据
 * @author Zero
 * @date 2020/11/4 23:00
 */
@Data
@Component
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