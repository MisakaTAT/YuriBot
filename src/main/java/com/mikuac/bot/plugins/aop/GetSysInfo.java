package com.mikuac.bot.plugins.aop;

import cn.hutool.core.io.FileUtil;
import cn.hutool.system.JvmInfo;
import cn.hutool.system.OsInfo;
import cn.hutool.system.RuntimeInfo;
import cn.hutool.system.SystemUtil;
import cn.hutool.system.oshi.CpuInfo;
import cn.hutool.system.oshi.OshiUtil;
import com.mikuac.bot.config.RegexConst;
import com.mikuac.shiro.bot.Bot;
import com.mikuac.shiro.bot.BotPlugin;
import com.mikuac.shiro.dto.event.message.GroupMessageEvent;
import com.mikuac.shiro.dto.event.message.PrivateMessageEvent;
import com.mikuac.shiro.utils.Msg;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import oshi.hardware.*;

/**
 * @author Zero
 * @date 2020/12/9 13:11
 */
@Slf4j
@Component
public class GetSysInfo extends BotPlugin {

    public Msg sendSysInfoMsg(boolean isGroup, long userId) {
        CpuInfo cpu = OshiUtil.getCpuInfo();
        GlobalMemory memory = OshiUtil.getMemory();
        JvmInfo jvm = SystemUtil.getJvmInfo();
        RuntimeInfo runtimeInfo = SystemUtil.getRuntimeInfo();
        OsInfo osInfo = SystemUtil.getOsInfo();

        Msg msg = Msg.builder();
        if (isGroup) {
            msg.at(userId);
            msg.text("\n");
        }
        msg.text("[处理器信息]");
        msg.text("\nCPU Num: " + cpu.getCpuNum());
        msg.text("\nCPU Used: " + cpu.getUsed() + "%");
        msg.text("\nCPU Free: " + cpu.getFree() + "%");
        msg.text("\n\n[内存信息]");
        msg.text("\nMem Total: " + FileUtil.readableFileSize(memory.getTotal()));
        msg.text("\nMem Available: " + FileUtil.readableFileSize(memory.getAvailable()));
        msg.text("\n\n[JVM信息]");
        msg.text("\nJVM Name: " + jvm.getName());
        msg.text("\nJVM Vendor: " + jvm.getVendor());
        msg.text("\nJVM Version: " + jvm.getVersion());
        msg.text("\n\n[运行信息]");
        msg.text("\nMax Memory: " + FileUtil.readableFileSize(runtimeInfo.getMaxMemory()));
        msg.text("\nTotal Memory: " + FileUtil.readableFileSize(runtimeInfo.getTotalMemory()));
        msg.text("\nFree Memory: " + FileUtil.readableFileSize(runtimeInfo.getFreeMemory()));
        msg.text("\nUsable Memory: " + FileUtil.readableFileSize(runtimeInfo.getUsableMemory()));
        msg.text("\n\n[系统信息]");
        msg.text("\nOS Name: " + osInfo.getName());
        msg.text("\nOS Arch:" + osInfo.getArch());
        msg.text("\nOS Version: " + osInfo.getVersion());
        return msg;
    }

    public Msg sendHardwareInfoMsg(boolean isGroup, long userId) {
        ComputerSystem computerSystem = OshiUtil.getHardware().getComputerSystem();
        Sensors sensors = OshiUtil.getHardware().getSensors();
        CentralProcessor processor = OshiUtil.getHardware().getProcessor();
        Baseboard baseboard = computerSystem.getBaseboard();

        Msg msg = Msg.builder();
        if (isGroup) {
            msg.at(userId);
            msg.text("\n");
        }
        msg.text("[基本信息]");
        msg.text("\nDevice Model: " + computerSystem.getModel());
        msg.text("\nDevice Manufacturer: " + computerSystem.getManufacturer());
        msg.text("\n\n[处理器信息]");
        msg.text("\nCPU Name: " + processor.getProcessorIdentifier().getName());
        msg.text("\nCPU Model: " + processor.getProcessorIdentifier().getModel());
        msg.text("\nCPU Vendor: " + processor.getProcessorIdentifier().getVendor());
        msg.text("\nCPU Micro: " + processor.getProcessorIdentifier().getMicroarchitecture());
        msg.text("\nCPU Stepping: " + processor.getProcessorIdentifier().getStepping());
        msg.text("\nCPU Voltage: " + sensors.getCpuVoltage());
        msg.text("\nCPU Temp：" + sensors.getCpuTemperature());
        msg.text("\n" + processor.getPhysicalPackageCount() + " physical CPU package(s) " + processor.getPhysicalProcessorCount()
                + " physical CPU core(s) " + processor.getLogicalProcessorCount() + " logical CPU(s)");
        msg.text("\n\n[主板信息]");
        msg.text("\nModel: " + baseboard.getModel());
        msg.text("\nVersion: " + baseboard.getVersion());
        msg.text("\nManufacturer: " + baseboard.getManufacturer());
        return msg;
    }

    @Override
    public int onPrivateMessage(@NotNull Bot bot, PrivateMessageEvent event) {
        String msg = event.getRawMessage();
        // 获取系统信息
        if (msg.matches(RegexConst.GET_SYS_INFO)) {
            long uerId = event.getUserId();
            try {
                bot.sendPrivateMsg(uerId, "⁄(⁄ ⁄•⁄ω⁄•⁄ ⁄)⁄ 需要一点时间来为您收集系统信息，请稍后~", false);
                // doGet();
                bot.sendPrivateMsg(uerId, sendSysInfoMsg(false, 0L).build(), false);
            } catch (Exception e) {
                log.info("系统信息收集异常", e);
                bot.sendPrivateMsg(uerId, "系统信息收集异常~", false);
            }
        }
        // 获取硬件信息
        if (msg.matches(RegexConst.GET_HARDWARE_INFO)) {
            long uerId = event.getUserId();
            try {
                bot.sendPrivateMsg(uerId, "⁄(⁄ ⁄•⁄ω⁄•⁄ ⁄)⁄ 需要一点时间来为您收集硬件信息，请稍后~", false);
                bot.sendPrivateMsg(uerId, sendHardwareInfoMsg(false, 0L).build(), false);
            } catch (Exception e) {
                log.info("硬件信息收集异常", e);
                bot.sendPrivateMsg(uerId, "硬件信息收集异常~", false);
            }
        }
        return MESSAGE_IGNORE;
    }

    @Override
    public int onGroupMessage(@NotNull Bot bot, @NotNull GroupMessageEvent event) {
        String msg = event.getRawMessage();
        if (msg.matches(RegexConst.GET_SYS_INFO)) {
            long uerId = event.getUserId();
            long groupId = event.getGroupId();
            try {
                bot.sendGroupMsg(groupId, Msg.builder().at(uerId).text("⁄(⁄ ⁄•⁄ω⁄•⁄ ⁄)⁄ 需要一点时间来为您收集系统信息，请稍后~").build(), false);
                bot.sendGroupMsg(groupId, sendSysInfoMsg(true, uerId).build(), false);
            } catch (Exception e) {
                log.info("系统信息收集异常", e);
                bot.sendGroupMsg(groupId, Msg.builder().at(uerId).text("系统信息收集异常~").build(), false);
            }
        }
        if (msg.matches(RegexConst.GET_HARDWARE_INFO)) {
            long uerId = event.getUserId();
            long groupId = event.getGroupId();
            try {
                bot.sendGroupMsg(groupId, Msg.builder().at(uerId).text("⁄(⁄ ⁄•⁄ω⁄•⁄ ⁄)⁄ 需要一点时间来为您收集硬件信息，请稍后~").build(), false);
                bot.sendGroupMsg(groupId, sendHardwareInfoMsg(true, uerId).build(), false);
            } catch (Exception e) {
                log.info("硬件信息收集异常", e);
                bot.sendGroupMsg(groupId, Msg.builder().at(uerId).text("硬件信息收集异常~").build(), false);
            }
        }
        return MESSAGE_IGNORE;
    }

}
