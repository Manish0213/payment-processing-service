package com.manish.payments.service.helper;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.manish.payments.constant.Constant;
import com.manish.payments.constant.ErrorCodeEnum;
import com.manish.payments.dto.TransactionDto;
import com.manish.payments.exception.ProcessingServiceException;
import com.manish.payments.http.HttpRequest;
import com.manish.payments.paypalprovider.PPCreateOrderRequest;
import com.manish.payments.paypalprovider.PPOrderResponse;
import com.manish.payments.pojo.InitiatePaymentRequest;
import com.manish.payments.pojo.PPErrorResponse;
import com.manish.payments.util.JsonUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class PPCreateOrderHelper {
	
	private final JsonUtil jsonUtil;
	
	@Value("${paypal.provider.create.order.url}")
	private String providerCreateOrderUrl;

	public HttpRequest prepareHttpRequest(
			String txnReference, InitiatePaymentRequest initiatePaymentRequest, TransactionDto txnDto) {
		log.info("Preparing HttpRequest for creating PayPal order... || initiatePaymentRequest: {} || txnReference: {} || txnDto: {}",
				initiatePaymentRequest, txnReference, txnDto);
		
		// prepare http header
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setContentType(MediaType.APPLICATION_JSON);
		
		//prepare body
		PPCreateOrderRequest ppCreateOrderRequest = new PPCreateOrderRequest();
		ppCreateOrderRequest.setCurrencyCode(txnDto.getCurrency());
		ppCreateOrderRequest.setAmount(txnDto.getAmount().doubleValue());
		ppCreateOrderRequest.setSuccessUrl(initiatePaymentRequest.getSuccessUrl());
		ppCreateOrderRequest.setCancelUrl(initiatePaymentRequest.getCancelUrl());
		
		String requestAsJson = jsonUtil.toJson(ppCreateOrderRequest);
		
		// preare HttpRequest
		HttpRequest httpRequest = new HttpRequest();
		httpRequest.setUrl(providerCreateOrderUrl);
		httpRequest.setMethod(HttpMethod.POST);
		httpRequest.setHeaders(httpHeaders);
		httpRequest.setBody(requestAsJson);
		log.info("Prepared HttpRequest for creating PayPal order: {}", httpRequest);
		
		return httpRequest;	
	}
	
	public PPOrderResponse processResponse(ResponseEntity<String> httpResponse) {
		log.info("Processing response from PayPal API... || httpResponse: {}", httpResponse);
		
		if(httpResponse.getStatusCode().equals(HttpStatus.OK)) {
			log.info("Received successful response from PayPal API: {}", 
					httpResponse.getBody());
			
			PPOrderResponse ppOrderResponse = jsonUtil.fromJson(
					httpResponse.getBody(), PPOrderResponse.class);
			log.info("Parsed PayPal API response into PPOrderResponse: {}", ppOrderResponse);
			
			if(ppOrderResponse != null 
					&& ppOrderResponse.getOrderId() != null
					&& !ppOrderResponse.getOrderId().isEmpty()
					&& ppOrderResponse.getPaypalStatus() != null
					&& ppOrderResponse.getPaypalStatus().equalsIgnoreCase(Constant.PAYER_ACTION_REQUIRED)
					&& ppOrderResponse.getRedirectUrl() != null
					&& !ppOrderResponse.getRedirectUrl().isEmpty()) {
				log.info("Parsed PayPal API response contains all required fields");
				
				return ppOrderResponse;
			}
		}
		
		if(httpResponse.getStatusCode().is4xxClientError() 
				|| httpResponse.getStatusCode().is5xxServerError()) {
			log.error("Received error response from PayPal API: {}", httpResponse);
			
			PPErrorResponse ppErrorResponse = jsonUtil.fromJson(
					httpResponse.getBody(), PPErrorResponse.class);
			log.info("Parsed PayPal API error response into PPErrorResponse: {}", ppErrorResponse);
			
			throw new ProcessingServiceException(
					ppErrorResponse.getErrorCode(),
					ppErrorResponse.getErrorMessage(),
					HttpStatus.valueOf(httpResponse.getStatusCode().value())
					);
		}
		
		log.error("Unknown error occurred while processing PayPal API response: {}", httpResponse);
		throw new ProcessingServiceException(
				ErrorCodeEnum.PAYPAL_PROVIDER_UNKNOWN_ERROR.getErrorCode(),
				ErrorCodeEnum.PAYPAL_PROVIDER_UNKNOWN_ERROR.getErrorMessage(),
				HttpStatus.BAD_GATEWAY
				);
	}
}