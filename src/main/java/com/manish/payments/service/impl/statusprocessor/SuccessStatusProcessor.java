package com.manish.payments.service.impl.statusprocessor;

import com.manish.payments.interfaces.TransactionStatusProcessor;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SuccessStatusProcessor implements TransactionStatusProcessor {

	@Override
	public String processStatus(int statusId) {
		log.info("processing the success status for statusId: {}", statusId);
		
		return "status success successfully for statusId: " + statusId;
	}

}
