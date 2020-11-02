package com.mikuac.bot.aop;

import lombok.extern.slf4j.Slf4j;
import net.lz1998.pbbot.bot.BotPlugin;
import onebot.OnebotBase;
import onebot.OnebotEvent;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author Zero
 * @date  2020/10/23 22:50
 */

@Slf4j
@Aspect
@Component
public class CommandPrefix {

    @Value("${yuri.plugins.prefix-config.prefix}")
    private String prefix;

    /**
     * 声明切点
     */
    @Pointcut("execution(* com.mikuac.bot.plugins.*.on*Message(..))")
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
            //处理群组消息
            if (args[i] instanceof OnebotEvent.GroupMessageEvent) {
                OnebotEvent.GroupMessageEvent event = (OnebotEvent.GroupMessageEvent)args[i];
                var msg = event.getRawMessage();
                if (!msg.startsWith(prefix)) {
                    return BotPlugin.MESSAGE_IGNORE;
                }
                var eventBuilder = event.toBuilder();
                msg = msg.substring(prefix.length());
                eventBuilder.addMessage(0, OnebotBase.Message.newBuilder().setType("text").putData("text", msg).build());
                eventBuilder.setRawMessage(msg);
                args[i] = eventBuilder.build();
            }
            //处理私聊消息
            if (args[i] instanceof OnebotEvent.PrivateMessageEvent) {
                OnebotEvent.PrivateMessageEvent event = (OnebotEvent.PrivateMessageEvent)args[i];
                var msg = event.getRawMessage();
                if (!msg.startsWith(prefix)) {
                    return BotPlugin.MESSAGE_IGNORE;
                }
                var eventBuilder = event.toBuilder();
                msg = msg.substring(prefix.length());
                eventBuilder.addMessage(0, OnebotBase.Message.newBuilder().setType("text").putData("text", msg).build());
                eventBuilder.setRawMessage(msg);
                args[i] = eventBuilder.build();
            }
        }

        return pjp.proceed(args);

    }

}