package com.dws.casestudy.tradestore.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(LowerVersionException.class)
	public ResponseEntity<ApiError> lowerVersion(LowerVersionException ex) {
		return ResponseEntity.status(HttpStatus.CONFLICT)
				.body(new ApiError("LOWER_VERSION", ex.getMessage()));
	}

	@ExceptionHandler(MaturityInPastException.class)
	public ResponseEntity<ApiError> maturity(MaturityInPastException ex) {
		return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
				.body(new ApiError("MATURITY_IN_PAST", ex.getMessage()));
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ApiError> validation(MethodArgumentNotValidException ex) {
		return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
				.body(new ApiError("INVALID_REQUEST", ex.getMessage()));
	}

}