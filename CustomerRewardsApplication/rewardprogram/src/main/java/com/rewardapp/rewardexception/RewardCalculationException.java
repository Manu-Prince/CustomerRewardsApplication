package com.rewardapp.rewardexception;

/*This will handle when the reward point calculated wrongly*/
public class RewardCalculationException extends RuntimeException {

	public RewardCalculationException(String message) {
		super(message);
	}
}
