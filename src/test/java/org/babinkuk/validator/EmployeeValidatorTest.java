package org.babinkuk.validator;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.babinkuk.service.EmployeeService;
import org.babinkuk.vo.EmployeeVO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;

import static org.junit.jupiter.api.Assertions.*;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import static org.babinkuk.controller.Api.ROOT;
import static org.babinkuk.controller.Api.EMPLOYEES;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@AutoConfigureMockMvc
public class EmployeeValidatorTest {
	
	public static final Logger log = LogManager.getLogger(EmployeeValidatorTest.class);
	
	private static String VALIDATION_FAILED = "validation_failed";
	
	private static MockHttpServletRequest request;
	
	@PersistenceContext
	private EntityManager entityManager;
	
	@Autowired
	private MockMvc mockMvc;
	
	@Autowired
	private JdbcTemplate jdbc;
	
	@Autowired
	private MessageSource messageSource;
	
	@Autowired
	ObjectMapper objectMApper;
	
	@Autowired
	private EmployeeService employeeService;
		
	@Value("${sql.script.employee.insert}")
	private String sqlAddEmployee;
	
	@Value("${sql.script.employee.delete}")
	private String sqlDeleteEmployee;
	
	public static final MediaType APPLICATION_JSON_UTF8 = MediaType.APPLICATION_JSON;
	
	@BeforeAll
	public static void setup() {
		log.info("BeforeAll");

		// init
		request = new MockHttpServletRequest();
	}
	
	@BeforeEach
    public void setupDatabase() {
		log.info("BeforeEach");
		jdbc.execute(sqlAddEmployee);
	}
	
	@AfterEach
	public void setupAfterTransaction() {
		log.info("AfterEach");
		jdbc.execute(sqlDeleteEmployee);
	}
	
	@Test
	void addEmptyEmployee() throws Exception {
		log.info("addEmptyEmployee {}");
		
		// create invalid employee (empty fields)
		EmployeeVO employeeVO = new EmployeeVO();
		
		mockMvc.perform(MockMvcRequestBuilders.post(ROOT + EMPLOYEES)
				.contentType(APPLICATION_JSON_UTF8)
				.content(objectMApper.writeValueAsString(employeeVO)) // generate json from java object
			)
			.andDo(MockMvcResultHandlers.print())
			.andExpect(status().is4xxClientError())
			.andExpect(status().isBadRequest()) // verify json root element status $ is 400 BAD_REQUEST
			.andExpect(jsonPath("$.message", is(String.format(getMessage(VALIDATION_FAILED), ActionType.CREATE)))) // verify json root element message
			.andExpect(jsonPath("$.fieldErrors", hasSize(3))) // verify that json root element $ is size 3
	        .andExpect(jsonPath("$.fieldErrors", hasItem(String.format(getMessage(ValidatorCodes.ERROR_CODE_FIRST_NAME_EMPTY.getMessage()), ActionType.CREATE))))
	        .andExpect(jsonPath("$.fieldErrors", hasItem(String.format(getMessage(ValidatorCodes.ERROR_CODE_LAST_NAME_EMPTY.getMessage()), ActionType.CREATE))))
	        .andExpect(jsonPath("$.fieldErrors", hasItem(String.format(getMessage(ValidatorCodes.ERROR_CODE_EMAIL_EMPTY.getMessage()), ActionType.CREATE))))
			;
		
		// additional check
		// get all employees
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + EMPLOYEES))
			.andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$", hasSize(1))) // verify that json root element $ is size 1
			.andDo(MockMvcResultHandlers.print())
			;
	}

	@Test
	void addEmployeeInvalidEmail() throws Exception {
		log.info("addEmployeeInvalidEmail {}");
		
		// create invalid employee (invalid email)
		EmployeeVO employeeVO = new EmployeeVO("ime", "prezime", "this is invalid email");
		
		mockMvc.perform(MockMvcRequestBuilders.post(ROOT + EMPLOYEES)
				.contentType(APPLICATION_JSON_UTF8)
				.content(objectMApper.writeValueAsString(employeeVO)) // generate json from java object
			)
			.andDo(MockMvcResultHandlers.print())
			.andExpect(status().is4xxClientError())
			.andExpect(status().isBadRequest()) // verify json root element status $ is 400 BAD_REQUEST
			.andExpect(jsonPath("$.message", is(String.format(getMessage(VALIDATION_FAILED), ActionType.CREATE)))) // verify json root element message
			.andExpect(jsonPath("$.errors", hasSize(1))) // verify that json root element $ is size 1
//	        .andExpect(jsonPath("$.errors", hasItem(String.format(getMessage(ValidatorCodes.ERROR_CODE_FIRST_NAME_EMPTY.getMessage()), ActionType.CREATE))))
//	        .andExpect(jsonPath("$.errors", hasItem(String.format(getMessage(ValidatorCodes.ERROR_CODE_LAST_NAME_EMPTY.getMessage()), ActionType.CREATE))))
	        .andExpect(jsonPath("$.errors", hasItem(String.format(getMessage(ValidatorCodes.ERROR_CODE_EMAIL_INVALID.getMessage()), ActionType.CREATE))))
			;
		
		// additional check
		// get all employees
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + EMPLOYEES))
			.andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$", hasSize(1))) // verify that json root element $ is now size 1
			.andDo(MockMvcResultHandlers.print())
			;
	}
	
	@Test
	void addEmployeeEmailNotUnique() throws Exception {
		log.info("addEmployeeEmailNotUnique {}");
		
		// create invalid employee (email already exists in db)
		EmployeeVO employeeVO = new EmployeeVO("ime", "prezime", "firstNameStudent@babinuk.com");
				
		mockMvc.perform(MockMvcRequestBuilders.post(ROOT + EMPLOYEES)
				.contentType(APPLICATION_JSON_UTF8)
				.content(objectMApper.writeValueAsString(employeeVO)) // generate json from java object
			)
			.andDo(MockMvcResultHandlers.print())
			.andExpect(status().is4xxClientError())
			.andExpect(status().isBadRequest()) // verify json root element status $ is 400 BAD_REQUEST
			.andExpect(jsonPath("$.message", is(String.format(getMessage(VALIDATION_FAILED), ActionType.CREATE)))) // verify json root element message
			.andExpect(jsonPath("$.errors", hasSize(1))) // verify that json root element $ is size 1
//	        .andExpect(jsonPath("$.errors", hasItem(String.format(getMessage(ValidatorCodes.ERROR_CODE_FIRST_NAME_EMPTY.getMessage()), ActionType.CREATE))))
//	        .andExpect(jsonPath("$.errors", hasItem(String.format(getMessage(ValidatorCodes.ERROR_CODE_LAST_NAME_EMPTY.getMessage()), ActionType.CREATE))))
	        .andExpect(jsonPath("$.errors", hasItem(String.format(getMessage(ValidatorCodes.ERROR_CODE_EMAIL_ALREADY_EXIST.getMessage()), ActionType.CREATE))))
			;
		
		// additional check
		// get all instructors
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + EMPLOYEES))
			.andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$", hasSize(1))) // verify that json root element $ is now size 1
			.andDo(MockMvcResultHandlers.print())
			;
	}
	
	@Test
	void updateEmployeeInvalidId() throws Exception {
		log.info("updateEmployeeInvalidId {}");
		
		int id = 2;
		
		// check if employee id 2 exists
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + EMPLOYEES + "/{id}", id))
			.andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$.message", is(String.format(getMessage("error_code_employee_id_not_found"), id)))) // verify json element
			;
		
		// create invalid employee 
		EmployeeVO employeeVO = new EmployeeVO("firstName", "lastName", "emailAddress@email.hr");
		employeeVO.setId(id);
		
		mockMvc.perform(MockMvcRequestBuilders.put(ROOT + EMPLOYEES)
				.contentType(APPLICATION_JSON_UTF8)
				.content(objectMApper.writeValueAsString(employeeVO)) // generate json from java object
			)
			.andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.message", is(String.format(getMessage("error_code_employee_id_not_found"), id)))) // verify json element
			;
	}
	
	@Test
	void updateEmptyEmployee() throws Exception {
		log.info("updateEmptyEmployee {}");
		
		int id = 1;
		
		// check if employee id 1 exists
		EmployeeVO employeeVO = employeeService.findById(id);
		
		assertNotNull(employeeVO,"employeeVO null");
		assertEquals(1, employeeVO.getId());
		assertNotNull(employeeVO.getFirstName(),"employeeVO.getFirstName() null");
		assertEquals("firstNameStudent", employeeVO.getFirstName(),"assertEquals employeeVO.getFirstName() failure");
		
		// update instructor
		employeeVO.setFirstName("");
		employeeVO.setLastName("");
		employeeVO.setEmail("");
		
		mockMvc.perform(MockMvcRequestBuilders.put(ROOT + EMPLOYEES)
				.contentType(APPLICATION_JSON_UTF8)
				.content(objectMApper.writeValueAsString(employeeVO)) // generate json from java object
			)
			.andDo(MockMvcResultHandlers.print())
			.andExpect(status().is4xxClientError())
			.andExpect(status().isBadRequest()) // verify json root element status $ is 400 BAD_REQUEST
			.andExpect(jsonPath("$.message", is(String.format(getMessage(VALIDATION_FAILED), ActionType.UPDATE)))) // verify json root element message
			.andExpect(jsonPath("$.fieldErrors", hasSize(3))) // verify that json root element $ is size 3
	        .andExpect(jsonPath("$.fieldErrors", hasItem(String.format(getMessage(ValidatorCodes.ERROR_CODE_FIRST_NAME_EMPTY.getMessage()), ActionType.CREATE))))
	        .andExpect(jsonPath("$.fieldErrors", hasItem(String.format(getMessage(ValidatorCodes.ERROR_CODE_LAST_NAME_EMPTY.getMessage()), ActionType.CREATE))))
	        .andExpect(jsonPath("$.fieldErrors", hasItem(String.format(getMessage(ValidatorCodes.ERROR_CODE_EMAIL_EMPTY.getMessage()), ActionType.CREATE))))
			;
	}
	
	@Test
	void updateEmployeeInvalidEmail() throws Exception {
		log.info("updateEmployeeInvalidEmail {}");
		
		int id = 1;
		
		// check if employee id 1 exists
		EmployeeVO employeeVO = employeeService.findById(id);
		
		assertNotNull(employeeVO,"employeeVO null");
		assertEquals(1, employeeVO.getId());
		assertNotNull(employeeVO.getFirstName(),"employeeVO.getFirstName() null");
		assertEquals("firstNameStudent", employeeVO.getFirstName(),"assertEquals employeeVO.getFirstName() failure");
		
		// update employee
		employeeVO.setEmail("this is invalid email");
		
		mockMvc.perform(MockMvcRequestBuilders.put(ROOT + EMPLOYEES)
				.contentType(APPLICATION_JSON_UTF8)
				.content(objectMApper.writeValueAsString(employeeVO)) // generate json from java object
			)
			.andDo(MockMvcResultHandlers.print())
			.andExpect(status().is4xxClientError())
			.andExpect(status().isBadRequest()) // verify json root element status $ is 400 BAD_REQUEST
			.andExpect(jsonPath("$.message", is(String.format(getMessage(VALIDATION_FAILED), ActionType.UPDATE)))) // verify json root element message
			.andExpect(jsonPath("$.errors", hasSize(1))) // verify that json root element $ is size 1
//        .andExpect(jsonPath("$.errors", hasItem(String.format(getMessage(ValidatorCodes.ERROR_CODE_FIRST_NAME_EMPTY.getMessage()), ActionType.CREATE))))
//        .andExpect(jsonPath("$.errors", hasItem(String.format(getMessage(ValidatorCodes.ERROR_CODE_LAST_NAME_EMPTY.getMessage()), ActionType.CREATE))))
	        .andExpect(jsonPath("$.errors", hasItem(String.format(getMessage(ValidatorCodes.ERROR_CODE_EMAIL_INVALID.getMessage()), ActionType.CREATE))))
			;
	}
	
	@Test
	void updateEmployeeEmailNotUnique() throws Exception {
		log.info("updateEmployeeEmailNotUnique {}");
		
		int id = 1;
		
		// check if employee id 1 exists
		EmployeeVO employeeVO = employeeService.findById(id);
		
		assertNotNull(employeeVO,"employeeVO null");
		assertEquals(1, employeeVO.getId());
		assertNotNull(employeeVO.getFirstName(),"employeeVO.getFirstName() null");
		assertEquals("firstNameStudent", employeeVO.getFirstName(),"assertEquals employeeVO.getFirstName() failure");
		
		// create new employee
		EmployeeVO newEmployeeVO = new EmployeeVO("firstName", "lastName", "email@email.com");
		newEmployeeVO.setId(0);
		
		// save new employee
		employeeService.save(newEmployeeVO);
		
		// check if new employee exists
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + EMPLOYEES))
			.andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$", hasSize(2))) // verify that json root element $ is size 2
			;
		
		EmployeeVO dbNewEmployeeVO = employeeService.findByEmail(newEmployeeVO.getEmail());
		
		assertNotNull(dbNewEmployeeVO,"dbNewEmployeeVO null");
		//assertEquals(1, dbNewEmployeeVO.getId());
		assertNotNull(dbNewEmployeeVO.getFirstName(),"dbNewEmployeeVO.getFirstName() null");
		assertEquals(dbNewEmployeeVO.getFirstName(), dbNewEmployeeVO.getFirstName(),"assertEquals dbNewEmployeeVO.getFirstName() failure");
		assertEquals(dbNewEmployeeVO.getEmail(), dbNewEmployeeVO.getEmail(),"assertEquals dbNewEmployeeVO.getEmailAddress() failure");
		
		// update employee email (value belong to other instructor id 1)
		dbNewEmployeeVO.setEmail(employeeVO.getEmail());
				
		mockMvc.perform(MockMvcRequestBuilders.put(ROOT + EMPLOYEES)
				.contentType(APPLICATION_JSON_UTF8)
				.content(objectMApper.writeValueAsString(dbNewEmployeeVO)) // generate json from java object
			)
			.andDo(MockMvcResultHandlers.print())
			.andExpect(status().is4xxClientError())
			.andExpect(status().isBadRequest()) // verify json root element status $ is 400 BAD_REQUEST
			.andExpect(jsonPath("$.message", is(String.format(getMessage(VALIDATION_FAILED), ActionType.UPDATE)))) // verify json root element message
			.andExpect(jsonPath("$.errors", hasSize(1))) // verify that json root element $ size
	        .andExpect(jsonPath("$.errors", hasItem(String.format(getMessage(ValidatorCodes.ERROR_CODE_EMAIL_ALREADY_EXIST.getMessage()), ActionType.CREATE))))
			;
	}
	
	@Test
	void updateEmployeeNotExist() throws Exception {
		log.info("updateEmployeeNotExist {}");
		
		int id = 2;
		
		// get employee with id=2 (non existing)
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + EMPLOYEES + "/{id}", id))
			.andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$.message", is(String.format(getMessage("error_code_employee_id_not_found"), 2)))) // verify json element
			;
		
		// create new employee
		EmployeeVO employeeVO = new EmployeeVO("firstName", "lastName", "email@email.com");
		employeeVO.setId(id);
		
		mockMvc.perform(MockMvcRequestBuilders.put(ROOT + EMPLOYEES)
				.contentType(APPLICATION_JSON_UTF8)
				.content(objectMApper.writeValueAsString(employeeVO)) // generate json from java object
			)
			.andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$.message", is(String.format(getMessage("error_code_employee_id_not_found"), id)))) // verify json element
			;
	}
	
	private String getMessage(String str) {
		return messageSource.getMessage(str, new Object[] {}, LocaleContextHolder.getLocale());
	}
}
