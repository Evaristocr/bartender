package com.evaristo.bartender;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@SpringBootApplication
public class BartenderApp {
	
	@Value("${bartender.beer-max-pool-size}")
	private int beerMaxPoolSize;
	
	@Value("${bartender.drink-max-pool-size}")
	private int drinkMaxPoolSize;
	
	@Value("${bartender.queue-capacity}")
	private int queueCapacity;
	
	public static void main(String[] args) {
		SpringApplication.run(BartenderApp.class, args);
	}
	
	/**
	 * TaskExecutor that serve the BEER requests.
	 */
	@Bean
    public ThreadPoolTaskExecutor beerTaskExecutor() {
        ThreadPoolTaskExecutor beerTaskExecutor = new ThreadPoolTaskExecutor();
        beerTaskExecutor.setMaxPoolSize(beerMaxPoolSize);
        beerTaskExecutor.setQueueCapacity(queueCapacity);
        return beerTaskExecutor;
    }
	
	/**
	 * TaskExecutor that serve the DRINK requests.
	 */
	@Bean
    public ThreadPoolTaskExecutor drinkTaskExecutor() {
        ThreadPoolTaskExecutor drinkTaskExecutor = new ThreadPoolTaskExecutor();
        drinkTaskExecutor.setMaxPoolSize(drinkMaxPoolSize);
        drinkTaskExecutor.setQueueCapacity(queueCapacity);
        return drinkTaskExecutor;
    }

}
