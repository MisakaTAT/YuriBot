package com.mikuac.bot.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

/**
 * @author zero
 */
@Slf4j
@Aspect
@Component
public class HandlingTimeAspect {

    @Pointcut("@annotation(com.mikuac.bot.common.annotation.HandlingTime)")
    public void handlingTimePointcut() {
    }

    @Around("handlingTimePointcut()")
    public Object handlingTimeAround(ProceedingJoinPoint joinPoint) {
        try {
            long startTime = System.currentTimeMillis();
            Object proceed = joinPoint.proceed();
            System.out.println("方法执行耗时: " + (System.currentTimeMillis() - startTime) + "ms");
            return proceed;
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        return null;
    }

}
