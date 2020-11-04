package com.mikuac.bot.bean.r6s;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * R6S战绩实体类
 * @author Zero
 * @date 2020/11/5 24:00
 */
@Data
@Component
public class BasicStat {

    private int wins;
    @JsonProperty("skill_stdev")
    private double skillStdev;
    @JsonProperty("max_mmr")
    private int maxMmr;
    private int level;
    private int abandons;
    @JsonProperty("top_rank_position")
    private int topRankPosition;
    private int losses;
    private String platform;
    private int mmr;
    @JsonProperty("updated_at")
    private Date updatedAt;
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