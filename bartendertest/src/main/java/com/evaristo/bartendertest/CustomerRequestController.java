package com.evaristo.bartendertest;

import java.util.concurrent.RejectedExecutionException;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CustomerRequestController {

	@Autowired
	private ThreadPoolTaskExecutor taskExecutor;

	@RequestMapping(value="/request", method=RequestMethod.POST)
	public void queryMethod(@RequestParam String id, @RequestParam String type, HttpServletResponse response) {
		System.out.println("ID=" + id + ". " + type + " request received.");
		
		try {
			if (type.equals("BEER")) {
				taskExecutor.execute(new Drink());
				System.out.println("Serving a beer for the customer: " + id);
			} else if (type.equals("DRINK")) {
				if (taskExecutor.getActiveCount() == 0) {
					taskExecutor.execute(new Drink());
					taskExecutor.execute(new Drink());
					System.out.println("Serving a drink for the customer: " + id);
				} else {
					throw new RejectedExecutionException();
				}
			} else {
				System.out.println("ID=" + id + ". Invalid type of drink.");
			}
		} catch (RejectedExecutionException e) {
			System.out.println("ID=" + id + ". Sorry, the bartender is busy.");
			response.setStatus(429);
		}
	}
}