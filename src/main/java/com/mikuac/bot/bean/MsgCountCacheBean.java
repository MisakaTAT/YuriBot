package com.mikuac.bot.bean;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.util.List;

@Data
public class MsgCountCacheBean {

    @JSONField(name = "data")
    private List<CacheData> cacheData;

    @Data
    public static class CacheData {
        private String groupId;
        private String userId;
    }

}


