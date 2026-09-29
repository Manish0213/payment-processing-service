package com.manish.payments.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.manish.payments.constant.ErrorCodeEnum;
import com.manish.payments.dto.TransactionDto;
import com.manish.payments.exception.ProcessingServiceException;
import com.manish.payments.factory.PaymentStatusFactory;
import com.manish.payments.interfaces.TransactionStatusProcessor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class PaymentStatusService {
	
//	private TransactionStatusProcessor transactionStatusProcessor; // not good
	private final PaymentStatusFactory paymentStatusFactory;
	
	public TransactionDto processPayment(TransactionDto txnDto) {
		log.info("processing the payment for statusId: {}", txnDto);
		
		int statusId = txnDto.getTxnStatusId();
		
		TransactionStatusProcessor transactionStatusProcessor =
				paymentStatusFactory.getStatusProcessor(statusId);
		log.info("Obtained TransactionStatusProcessor: {}", transactionStatusProcessor);
		
		if(transactionStatusProcessor == null) {
			log.error("No processor found for statusId: {}", statusId);
			
			throw new ProcessingServiceException(
					ErrorCodeEnum.NO_STATUS_PROCESSOR_FOUND.getErrorCode(),
					ErrorCodeEnum.NO_STATUS_PROCESSOR_FOUND.getErrorCode(),
					HttpStatus.INTERNAL_SERVER_ERROR
					);
		}
		
		TransactionDto response = transactionStatusProcessor.processStatus(txnDto);
		log.info("Response from TransactionStatusProcessor: {}", response);
		
		return response;
	}
}
