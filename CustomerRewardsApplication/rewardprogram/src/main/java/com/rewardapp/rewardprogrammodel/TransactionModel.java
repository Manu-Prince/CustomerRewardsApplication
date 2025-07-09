package com.rewardapp.rewardprogrammodel;

import java.time.LocalDate;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Simplified transaction data with reward points.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionModel {
	@NotNull(message = "Customer name is required")
	private String customerName;
	private LocalDate date;
	private double amount;
	private int points;

}
