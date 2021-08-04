package com.mikuac.bot.plugins.aop;

import com.mikuac.bot.config.Global;
import com.mikuac.bot.config.RegexConst;
import com.mikuac.bot.entity.PluginSwitchEntity;
import com.mikuac.bot.repository.PluginSwitchRepository;
import com.mikuac.shiro.bot.Bot;
import com.mikuac.shiro.bot.BotPlugin;
import com.mikuac.shiro.dto.event.message.PrivateMessageEvent;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

/**
 * 插件启停
 *
 * @author Zero
 * @date 2020/11/20 11:20
 */
@Slf4j
@Lazy(value = false)
@Component
public class PluginSwitch extends BotPlugin {

    private PluginSwitchRepository pluginSwitchRepository;

    @Autowired
    public void setPluginSwitchRepository(PluginSwitchRepository pluginSwitchRepository) {
        this.pluginSwitchRepository = pluginSwitchRepository;
    }

    /**
     * 初始化数据库
     * 禁用Lazy懒加载，否则此注解不生效
     */
    @PostConstruct
    public void initDataBase() {

        List<String> pluginNameList = new ArrayList<>() {
            {
                add("SeTu");
            }
        };

        for (String pluginName : pluginNameList) {
            if (pluginSwitchRepository.findByPluginName(pluginName).isPresent()) {
                log.info("插件启停[{}]表字段已初始化，即将跳过此项", pluginName);
            } else {
                log.info("插件启停[{}]表字段不存在，即将初始化", pluginName);
                PluginSwitchEntity pluginSwitchEntity = new PluginSwitchEntity();
                pluginSwitchEntity.setPluginName(pluginName);
                pluginSwitchEntity.setGroupDisable(false);
                pluginSwitchEntity.setPrivateDisable(false);
                pluginSwitchEntity.setGlobalDisable(false);
                pluginSwitchRepository.save(pluginSwitchEntity);
            }
        }

    }

    @Override
    public int onPrivateMessage(@NotNull Bot bot, PrivateMessageEvent event) {

        String msg = event.getRawMessage();
        long userId = event.getUserId();

        String pluginName = msg.replaceAll("(.*)插件(.*)用-", "");
        String type = msg.replaceAll("插件(.*)用-(.*)", "");

        if (msg.matches(RegexConst.PLUGIN_DISABLE) && userId == Global.bot_adminId) {
            switch (type) {
                case "群组":
                    pluginSwitchRepository.groupDisable(pluginName, true);
                    log.info("[{}]插件已被[{}]禁用", pluginName, type);
                    bot.sendPrivateMsg(userId, pluginName + "插件已被" + type + "禁用", false);
                    break;
                case "私聊":
                    pluginSwitchRepository.privateDisable(pluginName, true);
                    log.info("[{}]插件已被[{}]禁用", pluginName, type);
                    bot.sendPrivateMsg(userId, pluginName + "插件已被" + type + "禁用", false);
                    break;
                case "全局":
                    pluginSwitchRepository.globalDisable(pluginName, true);
                    log.info("[{}]插件已被[{}]禁用", pluginName, type);
                    bot.sendPrivateMsg(userId, pluginName + "插件已被" + type + "禁用", false);
                default:
                    break;
            }
        }

        if (msg.matches(RegexConst.PLUGIN_ENABLE) && userId == Global.bot_adminId) {
            switch (type) {
                case "群组":
                    pluginSwitchRepository.groupDisable(pluginName, false);
                    log.info("[{}]插件已被[{}]启用", pluginName, type);
                    bot.sendPrivateMsg(userId, pluginName + "插件已被" + type + "启用", false);
                    break;
                case "私聊":
                    pluginSwitchRepository.privateDisable(pluginName, false);
                    log.info("[{}]插件已被[{}]启用", pluginName, type);
                    bot.sendPrivateMsg(userId, pluginName + "插件已被" + type + "启用", false);
                    break;
                case "全局":
                    pluginSwitchRepository.globalDisable(pluginName, false);
                    log.info("[{}]插件已被[{}]启用", pluginName, type);
                    bot.sendPrivateMsg(userId, pluginName + "插件已被" + type + "启用", false);
                default:
                    break;
            }
        }

        return MESSAGE_IGNORE;

    }

}
