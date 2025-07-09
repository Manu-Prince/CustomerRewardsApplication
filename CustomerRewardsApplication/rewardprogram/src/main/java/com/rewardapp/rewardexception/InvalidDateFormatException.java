package com.rewardapp.rewardexception;

/**
 * Exception thrown when a date format provided is invalid.
 */
public class InvalidDateFormatException extends RuntimeException {
	public InvalidDateFormatException(String message) {
		super(message);
	}
}