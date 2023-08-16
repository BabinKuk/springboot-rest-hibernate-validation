package org.babinkuk.validator;

import java.util.Arrays;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.babinkuk.exception.ObjectNotFoundException;
import org.babinkuk.service.EmployeeService;
import org.babinkuk.vo.EmployeeVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * implementation class for different field validations
 *  
 * @author Nikola
 *
 */
@Component
public class BusinessValidator {
	
	private final Logger log = LogManager.getLogger(getClass());
	
	@Autowired
	private EmployeeService employeeService;
	
	/**
	 * @param name
	 * @throws ValidationException
	 */
	public void validateFirstName(String name) throws ValidatorException {
		validateStringIsBlank(name, ValidatorCodes.ERROR_CODE_FIRST_NAME_EMPTY);
	}
	
	/**
	 * @param name
	 * @throws ValidationException
	 */
	public void validateLastName(String name) throws ValidatorException {
		validateStringIsBlank(name, ValidatorCodes.ERROR_CODE_LAST_NAME_EMPTY);
	}
	
	/**
	 * @param email
	 * @throws ValidationException
	 */
	public void validateEmail(EmployeeVO vo) throws ValidatorException {
		validateStringIsBlank(vo.getEmail(), ValidatorCodes.ERROR_CODE_EMAIL_EMPTY);
		validateEmailFormat(vo.getEmail(), ValidatorCodes.ERROR_CODE_EMAIL_INVALID);
		emailExists(vo);
	}
	
	/**
	 * @param email
	 * @param errorCode
	 * @throws ValidatorException
	 */
	public void validateEmailFormat(String email, ValidatorCodes errorCode) throws ValidatorException {
		if (!validateEmailAddress(email)) {
			throw new ValidatorException(errorCode);
		}
	}

	/**
	 * validate email format
	 * @param email
	 * @return
	 */
	private boolean validateEmailAddress(String email) {
		if (StringUtils.isNotBlank(email)) {
			email = StringUtils.upperCase(StringUtils.replace(email, " ", ""));
			for (String pattern : Arrays.asList("^(.+)@(\\S+)$")) {
				if (Pattern.matches(pattern, email)) {
					return true;
				}
			}
			return false;
		} else {
			// if empty return true
			return true;
		}
	}

	/**
	 * validate if email already exist must be unique (call repository findByEmail)
	 * 
	 * @param vo
	 * @param isInsert
	 * @return
	 * @throws ValidatorException
	 */
	public void emailExists(EmployeeVO vo) throws ValidatorException {
		log.info("email " + vo.toString());
		EmployeeVO dbVO = null;
		
		dbVO = employeeService.findByEmail(vo.getEmail());
		 
		if (dbVO == null) {
			// email not found
			// that's ok
			log.info("email not found");
		} else {
			log.info("email found");
			if (dbVO.getId() == vo.getId()) {
				// same employee, email has not changed
				log.info("belongs to same instructor/student, email has not changed");
			} else {
				// another employee with same email already exists in db
				log.error(ValidatorCodes.ERROR_CODE_EMAIL_ALREADY_EXIST.getMessage());
				throw new ValidatorException(ValidatorCodes.ERROR_CODE_EMAIL_ALREADY_EXIST);
			}
		}
	}
	
	/**
	 * validate if object already exist
	 * @param vo
	 * @param isInsert
	 * @return
	 * @throws ValidatorException
	 */
	public void objectExists(Object vo, ValidatorType validatorType) throws ValidatorException {
		
		Object result;
		log.info("validate employee on update");
		
		result = objectExists(((EmployeeVO) vo).getId(), validatorType);
		
		if (result != null) {
			// id found
			log.info("employee id found");
		} else {
			// id not found
			//log.error("result.notPresent");
			throw new ValidatorException(ValidatorCodes.ERROR_CODE_INSTRUCTOR_INVALID);
		}
	}

	/**
	 * @param str
	 * @param errorCode
	 * @throws ValidatorException
	 */
	private void validateStringIsBlank(String str, ValidatorCodes errorCode) throws ValidatorException {
		if (StringUtils.isBlank(str)) {
			throw new ValidatorException(errorCode);
		}
	}

	/**
	 * @param id
	 * @param validatorType
	 * @return
	 * @throws ObjectNotFoundException
	 */
	public Object objectExists(int id, ValidatorType validatorType) throws ObjectNotFoundException {
		
		Object dbVO = null;
		
		switch (validatorType) {
		case EMPLOYEE:
			dbVO = employeeService.findById(id);
			break;
		default:
			break;
		} 
		
		return dbVO;
	}

}
