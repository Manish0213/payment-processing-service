package com.manish.payments.constant;

import lombok.Getter;

@Getter
public enum ErrorCodeEnum {
	
	GENERIC_ERROR("30000", "Something went wrong. Please try again later."),
	PAYPAL_PROVIDER_SERVICE_UNAVAILABLE("30001", "PayPal Provider service is currently unavailable"),
	UNEXPECTED_HTTP_SERVICE_ERROR("30002", "An unexpected error occurred while making the HTTP request"),
	RESOURCE_NOT_FOUND("30003", "Invalid URL. Please check and try again."),
	NO_STATUS_PROCESSOR_FOUND("20003", "No status processor found."),
	PAYPAL_ERROR("30012", "<Error as Paypal>"),
	PAYPAL_UNKNOWN_ERROR("30009", "An unknown error occurred while processing the PayPal request");
	
	private final String errorCode;
	private final String errorMessage;
	
	ErrorCodeEnum(String errorCode, String errorMessage) {
		this.errorCode = errorCode;
		this.errorMessage = errorMessage;
	}
}