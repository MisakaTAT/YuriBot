package com.mikuac.bot.bean.r6s;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.springframework.stereotype.Component;

/**
 * R6S战绩实体类
 * @author Zero
 * @date 2020/11/4 23:00
 */
@Data
@Component
public class R6S {

    /**
     * 响应状态码
     */
    private int status;

    /**
     * 游戏名
     */
    private String username;

    /**
     * 当前赛季基本数据
     */
    @JsonProperty("Basicstat")
    private List<BasicStat> basicStat;

    /**
     * 历史赛季排位数据
     */
    @JsonProperty("SeasonRanks")
    private List<SeasonRanks> seasonRanks;

    /**
     * 常规战 & 排名战总数据
     */
    @JsonProperty("StatCR")
    private List<StatCR> statCR;

    /**
     * 模式数据数据
     */
    @JsonProperty("StatBHS")
    private List<StatBHS> statBHS;

    /**
     * 武器数据
     */
    @JsonProperty("StatWeapon")
    private List<StatWeapon> statWeapon;

    /**
     * 综合数据
     */
    @JsonProperty("StatGeneral")
    private List<StatGeneral> statGeneral;

    /**
     * 干员数据
     */
    @JsonProperty("StatOperator")
    private List<StatOperator> statOperator;

}