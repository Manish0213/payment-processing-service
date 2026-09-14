package com.manish.payments.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.manish.payments.constant.ErrorCodeEnum;
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
	
	public String processPayment(int statusId) {
		log.info("processing the payment for statusId: {}", statusId);
		
		TransactionStatusProcessor transactionStatusProcessor =
				paymentStatusFactory.getStatusProcessor(statusId);
		
		if(transactionStatusProcessor == null) {
			log.error("No processor found for statusId: {}", statusId);
			
			throw new ProcessingServiceException(
					ErrorCodeEnum.NO_STATUS_PROCESSOR_FOUND.getErrorCode(),
					ErrorCodeEnum.NO_STATUS_PROCESSOR_FOUND.getErrorCode(),
					HttpStatus.INTERNAL_SERVER_ERROR
					);
		}
		
		String response = transactionStatusProcessor.processStatus(statusId);
		return response;
	}
}
