package com.manish.payments.service.helper;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import com.manish.payments.http.HttpRequest;
import com.manish.payments.paypalprovider.PPCreateOrderRequest;
import com.manish.payments.pojo.InitiatePaymentRequest;
import com.manish.payments.util.JsonUtil;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PPCreateOrderHelper {
	
	private final JsonUtil jsonUtil;

	public HttpRequest prepareHttpRequest(String txnReference, InitiatePaymentRequest initiatePaymentRequest) {
		
		// prepare http header
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setContentType(MediaType.APPLICATION_JSON);
		
		//prepare body
		PPCreateOrderRequest ppCreateOrderRequest = new PPCreateOrderRequest();
		ppCreateOrderRequest.setCurrencyCode("USD");
		ppCreateOrderRequest.setAmount(234.67);
		ppCreateOrderRequest.setSuccessUrl(initiatePaymentRequest.getSuccessUrl());
		ppCreateOrderRequest.setCancelUrl(initiatePaymentRequest.getCancelUrl());
		
		String requestAsJson = jsonUtil.toJson(ppCreateOrderRequest);
		
		HttpRequest httpRequest = new HttpRequest();
		httpRequest.setUrl("http://localhost:8082/order");
		httpRequest.setMethod(HttpMethod.POST);
		httpRequest.setHeaders(httpHeaders);
		httpRequest.setBody(requestAsJson);
		
		return httpRequest;	
	}
	
}