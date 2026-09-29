package com.manish.payments.service;

import com.manish.payments.pojo.CreatePaymentRequest;
import com.manish.payments.pojo.InitiatePaymentRequest;
import com.manish.payments.pojo.PaymentResponse;

public interface PaymentService {
	
	public PaymentResponse createPayment(CreatePaymentRequest createPaymentRequest);
	
	public PaymentResponse initiatePayment(String txnReference, InitiatePaymentRequest initiatePaymentRequest);
	
	public String capturePayment(String txnReference);
}
