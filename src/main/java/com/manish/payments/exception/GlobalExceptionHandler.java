package com.manish.payments.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import com.manish.payments.constant.ErrorCodeEnum;
import com.manish.payments.pojo.ErrorResponse;

import lombok.extern.slf4j.Slf4j;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
	
	@ExceptionHandler(ProcessingServiceException.class)
    public ResponseEntity<ErrorResponse> handleProcessingServiceException(
    		ProcessingServiceException ex) {
		
        log.error("Processing Service Exception | errorCode: {} | message: {}",
        	    ex.getErrorCode(),
        	    ex.getErrorMessage(),
        	    ex
        	);

//        return ResponseEntity
//                .status(ex.getHttpStatus())
//                .body(ex);
        
        ErrorResponse errorResponse = new ErrorResponse(
				ex.getErrorCode(),
				ex.getErrorMessage()
		);
        
//        return errorResponse;
        
        return ResponseEntity
				.status(ex.getHttpStatus())
				.body(errorResponse);
    }
	
	@ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(
    		Exception ex) {
		
        log.error("Generic Exception | errorCode: {} | message: {}",
        	    ErrorCodeEnum.GENERIC_ERROR.getErrorCode(),
        	    ErrorCodeEnum.GENERIC_ERROR.getErrorMessage(),
        	    ex
        	);
        
        ErrorResponse errorResponse = new ErrorResponse(
        		ErrorCodeEnum.GENERIC_ERROR.getErrorCode(),
        		ErrorCodeEnum.GENERIC_ERROR.getErrorMessage()
		);
        
        return ResponseEntity
				.status(HttpStatus.INTERNAL_SERVER_ERROR)
				.body(errorResponse);
    }
	
	@ExceptionHandler(NoResourceFoundException.class)
	public ResponseEntity<ErrorResponse> handleNoResourceFoundException(
		NoResourceFoundException ex) {
		log.error("Handling NoResourceFoundException: {}", ex.getMessage(), ex);

		ErrorResponse errorResponse = new ErrorResponse(
			ErrorCodeEnum.RESOURCE_NOT_FOUND.getErrorCode(),
			ErrorCodeEnum.RESOURCE_NOT_FOUND.getErrorMessage()
		);

		return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
	}
	
}
