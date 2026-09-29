package com.manish.payments.service.impl.statusprocessor;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import com.manish.payments.dao.interfaces.TransactionDao;
import com.manish.payments.dto.TransactionDto;
import com.manish.payments.entity.TransactionEntity;
import com.manish.payments.interfaces.TransactionStatusProcessor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Component
public class PendingStatusProcessor implements TransactionStatusProcessor {
	
	private final TransactionDao transactionDao;
	
	private final ModelMapper modelMapper;

	@Override
	public TransactionDto processStatus(TransactionDto txnDto) {
		log.info("processing the pending status for statusId: {}", txnDto.getTxnStatusId());
		
		TransactionEntity txnEntity = modelMapper.map(txnDto, TransactionEntity.class);
		log.info("Mapped TransactionDto to TransactionEntity: {}", txnEntity);
		
		transactionDao.updateTransaction(txnEntity);
		log.info("");
				
		log.info("Returning TransactionDto with updated statusId: {}", txnDto);
		return txnDto;
	}

}