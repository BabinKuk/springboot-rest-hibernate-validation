package org.babinkuk.vo;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

/**
 * instance of this class is used to represent employee data
 * 
 * @author BabinKuk
 *
 */
public class EmployeeVO {
	
	private int id;
	
	@NotBlank(message = "error_code_first_name_empty")
	private String firstName;
	
	@NotBlank(message = "error_code_last_name_empty")
	private String lastName;
	
	@NotBlank(message = "error_code_email_empty")
	@Email(message = "error_code_email_invalid", regexp = "^(.+)@(\\S+)$")
	private String email;
	
	public EmployeeVO() {
		// TODO Auto-generated constructor stub
	}
	
	public EmployeeVO(String firstName, String lastName, String email) {
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	@Override
	public String toString() {
		return "StudentVO [firstName=" + firstName + ", lastName=" + lastName + ", email=" + email + "]";
	}

}
