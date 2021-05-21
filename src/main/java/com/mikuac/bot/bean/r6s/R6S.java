package com.mikuac.bot.bean.r6s;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * R6S战绩实体类
 *
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
    @JSONField(name = "Basicstat")
    private List<BasicStat> basicStat;

    /**
     * 历史赛季排位数据
     */
    @JSONField(name = "SeasonRanks")
    private List<SeasonRanks> seasonRanks;

    /**
     * 常规战 & 排名战总数据
     */
    @JSONField(name = "StatCR")
    private List<StatCR> statCR;

    /**
     * 模式数据数据
     */
    @JSONField(name = "StatBHS")
    private List<StatBHS> statBHS;

    /**
     * 武器数据
     */
    @JSONField(name = "StatWeapon")
    private List<StatWeapon> statWeapon;

    /**
     * 综合数据
     */
    @JSONField(name = "StatGeneral")
    private List<StatGeneral> statGeneral;

    /**
     * 干员数据
     */
    @JSONField(name = "StatOperator")
    private List<StatOperator> statOperator;

}