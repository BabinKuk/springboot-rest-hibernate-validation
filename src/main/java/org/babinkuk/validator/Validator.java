package org.babinkuk.validator;

import org.babinkuk.exception.ObjectNotFoundException;
import org.babinkuk.exception.ObjectValidationException;
import org.babinkuk.vo.EmployeeVO;

public interface Validator {
	
	/** 
	 * @param employee
	 * @param action
	 * @param validatorType
	 * @return
	 * @throws ObjectValidationException
	 */
	public EmployeeVO validate(EmployeeVO vo, ActionType action, ValidatorType validatorType) throws ObjectValidationException;
	
	/**
	 * @param id
	 * @param validatorType
	 * @return
	 * @throws ObjectNotFoundException
	 */
	public void validate(int id, ActionType action, ValidatorType validatorType) throws ObjectNotFoundException;

}
