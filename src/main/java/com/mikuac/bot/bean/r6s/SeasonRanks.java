package com.mikuac.bot.bean.r6s;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.checkerframework.checker.units.qual.C;
import org.springframework.stereotype.Component;

/**
 * R6S战绩实体类
 * @author Zero
 * @date 2020/11/5 24:00
 */
@Data
@Component
public class SeasonRanks {

    private int wins;
    @JsonProperty("skill_stdev")
    private double skillStdev;
    @JsonProperty("max_mmr")
    private int maxMmr;
    private int abandons;
    private int losses;
    private int mmr;
    @JsonProperty("max_rank")
    private int maxRank;
    @JsonProperty("user_id")
    private String userId;
    private int rank;
    private int season;
    private String id;
    private String region;
    @JsonProperty("skill_mean")
    private double skillMean;

}