package com.mikuac.bot.common.utils;

import org.springframework.stereotype.Component;

import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

/**
 * Created on 2021/5/26.
 *
 * @author Zero
 */
@Component
public class FileUtils {

    public static String readFile(String file) {
        try {
            InputStreamReader isr = new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8);
            int ch = 0;
            StringBuilder sb = new StringBuilder();
            while ((ch = isr.read()) != -1) {
                sb.append((char) ch);
            }
            isr.close();
            return sb.toString();
        } catch (Exception e) {
            return null;
        }

    }

}
