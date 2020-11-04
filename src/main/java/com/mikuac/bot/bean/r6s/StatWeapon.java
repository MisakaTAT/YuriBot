package com.mikuac.bot.bean.r6s;

import lombok.Data;
import org.springframework.stereotype.Component;

/**
 * R6S战绩实体类
 * @author Zero
 * @date 2020/11/5 24:00
 */
@Data
@Component
public class StatWeapon {

    private int kills;
    private int headshot;
    private String id;
    private int bullethit;
    private String type;
    private int bulletfired;
    private String userid;

}