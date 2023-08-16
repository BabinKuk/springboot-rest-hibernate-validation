package org.babinkuk.mapper;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.babinkuk.entity.Employee;
import org.babinkuk.vo.EmployeeVO;
import org.mapstruct.AfterMapping;
import org.mapstruct.BeanMapping;
import org.mapstruct.BeforeMapping;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;
import org.springframework.util.CollectionUtils;

/**
 * mapper for the entity @link {@link Employee} and its DTO {@link EmployeeVO}
 * 
 * @author Nikola
 */
@Mapper
(
	componentModel = "spring",
	unmappedSourcePolicy = ReportingPolicy.WARN,
	imports = {StringUtils.class, Objects.class}
	//if needed add uses = {add different classes for complex objects} 
)
public interface EmployeeMapper {
	
	public EmployeeMapper employeeMapperInstance = Mappers.getMapper(EmployeeMapper.class);
	
//	@AfterMapping
//	default void afterMapEmployee(@MappingTarget Employee entity, EmployeeVO employeeVO) {
//		System.out.println("afterMapEmployee");
//		System.out.println(employeeVO.toString());
//		if (!StringUtils.isBlank(employeeVO.getEmail())) {
//			entity.setEmail(employeeVO.getEmail());
//		}
//		System.out.println(entity.toString());
//	}
	
	// for insert
	@Named("toEntity")
	//@Mapping(source = "email", target = "email")
	Employee toEntity(EmployeeVO employeeVO);
	
	// for update
	@Named("toEntity")
	//@Mapping(source = "email", target = "email")
	Employee toEntity(EmployeeVO employeeVO, @MappingTarget Employee entity);
	
	@Named("toVO")
	//@Mapping(source = "email", target = "email")
	EmployeeVO toVO(Employee employee);
	
	@Named("toVODetails")
	@Mapping(source = "email", target = "email")
	EmployeeVO toVODetails(Employee employee);
	
	@IterableMapping(qualifiedByName = "toEntity")
	@BeanMapping(ignoreByDefault = true)
	Iterable<Employee> toEntity(Iterable<EmployeeVO> employeeList);
	
	@IterableMapping(qualifiedByName = "toVO")
	@BeanMapping(ignoreByDefault = true)
	Iterable<EmployeeVO> toVO(Iterable<Employee> employeeList);
}