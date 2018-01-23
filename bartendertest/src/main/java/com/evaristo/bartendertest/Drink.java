package com.evaristo.bartendertest;

public class Drink implements Runnable {

	@Override
	public void run() {
		try {
			Thread.sleep(7000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}
