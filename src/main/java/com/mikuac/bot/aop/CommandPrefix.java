package com.mikuac.bot.aop;

import lombok.extern.slf4j.Slf4j;
import net.lz1998.pbbot.bot.BotPlugin;
import onebot.OnebotEvent;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * AOP前缀处理
 * @author Zero
 * @date  2020/10/23 22:50
 */
@Slf4j
@Aspect
@Component
public class CommandPrefix {

    @Value("${yuri.plugins.prefix-config.prefix}")
    private String prefix;

    final String imgMsgRegex = "<image.*>";

    /**
     * 声明切点
     */
    @Pointcut("execution(* com.mikuac.bot.plugins.*.on*Message(..)) && !execution(* com.mikuac.bot.plugins.GroupMsgCount.on*Message(..)))")
    private void prefixPoint() {}

    /**
     * Around为环绕通知，方法前后各执行一次
     * @param pjp
     * @return
     * @throws Throwable
     */
    @Around(value = "prefixPoint()")
    public Object prefixCheck(ProceedingJoinPoint pjp) throws Throwable {

        Object[] args = pjp.getArgs();

        for (int i = 0; i < args.length; i++){
            // 处理群组消息
            if (args[i] instanceof OnebotEvent.GroupMessageEvent) {
                OnebotEvent.GroupMessageEvent event = (OnebotEvent.GroupMessageEvent)args[i];
                String msg = event.getRawMessage();
                // 如果消息未携带prefix，且未匹配到img标签则拦截（用于搜图模式）
                if (!msg.startsWith(prefix) && !msg.matches(imgMsgRegex)) {
                    return BotPlugin.MESSAGE_IGNORE;
                }
                // 如果消息携带prefix则去除prefix并放行
                if (msg.startsWith(prefix)) {
                    var eventBuilder = event.toBuilder();
                    msg = msg.substring(prefix.length());
                    eventBuilder.setRawMessage(msg);
                    args[i] = eventBuilder.build();
                }
            }
            // 处理私聊消息
            if (args[i] instanceof OnebotEvent.PrivateMessageEvent) {
                OnebotEvent.PrivateMessageEvent event = (OnebotEvent.PrivateMessageEvent)args[i];
                String msg = event.getRawMessage();
                // 如果消息未携带prefix，且未匹配到img标签则拦截（用于搜图模式）
                if (!msg.startsWith(prefix) && !msg.matches(imgMsgRegex)) {
                    return BotPlugin.MESSAGE_IGNORE;
                }
                // 如果消息携带prefix则去除prefix并放行
                if (msg.startsWith(prefix)) {
                    var eventBuilder = event.toBuilder();
                    msg = msg.substring(prefix.length());
                    eventBuilder.setRawMessage(msg);
                    args[i] = eventBuilder.build();
                }
            }
        }

        return pjp.proceed(args);

    }

}