package com.zhouzifei.tool.config;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.stereotype.Component;

/**
 * 
 * @author 周子斐 (17600004572@163.com)
 * @version 1.0
 * @remark 2019年7月16日
 * @since 1.0
 */
@Component
@EnableScheduling
public class SchedulingConfig implements SchedulingConfigurer {

    private final AtomicInteger integer = new AtomicInteger(0);

    @Override
    public void configureTasks(ScheduledTaskRegistrar scheduledTaskRegistrar) {
        // 指定使用自定义的调度器
        scheduledTaskRegistrar.setScheduler(newExecutors());
    }

    /**
     * 实现多线程并行执行定时任务，防止串行带来了性能消耗
     */
    @Bean(destroyMethod = "shutdown")
    private Executor newExecutors() {
        return Executors.newScheduledThreadPool(5, r -> new Thread(r, String.format("Blog-Task-%s", integer.incrementAndGet())));
    }
}
