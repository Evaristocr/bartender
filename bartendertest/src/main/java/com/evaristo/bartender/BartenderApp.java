package com.evaristo.bartender;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@SpringBootApplication
public class BartenderApp {
	
	@Value("${bartender.max-pool-size}")
	private int maxPoolSize;
	
	@Value("${bartender.queue-capacity}")
	private int queueCapacity;
	
	public static void main(String[] args) {
		SpringApplication.run(BartenderApp.class, args);
	}
	
	/**
	 * TaskExecutor that handle the drink tasks.
	 */
	@Bean
    public ThreadPoolTaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setMaxPoolSize(maxPoolSize);
        taskExecutor.setQueueCapacity(queueCapacity);
        return taskExecutor;
    }

}
