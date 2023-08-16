package org.babinkuk.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

	private final Logger log = LogManager.getLogger(getClass());
	
	@Autowired
	private MessageSource messageSource;
	
	@Override
	protected ResponseEntity<Object> handleMethodArgumentNotValid(
			MethodArgumentNotValidException exception,
			HttpHeaders headers,
			HttpStatus status,
			WebRequest request) {
		
		log.warn("Handling MethodArgumentNotValidException", exception);
		BindingResultApiResponse apiResponse = new BindingResultApiResponse(exception.getBindingResult(), messageSource);
		apiResponse.setMessage(messageSource.getMessage("validation_failed", new Object[] {}, LocaleContextHolder.getLocale()));
		apiResponse.setStatus(status);
		
		return handleExceptionInternal(exception, apiResponse, headers, status, request);
	}
}
