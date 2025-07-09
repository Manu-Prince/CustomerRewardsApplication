package com.rewardapp.rewardexception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents the structure of error response returned to the client.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ErrorDetails {
	private int statusCode;
	private String message;
	private String details;

}