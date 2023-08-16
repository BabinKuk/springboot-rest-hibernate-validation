package org.babinkuk.validator;

import java.util.LinkedList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.babinkuk.exception.ObjectNotFoundException;
import org.babinkuk.exception.ObjectValidationException;
import org.babinkuk.vo.EmployeeVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

/**
 * special validations are required depending on the role
 * 
 * @author Nikola
 *
 */
@Component()
public class ValidatorImpl implements Validator {

private final Logger log = LogManager.getLogger(getClass());
	
	@Autowired
	private MessageSource messageSource;
	
	@Autowired
	private ValidatorHelper validatorHelper;
	
	@Override
	public EmployeeVO validate(EmployeeVO vo, ActionType action, ValidatorType validatorType) throws ObjectValidationException {
		log.info("Validating {} {} (vo={})", action, validatorType, vo);
		
		List<ValidatorException> exceptionList = new LinkedList<ValidatorException>();
		
		// all action types are enabled
		exceptionList.addAll(validatorHelper.validate(vo, action, validatorType));
		
		String message = String.format(messageSource.getMessage("validation_failed", new Object[] {}, LocaleContextHolder.getLocale()), action);
		ObjectValidationException e = new ObjectValidationException(message);
		
		for (ValidatorException validationException : exceptionList) {
			e.addValidationError(messageSource.getMessage(validationException.getErrorCode().getMessage(), new Object[] {}, LocaleContextHolder.getLocale()));
		}
		
		if (e.hasErrors()) {
			throw e;
		}

		return vo;
	}

	@Override
	public void validate(int id, ActionType action, ValidatorType validatorType) throws ObjectNotFoundException {
		log.info("Validating {} {} (id={})", action, validatorType, id);
		
		List<ValidatorException> exceptionList = new LinkedList<ValidatorException>();
		
		// all action types are enabled
		exceptionList.addAll(validatorHelper.validate(id, validatorType));
		
		String message = String.format(messageSource.getMessage("validation_failed", new Object[] {}, LocaleContextHolder.getLocale()), action);
		ObjectValidationException e = new ObjectValidationException(message);
		
		for (ValidatorException validationException : exceptionList) {
			e.addValidationError(messageSource.getMessage(validationException.getErrorCode().getMessage(), new Object[] {}, LocaleContextHolder.getLocale()));
		}
		
		if (e.hasErrors()) {
			throw e;
		}
	}

}
