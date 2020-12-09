package com.mikuac.bot.bean.sysinfo;

import lombok.Data;

/**
 * @author Zero
 * @date 2020/12/9 14:25
 */
@Data
public class MemInfoBean {

    private String totalMem;

    private String memTotalUse;

    private String memUseRate;

    private String freeMem;

}
