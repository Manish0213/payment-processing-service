package com.manish.payments.service.impl.statusprocessor;

import com.manish.payments.interfaces.TransactionStatusProcessor;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PendingStatusProcessor implements TransactionStatusProcessor {

	@Override
	public String processStatus(int statusId) {
		log.info("processing the pending status for statusId: {}", statusId);
		// TODO Auto-generated method stub
		return "status pending successfully for statusId: " + statusId;
	}

}