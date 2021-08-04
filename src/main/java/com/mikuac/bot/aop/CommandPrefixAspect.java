package com.mikuac.bot.aop;

import com.mikuac.bot.common.utils.SearchModeUtils;
import com.mikuac.bot.config.Global;
import com.mikuac.shiro.bot.BotPlugin;
import com.mikuac.shiro.dto.event.message.GroupMessageEvent;
import com.mikuac.shiro.dto.event.message.PrivateMessageEvent;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

/**
 * AOP前缀处理
 *
 * @author Zero
 * @date 2020/10/23 22:50
 */
@Slf4j
@Aspect
@Component
public class CommandPrefixAspect {

    private final static String IMG_MSG_REGEX = "<image.*>";

    /**
     * 声明切点
     */
    @Pointcut("execution(* com.mikuac.bot.plugins.aop.*.on*Message(..)))")
    private void prefixPoint() {
    }

    @Around(value = "prefixPoint()")
    public Object prefixCheck(ProceedingJoinPoint pjp) throws Throwable {
        Object[] args = pjp.getArgs();
        for (int i = 0; i < args.length; i++) {
            // 处理群组消息
            if (args[i] instanceof GroupMessageEvent) {
                GroupMessageEvent event = (GroupMessageEvent) args[i];
                String rawMsg = event.getRawMessage();
                // 如果消息未携带prefix，且未匹配到纯图片信息则拦截
                if (!rawMsg.startsWith(Global.prefix_prefix) && !rawMsg.matches(IMG_MSG_REGEX)) {
                    return BotPlugin.MESSAGE_IGNORE;
                }
                // 匹配到纯图片信息判断用户是否处于搜(图/番/本)模式，否则拦截
                if (rawMsg.matches(IMG_MSG_REGEX)) {
                    long waKey = event.getUserId() + event.getGroupId() + 1;
                    long snKey = event.getUserId() + event.getGroupId() + 2;
                    if (SearchModeUtils.getMap().get(waKey) == null && SearchModeUtils.getMap().get(snKey) == null) {
                        return BotPlugin.MESSAGE_IGNORE;
                    }
                }
                // 如果消息携带prefix则去除prefix并放行
                if (rawMsg.startsWith(Global.prefix_prefix)) {
                    var eventBuilder = event.toBuilder();
                    eventBuilder.rawMessage(rawMsg.substring(Global.prefix_prefix.length()));
                    args[i] = eventBuilder.build();
                }
            }
            // 处理私聊消息
            if (args[i] instanceof PrivateMessageEvent) {
                PrivateMessageEvent event = (PrivateMessageEvent) args[i];
                String rawMsg = event.getRawMessage();
                // 如果消息未携带prefix，且未匹配到img标签则拦截（用于搜图模式）
                if (!rawMsg.startsWith(Global.prefix_prefix) && !rawMsg.matches(IMG_MSG_REGEX)) {
                    return BotPlugin.MESSAGE_IGNORE;
                }
                // 匹配到纯图片信息判断用户是否处于搜(图/番/本)模式，否则拦截
                if (rawMsg.matches(IMG_MSG_REGEX)) {
                    long waKey = event.getUserId() + 1;
                    long snKey = event.getUserId() + 2;
                    if (SearchModeUtils.getMap().get(waKey) == null && SearchModeUtils.getMap().get(snKey) == null) {
                        return BotPlugin.MESSAGE_IGNORE;
                    }
                }
                // 如果消息携带prefix则去除prefix并放行
                if (rawMsg.startsWith(Global.prefix_prefix)) {
                    var eventBuilder = event.toBuilder();
                    eventBuilder.rawMessage(rawMsg.substring(Global.prefix_prefix.length()));
                    args[i] = eventBuilder.build();
                }
            }
        }
        return pjp.proceed(args);
    }
}
