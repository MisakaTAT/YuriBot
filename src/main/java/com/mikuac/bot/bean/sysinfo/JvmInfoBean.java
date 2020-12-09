package com.mikuac.bot.bean.sysinfo;

import lombok.Data;

/**
 * @author Zero
 * @date 2020/12/9 14:25
 */
@Data
public class JvmInfoBean {

    private String jdkVersion;

    private String jvmTotalMem;

    private String jvmUseMem;

    private String jvmFreeMem;

    private String jvmMemUseRate;

}
