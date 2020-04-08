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

  @Bean("generateImageAsyncExecutor")
  public TaskExecutor generateImageAsyncExecutor() {
    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
    executor.setCorePoolSize(10);
    executor.setMaxPoolSize(20);
    executor.setQueueCapacity(50);
    executor.setWaitForTasksToCompleteOnShutdown(true);
    executor.setThreadNamePrefix("GenerateImage-");
    executor.initialize();
    return executor;
  }

  @Bean("calculateScoreAsyncExecutor")
  public TaskExecutor calculateScoreAsyncExecutor() {
    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
    executor.setCorePoolSize(1);
    executor.setMaxPoolSize(1);
    executor.setQueueCapacity(10);
    executor.setThreadNamePrefix("CalculateScore-");
    executor.initialize();
    return executor;
  }

  @Bean("checkDuplicateAsyncExecutor")
  public TaskExecutor checkDuplicateAsyncExecutor() {
    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
    executor.setCorePoolSize(1);
    executor.setMaxPoolSize(1);
    executor.setQueueCapacity(10);
    executor.setWaitForTasksToCompleteOnShutdown(true);
    executor.setThreadNamePrefix("CheckDuplicate-");
    executor.initialize();
    return executor;
  }

  @Bean("checkDuplicateSingleAsyncExecutor")
  public TaskExecutor checkDuplicateSingleAsyncExecutor() {
    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
    executor.setCorePoolSize(10);
    executor.setMaxPoolSize(20);
    executor.setQueueCapacity(20);
    executor.setWaitForTasksToCompleteOnShutdown(true);
    executor.setThreadNamePrefix("CheckDuplicateSingle-");
    executor.initialize();
    return executor;
  }

  @Bean("notificationAsyncExecutor")
  public TaskExecutor notificationAsyncExecutor() {
    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
    executor.setCorePoolSize(20);
    executor.setMaxPoolSize(50);
    executor.setQueueCapacity(50);
    executor.setWaitForTasksToCompleteOnShutdown(true);
    executor.setThreadNamePrefix("Notification-");
    executor.initialize();
    return executor;
  }

}
