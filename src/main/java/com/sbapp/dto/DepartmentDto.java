package com.sbapp.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import io.swagger.annotations.ApiModel;

import javax.validation.Valid;
import java.util.List;

@JsonRootName(value = "department")
@ApiModel(value = "department")
public class DepartmentDto {

	@Valid
	@JsonProperty(value = "employee")
	@JacksonXmlElementWrapper(useWrapping = false)
	private List<EmployeeDto> employees;

	public List<EmployeeDto> getEmployees() {
		return employees;
	}

	public void setEmployees(List<EmployeeDto> employees) {
		this.employees = employees;
	}

}
