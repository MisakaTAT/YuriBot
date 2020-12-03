package com.mikuac.bot.bean.whatanime;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;
import java.util.List;

/**
 * @author Zero
 * @date 2020/12/1 9:50
 */
@Data
public class Docs {

    private double from;

    private double to;

    @JSONField(name = "anilist_id")
    private int aniListId;

    private double at;

    private String season;

    private String anime;

    private String filename;

    private int episode;

    @JSONField(name = "tokenthumb")
    private String tokenThumb;

    private double similarity;

    private String title;

    @JSONField(name = "title_native")
    private String titleNative;

    @JSONField(name = "title_chinese")
    private String titleChinese;

    @JSONField(name = "title_english")
    private String titleEnglish;

    @JSONField(name = "title_romaji")
    private String titleRomaji;

    @JSONField(name = "mal_id")
    private int malId;

    private List<String> synonyms;

    @JSONField(name = "synonyms_chinese")
    private List<String> synonymsChinese;

    @JSONField(name = "is_adult")
    private boolean isAdult;

}