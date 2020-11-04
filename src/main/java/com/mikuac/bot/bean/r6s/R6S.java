package com.mikuac.bot.bean.r6s;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.springframework.stereotype.Component;

/**
 * R6S战绩实体类
 * @author Zero
 * @date 2020/11/5 24:00
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
     * 基本战绩
     */
    @JsonProperty("Basicstat")
    private List<BasicStat> basicStat;

    /**
     * 赛季排位数据
     */
    @JsonProperty("SeasonRanks")
    private List<SeasonRanks> seasonRanks;

    /**
     * 常规战数据
     */
    @JsonProperty("StatCR")
    private List<StatCR> statCr;

    /**
     * 近日概况
     */
    @JsonProperty("StatCR2")
    private List<StatCR2> statCr2;

    /**
     * 模式数据数据
     */
    @JsonProperty("StatBHS")
    private List<StatBHS> statBhs;

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