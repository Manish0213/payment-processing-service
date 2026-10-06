package com.manish.payments.constant;

import lombok.Getter;

@Getter
public enum ErrorCodeEnum {
	
	GENERIC_ERROR("20000", "Something went wrong. Please try again later."),
	PAYPAL_PROVIDER_SERVICE_UNAVAILABLE("20001", "PayPal Provider service is currently unavailable"),
	UNEXPECTED_HTTP_SERVICE_ERROR("20002", "An unexpected error occurred while making the HTTP request"),
	RESOURCE_NOT_FOUND("20003", "Invalid URL. Please check and try again."),
	NO_STATUS_PROCESSOR_FOUND("20004", "No status processor found."),
	PAYPAL_PROVIDER_UNKNOWN_ERROR("20005", "Unknown error occurred in paypal-provider service."),
	ERROR_UPDATING_TRANSACTION("20006", "Error occurred while updating transaction in DB."),
	ALREADY_PROCESSED_STATUS("20007", "this payment is already processed and is in failed status");
	
	private final String errorCode;
	private final String errorMessage;
	
	ErrorCodeEnum(String errorCode, String errorMessage) {
		this.errorCode = errorCode;
		this.errorMessage = errorMessage;
	}
}