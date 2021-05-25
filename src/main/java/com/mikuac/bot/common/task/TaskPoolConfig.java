package com.mikuac.bot.common.task;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * Created on 2021/5/25.
 *
 * @author Zero
 */
@Configuration
public class TaskPoolConfig {

    /**
     * 核心线程数（默认线程数）
     */
    private static final int CORE_POOL_SIZE = 10;

    /**
     * 最大线程数
     */
    private static final int MAX_POOL_SIZE = 30;

    /**
     * 允许线程空闲时间（单位：默认为秒）
     */
    private static final int KEEP_ALIVE_TIME = 10;

    /**
     * 缓冲队列大小
     */
    private static final int QUEUE_CAPACITY = 200;

    /**
     * 线程池名前缀
     */
    private static final String THREAD_NAME_PREFIX = "YuriTaskPool-";

    @Bean("taskExecutor")
    public ThreadPoolTaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(CORE_POOL_SIZE);
        executor.setMaxPoolSize(MAX_POOL_SIZE);
        executor.setQueueCapacity(QUEUE_CAPACITY);
        executor.setKeepAliveSeconds(KEEP_ALIVE_TIME);
        executor.setThreadNamePrefix(THREAD_NAME_PREFIX);
        // 当线程池的任务缓存队列已满并且线程池中的线程数目达到maximumPoolSize，如果还有任务到来就会采取任务拒绝策略
        // 通常有以下四种策略：
        // ThreadPoolExecutor.AbortPolicy:丢弃任务并抛出RejectedExecutionException异常。
        // ThreadPoolExecutor.DiscardPolicy：也是丢弃任务，但是不抛出异常。
        // ThreadPoolExecutor.DiscardOldestPolicy：丢弃队列最前面的任务，然后重新尝试执行任务（重复此过程）
        // ThreadPoolExecutor.CallerRunsPolicy：重试添加当前的任务，自动重复调用 execute() 方法，直到成功
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        // 等待所有任务结束后再关闭线程池
        executor.setWaitForTasksToCompleteOnShutdown(true);
        // 初始化
        executor.initialize();
        return executor;
    }

}
