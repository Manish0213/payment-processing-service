package com.manish.payments.service.impl;

import java.util.UUID;

import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.manish.payments.constant.ErrorCodeEnum;
import com.manish.payments.dao.interfaces.TransactionDao;
import com.manish.payments.dto.TransactionDto;
import com.manish.payments.entity.TransactionEntity;
import com.manish.payments.exception.ProcessingServiceException;
import com.manish.payments.http.HttpRequest;
import com.manish.payments.http.HttpServiceEngine;
import com.manish.payments.paypalprovider.PPOrderResponse;
import com.manish.payments.pojo.CreatePaymentRequest;
import com.manish.payments.pojo.InitiatePaymentRequest;
import com.manish.payments.pojo.PaymentResponse;
import com.manish.payments.service.PaymentService;
import com.manish.payments.service.PaymentStatusService;
import com.manish.payments.service.helper.PPCaptureOrderHelper;
import com.manish.payments.service.helper.PPCreateOrderHelper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentServiceImpl implements PaymentService{
	
	private final PaymentStatusService paymentStatusService;
	
	private final PPCreateOrderHelper ppCreateOrderHelper;
	
	private final HttpServiceEngine httpServiceEngine;
	
	private final ModelMapper modelMapper;
	
	private final TransactionDao transactionDao;
	
	private final PPCaptureOrderHelper ppCaptureOrderHelper;
	
	@Override
	public PaymentResponse createPayment(CreatePaymentRequest createPaymentRequest) {
		log.info("Creating payment with request: {}", createPaymentRequest);
		
		TransactionDto txnDto = modelMapper.map(createPaymentRequest, TransactionDto.class);
		log.info("Mapped CreatePaymentRequest to TransactionDto: {}", txnDto);
		
		// generate a UUID transaction reference (txnReference) for the payment
		String txnReference = UUID.randomUUID().toString();  // for every payment, have unique reference
		txnDto.setTxnReference(txnReference);
		
		int statusId = 1; // CREATED
		txnDto.setTxnStatusId(statusId);
		
		txnDto.setRetry(0);
		
		TransactionDto response = paymentStatusService.processPayment(txnDto);
		log.info("Response from PaymentStatusService after processing payment: {}", response);
		
		PaymentResponse paymentResponse = new PaymentResponse();
		paymentResponse.setTxnReference(response.getTxnReference());
		paymentResponse.setTxnStatusId(response.getTxnStatusId());
		log.info("Returning PaymentResponse: {}", paymentResponse);
		
		return paymentResponse;
	}

	@Override
	public PaymentResponse initiatePayment(String txnReference, InitiatePaymentRequest initiatePaymentRequest) {
		log.info("initiating payment for txnReference: {} || initiatePaymentRequest: {}", 
				txnReference, initiatePaymentRequest);
		
		TransactionEntity txnEntity = transactionDao.getTransactionByTxnReference(txnReference);
		log.info("Fetched TransactionEntity from database for txnReference: {} || txnEntity: {}", txnReference, txnEntity);
		
		TransactionDto txnDto = modelMapper.map(txnEntity, TransactionDto.class);
		int statusId = 2; // INITIATED
		txnDto.setTxnStatusId(statusId); 
		
		TransactionDto response = paymentStatusService.processPayment(txnDto);
		log.info("Response from PaymentStatusService after processing payment: {}", response);
		
		HttpRequest httpRequest = ppCreateOrderHelper.prepareHttpRequest(
				txnReference, initiatePaymentRequest, txnDto);
		log.info("HttpRequest prepared for txnReference: {} || httpRequest: {}", 
				txnReference, httpRequest);
		
		PPOrderResponse ppOrderResponse = null;
		try {
		ResponseEntity<String> httpResponse =  httpServiceEngine.makeHttpCall(httpRequest);
		log.info("HttpResponse received for txnReference: {} || httpResponse: {}", 
				txnReference, httpResponse);
		
//		OrderResponse orderResponse = processResponse(httpResponse); totally wrong 
		
//		processResponse(httpResponse, response); this is wrong
		
		ppOrderResponse = ppCreateOrderHelper.processResponse(httpResponse);
		log.info("Processed HttpResponse to PPOrderResponse: {}", ppOrderResponse);
		
		} catch(ProcessingServiceException e) {
			log.error("Error occurred while making HTTP call to PayPalProvider: ", e);
			
			// update txn status to FAILED
			statusId = 6; // FAILED
			txnDto.setTxnStatusId(statusId); 
			txnDto.setErrorCode(e.getErrorCode());
			txnDto.setErrorDescription(e.getErrorMessage());
			
			response = paymentStatusService.processPayment(txnDto);
			log.info("Updated transaction status to FAILED for txnReference: {}", txnReference);
			
			throw e;
		} catch(Exception e) {
			log.error("Error occurred while making HTTP call to PayPalProvider: ", e);
			
			// update txn status to FAILED
			statusId = 6; // FAILED
			txnDto.setTxnStatusId(statusId); 
			// we can change here the correct error code enum
			txnDto.setErrorCode(ErrorCodeEnum.GENERIC_ERROR.getErrorCode());
			txnDto.setErrorDescription(e.getMessage());
			
			response = paymentStatusService.processPayment(txnDto);
			log.info("Updated transaction status to FAILED for txnReference: {}", txnReference);
			
			throw e;
		}
		
		// update txn status to PENDNG
		txnDto.setTxnStatusId(3); // PENDING
		txnDto.setProviderReference(ppOrderResponse.getOrderId());
		response = paymentStatusService.processPayment(txnDto);
		
		PaymentResponse paymentResponse = new PaymentResponse();
		paymentResponse.setTxnReference(response.getTxnReference());
		paymentResponse.setTxnStatusId(response.getTxnStatusId());
		paymentResponse.setProviderReference(ppOrderResponse.getOrderId());
		paymentResponse.setRedirectUrl(ppOrderResponse.getRedirectUrl());
		
		return paymentResponse;
	}

	@Override
	public PaymentResponse capturePayment(String txnReference) {
		log.info("Capturing payment for txnReference: {}", txnReference);
		
		TransactionEntity txnEntity = transactionDao.getTransactionByTxnReference(txnReference);
		log.info("Fetched TransactionEntity from database for txnReference: {} || txnEntity: {}", 
				txnReference, txnEntity);
		
		// check if status is already in failed status
		if(txnEntity.getTxnStatusId() == 6) {
			throw new ProcessingServiceException (
				ErrorCodeEnum.ALREADY_PROCESSED_STATUS.getErrorCode(),
				ErrorCodeEnum.ALREADY_PROCESSED_STATUS.getErrorMessage(),
				HttpStatus.INTERNAL_SERVER_ERROR);
		}
				
		// check if status is already in success status
		// TODO
		
		TransactionDto txnDto = modelMapper.map(txnEntity, TransactionDto.class);
		log.info("Mapped TransactionDto to TransactionEntity: {}", txnEntity);
		
		// update to APPROVED status
		int statusId = 4;
		txnDto.setTxnStatusId(statusId); // APPROVED
		TransactionDto response = paymentStatusService.processPayment(txnDto);
		log.info("Response from PaymentStatusService after processing payment: {}", response);
		
		HttpRequest httpRequest = ppCaptureOrderHelper.prepareCaptureOrderRequest(txnDto);
		log.info("HttpRequest prepared for txnReference: {} || httpRequest: {}", 
				txnReference, httpRequest);
		
		try {
		ResponseEntity<String> httpResponse = httpServiceEngine.makeHttpCall(httpRequest);
		log.info("HttpResponse received for txnReference: {} || httpResponse: {}", 
				txnReference, httpResponse);
		
		PPOrderResponse ppOrderResponse = ppCaptureOrderHelper.processResponse(httpResponse);
		log.info("Processed HttpResponse to PPOrderResponse: {}", ppOrderResponse);
		} catch(Exception e) {
			log.error("Error occurred while making captureOrder HTTP call to PayPalProvider: ", e);
			
// 			Note, don't change the status to FAILED since user already APPROVED.
//			Let reconciliation job handle such cases.
//			In case reconciliation also resolved it as failed,
//			then manually back-office can handle this payment..
//			just throw error back
			
			throw e;
		}
		
		// update status to SUCCESS
		statusId = 5;
		txnDto.setTxnStatusId(statusId); // SUCCESS
		response = paymentStatusService.processPayment(txnDto);
		log.info("Response from PaymentStatusService after processing payment: {}", response);
		
		PaymentResponse paymentResponse = new PaymentResponse();
		paymentResponse.setTxnReference(txnReference);
		paymentResponse.setTxnStatusId(response.getTxnStatusId());
		
		return paymentResponse;
	}
	
//	private void processResponse(ResponseEntity<String> httpResponse, TransactionDto response) {
//	
//	if(httpResponse.getStatusCode().is2xxSuccessful()) {
//		PPOrderResponse ppOrderResponse = jsonUtil.fromJson(
//				httpResponse.getBody(), PPOrderResponse.class);
//		
//		if(ppOrderResponse != null ) {
//			PaymentResponse paymentResponse = new PaymentResponse();
//			paymentResponse.setTxnReference(response.getTxnReference());
//			paymentResponse.setTxnStatusId(response.getTxnStatusId());
////			paymentResponse.setProviderReference(ppOrde)
//			paymentResponse.setOrderId(ppOrderResponse.getOrderId());
//			paymentResponse.setRedirectUrl(ppOrderResponse.getRedirectUrl());
//		}
//		
//	}
//	
//}

}
