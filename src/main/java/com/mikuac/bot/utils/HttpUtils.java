package com.mikuac.bot.utils;

import okhttp3.*;

import java.io.IOException;
import java.util.Objects;

/**
 * @author Zero
 */
public class HttpUtils {

    private static final MediaType MEDIA_TYPE = MediaType.Companion.parse("application/json;charset=utf-8");
    private static final OkHttpClient CLIENT = new OkHttpClient();

    public static String post(String url, String json) throws IOException {
        RequestBody stringBody = RequestBody.Companion.create(json, MEDIA_TYPE);
        Request request = new Request
                .Builder()
                .url(url)
                .post(stringBody)
                .build();
        return Objects.requireNonNull(CLIENT.newCall(request).execute().body()).string();
    }

}
