package org.babinkuk.controller;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.babinkuk.common.ApiResponse;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.util.Assert;
import org.springframework.validation.BindingResult;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@JsonInclude(value = Include.NON_EMPTY)
public class BindingResultApiResponse extends ApiResponse implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private final Logger log = LogManager.getLogger(getClass());
	
	private transient MessageSource messageSource;
	
	private transient BindingResult bindingResult;
	
	public BindingResultApiResponse(BindingResult bindingResult, MessageSource messageSource) {
		super();
		Assert.notNull(bindingResult, "BindingResult must not be null");
		Assert.notNull(messageSource, "MessageSource must not be null");
		this.bindingResult = bindingResult;
		this.messageSource = messageSource;
	}
	
	public int getErrorCount() {
		return bindingResult.getErrorCount();
	}
	
	public List<String> getErrors() {
		Locale locale = LocaleContextHolder.getLocale();
		return this.bindingResult.getGlobalErrors().stream()
			.map(e -> {
				return messageSource.getMessage(e.getDefaultMessage(), new Object[] {}, locale);
			}).collect(Collectors.toList());
	}
	
	public List<String> getFieldErrors() {
		Locale locale = LocaleContextHolder.getLocale();
		return this.bindingResult.getFieldErrors().stream()
			.map(e -> {
				return messageSource.getMessage(e.getDefaultMessage(), new Object[] {}, locale);
			}).collect(Collectors.toList());
	}
	
	@JsonIgnore
	public Map<String, Object> getErrorMap() {
		ObjectMapper mapper = new ObjectMapper();
		return mapper.convertValue(this, new TypeReference<HashMap<String, Object>>() {});
	}

}
