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
public class StatBHS {

    private int bestscore;
    @JsonProperty("user_id")
    private String userId;
    private int lost;
    private int won;
    private String model;
    private String id;
    private int played;

}