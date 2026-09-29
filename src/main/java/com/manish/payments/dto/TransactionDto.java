package com.manish.payments.dto;

import java.math.BigDecimal;
import java.sql.Timestamp;

import lombok.Data;

@Data
public class TransactionDto {
	
	private Integer id;
    private Integer userId;

    private Integer paymentTypeId;
    private Integer paymentMethodId;
    private Integer providerId;

    private BigDecimal amount;
    private String currency;

    private String merchantTxnReference;
    private String txnReference;
    private String providerReference;

    private Integer txnStatusId;

    private String errorCode;
    private String errorDescription;

    private Integer retry;
    private Timestamp creationDate;
}
