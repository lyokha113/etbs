package fpt.capstone.etbs.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@EnableAsync
public class ThreadConfig {

  @Bean("mailAsyncExecutor")
  public TaskExecutor mailAsyncExecutor() {
    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
    executor.setCorePoolSize(10);
    executor.setMaxPoolSize(20);
    executor.setQueueCapacity(50);
    executor.setWaitForTasksToCompleteOnShutdown(true);
    executor.setThreadNamePrefix("Email-");
    executor.initialize();
    return executor;
  }

  @Bean("imageGenAsyncExecutor")
  public TaskExecutor imageGenAsyncExecutor() {
    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
    executor.setCorePoolSize(10);
    executor.setMaxPoolSize(20);
    executor.setQueueCapacity(50);
    executor.setWaitForTasksToCompleteOnShutdown(true);
    executor.setThreadNamePrefix("ImageGen-");
    executor.initialize();
    return executor;
  }

  @Bean("checkContentAsyncExecutor")
  public TaskExecutor checkContentAsyncExecutor() {
    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
    executor.setCorePoolSize(5);
    executor.setMaxPoolSize(5);
    executor.setQueueCapacity(20);
    executor.setWaitForTasksToCompleteOnShutdown(true);
    executor.setThreadNamePrefix("CheckContent-");
    executor.initialize();
    return executor;
  }

}
