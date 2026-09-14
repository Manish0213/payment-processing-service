package com.manish.payments.service;

import com.manish.payments.pojo.CreatePaymentRequest;
import com.manish.payments.pojo.InitiatePaymentRequest;

public interface PaymentService {
	
	public String createPayment(CreatePaymentRequest createPaymentRequest);
	
	public String initiatePayment(String txnReference, InitiatePaymentRequest initiatePaymentRequest);
	
	public String capturePayment(String txnReference);
}
