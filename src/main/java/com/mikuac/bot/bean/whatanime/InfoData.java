package com.mikuac.bot.bean.whatanime;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.springframework.stereotype.Component;

/**
 * @author Zero
 * @date 2020/12/1 10:15
 */
@Data
@Component
public class InfoData {

    private int id;

    private int idMal;

    private String type;

    private String format;

    private String status;

    private String description;

    private Date startDate;

    private Date endDate;

    private String season;

    private int episodes;

    private int duration;

    private String source;

    private int updatedAt;

    private String bannerImage;

    @JsonProperty("coverImage")
    private CoverImage coverImage;

}