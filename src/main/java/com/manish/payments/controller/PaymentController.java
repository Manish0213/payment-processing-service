package com.manish.payments.controller;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.manish.payments.pojo.CreatePaymentRequest;
import com.manish.payments.pojo.InitiatePaymentRequest;
import com.manish.payments.service.PaymentService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequiredArgsConstructor
public class PaymentController {
	
	private final PaymentService paymentService;
	
	@PostMapping("/payment")
	public String createPayment(@RequestBody CreatePaymentRequest createPaymentRequest) {
		log.info("creating payment with createPaymentRequest: {}", createPaymentRequest);
		
		String response = paymentService.createPayment(createPaymentRequest);
		return response;
	}
	
	@PostMapping("/payment/{txnReference}/initiate")
	public String initiatePayment(@PathVariable String txnReference, 
			@RequestBody InitiatePaymentRequest initiatePaymentRequest) {
		log.info("initiating payment for txnReference: {} "
				+ "|| initiatePaymentRequest: {}", txnReference, initiatePaymentRequest);
		
		String response = paymentService.initiatePayment(txnReference, initiatePaymentRequest);
		return response;
	}
	
	@PostMapping("/payment/{txnReference}/capture")
	public String capturePayment(@PathVariable String txnReference) {
		log.info("capture payment for txnReference: {}", txnReference);
		
		String response = paymentService.capturePayment(txnReference);
		return response;
	}
}
