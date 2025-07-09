package com.rewardapp.rewardrepository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.rewardapp.rewardentity.Transaction;

/* Interface to implement JPA repository*/

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

	List<Transaction> findByCustomerNameIgnoreCaseAndDateBetween(String customerName, LocalDate start, LocalDate end);

	List<Transaction> findByDateBetween(LocalDate start, LocalDate end);
}
