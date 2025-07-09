package com.rewardapp.rewardexception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/*Custom exception thrown when a customer is not found.
Extends RuntimeException to indicate an unchecked exception
 with a message specifying the missing customer identifier.
*/

public class CustomerNotFoundException extends RuntimeException {

	private static final Logger logger = LoggerFactory.getLogger(CustomerNotFoundException.class);

	public CustomerNotFoundException(String c) {
		super("Customer not found: " + c);
		logger.error("This is an error log: customer not found: unknown");
	}
}
