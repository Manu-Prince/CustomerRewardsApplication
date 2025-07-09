package com.rewardapp.rewardentity;

import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/*Entity representing a customer transaction, including amount, date, and customer name.*/

@Entity
@Data
@NoArgsConstructor
@RequiredArgsConstructor
@Table(name = "transactions")
public class Transaction {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@NonNull
	private String customerName;
	@NonNull
	private LocalDate date;
	@NonNull
	private double amount;

}
