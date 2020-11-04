package com.mikuac.bot.bean.r6s;

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
public class StatCR2 {

    private int kills;
    @JsonProperty("timePlayed")
    private int timeplayed;
    private int mmr;
    @JsonProperty("user_id")
    private String userId;
    private int lost;
    private int won;
    @JsonProperty("update_at")
    private UpdateAt updateAt;
    private String model;
    private String id;
    private int played;
    private int deaths;

}