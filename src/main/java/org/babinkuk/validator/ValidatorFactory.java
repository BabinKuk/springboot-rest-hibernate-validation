package org.babinkuk.validator;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class ValidatorFactory {
	
	private final Logger log = LogManager.getLogger(getClass());
	
	@Autowired
	private ApplicationContext applicationContext;
	
	public Validator getValidator() {
		
		Validator validator = applicationContext.getBean(Validator.class);
		
		if (validator == null) {
			throw new IllegalStateException("Cannot acquire validator instance");
		}
		
		return validator;
	}
	
}
