package com.mikuac.bot.bean.whatanime;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;
import java.util.List;

/**
 * @author Zero
 * @date 2020/12/1 9:48
 */
@Data
public class BasicData {

    @JSONField(name = "RawDocsCount")
    private long rawDocsCount;

    @JSONField(name = "RawDocsSearchTime")
    private long rawDocsSearchTime;

    @JSONField(name = "ReRankSearchTime")
    private long reRankSearchTime;

    @JSONField(name = "CacheHit")
    private boolean cacheHit;

    private int trial;

    private List<Docs> docs;

    private int limit;

    @JSONField(name = "limit_ttl")
    private int limitTtl;

    private int quota;

    @JSONField(name = "quota_ttl")
    private int quotaTtl;

}