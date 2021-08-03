package com.mikuac.bot.plugins.aop;

import com.mikuac.bot.common.utils.RegexUtils;
import com.mikuac.bot.config.Global;
import com.mikuac.bot.config.RegexConst;
import com.mikuac.bot.entity.SensitiveWordEntity;
import com.mikuac.bot.repository.SensitiveWordRepository;
import com.mikuac.shiro.bot.Bot;
import com.mikuac.shiro.bot.BotPlugin;
import com.mikuac.shiro.dto.event.message.GroupMessageEvent;
import com.mikuac.shiro.dto.event.message.PrivateMessageEvent;
import com.mikuac.shiro.utils.Msg;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author Zero
 */
@Component
public class AddSensitiveWord extends BotPlugin {

    private SensitiveWordRepository sensitiveWordRepository;

    @Autowired
    public void setSensitiveWordRepository(SensitiveWordRepository sensitiveWordRepository) {
        this.sensitiveWordRepository = sensitiveWordRepository;
    }

    public String getWord(String msg) {
        String word = null;
        // 正则group数
        int groupCount = 3;
        for (int i = 1; i <= groupCount; i++) {
            if (RegexUtils.regexGroup(RegexConst.SENSITIVE_WORD, msg, i) != null) {
                word = RegexUtils.regexGroup(RegexConst.SENSITIVE_WORD, msg, i);
                break;
            }
        }
        return word;
    }

    @Override
    public int onGroupMessage(Bot bot, GroupMessageEvent event) {
        String msg = event.getRawMessage();
        long groupId = event.getGroupId();
        long userId = event.getUserId();
        if (msg.matches(RegexConst.SENSITIVE_WORD)) {
            if (Global.bot_adminId != userId) {
                bot.sendGroupMsg(groupId, Msg.builder().at(userId).text("此操作仅管理员可执行").build(), false);
                return MESSAGE_BLOCK;
            }
            String word = getWord(msg);
            if (word != null && word.isEmpty()) {
                return MESSAGE_BLOCK;
            }
            if (sensitiveWordRepository.findWord(word).isPresent()) {
                bot.sendGroupMsg(groupId, Msg.builder().at(userId).text("敏感词已存在，请勿重复添加").build(), false);
                return MESSAGE_BLOCK;
            }
            SensitiveWordEntity sensitiveWordEntity = new SensitiveWordEntity();
            sensitiveWordEntity.setWord(word);
            sensitiveWordRepository.save(sensitiveWordEntity);
            bot.sendGroupMsg(groupId, Msg.builder().at(userId).text("敏感词添加成功").build(), false);
        }
        return MESSAGE_IGNORE;
    }

    @Override
    public int onPrivateMessage(Bot bot, PrivateMessageEvent event) {
        String msg = event.getRawMessage();
        long userId = event.getUserId();
        if (msg.matches(RegexConst.SENSITIVE_WORD)) {
            if (Global.bot_adminId != userId) {
                bot.sendPrivateMsg(userId, "此操作仅管理员可执行", false);
                return MESSAGE_BLOCK;
            }
            String word = getWord(msg);
            if (word != null && word.isEmpty()) {
                return MESSAGE_BLOCK;
            }
            if (sensitiveWordRepository.findWord(word).isPresent()) {
                bot.sendPrivateMsg(userId, "敏感词已存在，请勿重复添加", false);
                return MESSAGE_BLOCK;
            }
            SensitiveWordEntity sensitiveWordEntity = new SensitiveWordEntity();
            sensitiveWordEntity.setWord(word);
            sensitiveWordRepository.save(sensitiveWordEntity);
            bot.sendPrivateMsg(userId, "敏感词添加成功", false);
        }
        return MESSAGE_IGNORE;
    }

}

