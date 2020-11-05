package com.mikuac.bot.bean.r6s;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.springframework.stereotype.Component;

/**
 * 赛季排位数据
 * @author Zero
 * @date 2020/11/4 23:00
 */
@Data
@Component
public class SeasonRanks {

    /**
     * 胜场
     */
    private int wins;

    /**
     * 最高MMR
     */
    @JsonProperty("max_mmr")
    private int maxMmr;

    /**
     * 弃赛场次
     */
    private int abandons;

    /**
     * 败场
     */
    private int losses;

    /**
     * 最终MMR
     */
    private int mmr;

    /**
     * 最高段位（取图标用）
     */
    @JsonProperty("max_rank")
    private int maxRank;

    /**
     * 最终段位（取图标用）
     */
    private int rank;

    /**
     * 赛季
     */
    private int season;

    /**
     * 区域
     */
    private String region;

}