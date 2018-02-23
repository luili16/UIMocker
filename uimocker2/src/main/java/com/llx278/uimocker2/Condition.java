package com.llx278.uimocker2;

/**
 * Represents a conditional statement.<br/>
 */
public interface Condition {

	/**
	 * Should do the necessary work needed to check a condition and then return whether this condition is satisfied or not.
	 * @return {@code true} if condition is satisfied and {@code false} if it is not satisfied
	 */
	public boolean isSatisfied();

}
