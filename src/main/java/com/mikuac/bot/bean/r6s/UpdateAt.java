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
public class UpdateAt {

    private int date;
    private int hours;
    private int seconds;
    private int month;
    private int nanos;
    @JsonProperty("timezoneOffset")
    private int timezoneoffset;
    private int year;
    private int minutes;
    private int time;
    private int day;

}