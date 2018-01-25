package com.evaristo.bartender.controller;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CustomerRequestController {
	
	private final Logger logger = Logger.getLogger(getClass().getName());

	@Autowired
	private ThreadPoolTaskExecutor taskExecutor;
	
	@Value("${bartender.max-resources}")
	private int bartenderMaxResources;
	
	@Value("${bartender.beer.consumed-resources}")
	private int beerResources;
	
	@Value("${bartender.drink.consumed-resources}")
	private int drinkResources;

	@Value("${bartender.beer.serve-time}")
	private int beerServeTime;

	@Value("${bartender.drink.serve-time}")
	private int drinkServeTime;
	
	private AtomicInteger usedResources = new AtomicInteger(0);

	/**
	 * Method to handle the "/drink" POST requests.
	 * @param id - Request Identifier
	 * @param type - Type of drink
	 */
	@RequestMapping(value="/drink", method=RequestMethod.POST)
	public ResponseEntity<String> serveDrink(@RequestParam String id, @RequestParam String type) {
		logger.log(Level.INFO, "Request id: " + id + " Incoming request. Type: " + type);
		ResponseEntity<String> response;
		int freeResources = bartenderMaxResources - usedResources.get();

		if (type.equals("BEER")) {
			if (freeResources >= beerResources) {
				//If there are enough free resources serve the beer
				response = serveRequest(id, beerServeTime);
			} else {
				response = rejectRequest(id);
			}
		} else if (type.equals("DRINK")) {
			if (freeResources >= drinkResources) {
				//If there are enough free resources serve the drink
				response = serveRequest(id, drinkServeTime);
			} else {
				response = rejectRequest(id);
			}
		} else {
			//Unrecognized type of drink
			logger.log(Level.SEVERE, "Request id: " + id + " ERROR: Invalid type of drink.");
			response = new ResponseEntity<String>("NOT FOUND", HttpStatus.NOT_FOUND);
		}
		return response;
	}
	
	private ResponseEntity<String> serveRequest(String id, int serveTime) {
		//Execute the task and return HTTP 200 (OK)
		taskExecutor.execute(new DrinkTask(serveTime, usedResources));
		logger.log(Level.INFO, "Request id: " + id + " Request accepted. Serving.");
		return new ResponseEntity<String>("OK", HttpStatus.OK);
	}
	
	private ResponseEntity<String> rejectRequest(String id) {
		//Reject the task and return HTTP 429 (TOO MANY REQUESTS)
		logger.log(Level.WARNING, "Request id: " + id + " The bartender is busy, can't serve the request.");
		return new ResponseEntity<String>("TOO MANY REQUESTS", HttpStatus.TOO_MANY_REQUESTS);
	}
	
	/**
	 * Task that emulate the elaboration of a drink in the given time
	 * @author e.a.carmona.robledo
	 *
	 */
	private final class DrinkTask implements Runnable {
		private int timeToServe;
		
		public DrinkTask(int timeToServe, AtomicInteger counter) {
			this.timeToServe = timeToServe;
		}
		
		@Override
		public void run() {
			try {
				usedResources.incrementAndGet();
				//Wait until the drink is served
				Thread.sleep(timeToServe);
				usedResources.decrementAndGet();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}