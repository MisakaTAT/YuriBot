package com.mikuac.bot.bean.antibili;

import lombok.Data;
import org.springframework.stereotype.Component;

/**
 * @author Zero
 * @date 2020/12/7 15:35
 */

@Data
@Component
public class AntiBiliBean {

    private int code;

    private String message;

    private int ttl;

    private AntiBiliData data;

}
