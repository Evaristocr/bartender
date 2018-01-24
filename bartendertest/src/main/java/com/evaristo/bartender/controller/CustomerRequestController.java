package com.evaristo.bartender.controller;

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

import com.evaristo.bartender.task.DrinkTask;

@RestController
public class CustomerRequestController {
	
	private final Logger logger = Logger.getLogger(getClass().getName());

	@Autowired
	private ThreadPoolTaskExecutor beerTaskExecutor;

	@Autowired
	private ThreadPoolTaskExecutor drinkTaskExecutor;

	@Value("${bartender.beer-max-pool-size}")
	private int beerMaxPoolSize;

	@Value("${bartender.drink-max-pool-size}")
	private int drinkMaxPoolSize;

	@Value("${bartender.beer-serve-time}")
	private int beerServeTime;

	@Value("${bartender.drink-serve-time}")
	private int drinkServeTime;

	/**
	 * Method to handle the "/drink" POST requests.
	 * @param id - Request Identifier
	 * @param type - Type of drink
	 */
	@RequestMapping(value="/drink", method=RequestMethod.POST)
	public ResponseEntity<String> serveDrink(@RequestParam String id, @RequestParam String type) {
		logger.log(Level.INFO, "Request id: " + id + " Incoming request. Type: " + type);
		ResponseEntity<String> response;

		int activeDrinkTasks = drinkTaskExecutor.getActiveCount();
		int activeBeerTasks = beerTaskExecutor.getActiveCount();

		if (type.equals("BEER")) {
			if (activeDrinkTasks == 0 && activeBeerTasks < beerMaxPoolSize) {
				//If there are no DRINK tasks and less than 'beerMaxPoolSize' BEER tasks:
				response = serveRequest(beerTaskExecutor, id);
			} else {
				response = rejectRequest(id);
			}
		} else if (type.equals("DRINK")) {
			if (activeDrinkTasks == 0 && activeBeerTasks == 0) {
				//If there are no DRINK tasks and no BEER tasks:
				response = serveRequest(drinkTaskExecutor, id);
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
	
	private ResponseEntity<String> serveRequest(ThreadPoolTaskExecutor taskExecutor, String id) {
		//Execute the task in the given taskExecutor and return HTTP 200 (OK)
		taskExecutor.execute(new DrinkTask(drinkServeTime));
		logger.log(Level.INFO, "Request id: " + id + " Request accepted. Serving.");
		return new ResponseEntity<String>("OK", HttpStatus.OK);
	}
	
	private ResponseEntity<String> rejectRequest(String id) {
		//Reject the task and return HTTP 429 (TOO MANY REQUESTS)
		logger.log(Level.WARNING, "Request id: " + id + " The bartender is busy, can't serve the request.");
		return new ResponseEntity<String>("TOO MANY REQUESTS", HttpStatus.TOO_MANY_REQUESTS);
	}
}