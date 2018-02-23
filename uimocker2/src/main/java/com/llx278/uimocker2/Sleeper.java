package com.llx278.uimocker2;

class Sleeper {

	private long pauseDuration;
	private long miniPauseDuration;

	private Sleeper() {}

	/**
	 * Constructs this object.
	 *
	 * @param pauseDuration pause duration used in {@code sleep}
	 * @param miniPauseDuration pause duration used in {@code sleepMini}
	 */

	public Sleeper(long pauseDuration, long miniPauseDuration) {
		this.pauseDuration = pauseDuration;
		this.miniPauseDuration = miniPauseDuration;
	}

	/**
	 * Sleeps the current thread for the pause length.
	 */

	public void sleep() {
        sleep(pauseDuration);
	}


	/**
	 * Sleeps the current thread for the mini pause length.
	 */

	public void sleepMini() {
        sleep(miniPauseDuration);
	}


	/**
	 * Sleeps the current thread for <code>time</code> milliseconds.
	 *
	 * @param time the length of the sleep in milliseconds
	 */

	public void sleep(long time) {
		try {
			Thread.sleep(time);
		} catch (InterruptedException ignored) {}
	}
}
