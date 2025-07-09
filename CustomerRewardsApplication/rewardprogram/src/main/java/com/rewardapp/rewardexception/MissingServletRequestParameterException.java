package com.rewardapp.rewardexception;

/*This will handle exception when invalid parameter passed*/
public class MissingServletRequestParameterException extends RuntimeException {

	public MissingServletRequestParameterException(String message) {
		super(message);
	}
}
