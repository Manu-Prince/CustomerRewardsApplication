package com.rewardapp.rewardservice;

import java.time.LocalDate;
import java.util.List;

import com.rewardapp.rewardprogrammodel.RewardSummary;

/*Service interface to get all methods which will communicate with other layers to be implemented*/
public interface RewardService {
	RewardSummary getSpecificCustomerRewards(String customer, LocalDate start, LocalDate end);

	List<RewardSummary> getAllCustomerRewards(LocalDate start, LocalDate end);

}
