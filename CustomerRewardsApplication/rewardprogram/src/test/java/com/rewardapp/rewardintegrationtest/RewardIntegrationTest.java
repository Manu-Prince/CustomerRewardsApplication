package com.rewardapp.rewardintegrationtest;

import com.rewardapp.rewardentity.Transaction;
import com.rewardapp.rewardrepository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/*This class contains integration tests for verifying the reward system functionality.*/
@SpringBootTest
@AutoConfigureMockMvc
class RewardIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TransactionRepository repository;

    @BeforeEach
    void setup() {
        repository.deleteAll();
        repository.saveAll(List.of(
                new Transaction("Satyam", LocalDate.of(2025, 7, 1), 120.0), // 90 points
                new Transaction("Satyam", LocalDate.of(2025, 7, 2), 50.0)   // 0 points
        ));
    }

    @Test
    void shouldReturnRewards_ForValidCustomer() throws Exception {
        mockMvc.perform(get("/api/rewards/Satyam")
                        .param("startDate", "2025-07-01")
                        .param("endDate", "2025-07-31"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customerName").value("Satyam"))
                .andExpect(jsonPath("$.totalPoints").value(90)); // 120.0 → 90 pts, 50.0 → 0 pts
    }

    @Test
    void shouldReturn400_ForInvalidDateFormat() throws Exception {
        mockMvc.perform(get("/api/rewards")
                        .param("startDate", "07-01-2025")
                        .param("endDate", "2025-07-06"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn404_ForUnknownCustomer() throws Exception {
        mockMvc.perform(get("/api/rewards/ManuTiwari")
                        .param("startDate", "2025-07-01")
                        .param("endDate", "2025-07-31"))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnZeroRewards_WhenNoTransactionsInDateRange() throws Exception {
        mockMvc.perform(get("/api/rewards/Satyam")
                        .param("startDate", "2025-06-01")
                        .param("endDate", "2025-06-30"))
        .andExpect(status().isNotFound());
    }

    @Test
    void shouldIncludeTransactions_OnEdgeDates() throws Exception {
        mockMvc.perform(get("/api/rewards/Satyam")
                        .param("startDate", "2025-07-01")
                        .param("endDate", "2025-07-02"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customerName").value("Satyam"))
                .andExpect(jsonPath("$.totalPoints").value(90));
    }

    @Test
    void shouldReturn400_WhenStartDateMissing() throws Exception {
        mockMvc.perform(get("/api/rewards/Satyam")
                        .param("endDate", "2025-07-31"))
                .andExpect(status().isBadRequest());
    }


    @Test
    void shouldCorrectlyCalculateRewards_AtThresholds() throws Exception {
        repository.deleteAll();
        repository.saveAll(List.of(
                new Transaction("Satyam", LocalDate.of(2025, 7, 1), 50.0),   // 0 pts
                new Transaction("Satyam", LocalDate.of(2025, 7, 2), 100.0)   // 50 pts
        ));

        mockMvc.perform(get("/api/rewards/Satyam")
                        .param("startDate", "2025-07-01")
                        .param("endDate", "2025-07-31"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customerName").value("Satyam"))
                .andExpect(jsonPath("$.totalPoints").value(50));
    }

    @Test
    void shouldReturn400_WhenStartDateAfterEndDate() throws Exception {
        mockMvc.perform(get("/api/rewards/Satyam")
                        .param("startDate", "2025-07-31")
                        .param("endDate", "2025-07-01"))
                .andExpect(status().isBadRequest());
    }
}
