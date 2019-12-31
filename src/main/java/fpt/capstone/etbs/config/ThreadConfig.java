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
  

  @Bean("checkDuplicateAsyncExecutor")
  public TaskExecutor checkDuplicateAsyncExecutor() {
    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
    executor.setCorePoolSize(5);
    executor.setMaxPoolSize(5);
    executor.setQueueCapacity(20);
    executor.setWaitForTasksToCompleteOnShutdown(true);
    executor.setThreadNamePrefix("CheckDuplicate-");
    executor.initialize();
    return executor;
  }

  @Bean("approvePublishAsyncExecutor")
  public TaskExecutor approvePublishAsyncExecutor() {
    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
    executor.setCorePoolSize(5);
    executor.setMaxPoolSize(5);
    executor.setQueueCapacity(20);
    executor.setWaitForTasksToCompleteOnShutdown(true);
    executor.setThreadNamePrefix("ApprovePublish-");
    executor.initialize();
    return executor;
  }

}
