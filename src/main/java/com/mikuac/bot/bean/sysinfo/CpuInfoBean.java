package com.mikuac.bot.bean.sysinfo;

import lombok.Data;

/**
 * @author Zero
 * @date 2020/12/9 14:25
 */
@Data
public class CpuInfoBean {

    private int totalCore;

    private String totalUse;

    private String cpuMode;

}
