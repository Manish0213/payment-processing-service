package com.manish.payments.dao.impl;

import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.manish.payments.constant.ErrorCodeEnum;
import com.manish.payments.dao.interfaces.TransactionDao;
import com.manish.payments.entity.TransactionEntity;
import com.manish.payments.exception.ProcessingServiceException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Repository
@RequiredArgsConstructor
@Slf4j
public class TransactionDaoImpl implements TransactionDao {
	
	private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	
	@Override
	public TransactionEntity createTransaction(TransactionEntity txnEntity) {
		log.info("Creating transaction with entity: {}", txnEntity);

        String sql = """
                INSERT INTO Transaction
                (
                    userId,
                    paymentTypeId,
                    paymentMethodId,
                    providerId,
                    amount,
                    currency,
                    merchantTxnReference,
                    txnReference,
                    providerReference,
                    txnStatusId,
                    errorCode,
                    errorDescription,
                    retry
                )
                VALUES
                (
                    :userId,
                    :paymentTypeId,
                    :paymentMethodId,
                    :providerId,
                    :amount,
                    :currency,
                    :merchantTxnReference,
                    :txnReference,
                    :providerReference,
                    :txnStatusId,
                    :errorCode,
                    :errorDescription,
                    :retry
                )
                """;

//        MapSqlParameterSource params = new MapSqlParameterSource()
//                .addValue("userId", txnEntity.getUserId())
//                .addValue("paymentTypeId", txnEntity.getPaymentTypeId())
//                .addValue("paymentMethodId", txnEntity.getPaymentMethodId())
//                .addValue("providerId", txnEntity.getProviderId())
//                .addValue("amount", txnEntity.getAmount())
//                .addValue("currency", txnEntity.getCurrency())
//                .addValue("merchantTxnReference",
//                        txnEntity.getMerchantTxnReference())
//                .addValue("txnReference",
//                        txnEntity.getTxnReference())
//                .addValue("providerReference",
//                        txnEntity.getProviderReference())
//                .addValue("txnStatusId",
//                        txnEntity.getTxnStatusId())
//                .addValue("errorCode",
//                        txnEntity.getErrorCode())
//                .addValue("errorDescription",
//                        txnEntity.getErrorDescription())
//                .addValue("retry",
//                        txnEntity.getRetry());
        
        SqlParameterSource params =
                new BeanPropertySqlParameterSource(txnEntity);

        KeyHolder keyHolder = new GeneratedKeyHolder();
        
        // handle duplicate entry with same transaction reference -
        // if with same transaction reference we are creating payment then interval server error 
        // occur becuase transaction reference has unique constraint.
        // and if we want to handle it and give proper error message to the client then we have to 
        // check if payment available in db with given txnReference then we throw custom exception with
        // proper error code & error message. like we do in signup.

        namedParameterJdbcTemplate.update(sql, params, keyHolder, new String[]{"id"});
        
        Number generatedId = keyHolder.getKey();
        log.info("Generated ID: {}", generatedId);
        
        if(generatedId != null) {
        	txnEntity.setId(keyHolder.getKey().intValue());
        }
        
        return txnEntity;
    }

	@Override
	public void updateTransaction(TransactionEntity txnEntity) {
	    log.info("Updating transaction with entity: {}", txnEntity);

	    String sql = """
	            UPDATE `Transaction`
	            SET txnStatusId = :txnStatusId,
	                providerReference = :providerReference,
	                errorCode = :errorCode,
	                errorDescription = :errorDescription
	            WHERE id = :id
	            """;

	    MapSqlParameterSource params = new MapSqlParameterSource()
	            .addValue("txnStatusId", txnEntity.getTxnStatusId())
	            .addValue("providerReference", txnEntity.getProviderReference())
	            .addValue("errorCode", txnEntity.getErrorCode())
	            .addValue("errorDescription", txnEntity.getErrorDescription())
	            .addValue("id", txnEntity.getId());

	    int rowsUpdated = namedParameterJdbcTemplate.update(sql, params);

	    log.info("Transaction updated successfully. id: {}, rowsUpdated: {}",
	            txnEntity.getId(), rowsUpdated);

	    if (rowsUpdated == 0) {
	        throw new ProcessingServiceException(
	                ErrorCodeEnum.ERROR_UPDATING_TRANSACTION.getErrorCode(),
	                ErrorCodeEnum.ERROR_UPDATING_TRANSACTION.getErrorMessage(),
	                HttpStatus.INTERNAL_SERVER_ERROR);
	    }
	}

	@Override
	public TransactionEntity getTransactionByTxnReference(String txnReference) {
		log.info("Fetching transaction with txnReference: {}", txnReference);

	    String sql = """
	            SELECT *
	            FROM Transaction
	            WHERE txnReference = :txnReference
	            """;

	    MapSqlParameterSource params = new MapSqlParameterSource()
	            .addValue("txnReference", txnReference);

	    TransactionEntity txnEntity = namedParameterJdbcTemplate.queryForObject(
	            sql,
	            params,
	            new BeanPropertyRowMapper<>(TransactionEntity.class)
	    );
	    
	    log.info("");
		return txnEntity;
	}

}
