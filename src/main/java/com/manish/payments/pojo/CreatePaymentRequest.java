package com.manish.payments.pojo;

import lombok.Data;

@Data
public class CreatePaymentRequest {

    private int userId;
    private int paymentTypeId;
    private int paymentMethodId;
    private int providerId;
    
//    private BigDecimal amount;
    private double amount;
    private String currency;
    
    private String merchantTxnReference;
}