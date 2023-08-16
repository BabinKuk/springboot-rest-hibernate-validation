package org.babinkuk.dao;

import java.util.Optional;

import org.babinkuk.entity.Employee;
import org.springframework.data.repository.CrudRepository;

public interface EmployeeRepository extends CrudRepository<Employee, Integer> {
	
	// optional
	public Optional<Employee> findByEmail(String email);
}
