package com.mikuac.bot.bean.setu;

import java.util.List;
import org.springframework.stereotype.Component;

/**
 * @author Zero
 * @date 2020/11/9 14:33
 */
@lombok.Data
@Component
public class SetuBean {

    private int code;

    private String msg;

    private int quota;

    private int quota_min_ttl;

    private int count;

    private List<Data> data;

}
