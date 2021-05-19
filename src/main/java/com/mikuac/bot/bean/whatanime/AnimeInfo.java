package com.mikuac.bot.bean.whatanime;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.util.List;

/**
 * @author Zero
 */
@Data
public class AnimeInfo {

    @JSONField(name = "Data")
    private GetData getData;

    @Data
    public static class StartDate {
        private int year;
        private int month;
        private int day;
    }

    @Data
    public static class EndDate {
        private int year;
        private int month;
        private int day;
    }

    @Data
    public static class CoverImage {
        private String large;
    }

    @Data
    public static class Media {
        private Long id;
        private String type;
        private String format;
        private String status;
        private String season;
        private List<String> synonyms;
        private String episodes;
        private StartDate startDate;
        private EndDate endDate;
        private CoverImage coverImage;
        private Title title;
    }

    @Data
    public static class GetData {
        @JSONField(name = "Media")
        private Media media;
    }

    @Data
    public static class Title {
        @JSONField(name = "native")
        private String nativeName;
        private String romaji;
        private String english;
        private String chinese;
    }

}
