package com.mikuac.bot.bean;

import lombok.Data;

/**
 * @author Zero
 * @date 2020/12/1 15:23
 */
@Data
public class SearchBean {

    /**
     * 群组为UserId加GroupId
     * 私聊为UserId
     */
    private Long key;

    private Long groupId;

    private Long userId;

    private Boolean enable;

    private Long startTime;

    private String msgType;

}
