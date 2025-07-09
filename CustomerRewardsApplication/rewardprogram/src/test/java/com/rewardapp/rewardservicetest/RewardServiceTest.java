package com.rewardapp.rewardservicetest;

import com.rewardapp.rewardentity.Transaction;
import com.rewardapp.rewardexception.CustomerNotFoundException;
import com.rewardapp.rewardexception.InvalidDateRangeException;
import com.rewardapp.rewardexception.RewardCalculationException;
import com.rewardapp.rewardprogrammodel.RewardSummary;
import com.rewardapp.rewardrepository.TransactionRepository;
import com.rewardapp.rewardservice.RewardServiceImpl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
This class contains unit tests for `RewardServiceImpl`, 
validating reward calculation logic and exception handling for various transaction scenarios.

 */
class RewardServiceTest {

    @InjectMocks
    private RewardServiceImpl rewardService;

    @Mock
    private TransactionRepository repository;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldReturnRewardSummary_WhenValidTransactionExists() {
        LocalDate start = LocalDate.of(2025, 7, 1);
        LocalDate end = LocalDate.of(2025, 7, 6);
        List<Transaction> transactions = List.of(
                new Transaction("Satyam", start, 120.0)
        );

        when(repository.findByCustomerNameIgnoreCaseAndDateBetween("Satyam", start, end))
                .thenReturn(transactions);

        RewardSummary summary = rewardService.getSpecificCustomerRewards("Satyam", start, end);

        assertEquals("Satyam", summary.getCustomerName());
        assertTrue(summary.getTotalPoints() > 0, "Total points should be positive for valid transaction");
    }

    @Test
    void shouldReturnRewardsForAllCustomers_WhenValidTransactionsExist() {
        LocalDate start = LocalDate.of(2025, 7, 1);
        LocalDate end = LocalDate.of(2025, 7, 6);

        List<Transaction> allTransactions = List.of(
                new Transaction("Satyam", start, 120.0),
                new Transaction("ManuTiwari", start, 60.0)
        );

        when(repository.findByDateBetween(start, end)).thenReturn(allTransactions);

        when(repository.findByCustomerNameIgnoreCaseAndDateBetween(eq("Satyam"), eq(start), eq(end)))
                .thenReturn(List.of(new Transaction("Satyam", start, 120.0)));

        when(repository.findByCustomerNameIgnoreCaseAndDateBetween(eq("ManuTiwari"), eq(start), eq(end)))
                .thenReturn(List.of(new Transaction("ManuTiwari", start, 60.0)));

        List<RewardSummary> summaries = rewardService.getAllCustomerRewards(start, end);

        assertEquals(2, summaries.size(), "Should return two reward summaries");
        assertTrue(summaries.stream().anyMatch(r -> r.getCustomerName().equals("Satyam")));
        assertTrue(summaries.stream().anyMatch(r -> r.getCustomerName().equals("ManuTiwari")));
    }

    @Test
    void shouldThrowCustomerNotFoundException_WhenNoTransactionsExist() {
        when(repository.findByCustomerNameIgnoreCaseAndDateBetween(anyString(), any(), any()))
                .thenReturn(Collections.emptyList());

        assertThrows(CustomerNotFoundException.class, () ->
                rewardService.getSpecificCustomerRewards("NonExistingUser", LocalDate.now(), LocalDate.now()),
                "Expected CustomerNotFoundException for non-existent customer");
    }

    @Test
    void shouldThrowRewardCalculationException_WhenTransactionHasNegativeAmount() {
        List<Transaction> transactions = List.of(
                new Transaction("ManuTiwari", LocalDate.now(), -100.0)
        );

        when(repository.findByCustomerNameIgnoreCaseAndDateBetween(anyString(), any(), any()))
                .thenReturn(transactions);

        RewardCalculationException exception = assertThrows(RewardCalculationException.class, () ->
                rewardService.getSpecificCustomerRewards("ManuTiwari", LocalDate.now(), LocalDate.now()),
                "Expected RewardCalculationException for negative amount");

        assertTrue(exception.getMessage().contains("ManuTiwari"), "Exception message should mention the customer name");
    }

    @Test
    void shouldThrowInvalidDateRangeException_WhenStartDateAfterEndDate() {
        LocalDate start = LocalDate.of(2025, 7, 10);
        LocalDate end = LocalDate.of(2025, 7, 1);

        assertThrows(InvalidDateRangeException.class, () ->
                rewardService.getSpecificCustomerRewards("Satyam", start, end));
    }

    @Test
    void shouldCalculateMonthlyAndTotalPoints_ForMultipleMonths() {
        LocalDate date1 = LocalDate.of(2025, 7, 1);
        LocalDate date2 = LocalDate.of(2025, 8, 2);

        List<Transaction> transactions = List.of(
                new Transaction("Satyam", date1, 120.0),
                new Transaction("Satyam", date2, 75.0)
        );

        when(repository.findByCustomerNameIgnoreCaseAndDateBetween(eq("Satyam"), any(), any()))
                .thenReturn(transactions);

        RewardSummary summary = rewardService.getSpecificCustomerRewards("Satyam", date1, date2);

        assertEquals("Satyam", summary.getCustomerName());
        assertEquals(2, summary.getMonthlyPoints().size(), "Should have rewards in two different months");
        assertTrue(summary.getTotalPoints() > 0, "Total points should be greater than zero");
    }

    @Test
    void shouldHandleTransactionWithZeroAmount() {
        Transaction tx = new Transaction("Satyam", LocalDate.of(2025, 7, 1), 0.0);

        when(repository.findByCustomerNameIgnoreCaseAndDateBetween(eq("Satyam"), any(), any()))
                .thenReturn(List.of(tx));

        RewardSummary summary = rewardService.getSpecificCustomerRewards("Satyam", LocalDate.of(2025, 7, 1), LocalDate.of(2025, 7, 1));

        assertEquals(0, summary.getTotalPoints(), "Zero amount should result in zero reward points");
    }

    @Test
    void shouldThrowCustomerNotFoundException_WhenNoTransactionsInDateRange() {
        when(repository.findByDateBetween(any(), any()))
                .thenReturn(Collections.emptyList());

        assertThrows(CustomerNotFoundException.class, () ->
                rewardService.getAllCustomerRewards(LocalDate.of(2025, 7, 1), LocalDate.of(2025, 7, 10)));
    }
}
