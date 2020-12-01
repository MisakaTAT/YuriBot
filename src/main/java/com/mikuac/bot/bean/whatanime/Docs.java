package com.mikuac.bot.bean.whatanime;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.springframework.stereotype.Component;
import java.util.Date;
import java.util.List;

/**
 * @author Zero
 * @date 2020/12/1 9:50
 */
@Data
@Component
public class Docs {

    private double from;

    private double to;

    @JsonProperty("anilist_id")
    private int aniListId;

    private double at;

    private String season;

    private String anime;

    private String filename;

    private int episode;

    @JsonProperty("tokenthumb")
    private String tokenThumb;

    private double similarity;

    private String title;

    @JsonProperty("title_native")
    private String titleNative;

    @JsonProperty("title_chinese")
    private String titleChinese;

    @JsonProperty("title_english")
    private String titleEnglish;

    @JsonProperty("title_romaji")
    private String titleRomaji;

    @JsonProperty("mal_id")
    private int malId;

    private List<String> synonyms;

    @JsonProperty("synonyms_chinese")
    private List<String> synonymsChinese;

    @JsonProperty("is_adult")
    private boolean isAdult;

}