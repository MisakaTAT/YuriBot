package com.mikuac.bot.plugins.aop;

import com.mikuac.bot.bean.sysinfo.*;
import com.mikuac.bot.config.RegexConst;
import lombok.extern.slf4j.Slf4j;
import net.lz1998.pbbot.bot.Bot;
import net.lz1998.pbbot.bot.BotPlugin;
import net.lz1998.pbbot.utils.Msg;
import onebot.OnebotEvent;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import oshi.SystemInfo;
import oshi.hardware.Baseboard;
import oshi.hardware.CentralProcessor;
import oshi.hardware.GlobalMemory;
import java.text.DecimalFormat;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

/**
 * @author Zero
 * @date 2020/12/9 13:11
 */
@Slf4j
@Component
public class GetSysInfo extends BotPlugin {

    CpuInfoBean cpu = new CpuInfoBean();
    MemInfoBean mem = new MemInfoBean();
    OSInfoBean os = new OSInfoBean();
    JvmInfoBean jvm = new JvmInfoBean();
    BaseBoardBean bd = new BaseBoardBean();

    SystemInfo systemInfo = new SystemInfo();

    public String formatByte(long byteNumber){
        //换算单位
        double FORMAT = 1024.0;
        double kbNumber = byteNumber/FORMAT;
        if(kbNumber<FORMAT){
            return new DecimalFormat("#.##KB").format(kbNumber);
        }
        double mbNumber = kbNumber/FORMAT;
        if(mbNumber<FORMAT){
            return new DecimalFormat("#.##MB").format(mbNumber);
        }
        double gbNumber = mbNumber/FORMAT;
        if(gbNumber<FORMAT){
            return new DecimalFormat("#.##GB").format(gbNumber);
        }
        double tbNumber = gbNumber/FORMAT;
        return new DecimalFormat("#.##TB").format(tbNumber);
    }

    public Msg sendSysInfoMsg(boolean isGroup,long  userId) {
        Msg msg = Msg.builder();
        if (isGroup) {
            msg.at(userId);
            msg.text("\n");
        }
        msg.text("---------CPU INFO---------");
        msg.text("\nCPU核心数：" + cpu.getTotalCore());
        msg.text("\nCPU使用率：" + cpu.getTotalUse());
        msg.text("\n\n---------MEM INFO---------");
        msg.text("\n总内存：" + mem.getTotalMem());
        msg.text("\n已使用：" + mem.getMemTotalUse());
        msg.text("\n使用率：" + mem.getMemUseRate());
        msg.text("\n剩余内存：" + mem.getFreeMem());
        msg.text("\n\n---------JVM INFO---------");
        msg.text("\nJDK版本：" + jvm.getJdkVersion());
        msg.text("\nJVM内存总量：" + jvm.getJvmTotalMem());
        msg.text("\nJVM已用内存：" + jvm.getJvmUseMem());
        msg.text("\nJVM剩余内存：" + jvm.getJvmFreeMem());
        msg.text("\nJVM内存使用率：" + jvm.getJvmMemUseRate());
        msg.text("\n\n---------SYS INFO---------");
        msg.text("\n操作系统：" + os.getOSName());
        msg.text("\n系统架构：" + os.getOSArch());
        return msg;
    }

    public Msg sendHardwareInfoMsg(boolean isGroup,long  userId) {
        Msg msg = Msg.builder();
        if (isGroup) {
            msg.at(userId);
            msg.text("\n");
        }
        msg.text("处理器信息");
        msg.text("\n型号: " + cpu.getCpuMode());
        msg.text("\n步进: " + cpu.getStepping());
        msg.text("\n架构: " + cpu.getMicrocircuit());
        msg.text("\n" + cpu.getLogicalCount() + " logical CPU(s)");
        msg.text("\n" + cpu.getCoreCount() + " physical CPU core(s)");
        msg.text("\n" + cpu.getPackageCount() + " physical CPU package(s)");
        msg.text("\n\n主板信息");
        msg.text("\n型号: " + bd.getModel());
        msg.text("\n版本: " + bd.getVersion());
        msg.text("\n制造商: " + bd.getManufacturer());
        return msg;
    }

    public void getCpuInfo () throws InterruptedException {
        CentralProcessor processor = systemInfo.getHardware().getProcessor();
        long[] prevTicks = processor.getSystemCpuLoadTicks();
        TimeUnit.SECONDS.sleep(1);
        long[] ticks = processor.getSystemCpuLoadTicks();
        long nice = ticks[CentralProcessor.TickType.NICE.getIndex()] - prevTicks[CentralProcessor.TickType.NICE.getIndex()];
        long irq = ticks[CentralProcessor.TickType.IRQ.getIndex()] - prevTicks[CentralProcessor.TickType.IRQ.getIndex()];
        long softIrq = ticks[CentralProcessor.TickType.SOFTIRQ.getIndex()] - prevTicks[CentralProcessor.TickType.SOFTIRQ.getIndex()];
        long steal = ticks[CentralProcessor.TickType.STEAL.getIndex()] - prevTicks[CentralProcessor.TickType.STEAL.getIndex()];
        long cSys = ticks[CentralProcessor.TickType.SYSTEM.getIndex()] - prevTicks[CentralProcessor.TickType.SYSTEM.getIndex()];
        long user = ticks[CentralProcessor.TickType.USER.getIndex()] - prevTicks[CentralProcessor.TickType.USER.getIndex()];
        long ioWait = ticks[CentralProcessor.TickType.IOWAIT.getIndex()] - prevTicks[CentralProcessor.TickType.IOWAIT.getIndex()];
        long idle = ticks[CentralProcessor.TickType.IDLE.getIndex()] - prevTicks[CentralProcessor.TickType.IDLE.getIndex()];
        // 总使用率
        long totalCpu = user + nice + cSys + idle + ioWait + irq + softIrq + steal;

        cpu.setTotalCore(processor.getLogicalProcessorCount());
        cpu.setTotalUse(new DecimalFormat("#.##%").format(1.0-(idle * 1.0 / totalCpu)));
        cpu.setCpuMode(processor.getProcessorIdentifier().getName());
        cpu.setStepping(processor.getProcessorIdentifier().getStepping());
        cpu.setPackageCount(processor.getPhysicalPackageCount());
        cpu.setLogicalCount(processor.getLogicalProcessorCount());
        cpu.setCoreCount(processor.getPhysicalProcessorCount());
        cpu.setMicrocircuit(processor.getProcessorIdentifier().getMicroarchitecture());
    }

    public void getMemInfo() {
        SystemInfo systemInfo = new SystemInfo();
        GlobalMemory memory = systemInfo.getHardware().getMemory();
        // 总内存
        long totalMem = memory.getTotal();
        // 剩余内存
        long freeMem = memory.getAvailable();

        mem.setTotalMem(formatByte(totalMem));
        mem.setMemTotalUse(formatByte(totalMem-freeMem));
        mem.setMemUseRate(new DecimalFormat("#.##%").format((totalMem-freeMem)*1.0/totalMem));
        mem.setFreeMem(formatByte(freeMem));
    }

    public void getJvmInfo() {
        Properties props = System.getProperties();
        Runtime runtime = Runtime.getRuntime();
        //jvm总内存
        long totalMemory = runtime.totalMemory();
        //空闲空间
        long freeMemory = runtime.freeMemory();
        //jdk版本
        String jdkVersion = props.getProperty("java.version");

        jvm.setJdkVersion(jdkVersion);
        jvm.setJvmTotalMem(formatByte(totalMemory));
        jvm.setJvmUseMem(formatByte(totalMemory-freeMemory));
        jvm.setJvmFreeMem(formatByte(freeMemory));
        jvm.setJvmMemUseRate(new DecimalFormat("#.##%").format((totalMemory-freeMemory)*1.0/totalMemory));
    }

    public void getSysInfo() {
        Properties props = System.getProperties();
        //系统名称
        String osName = props.getProperty("os.name");
        //架构名称
        String osArch = props.getProperty("os.arch");

        os.setOSName(osName);
        os.setOSArch(osArch);
    }

    public void getBaseboard() {
        Baseboard baseboard = systemInfo.getHardware().getComputerSystem().getBaseboard();
        bd.setManufacturer(baseboard.getManufacturer());
        bd.setModel(baseboard.getModel());
        bd.setVersion(baseboard.getVersion());
    }

    public void doGet () throws InterruptedException {
        getCpuInfo();
        getMemInfo();
        getJvmInfo();
        getSysInfo();
        getBaseboard();
    }

    @Override
    public int onPrivateMessage(@NotNull Bot bot, @NotNull OnebotEvent.PrivateMessageEvent event) {
        String msg = event.getRawMessage();
        // 获取系统信息
        if (msg.matches(RegexConst.GET_SYS_INFO)) {
            long uerId = event.getUserId();
            try {
                bot.sendPrivateMsg(uerId,"⁄(⁄ ⁄•⁄ω⁄•⁄ ⁄)⁄ 需要一点时间来为您收集系统信息，请稍后~",false);
                doGet();
                bot.sendPrivateMsg(uerId,sendSysInfoMsg(false,0L).build(),false);
            } catch (Exception e) {
                log.info("系统信息收集异常",e);
                bot.sendPrivateMsg(uerId,"系统信息收集异常~",false);
           }
        }
        // 获取硬件信息
        if (msg.matches(RegexConst.GET_HARDWARE_INFO)) {
            long uerId = event.getUserId();
            try {
                bot.sendPrivateMsg(uerId,"⁄(⁄ ⁄•⁄ω⁄•⁄ ⁄)⁄ 需要一点时间来为您收集硬件信息，请稍后~",false);
                doGet();
                bot.sendPrivateMsg(uerId,sendHardwareInfoMsg(false,0L).build(),false);
            } catch (Exception e) {
                log.info("硬件信息收集异常",e);
                bot.sendPrivateMsg(uerId,"硬件信息收集异常~",false);
            }
        }
        return MESSAGE_IGNORE;
    }

    @Override
    public int onGroupMessage(@NotNull Bot bot, @NotNull OnebotEvent.GroupMessageEvent event) {
        String msg = event.getRawMessage();
        if (msg.matches(RegexConst.GET_SYS_INFO)) {
            long uerId = event.getUserId();
            long groupId = event.getGroupId();
            try {
                bot.sendGroupMsg(groupId,Msg.builder().at(uerId).text("⁄(⁄ ⁄•⁄ω⁄•⁄ ⁄)⁄ 需要一点时间来为您收集系统信息，请稍后~").build(),false);
                doGet();
                bot.sendGroupMsg(groupId,sendSysInfoMsg(true,uerId).build(),false);
            } catch (Exception e) {
                log.info("系统信息收集异常",e);
                bot.sendGroupMsg(groupId,Msg.builder().at(uerId).text("系统信息收集异常~").build(),false);
            }
        }
        if (msg.matches(RegexConst.GET_HARDWARE_INFO)) {
            long uerId = event.getUserId();
            long groupId = event.getGroupId();
            try {
                bot.sendGroupMsg(groupId,Msg.builder().at(uerId).text("⁄(⁄ ⁄•⁄ω⁄•⁄ ⁄)⁄ 需要一点时间来为您收集硬件信息，请稍后~").build(),false);
                doGet();
                bot.sendGroupMsg(groupId,sendHardwareInfoMsg(true,uerId).build(),false);
            } catch (Exception e) {
                log.info("硬件信息收集异常",e);
                bot.sendGroupMsg(groupId,Msg.builder().at(uerId).text("硬件信息收集异常~").build(),false);
            }
        }
        return MESSAGE_IGNORE;
    }

}
