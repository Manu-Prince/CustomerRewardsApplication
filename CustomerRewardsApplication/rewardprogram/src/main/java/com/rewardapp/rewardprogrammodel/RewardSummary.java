package com.rewardapp.rewardprogrammodel;

import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Aggregated reward result per customer.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RewardSummary {
	private String customerName;
	private int totalPoints;
	private Map<String, Integer> monthlyPoints;
	private List<TransactionModel> transactions;

}