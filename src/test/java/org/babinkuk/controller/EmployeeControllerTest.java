package org.babinkuk.controller;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.babinkuk.service.EmployeeService;
import org.babinkuk.service.EmployeeServiceImpl;
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
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import static org.babinkuk.controller.Api.ROOT;
import static org.babinkuk.controller.Api.EMPLOYEES;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@AutoConfigureMockMvc
public class EmployeeControllerTest {
	
	public static final Logger log = LogManager.getLogger(EmployeeControllerTest.class);
	
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
	void getAllEmployees() throws Exception {
		log.info("getAllEmployees");
		
		// get all employees
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + EMPLOYEES)
//				.param("validationRole", ROLE_ADMIN)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$", hasSize(1))) // verify that json root element $ is size 1
			;

		// add another employee
		EmployeeVO employeeVO = new EmployeeVO("firstName", "lastName", "emailAddress");
		
		employeeService.save(employeeVO);
				
		// get all employees
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + EMPLOYEES)
//				.param("validationRole", ROLE_INSTRUCTOR)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$", hasSize(2))) // verify that json root element $ is now size 2
			;
	}
	
	@Test
	void getEmployee() throws Exception {
		log.info("getEmployee");
		
		// get employee with id=1
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + EMPLOYEES + "/{id}", 1)
//				.param("validationRole", validationRole)
			).andDo(MockMvcResultHandlers.print()).andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$.id", is(1))) // verify json root element id is 1
			.andExpect(jsonPath("$.firstName", is("firstNameStudent"))) // verify json element
			;

		// get employee with id=2 (non existing)
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + EMPLOYEES + "/{id}", 2)
//				.param("validationRole", validationRole)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$.message", is(String.format(getMessage("error_code_employee_id_not_found"), 2)))) // verify json element
			;
	}
		
	@Test
	void addEmployee() throws Exception {
		log.info("addEmployee");
		
		// create employee
		EmployeeVO employeeVO = new EmployeeVO("firstName", "lastName", "emailAddress@email.hr");
		
		mockMvc.perform(MockMvcRequestBuilders.post(ROOT + EMPLOYEES)
//				.param("validationRole", validationRole)
				.contentType(APPLICATION_JSON_UTF8)
				.content(objectMApper.writeValueAsString(employeeVO)) // generate json from java object
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.message", is(getMessage(EmployeeServiceImpl.SAVE_SUCCESS)))) // verify json element
			;
		
		// additional check
		// get all employees
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + EMPLOYEES)
//				.param("validationRole", validationRole)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$", hasSize(2))) // verify that json root element $ is now size 2
			;
		
		// additional check
		employeeVO = employeeService.findByEmail("emailAddress@email.hr");
		
		log.info(employeeVO.toString());
		
		assertNotNull(employeeVO,"employeeVO null");
		assertNotNull(employeeVO.getFirstName(),"employeeVO.getFirstName() null");
		assertNotNull(employeeVO.getLastName(),"employeeVO.getLastName() null");
		assertNotNull(employeeVO.getEmail(),"employeeVO.getEmail() null");
		assertEquals("firstName", employeeVO.getFirstName(),"employeeVO.getFirstName() NOK");
		assertEquals("lastName", employeeVO.getLastName(),"employeeVO.getLastName() NOK");
	}
//	
//	@Test
//	void addStudentNoRole() throws Exception {
//
//		addStudentFail("");
//	}
//	
//	private void addStudentFail(String validationRole) throws Exception {
//		log.info("addStudentFail {}", validationRole);
//		
//		// create student
//		EmployeeVO studentVO = new EmployeeVO("firstName", "lastName", "emailAddress@email.hr");
//		
//		mockMvc.perform(MockMvcRequestBuilders.post(ROOT + EMPLOYEES)
//				.param("validationRole", validationRole)
//				.contentType(APPLICATION_JSON_UTF8)
//				.content(objectMApper.writeValueAsString(studentVO)) // generate json from java object
//			).andDo(MockMvcResultHandlers.print())
//			.andExpect(status().is4xxClientError())
//			.andExpect(status().isBadRequest()) // verify json root element status $ is 400 BAD_REQUEST
////			.andExpect(jsonPath("$.message", is(String.format(messageSource.getMessage(ValidatorCodes.ERROR_CODE_ACTION_INVALID.getMessage(), new Object[] {}, LocaleContextHolder.getLocale()), ActionType.CREATE)))) // verify json root element message
//			;
//		
//		// additional check
//		// get all students
//		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + EMPLOYEES)
//				.param("validationRole", validationRole)
//			).andDo(MockMvcResultHandlers.print())
//			.andExpect(status().isOk())
//			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
//			.andExpect(jsonPath("$", hasSize(1))) // verify that json root element $ is still size 1
//			;
//	}
	
	@Test
	void updateEmployee() throws Exception {
		log.info("updateEmployee");
		
		// check if student id 1 exists
		EmployeeVO employeeVO = employeeService.findById(1);
		log.info(employeeVO.toString());
		
		assertNotNull(employeeVO,"studentVO null");
		assertEquals(1, employeeVO.getId());
		assertNotNull(employeeVO.getFirstName(),"studentVO.getFirstName() null");
		assertEquals("firstNameStudent", employeeVO.getFirstName(),"assertEquals studentVO.getFirstName() failure");
		
		// update student
		employeeVO.setFirstName("firstName");
		employeeVO.setLastName("lastName");
		employeeVO.setEmail("email@email.com");
				
		mockMvc.perform(MockMvcRequestBuilders.put(ROOT + EMPLOYEES)
//				.param("validationRole", validationRole)
				.contentType(APPLICATION_JSON_UTF8)
				.content(objectMApper.writeValueAsString(employeeVO)) // generate json from java object
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.message", is(getMessage(EmployeeServiceImpl.SAVE_SUCCESS)))) // verify json element
			;
		
		// additional check
		// get employee with id=1
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + EMPLOYEES + "/{id}", 1)
//				.param("validationRole", validationRole)
			).andDo(MockMvcResultHandlers.print()).andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$.id", is(1))) // verify json root element id is 1
			.andExpect(jsonPath("$.firstName", is("firstName"))) // verify json element
			.andExpect(jsonPath("$.lastName", is("lastName"))) // verify json element
			.andExpect(jsonPath("$.email", is("email@email.com"))) // verify json element
			;
	}
//	
//	private void updateStudentFail(String validationRole) throws Exception {
//		log.info("updateStudentFail {}", validationRole);
//		
//		// check if student id 1 exists
//		EmployeeVO studentVO = employeeService.findById(1);
//		log.info(studentVO.toString());
//		
//		assertNotNull(studentVO,"studentVO null");
//		assertEquals(1, studentVO.getId());
//		assertNotNull(studentVO.getFirstName(),"studentVO.getFirstName() null");
//		assertEquals("firstNameStudent", studentVO.getFirstName(),"assertEquals studentVO.getFirstName() failure");
//		
//		// update student
//		studentVO.setFirstName("firstName");
//		studentVO.setLastName("lastName");
//		studentVO.setEmail("email@email.com");
//				
//		mockMvc.perform(MockMvcRequestBuilders.put(ROOT + EMPLOYEES)
//				.param("validationRole", validationRole)
//				.contentType(APPLICATION_JSON_UTF8)
//				.content(objectMApper.writeValueAsString(studentVO)) // generate json from java object
//			).andDo(MockMvcResultHandlers.print())
//			.andExpect(status().is4xxClientError())
//			.andExpect(status().isBadRequest()) // verify json root element status $ is 400 BAD_REQUEST
//			//.andExpect(jsonPath("$.message", is(String.format(messageSource.getMessage(ValidatorCodes.ERROR_CODE_ACTION_INVALID.getMessage(), new Object[] {}, LocaleContextHolder.getLocale()), ActionType.UPDATE)))) // verify json root element message
//			;
//		
//		// additional check
//		// get student with id=1
//		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + EMPLOYEES + "/{id}", 1)
//				.param("validationRole", validationRole)
//			).andDo(MockMvcResultHandlers.print()).andExpect(status().isOk())
//			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
//			.andExpect(jsonPath("$.id", is(1))) // verify json root element id is 1
//			.andExpect(jsonPath("$.firstName", is("firstNameStudent"))) // verify json element
//			.andExpect(jsonPath("$.lastName", is("lastNameStudent"))) // verify json element
//			.andExpect(jsonPath("$.emailAddress", is("firstNameStudent@babinuk.com"))) // verify json element
//			;
//	}
	
	@Test
	void deleteEmployee() throws Exception {
		log.info("deleteEmployee");
		
		// check if employee id 1 exists
		int id = 1;
		EmployeeVO employeeVO = employeeService.findById(id);
		log.info(employeeVO.toString());
		
		assertNotNull(employeeVO,"employeeVO null");
		assertEquals(1, employeeVO.getId());
		assertNotNull(employeeVO.getFirstName(),"employeeVO.getFirstName() null");
		assertEquals("firstNameStudent", employeeVO.getFirstName(),"assertEquals employeeVO.getFirstName() failure");
				
		// delete employee
		mockMvc.perform(MockMvcRequestBuilders.delete(ROOT + EMPLOYEES + "/{id}", id)
//				.param("validationRole", validationRole)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$.message", is(getMessage(EmployeeServiceImpl.DELETE_SUCCESS)))) // verify json element
			;
		
		// get employee with id=1 (non existing)
		mockMvc.perform(MockMvcRequestBuilders.get(ROOT + EMPLOYEES + "/{id}", id)
//				.param("validationRole", validationRole)
			).andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(content().contentType(APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$.message", is(String.format(getMessage("error_code_employee_id_not_found"), id)))) //verify json element
			;
	}
	
	private String getMessage(String str) {
		return messageSource.getMessage(str, new Object[] {}, LocaleContextHolder.getLocale());
	}
}
