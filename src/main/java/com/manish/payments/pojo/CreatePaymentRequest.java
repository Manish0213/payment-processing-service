package com.manish.payments.pojo;

import lombok.Data;

@Data
public class CreatePaymentRequest {

    private Integer userId;
    private Integer paymentTypeId;
    private Integer paymentMethodId;
    private Integer providerId;
    
//    private BigDecimal amount;
    private Double amount;
    private String currency;
    
    private String merchantTxnReference;
}