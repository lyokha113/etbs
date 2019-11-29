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
    executor.setCorePoolSize(5);
    executor.setMaxPoolSize(5);
    executor.setQueueCapacity(20);
    executor.setWaitForTasksToCompleteOnShutdown(true);
    executor.setThreadNamePrefix("EmailThread-");
    executor.initialize();
    return executor;
  }

//  @Bean("contentAsyncExecutor")
//  public TaskExecutor contentAsyncExecutor() {
//    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
//    executor.setCorePoolSize(5);
//    executor.setMaxPoolSize(5);
//    executor.setQueueCapacity(20);
//    executor.setWaitForTasksToCompleteOnShutdown(true);
//    executor.setThreadNamePrefix("ContentThread-");
//    executor.initialize();
//    return executor;
//  }
}
