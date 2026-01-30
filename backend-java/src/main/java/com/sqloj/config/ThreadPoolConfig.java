package com.sqloj.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 线程池配置
 * 用于判题任务的并发执行
 */
@Configuration
public class ThreadPoolConfig {

    @Value("${judge.thread-pool.core-size:5}")
    private int corePoolSize;

    @Value("${judge.thread-pool.max-size:10}")
    private int maxPoolSize;

    @Value("${judge.thread-pool.queue-capacity:100}")
    private int queueCapacity;

    @Value("${judge.thread-pool.keep-alive-seconds:60}")
    private int keepAliveSeconds;

    @Bean(name = "judgeExecutor")
    public Executor judgeExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        
        // 核心线程数
        executor.setCorePoolSize(corePoolSize);
        
        // 最大线程数
        executor.setMaxPoolSize(maxPoolSize);
        
        // 队列容量
        executor.setQueueCapacity(queueCapacity);
        
        // 线程空闲时间
        executor.setKeepAliveSeconds(keepAliveSeconds);
        
        // 线程名称前缀
        executor.setThreadNamePrefix("judge-task-");
        
        // 拒绝策略：由调用线程处理
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        
        // 等待所有任务完成后关闭线程池
        executor.setWaitForTasksToCompleteOnShutdown(true);
        
        // 等待时间
        executor.setAwaitTerminationSeconds(60);
        
        executor.initialize();
        
        return executor;
    }
}
