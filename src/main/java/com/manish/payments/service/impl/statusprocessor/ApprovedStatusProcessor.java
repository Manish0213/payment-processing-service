package com.manish.payments.service.impl.statusprocessor;

import com.manish.payments.interfaces.TransactionStatusProcessor;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ApprovedStatusProcessor implements TransactionStatusProcessor {

	@Override
	public String processStatus(int statusId) {
		log.info("processing the approved status for statusId: {}", statusId);
		
		return "status approved successfully for statusId: " + statusId;
	}

}