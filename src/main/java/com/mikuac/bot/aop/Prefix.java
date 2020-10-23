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
public class Prefix {

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

        Object arg = args[1];

        if (arg instanceof OnebotEvent.GroupMessageEvent) {
            OnebotEvent.GroupMessageEvent event = (OnebotEvent.GroupMessageEvent)arg;
            String msg = event.getRawMessage();
            if (!msg.startsWith(prefix)) {
                return BotPlugin.MESSAGE_IGNORE;
            }
            var eventBuilder = event.toBuilder();
            msg = msg.substring(prefix.length());
            eventBuilder.addMessage(0, OnebotBase.Message.newBuilder().setType("text").putData("text", msg).build());
            eventBuilder.setRawMessage(msg);
            args[1] = eventBuilder.build();
        }

        if (arg instanceof OnebotEvent.PrivateMessageEvent) {
            OnebotEvent.PrivateMessageEvent event = (OnebotEvent.PrivateMessageEvent)arg;
            String msg = event.getRawMessage();
            if (!msg.startsWith(prefix)) {
                return BotPlugin.MESSAGE_IGNORE;
            }
            var eventBuilder = event.toBuilder();
            msg = msg.substring(prefix.length());
            eventBuilder.addMessage(0, OnebotBase.Message.newBuilder().setType("text").putData("text", msg).build());
            eventBuilder.setRawMessage(msg);
            args[1] = eventBuilder.build();
        }

        return pjp.proceed(args);

    }

}
