package com.mikuac.bot.bean.saucenao;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

/**
 * @author Zero
 * @date 2020/12/3 13:33
 */
@Data
public class Header {

    @JSONField(name = "user_id")
    private String userId;

    @JSONField(name = "account_type")
    private String accountType;

    @JSONField(name = "short_limit")
    private String shortLimit;

    @JSONField(name = "long_limit")
    private String longLimit;

    @JSONField(name = "long_remaining")
    private int longRemaining;

    @JSONField(name = "short_remaining")
    private int shortRemaining;

    private int status;

    @JSONField(name = "results_requested")
    private int resultsRequested;

    @JSONField(name = "search_depth")
    private String searchDepth;

    @JSONField(name = "minimum_similarity")
    private double minimumSimilarity;

    @JSONField(name = "query_image_display")
    private String queryImageDisplay;

    @JSONField(name = "query_image")
    private String queryImage;

    @JSONField(name = "results_returned")
    private int resultsReturned;

}
