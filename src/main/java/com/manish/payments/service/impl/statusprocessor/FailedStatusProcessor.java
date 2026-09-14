package com.manish.payments.service.impl.statusprocessor;

import org.springframework.stereotype.Component;

import com.manish.payments.interfaces.TransactionStatusProcessor;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class FailedStatusProcessor implements TransactionStatusProcessor {

	@Override
	public String processStatus(int statusId) {
		log.info("processing the failed status for statusId: {}", statusId);
		
		return "status failed successfully for statusId: " + statusId;
	}

}
