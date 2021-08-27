package com.mikuac.bot.plugins.aop;

import com.mikuac.bot.common.utils.RegexUtils;
import com.mikuac.bot.config.Global;
import com.mikuac.bot.config.RegexConst;
import com.mikuac.bot.entity.SensitiveWordEntity;
import com.mikuac.bot.plugins.SensitiveWords;
import com.mikuac.bot.repository.SensitiveWordRepository;
import com.mikuac.shiro.common.utils.MsgUtils;
import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.core.BotPlugin;
import com.mikuac.shiro.dto.event.message.GroupMessageEvent;
import com.mikuac.shiro.dto.event.message.PrivateMessageEvent;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author Zero
 */
@Component
public class SensitiveWordMgmt extends BotPlugin {

    @Resource
    private SensitiveWordRepository sensitiveWordRepository;

    @Resource
    private SensitiveWords sensitiveWords;

    public String getAddWord(String msg) {
        String word = "";
        // 正则group数
        int groupCount = 3;
        for (int i = 1; i <= groupCount; i++) {
            if (RegexUtils.regexGroup(RegexConst.ADD_SENSITIVE_WORD, msg, i) != null) {
                word = RegexUtils.regexGroup(RegexConst.ADD_SENSITIVE_WORD, msg, i);
                break;
            }
        }
        return word;
    }

    public String getDelWord(String msg) {
        String word = "";
        // 正则group数
        int groupCount = 3;
        for (int i = 1; i <= groupCount; i++) {
            if (RegexUtils.regexGroup(RegexConst.DEL_SENSITIVE_WORD, msg, i) != null) {
                word = RegexUtils.regexGroup(RegexConst.DEL_SENSITIVE_WORD, msg, i);
                break;
            }
        }
        return word;
    }

    private boolean roleCheckFail(@NotNull Bot bot, long groupId, long userId) {
        // 检查指令发送者是否为admin
        if (Global.BOT_ADMIN_ID != userId) {
            if (groupId != 0L) {
                bot.sendGroupMsg(groupId, MsgUtils.builder().at(userId).text("此操作仅管理员可执行").build(), false);
            } else {
                bot.sendPrivateMsg(userId, "此操作仅管理员可执行", false);
            }
            return true;
        }
        return false;
    }

    private String getWord(String msg, boolean isAdd) {
        String word;
        if (isAdd) {
            word = getAddWord(msg);
        } else {
            word = getDelWord(msg);
        }
        if (!word.isEmpty()) {
            return word;
        }
        return null;
    }

    private void addWordToDb(@NotNull Bot bot, String word, long groupId, long userId) {
        if (sensitiveWordRepository.findWord(word).isPresent()) {
            if (groupId != 0L) {
                bot.sendGroupMsg(groupId, MsgUtils.builder().at(userId).text("敏感词已存在，请勿重复添加").build(), false);
            } else {
                bot.sendPrivateMsg(userId, "敏感词已存在，请勿重复添加", false);
            }
            return;
        }
        SensitiveWordEntity sensitiveWordEntity = new SensitiveWordEntity();
        sensitiveWordEntity.setWord(word);
        sensitiveWordRepository.save(sensitiveWordEntity);
        // 向关键词树添加单词，免去重启程序加载新增词
        sensitiveWords.addWord(word);
        if (groupId != 0L) {
            bot.sendGroupMsg(groupId, MsgUtils.builder().at(userId).text("敏感词添加成功").build(), false);
        } else {
            bot.sendPrivateMsg(userId, "敏感词添加成功", false);
        }
    }

    private void delWordToDb(@NotNull Bot bot, String word, long groupId, long userId) {
        sensitiveWordRepository.deleteByWord(word);
        // 从关键词树移除单词，免去重启程序
        sensitiveWords.removeWord(word);
        if (groupId != 0L) {
            bot.sendGroupMsg(groupId, MsgUtils.builder().at(userId).text("敏感词删除成功").build(), false);
        } else {
            bot.sendPrivateMsg(userId, "敏感词删除成功", false);
        }
    }

    @Override
    public int onGroupMessage(@NotNull Bot bot, @NotNull GroupMessageEvent event) {
        String msg = event.getMessage();
        long groupId = event.getGroupId();
        long userId = event.getUserId();

        // 添加敏感词
        if (msg.matches(RegexConst.ADD_SENSITIVE_WORD)) {
            // 权限检查
            if (roleCheckFail(bot, groupId, userId)) {
                return MESSAGE_IGNORE;
            }
            // 添加敏感词到数据库
            String word = getWord(msg, true);
            if (word != null && !word.isEmpty()) {
                addWordToDb(bot, word, groupId, userId);
            }
        }

        // 删除敏感词
        if (msg.matches(RegexConst.DEL_SENSITIVE_WORD)) {
            // 权限检查
            if (roleCheckFail(bot, groupId, userId)) {
                return MESSAGE_IGNORE;
            }
            // 从数据库删除敏感词
            String word = getWord(msg, false);
            if (word != null && !word.isEmpty()) {
                delWordToDb(bot, word, groupId, userId);
            }
        }

        return MESSAGE_IGNORE;
    }

    @Override
    public int onPrivateMessage(@NotNull Bot bot, PrivateMessageEvent event) {
        String msg = event.getMessage();
        long userId = event.getUserId();

        // 添加敏感词
        if (msg.matches(RegexConst.ADD_SENSITIVE_WORD)) {
            // 权限检查
            if (roleCheckFail(bot, 0L, userId)) {
                return MESSAGE_IGNORE;
            }
            // 添加敏感词到数据库
            String word = getWord(msg, true);
            if (word != null && !word.isEmpty()) {
                addWordToDb(bot, word, 0L, userId);
            }
        }

        // 删除敏感词
        if (msg.matches(RegexConst.DEL_SENSITIVE_WORD)) {
            // 权限检查
            if (roleCheckFail(bot, 0L, userId)) {
                return MESSAGE_IGNORE;
            }
            // 从数据库删除敏感词
            String word = getWord(msg, false);
            if (word != null && !word.isEmpty()) {
                delWordToDb(bot, word, 0L, userId);
            }
        }

        return MESSAGE_IGNORE;
    }

}
