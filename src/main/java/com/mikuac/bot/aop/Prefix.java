package com.mikuac.bot.aop;

import lombok.extern.slf4j.Slf4j;
import onebot.OnebotEvent;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class Prefix {

    @Value("${yuri.plugins.prefix-config.prefix}")
    private String prefix;

    //声明切点
    final private static String prefixPoint = "execution(* com.mikuac.bot.plugins.*.on*Message(..))";

    @Around(prefixPoint)
    public Object prefixCheck(ProceedingJoinPoint pjp) throws Throwable {

        Object[] args = pjp.getArgs();
        Object arg = args[1];

        String msgEventType = pjp.getSignature().getName();

        

        //for (Object arg : args) {
        //    if(msgEventType.equals("onGroupMessage")) {
        //        System.out.println(arg);
        //    }
        //
        //    if(msgEventType.equals("onPrivateMessage")) {
        //
        //    }
        //}


        return pjp.proceed(args);
    }

}
