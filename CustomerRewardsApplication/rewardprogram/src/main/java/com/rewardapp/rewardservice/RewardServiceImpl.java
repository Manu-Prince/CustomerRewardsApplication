package com.rewardapp.rewardservice;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.rewardapp.rewardentity.Transaction;
import com.rewardapp.rewardexception.CustomerNotFoundException;
import com.rewardapp.rewardexception.InvalidDateRangeException;
import com.rewardapp.rewardexception.RewardCalculationException;
import com.rewardapp.rewardprogrammodel.RewardSummary;
import com.rewardapp.rewardprogrammodel.TransactionModel;
import com.rewardapp.rewardrepository.TransactionRepository;
import com.rewardapp.rewardutil.RewardCalculator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Service for calculating rewards and building reward summaries.
 */
@Service
public class RewardServiceImpl implements RewardService {

	private static final Logger logger = LoggerFactory.getLogger(RewardServiceImpl.class);
	private final DateTimeFormatter monthFormat = DateTimeFormatter.ofPattern("yyyy-MM");

	@Autowired
	private TransactionRepository repository;

	public RewardSummary getSpecificCustomerRewards(String customer, LocalDate start, LocalDate end) {
		validateDateRange(start, end);

		List<Transaction> transactions = repository.findByCustomerNameIgnoreCaseAndDateBetween(customer, start, end);
		if (transactions.isEmpty()) {
			logger.warn("No transactions found for customer: {}", customer);
			throw new CustomerNotFoundException("No transactions found for customer: " + customer);
		}

		try {
			return buildRewardSummary(customer, transactions);
		} catch (Exception e) {
			logger.error("Reward calculation failed for customer: {}", customer, e);
			throw new RewardCalculationException("Reward calculation failed/negative for customers: " + customer);
		}
	}

	public List<RewardSummary> getAllCustomerRewards(LocalDate start, LocalDate end) {
		validateDateRange(start, end);

		List<Transaction> allTransactions = repository.findByDateBetween(start, end);
		if (allTransactions.isEmpty()) {
			logger.warn("No transactions found in date range {} to {}", start, end);

			throw new CustomerNotFoundException("No transactions found for any customer");
		}

		try {
			return allTransactions.stream().map(Transaction::getCustomerName).distinct().map(name -> {
				List<Transaction> customerTx = repository.findByCustomerNameIgnoreCaseAndDateBetween(name, start, end);
				return buildRewardSummary(name, customerTx);
			}).collect(Collectors.toList());
		} catch (Exception e) {
			logger.error("Reward calculation failed/negative for customers", e);
			throw new RewardCalculationException("Reward calculation failed/negative for customers.");
		}
	}

	public RewardSummary buildRewardSummary(String customer, List<Transaction> transactions) {
		Map<String, Integer> monthlyPoints = new HashMap<>();
		List<TransactionModel> transactionModels = new ArrayList<>();
		int totalPoints = 0;

		for (Transaction tx : transactions) {
			int points = RewardCalculator.calculate(tx.getAmount());
			if (points < 0) {
				logger.error("Negative reward points : amount={}", tx.getAmount());
				throw new RewardCalculationException("Reward calculation failed/negative for customers:" + customer);
			}
			String month = tx.getDate().format(monthFormat);

			monthlyPoints.merge(month, points, Integer::sum);
			transactionModels.add(new TransactionModel(tx.getCustomerName(), tx.getDate(), tx.getAmount(), points));
			totalPoints += points;

		}

		return new RewardSummary(customer, totalPoints, monthlyPoints, transactionModels);
	}

	private void validateDateRange(LocalDate start, LocalDate end) {
		if (start == null || end == null) {
			throw new InvalidDateRangeException("Start and end dates must be provided.");
		}
		if (start.isAfter(end)) {
			throw new InvalidDateRangeException("Start date must not be after end date.");
		}
	}
}
