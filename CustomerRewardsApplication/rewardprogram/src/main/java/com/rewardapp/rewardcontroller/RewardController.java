package com.rewardapp.rewardcontroller;

import com.rewardapp.rewardexception.InvalidDateFormatException;
import com.rewardapp.rewardexception.InvalidDateRangeException;
import com.rewardapp.rewardprogrammodel.RewardSummary;
import com.rewardapp.rewardprogrammodel.TransactionModel;
import com.rewardapp.rewardservice.RewardServiceImpl;

import jakarta.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

/**
 * REST controller for handling reward-related endpoints.
 */
@RestController
@RequestMapping("/api/rewards")
public class RewardController {

	@Autowired
	private RewardServiceImpl service;

	private static final Logger logger = LoggerFactory.getLogger(RewardController.class);

	@Operation(summary = "Create a new transaction", description = "Adds a transaction for a customer.")
	@ApiResponses(value = { @ApiResponse(responseCode = "201", description = "Transaction created successfully"),
			@ApiResponse(responseCode = "400", description = "Invalid transaction data", content = @Content(schema = @Schema(implementation = String.class))) })
	@PostMapping("/transaction")
	public ResponseEntity<String> createTransaction(
			@Valid @RequestBody @Parameter(description = "Transaction details") TransactionModel transaction) {
		logger.info("Received transaction from customer '{}', amount: {}", transaction.getCustomerName(),
				transaction.getAmount());

		return ResponseEntity.status(HttpStatus.CREATED).body("Transaction received successfully");
	}

	@Operation(summary = "Get reward summaries for all customers", description = "Fetches reward data for all customers within the specified date range.")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Successfully retrieved reward summaries"),
			@ApiResponse(responseCode = "400", description = "Invalid date format or range", content = @Content(schema = @Schema(implementation = String.class))) })
	@GetMapping
	public List<RewardSummary> getAllCustomerRewards(
			@RequestParam @Parameter(description = "Start date in yyyy-MM-dd format") String startDate,
			@RequestParam @Parameter(description = "End date in yyyy-MM-dd format") String endDate) {

		LocalDate start = parseDate(startDate, "startDate");
		LocalDate end = parseDate(endDate, "endDate");
		validateDateRange(start, end);

		logger.info("Fetching reward data for all customers between {} and {}", start, end);
		return service.getAllCustomerRewards(start, end);
	}

	@Operation(summary = "Get reward summary for a specific customer", description = "Fetches reward data for a specific customer within the specified date range.")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Successfully retrieved reward summary"),
			@ApiResponse(responseCode = "400", description = "Invalid date format or range", content = @Content(schema = @Schema(implementation = String.class))),
			@ApiResponse(responseCode = "404", description = "Customer not found") })
	@GetMapping("/{customer}")
	public RewardSummary getSpecificCustomerRewards(
			@PathVariable @Parameter(description = "Customer name or ID") String customer,
			@RequestParam @Parameter(description = "Start date in yyyy-MM-dd format") String startDate,
			@RequestParam @Parameter(description = "End date in yyyy-MM-dd format") String endDate) {

		LocalDate start = parseDate(startDate, "startDate");
		LocalDate end = parseDate(endDate, "endDate");
		validateDateRange(start, end);

		logger.info("Fetching reward data for customer '{}' between {} and {}", customer, start, end);
		return service.getSpecificCustomerRewards(customer, start, end);
	}

	private LocalDate parseDate(String dateStr, String paramName) {
		try {
			return LocalDate.parse(dateStr);
		} catch (DateTimeParseException e) {
			logger.error("Invalid date format for {}: {}", paramName, dateStr);
			throw new InvalidDateFormatException(
					"Invalid format for '" + paramName + "': " + dateStr + ". Expected format: yyyy-MM-dd");
		}
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
