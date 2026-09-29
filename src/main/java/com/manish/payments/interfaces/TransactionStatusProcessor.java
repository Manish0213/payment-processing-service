package com.manish.payments.interfaces;

import com.manish.payments.dto.TransactionDto;

public interface TransactionStatusProcessor {
	
	public TransactionDto processStatus(TransactionDto txnDto);
	
}
