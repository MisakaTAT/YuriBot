package com.mikuac.bot.bean.r6s;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * 当前赛季基本数据
 *
 * @author Zero
 * @date 2020/11/4 23:00
 */
@Data
@Component
public class BasicStat {

    /**
     * 胜场
     */
    private int wins;

    /**
     * 最高MMR
     */
    @JSONField(name = "max_mmr")
    private int maxMmr;

    /**
     * 等级
     */
    private int level;

    /**
     * 弃赛
     */
    private int abandons;


    @JSONField(name = "top_rank_position")
    private int topRankPosition;

    /**
     * 败场
     */
    private int losses;

    /**
     * 平台
     */
    private String platform;

    /**
     * 当前MMR
     */
    private int mmr;

    /**
     * 数据更新时间
     */
    @JSONField(name = "updated_at")
    private Date updatedAt;

    /**
     * 最高段位数值（方便取段位图标）
     */
    @JSONField(name = "max_rank")
    private int maxRank;

    /**
     * 当前段位数值（方便取段位图标）
     */
    private int rank;

    /**
     * 当前赛季
     */
    private int season;

    /**
     * 区服
     */
    private String region;

}