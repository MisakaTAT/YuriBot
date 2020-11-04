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
public class StatGeneral {

    @JsonProperty("killAssists")
    private int killassists;
    private int kills;
    @JsonProperty("meleeKills")
    private int meleekills;
    @JsonProperty("bulletsFired")
    private int bulletsfired;
    @JsonProperty("penetrationKills")
    private int penetrationkills;
    private int played;
    private int revives;
    @JsonProperty("timePlayed")
    private int timeplayed;
    @JsonProperty("user_id")
    private String userId;
    private int headshot;
    private int lost;
    @JsonProperty("bulletsHit")
    private int bulletshit;
    private int won;
    @JsonProperty("update_at")
    private UpdateAt updateAt;
    private String id;
    private int deaths;

}