package com.mikuac.bot.bean.antibili;

import lombok.Data;

/**
 * @author Zero
 * @date 2020/12/7 15:38
 */
@Data
public class AntiBiliData {

    private String bvid;

    private int aid;

    private String pic;

    private String title;

    private AntiBiliOwner owner;

    private AntiBiliStat stat;

}
