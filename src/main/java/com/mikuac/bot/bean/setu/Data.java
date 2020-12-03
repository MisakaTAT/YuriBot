package com.mikuac.bot.bean.setu;

import java.util.List;

/**
 * @author Zero
 * @date 2020/11/9 14:33
 */
@lombok.Data
public class Data {

    private long pid;

    private int p;

    private long uid;

    private String title;

    private String author;

    private String url;

    private boolean r18;

    private int width;

    private int height;

    private List<String> tags;

}