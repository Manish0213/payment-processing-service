package com.manish.payments.paypalprovider;

import lombok.Data;

@Data
public class PPCreateOrderRequest {
	
	private String currencyCode;
	private Double amount;
	private String successUrl;
	private String cancelUrl;
}