package com.evaristo.bartender.task;

/**
 * Task that emulate the elaboration of a drink in the given time
 * @author e.a.carmona.robledo
 *
 */
public class DrinkTask implements Runnable {
	private int timeToServe;
	
	public DrinkTask(int timeToServe) {
		this.timeToServe = timeToServe;
	}
	
	@Override
	public void run() {
		try {
			//Wait until the drink is served
			Thread.sleep(timeToServe);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}
