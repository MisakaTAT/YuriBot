package com.mikuac.bot.bean.whatanime;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.springframework.stereotype.Component;
import java.util.List;

/**
 * @author Zero
 * @date 2020/12/1 9:48
 */
@Data
@Component
public class BasicData {

    @JsonProperty("RawDocsCount")
    private long rawDocsCount;

    @JsonProperty("RawDocsSearchTime")
    private long rawDocsSearchTime;

    @JsonProperty("ReRankSearchTime")
    private long reRankSearchTime;

    @JsonProperty("CacheHit")
    private boolean cacheHit;

    private int trial;

    private List<Docs> docs;

    private int limit;

    @JsonProperty("limit_ttl")
    private int limitTtl;

    private int quota;

    @JsonProperty("quota_ttl")
    private int quotaTtl;

}