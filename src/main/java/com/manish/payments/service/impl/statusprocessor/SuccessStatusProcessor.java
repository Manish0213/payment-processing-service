package com.manish.payments.service.impl.statusprocessor;

import com.manish.payments.dto.TransactionDto;
import com.manish.payments.interfaces.TransactionStatusProcessor;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SuccessStatusProcessor implements TransactionStatusProcessor {

	@Override
	public TransactionDto processStatus(TransactionDto txnDto) {
		log.info("processing the success status for statusId: {}", txnDto.getTxnStatusId());
		
		return txnDto;
	}

}
