package com.dws.casestudy.tradestore.exception;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ApiError {
	private String errorCode;
	private String message;
}
