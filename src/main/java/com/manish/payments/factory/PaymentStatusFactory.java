package com.manish.payments.factory;

import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import com.manish.payments.interfaces.TransactionStatusProcessor;
import com.manish.payments.service.impl.statusprocessor.ApprovedStatusProcessor;
import com.manish.payments.service.impl.statusprocessor.CreatedStatusProcessor;
import com.manish.payments.service.impl.statusprocessor.FailedStatusProcessor;
import com.manish.payments.service.impl.statusprocessor.InitiatedStatusProcessor;
import com.manish.payments.service.impl.statusprocessor.PendingStatusProcessor;
import com.manish.payments.service.impl.statusprocessor.SuccessStatusProcessor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class PaymentStatusFactory {
	
	private final ApplicationContext applicationContext;
	
	public TransactionStatusProcessor getStatusProcessor(int statusId) {
		log.info("Getting status processor for statusId: {}", statusId);
		
		switch (statusId) {
			case 1:
				log.info("Returning CreatedStatusProcessor for statusId: {}", statusId);
				return applicationContext.getBean(CreatedStatusProcessor.class);
			case 2:
				log.info("Returning InitiatedStatusProcessor for statusId: {}", statusId);
				return applicationContext.getBean(InitiatedStatusProcessor.class);
			case 3:
				log.info("Returning PendingStatusProcessor for statusId: {}", statusId);
				return applicationContext.getBean(PendingStatusProcessor.class);
			case 4:
				log.info("Returning ApprovedStatusProcessor for statusId: {}", statusId);
				return applicationContext.getBean(ApprovedStatusProcessor.class);
			case 5:
				log.info("Returning SuccessStatusProcessor for statusId: {}", statusId);
				return applicationContext.getBean(SuccessStatusProcessor.class);
			case 6:
				log.info("Returning FailedStatusProcessor for statusId: {}", statusId);
				return applicationContext.getBean(FailedStatusProcessor.class);
				
			default:
				log.warn("No status processor found for statusId: {}", statusId);
				return null;
		}
	}
}
