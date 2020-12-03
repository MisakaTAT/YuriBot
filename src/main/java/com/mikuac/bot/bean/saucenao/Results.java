package com.mikuac.bot.bean.saucenao;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

/**
 * @author Zero
 * @date 2020/12/3 13:37
 */
@Data
public class Results {

    @JSONField(name = "header")
    private ResultHeader resultHeader;

    @JSONField(name = "data")
    private ResultData resultData;

}
