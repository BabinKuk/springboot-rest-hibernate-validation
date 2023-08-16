package org.babinkuk.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.babinkuk.exception.ObjectNotFoundException;
import org.babinkuk.vo.EmployeeVO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Collection;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class EmployeeServiceTest {
	
	public static final Logger log = LogManager.getLogger(EmployeeServiceTest.class);
	
	@Autowired
	private JdbcTemplate jdbc;
	
	@Autowired
	private MessageSource messageSource;
	
	@Autowired
	private EmployeeService employeeService;
	
	@Value("${sql.script.employee.insert}")
	private String sqlAddEmployee;
	
	@Value("${sql.script.employee.delete}")
	private String sqlDeleteEmployee;
	
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
	void getEmployeeById() {
		log.info("getEmployeeById");
		
		EmployeeVO employeeVO = employeeService.findById(1);
		
		//log.info(employeeVO.toString());
		
		assertNotNull(employeeVO,"employeeVO null");
		assertEquals(1, employeeVO.getId());
		assertNotNull(employeeVO.getFirstName(),"employeeVO.getFirstName() null");
		assertNotNull(employeeVO.getLastName(),"employeeVO.getLastName() null");
		assertNotNull(employeeVO.getEmail(),"employeeVO.getEmailAddress() null");
		assertEquals("firstNameStudent", employeeVO.getFirstName(),"employeeVO.getFirstName() NOK");
		assertEquals("lastNameStudent", employeeVO.getLastName(),"employeeVO.getLastName() NOK");
		assertEquals("firstNameStudent@babinuk.com", employeeVO.getEmail(),"employeeVO.getEmailAddress() NOK");
		// not neccessary
		assertNotEquals("test", employeeVO.getFirstName(),"employeeVO.getFirstName() NOK");
		
		// assert not existing instructor
		Exception exception = assertThrows(ObjectNotFoundException.class, () -> {
			employeeService.findById(2);
		});
		
		String expectedMessage = "Employee with id=2 not found.";
		String actualMessage = exception.getMessage();

	    assertTrue(actualMessage.contains(expectedMessage));
	}
	
	@Test
	void getEmployeeByEmail() {
		log.info("getEmployeeByEmail");
		
		EmployeeVO employeeVO  = employeeService.findByEmail("firstNameStudent@babinuk.com");
		
		//log.info(employeeVO.toString());
		
		assertNotNull(employeeVO,"employeeVO null");
		assertEquals(1, employeeVO.getId());
		assertNotNull(employeeVO.getFirstName(),"employeeVO.getFirstName() null");
		assertNotNull(employeeVO.getLastName(),"employeeVO.getLastName() null");
		assertNotNull(employeeVO.getEmail(),"employeeVO.getEmailAddress() null");
		assertEquals("firstNameStudent", employeeVO.getFirstName(),"employeeVO.getFirstName() NOK");
		assertEquals("lastNameStudent", employeeVO.getLastName(),"employeeVO.getLastName() NOK");
		assertEquals("firstNameStudent@babinuk.com", employeeVO.getEmail(),"employeeVO.getEmailAddress() NOK");
		// not neccessary
		assertNotEquals("test", employeeVO.getFirstName(),"employeeVO.getFirstName() NOK");
		
		// assert not existing instructor
		assertNull(employeeService.findByEmail("email"),"not existing student not null");
	}
	
	@Test
	void addEmployee() {
		log.info("addEmployee");
		
		// first create student
		// set id 0: this is to force a save of new item ... instead of update
		EmployeeVO employeeVO = new EmployeeVO("firstName", "lastName", "emailAddress");
		employeeVO.setId(0);
		
		employeeService.save(employeeVO);
		
		EmployeeVO employeeVO2 = employeeService.findByEmail("emailAddress");
		
		//log.info(employeeVO2);

		// assert
		//assertEquals(2, employeeVO2.getId());
		assertEquals(employeeVO.getFirstName(), employeeVO2.getFirstName(),"employeeVO.getFirstName() NOK");
		assertEquals(employeeVO.getLastName(), employeeVO2.getLastName(),"employeeVO.getLastName() NOK");
		assertEquals(employeeVO.getEmail(), employeeVO2.getEmail(),"employeeVO.getEmailAddress() NOK");
	}
	
	@Test
	void updateEmployee() {
		log.info("updateEmployee");
		
		EmployeeVO employeeVO = employeeService.findById(1);
		
		// update with new data
		String firstName = "ime";
		String lastName = "prezime";
		String email = "email";
		
		employeeVO.setFirstName(firstName);
		employeeVO.setLastName(lastName);
		employeeVO.setEmail(email);

		employeeService.save(employeeVO);
		
		// fetch again
		EmployeeVO employeeVO2 = employeeService.findById(1);
		
		// assert
		assertEquals(employeeVO.getId(), employeeVO2.getId());
		assertEquals(firstName, employeeVO2.getFirstName(),"employeeVO.getFirstName() NOK");
		assertEquals(lastName, employeeVO2.getLastName(),"employeeVO.getLastName() NOK");
		assertEquals(email, employeeVO2.getEmail(),"employeeVO.getEmailAddress() NOK");
	}
	
	@Test
	void deleteEmployee() {
		log.info("deleteEmployee");
		
		// first get student
		EmployeeVO employeeVO = employeeService.findById(1);
		
		// assert
		assertNotNull(employeeVO, "return true");
		assertEquals(1, employeeVO.getId());
		
		// delete
		employeeService.delete(1);
		
		// assert not existing student
		Exception exception = assertThrows(ObjectNotFoundException.class, () -> {
			employeeService.findById(1);
		});
				
		String expectedMessage = "Employee with id=1 not found.";
		String actualMessage = exception.getMessage();
		
	    assertTrue(actualMessage.contains(expectedMessage));

		// delete not existing student
		exception = assertThrows(EmptyResultDataAccessException.class, () -> {
			employeeService.delete(2);
		});
	}
	
	@Test
	void getAllEmployees() {
		log.info("getAllEmployees");
		
		Iterable<EmployeeVO> students = employeeService.getAllEmployees();
		
		// assert
		if (students instanceof Collection<?>) {
			assertEquals(1, ((Collection<?>) students).size(), "students size not 1");
		}
		
		// create student
		// set id 0: this is to force a save of new item ... instead of update
		EmployeeVO employeeVO = new EmployeeVO("firstName", "lastName", "emailAddress");
		employeeVO.setId(0);
		
		employeeService.save(employeeVO);
		
		students = employeeService.getAllEmployees();
		
		// assert
		if (students instanceof Collection<?>) {
			assertEquals(2, ((Collection<?>) students).size(), "students size not 2 after insert");
		}
		
		// delete student
		employeeService.delete(1);
		
		students = employeeService.getAllEmployees();
		log.info("after delete " + students.toString());
		
		// assert
		if (students instanceof Collection<?>) {
			assertEquals(1, ((Collection<?>) students).size(), "students size not 1 after delete");
		}
	}
	
	private String getMessage(String str) {
		return messageSource.getMessage(str, new Object[] {}, LocaleContextHolder.getLocale());
	}
}
