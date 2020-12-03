package com.mikuac.bot.bean.saucenao;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

/**
 * @author Zero
 * @date 2020/12/3 14:33
 */
@Data
public class ResultHeader {

    private String similarity;

    private String thumbnail;

    @JSONField(name = "index_id")
    private int indexId;

    @JSONField(name = "index_name")
    private String indexName;

    private int dupes;

}
