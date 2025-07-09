package com.rewardapp.rewardexception;

/*This will handle when date range provided incorrect*/
public class InvalidDateRangeException extends RuntimeException {

	public InvalidDateRangeException(String message) {
		super(message);
	}
}
