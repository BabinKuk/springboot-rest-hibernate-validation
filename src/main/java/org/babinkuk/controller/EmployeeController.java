package org.babinkuk.controller;


import org.babinkuk.service.EmployeeService;
import org.babinkuk.validator.ActionType;
import org.babinkuk.validator.ValidatorFactory;
import org.babinkuk.validator.ValidatorType;
import org.babinkuk.vo.EmployeeVO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.babinkuk.common.ApiResponse;
import org.babinkuk.exception.ObjectException;
import org.babinkuk.exception.ObjectNotFoundException;
import org.babinkuk.exception.ObjectValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Optional;

import javax.validation.Valid;

import static org.babinkuk.controller.Api.ROOT;
import static org.babinkuk.controller.Api.EMPLOYEES;

@RestController
@RequestMapping(ROOT + EMPLOYEES)
public class EmployeeController {
	
	private final Logger log = LogManager.getLogger(getClass());
	
	// service
	private EmployeeService employeeService;
	
	@Autowired
	private ValidatorFactory validatorFactory;
	
	@Autowired
	private ObjectMapper mapper;
	
	public EmployeeController() {
		// TODO Auto-generated constructor stub
	}

	@Autowired
	public EmployeeController(EmployeeService employeeService) {
		this.employeeService = employeeService;
	}

	/**
	 * expose GET "/employees"
	 * get employee list
	 *
	 * @param 
	 * @return ResponseEntity
	 */
	@GetMapping("")
	public ResponseEntity<Iterable<EmployeeVO>> getAllEmployees() {
		log.info("Called EmployeeController.getAllEmployees");

		return ResponseEntity.of(Optional.ofNullable(employeeService.getAllEmployees()));
	}
	
	/**
	 * expose GET "/employees/{employeeId}"
	 * get employee
	 * 			
	 * @param employeeId
	 * @return
	 */
	@GetMapping("/{employeeId}")
	public ResponseEntity<EmployeeVO> getEmployee(@PathVariable int employeeId) {
		log.info("Called EmployeeController.getEmployee(employeeId={})", employeeId);
		
		return ResponseEntity.of(Optional.ofNullable(employeeService.findById(employeeId)));
	}
	
	/**
	 * expose POST "/employees"
	 * add new employee
	 * 
	 * @param employeeVO
	 * @return
	 * @throws JsonProcessingException
	 */
	@PostMapping("")
	public ResponseEntity<ApiResponse> addEmployee(
			@Valid @RequestBody EmployeeVO employeeVO) throws JsonProcessingException {
		log.info("Called EmployeeController.addEmployee({})", mapper.writeValueAsString(employeeVO));
		
		// in case id is passed in json, set to 0
		// this is to force a save of new item ... instead of update
		employeeVO.setId(0);
		
		validatorFactory.getValidator().validate(employeeVO, ActionType.CREATE, ValidatorType.EMPLOYEE);
		
		return ResponseEntity.of(Optional.ofNullable(employeeService.save(employeeVO)));
	}
	
	/**
	 * expose PUT "/employees"
	 * update employee
	 * 
	 * @param employeeVO
	 * @return
	 * @throws JsonProcessingException
	 */
	@PutMapping("")
	public ResponseEntity<ApiResponse> updateEmployee(
			@Valid @RequestBody EmployeeVO employeeVO) throws JsonProcessingException {
		log.info("Called EmployeeController.updateEmployee({})", mapper.writeValueAsString(employeeVO));
		
		validatorFactory.getValidator().validate(employeeVO, ActionType.UPDATE, ValidatorType.EMPLOYEE);
		
		return ResponseEntity.of(Optional.ofNullable(employeeService.save(employeeVO)));
	}
	
	/**
	 * expose DELETE "/{employeeId}"
	 * 
	 * @param employeeId
	 * @param validationRole
	 * @return
	 */
	@DeleteMapping("/{employeeId}")
	public ResponseEntity<ApiResponse> deleteEmployee(
			@PathVariable int employeeId) {
		log.info("Called EmployeeController.deleteEmployee(employeeId={})", employeeId);
		
		validatorFactory.getValidator().validate(employeeId, ActionType.DELETE, ValidatorType.EMPLOYEE);
		
		return ResponseEntity.of(Optional.ofNullable(employeeService.delete(employeeId)));
	}

//	@ExceptionHandler
//	public ResponseEntity<ApiResponse> handleException(Exception exc) {
//		log.error("error 1");
//		return new ApiResponse(HttpStatus.BAD_REQUEST, exc.getMessage()).toEntity();
//	}
	
	@ExceptionHandler
	public ResponseEntity<ApiResponse> handleException(ObjectException exc) {

		return new ApiResponse(HttpStatus.INTERNAL_SERVER_ERROR, exc.getMessage()).toEntity();
	}

	@ExceptionHandler
	public ResponseEntity<ApiResponse> handleException(ObjectNotFoundException exc) {
		
		return new ApiResponse(HttpStatus.OK, exc.getMessage()).toEntity();
	}
	
	@ExceptionHandler
	public ResponseEntity<ApiResponse> handleException(ObjectValidationException exc) {
		
		ApiResponse apiResponse = new ApiResponse(HttpStatus.BAD_REQUEST, exc.getMessage());
		apiResponse.setErrors(exc.getValidationErrors());
		return apiResponse.toEntity();
	}

}
