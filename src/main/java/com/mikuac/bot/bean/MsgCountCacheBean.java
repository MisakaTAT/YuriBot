package com.mikuac.bot.bean;

import lombok.Data;

/**
 * Created on 2021/7/30.
 *
 * @author Zero
 */
@Data
public class MsgCountCacheBean {

    private Long groupId;

    private Long userId;

    private Integer count;

}
