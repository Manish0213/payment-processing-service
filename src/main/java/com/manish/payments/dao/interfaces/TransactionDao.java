package com.manish.payments.dao.interfaces;

import com.manish.payments.entity.TransactionEntity;

public interface TransactionDao {
	
	public void updateTransaction(TransactionEntity txnEntity);
	public TransactionEntity createTransaction(TransactionEntity txnEntity);
	public TransactionEntity getTransactionByTxnReference(String txnReference);
	
}