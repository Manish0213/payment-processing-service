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
import com.manish.payments.paypalprovider.PPOrderResponse;
import com.manish.payments.pojo.PPErrorResponse;
import com.manish.payments.util.JsonUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class PPCaptureOrderHelper {
	
	private final JsonUtil jsonUtil;
	
	@Value("${paypal.provider.capture.order.url}")
	private String providerCaptureOrderUrl;
	
	public HttpRequest prepareCaptureOrderRequest(TransactionDto txnDto) {
		log.info("Preparing HttpRequest for capturing payment ... || txnDto: {}", txnDto);
		
		// prepare header
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setContentType(MediaType.APPLICATION_JSON);
		
		// prepare body
		String reqBody = "";
		
		// prepare URL
		String captureUrl = providerCaptureOrderUrl.replace(Constant.PROVIDER_REFERENCE_REF, txnDto.getProviderReference());
		
		// Prepare Http Request
		HttpRequest httpRequest = new HttpRequest();
		httpRequest.setMethod(HttpMethod.POST);
		httpRequest.setUrl(captureUrl);
		httpRequest.setHeaders(httpHeaders);
		httpRequest.setBody(reqBody);
		log.info("Prepared HttpRequest for creating PayPal order: {}", httpRequest);
		
		return httpRequest;
	}
	
	public PPOrderResponse processResponse(ResponseEntity<String> httpResponse) {
		log.info("Processing capture response from paypal-provider API... || httpResponse: {}", 
				httpResponse);
		
		if(httpResponse.getStatusCode().equals(HttpStatus.OK)) {
			log.info("Received successful response from paypal-provider API: {}", 
					httpResponse.getBody());
			
			PPOrderResponse ppOrderResponse = jsonUtil.fromJson(
					httpResponse.getBody(), PPOrderResponse.class);
			log.info("Parsed paypal-provider capture API response into PPOrderResponse: {}", 
					ppOrderResponse);
			
			if(ppOrderResponse != null
					&& ppOrderResponse.getOrderId() != null
					&& !ppOrderResponse.getOrderId().isEmpty()
					&& ppOrderResponse.getPaypalStatus().equals(Constant.COMPLETED));
			log.info("Parsed paypal-provider capture API response contains all required fields");
			
			return ppOrderResponse;
		}
		
		if(httpResponse.getStatusCode().is4xxClientError() ||
			httpResponse.getStatusCode().is5xxServerError()) {
			log.error("Received error response from paypal-provider API: {}", httpResponse);
			
			PPErrorResponse ppErrorResponse = jsonUtil.fromJson(httpResponse.getBody(),
					PPErrorResponse.class);
			log.info("Parsed paypal-provider capture API error response into PPErrorResponse: {}",
					ppErrorResponse);
			
			throw new ProcessingServiceException (
					ppErrorResponse.getErrorCode(),
					ppErrorResponse.getErrorMessage(),
					HttpStatus.valueOf(httpResponse.getStatusCode().value())
					);
		}
		
		log.error("Unknown error occurred while processing paypal-provider capture API response: {}", 
				httpResponse);
		throw new ProcessingServiceException(
				ErrorCodeEnum.PAYPAL_PROVIDER_UNKNOWN_ERROR.getErrorCode(),
				ErrorCodeEnum.PAYPAL_PROVIDER_UNKNOWN_ERROR.getErrorMessage(),
				HttpStatus.BAD_GATEWAY
				);
	}

}
