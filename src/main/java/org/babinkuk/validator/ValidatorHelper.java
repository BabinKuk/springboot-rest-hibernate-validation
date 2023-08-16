package org.babinkuk.validator;

import java.util.LinkedList;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.babinkuk.exception.ObjectNotFoundException;
import org.babinkuk.exception.ObjectValidationException;
import org.babinkuk.vo.EmployeeVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ValidatorHelper {
	
	private final Logger log = LogManager.getLogger(getClass());
	
	@Autowired
	private BusinessValidator validator;
	
	public List<ValidatorException> validate(EmployeeVO vo, ActionType action, ValidatorType validatorType) throws ObjectValidationException {
		List<ValidatorException> exceptions = new LinkedList<ValidatorException>();
		
		try {
			validator.validateFirstName(vo.getFirstName());
		} catch (ValidatorException e) {
			exceptions.add(e);
		}
		
		try {
			validator.validateLastName(vo.getLastName());
		} catch (ValidatorException e) {
			exceptions.add(e);
		}
		
		try {
			validator.validateEmail(vo);
		} catch (ValidatorException e) {
			exceptions.add(e);
		}
		
		if (action == ActionType.UPDATE || action == ActionType.DELETE || action == ActionType.READ) {
			try {
				validator.objectExists(vo, validatorType);
			} catch (ValidatorException e) {
				exceptions.add(e);
			}
		}
		
		return exceptions;
	}
	
	public List<ValidatorException> validate(int id, ValidatorType validatorType) throws ObjectNotFoundException {
		List<ValidatorException> exceptions = new LinkedList<ValidatorException>();
		
		try {
			validator.objectExists(id, validatorType);
		} catch (ObjectNotFoundException e) {
			log.error(e.getMessage());
			throw e;
		}
		
		return exceptions;
	}
}
