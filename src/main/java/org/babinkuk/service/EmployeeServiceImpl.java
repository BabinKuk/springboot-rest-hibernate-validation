package org.babinkuk.service;

import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.babinkuk.common.ApiResponse;
import org.babinkuk.dao.EmployeeRepository;
import org.babinkuk.entity.Employee;
import org.babinkuk.exception.ObjectException;
import org.babinkuk.exception.ObjectNotFoundException;
import org.babinkuk.mapper.EmployeeMapper;
import org.babinkuk.vo.EmployeeVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class EmployeeServiceImpl implements EmployeeService {
	
	private final Logger log = LogManager.getLogger(getClass());
	
	public static String SAVE_SUCCESS = "employee_save_success";
	public static String DELETE_SUCCESS = "employee_delete_success";
	
	@Autowired
	private EmployeeRepository employeeRepository;
	
	@Autowired
	private ObjectMapper mapper;
		
	@Autowired
	private EmployeeMapper employeeMapper;
	
	@Autowired
	private MessageSource messageSource;
	
	@Autowired
	public EmployeeServiceImpl(EmployeeRepository studentRepository) {
		this.employeeRepository = studentRepository;
	}
	
	public EmployeeServiceImpl() {
		// TODO Auto-generated constructor stub
	}
	
	private String getMessage(String str) {
		return messageSource.getMessage(str, new Object[] {}, LocaleContextHolder.getLocale());
	}
	
	@Override
	public EmployeeVO findById(int id) throws ObjectNotFoundException {
		
		Optional<Employee> result = employeeRepository.findById(id);
		
		Employee employee = null;
		EmployeeVO employeeVO = null;
		
		if (result.isPresent()) {
			employee = result.get();
			log.info("employee ({})", employee);
			
			// mapping
			employeeVO = employeeMapper.toVODetails(employee);
			log.info("employeeVO ({})", employeeVO);
			
			return employeeVO;
		} else {
			// not found
			String message = String.format(getMessage("error_code_employee_id_not_found"), id);
			log.warn(message);
			throw new ObjectNotFoundException(message);
		}
	}
	
	@Override
	public EmployeeVO findByEmail(String email) {
		
		Optional<Employee> result = employeeRepository.findByEmail(email);
		
		Employee employee = null;
		EmployeeVO employeeVO = null;
		
		if (result.isPresent()) {
			employee = result.get();
			
			// mapping
			employeeVO = employeeMapper.toVO(employee);
			log.info("employeeVO ({})", employeeVO);
		} else {
			// not found
			String message = String.format(getMessage("error_code_employee_email_not_found"), email);
			log.warn(message);
		}

		return employeeVO;
	}
		
	@Override
	public ApiResponse save(EmployeeVO employeeVO) throws ObjectException {
		
		ApiResponse response = new ApiResponse();
		
		response.setStatus(HttpStatus.OK);
		response.setMessage(getMessage(SAVE_SUCCESS));
		
		Optional<Employee> entity = employeeRepository.findById(employeeVO.getId());
		
		Employee employee = null;
		
		if (entity.isPresent()) {
			employee = entity.get();
			//log.info("mapping for update");
			
			// mapping
			employee = employeeMapper.toEntity(employeeVO, employee);
		} else {
			// not found
			//log.info("mapping for insert");
			
			// mapping
			employee = employeeMapper.toEntity(employeeVO);
		}

		log.info("employee ({})", employee);
		
		employeeRepository.save(employee);
		
		return response;
	}
	
	@Override
	public ApiResponse delete(int id) throws ObjectNotFoundException {
		
		ApiResponse response = new ApiResponse();
		
		response.setStatus(HttpStatus.OK);
		response.setMessage(getMessage(DELETE_SUCCESS));
		
		employeeRepository.deleteById(id);
		
		return response;
	}

	@Override
	public Iterable<EmployeeVO> getAllEmployees() {
		return employeeMapper.toVO(employeeRepository.findAll());
	}
}