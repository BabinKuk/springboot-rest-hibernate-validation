package org.babinkuk.service;

import org.babinkuk.common.ApiResponse;
import org.babinkuk.exception.ObjectException;
import org.babinkuk.exception.ObjectNotFoundException;
import org.babinkuk.vo.EmployeeVO;

public interface EmployeeService {
	
	/**
	 * get student list
	 * 
	 * @return Iterable<StudentVO>
	 */
	public Iterable<EmployeeVO> getAllEmployees();
	
	/**
	 * get student (by id)
	 * 
	 * @param id
	 * @return StudentVO
	 * @throws ObjectNotFoundException
	 */
	public EmployeeVO findById(int id) throws ObjectNotFoundException;
	
	/**
	 * get student (by email)
	 * 
	 * @param email
	 * @return StudentVO
	 * @throws ObjectNotFoundException
	 */
	public EmployeeVO findByEmail(String email) throws ObjectNotFoundException;
	
	/**
	 * save student (on insert/update)
	 * 
	 * @param studentVO
	 * @return
	 * @throws ObjectException
	 */
	public ApiResponse save(EmployeeVO studentVO) throws ObjectException;
	
	/**
	 * delete student
	 * 
	 * @param id
	 * @return
	 * @throws ObjectNotFoundException
	 */
	public ApiResponse delete(int id) throws ObjectNotFoundException;
}
