package com.mikuac.bot.config;

import com.mikuac.bot.bean.ConfigBean;
import com.mikuac.bot.common.utils.ConfigUtils;
import org.springframework.stereotype.Component;

/**
 * Created on 2021/5/24.
 *
 * @author Zero
 */
@Component
public class Global {

    public static ConfigBean config = ConfigUtils.init();

}
