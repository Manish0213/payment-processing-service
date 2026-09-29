package com.manish.payments.pojo;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PaymentResponse {
	
	private String txnReference;
	private int txnStatusId;
	
	private String providerReference;
	private String redirectUrl;
}