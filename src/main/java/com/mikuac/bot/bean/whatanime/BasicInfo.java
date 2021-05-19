package com.mikuac.bot.bean.whatanime;

import lombok.Data;
import java.util.List;

/**
 * @author Zero
 */
@Data
public class BasicInfo {

    private long frameCount;

    private String error;

    private List<Result> result;

    @Data
    public static class Result {
        private long anilist;
        private String filename;
        private int episode;
        private double from;
        private double to;
        private double similarity;
        private String video;
        private String image;
    }

}
