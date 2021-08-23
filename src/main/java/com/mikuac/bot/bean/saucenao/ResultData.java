package com.mikuac.bot.bean.saucenao;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.util.List;

/**
 * @author Zero
 * @date 2020/12/3 14:35
 */
@Data
public class ResultData {

    @JSONField(name = "ext_urls")
    private List<String> extUrls;

    private String title;

    private String source;

    @JSONField(name = "pixiv_id")
    private String pixivId;

    @JSONField(name = "member_name")
    private String memberName;

    @JSONField(name = "member_id")
    private String memberId;

    @JSONField(name = "eng_name")
    private String engName;

    @JSONField(name = "jp_name")
    private String jpName;

    @JSONField(name = "tweet_id")
    private String tweetId;

    @JSONField(name = "twitter_user_id")
    private String twitterUserId;

    @JSONField(name = "twitter_user_handle")
    private String twitterUserHandle;

}
