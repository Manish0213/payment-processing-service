package com.manish.payments.service.impl;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.manish.payments.http.HttpRequest;
import com.manish.payments.http.HttpServiceEngine;
import com.manish.payments.pojo.CreatePaymentRequest;
import com.manish.payments.pojo.InitiatePaymentRequest;
import com.manish.payments.pojo.OrderResponse;
import com.manish.payments.service.PaymentService;
import com.manish.payments.service.PaymentStatusService;
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

	@Override
	public String createPayment(CreatePaymentRequest createPaymentRequest) {
		log.info("Creating payment with request: {}", createPaymentRequest);
		
		String response = paymentStatusService.processPayment(1);
		return "Payment created successfully!" + createPaymentRequest + "/n" + response;
	}

	@Override
	public String initiatePayment(String txnReference, InitiatePaymentRequest initiatePaymentRequest) {
		log.info("initiating payment for txnReference: {} || initiatePaymentRequest: {}", txnReference, initiatePaymentRequest);
		
		HttpRequest httpRequest = ppCreateOrderHelper.prepareHttpRequest(txnReference, initiatePaymentRequest);
		log.info("HttpRequest prepared for txnReference: {} || httpRequest: {}", txnReference, httpRequest);
		
		ResponseEntity<String> successResponse =  httpServiceEngine.makeHttpCall(httpRequest);
		log.info("HttpResponse received for txnReference: {} || httpResponse: {}", txnReference, successResponse);
		
		return successResponse.getBody();
	}

	@Override
	public String capturePayment(String txnReference) {
		// TODO Auto-generated method stub
		return "Payment captured for txnReference: " + txnReference;
	}

}
