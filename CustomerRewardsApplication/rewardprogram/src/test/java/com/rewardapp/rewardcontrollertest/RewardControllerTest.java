package com.rewardapp.rewardcontrollertest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rewardapp.rewardcontroller.RewardController;
import com.rewardapp.rewardprogrammodel.RewardSummary;
import com.rewardapp.rewardprogrammodel.TransactionModel;
import com.rewardapp.rewardservice.RewardServiceImpl;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/*This class tests the RewardController endpoints to ensure correct handling of 
  reward-related HTTP requests and responses.*/
@WebMvcTest(RewardController.class)
class RewardControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private RewardServiceImpl service;

	private final ObjectMapper objectMapper = new ObjectMapper();

	@Test
	void getAll_ShouldReturn200WithRewards() throws Exception {
		Mockito.when(service.getAllCustomerRewards(any(LocalDate.class), any(LocalDate.class)))
				.thenReturn(List.of(new RewardSummary("Satyam", 100, null, null)));

		mockMvc.perform(get("/api/rewards").param("startDate", "2025-07-01").param("endDate", "2025-07-06"))
				.andExpect(status().isOk()).andExpect(jsonPath("$[0].customerName").value("Satyam"));
	}

	@Test
	void getAll_ShouldReturnEmptyList_WhenNoRewards() throws Exception {
		Mockito.when(service.getAllCustomerRewards(any(LocalDate.class), any(LocalDate.class))).thenReturn(List.of());

		mockMvc.perform(get("/api/rewards").param("startDate", "2025-07-01").param("endDate", "2025-07-06"))
				.andExpect(status().isOk()).andExpect(jsonPath("$").isEmpty());
	}

	@Test
	void getAll_ShouldReturn400_ForInvalidDateFormat() throws Exception {
		mockMvc.perform(get("/api/rewards").param("startDate", "07-01-2025").param("endDate", "2025-07-06"))
				.andExpect(status().isBadRequest()).andExpect(content().string(containsString("Invalid format")));
	}

	@Test
	void getAll_ShouldReturn400_WhenMissingStartDate() throws Exception {
		mockMvc.perform(get("/api/rewards").param("endDate", "2025-07-06")).andExpect(status().isBadRequest());
	}

	@Test
	void getForCustomer_ShouldReturnRewardSummary() throws Exception {
		Mockito.when(service.getSpecificCustomerRewards(eq("Satyam"), any(LocalDate.class), any(LocalDate.class)))
				.thenReturn(new RewardSummary("Satyam", 150, null, null));

		mockMvc.perform(get("/api/rewards/Satyam").param("startDate", "2025-07-01").param("endDate", "2025-07-06"))
				.andExpect(status().isOk()).andExpect(jsonPath("$.customerName").value("Satyam"))
				.andExpect(jsonPath("$.totalPoints").value(150));
	}

	@Test
	void getForCustomer_ShouldReturn400_IfStartDateAfterEndDate() throws Exception {
		mockMvc.perform(get("/api/rewards/Satyam").param("startDate", "2025-07-10").param("endDate", "2025-07-01"))
				.andExpect(status().isBadRequest())
				.andExpect(content().string(containsString("Start date must not be after end date")));
	}

	@Test
	void getForCustomer_ShouldReturn400_ForInvalidDateFormat() throws Exception {
		mockMvc.perform(get("/api/rewards/Satyam").param("startDate", "01-07-2025").param("endDate", "2025-07-06"))
				.andExpect(status().isBadRequest()).andExpect(content().string(containsString("Invalid format")));
	}

	@Test
	void createTransaction_ShouldReturn400_WhenMissingFields() throws Exception {
		TransactionModel transaction = new TransactionModel(); // invalid: missing required fields

		mockMvc.perform(post("/api/rewards/transaction").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(transaction))).andExpect(status().isBadRequest());
	}
}
