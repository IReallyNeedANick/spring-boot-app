package com.sbapp.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

@ApiModel(value = "employee")
public class EmployeeDto {

	@NotNull(message = "employee.id1 must not be null")
	@ApiModelProperty(value = "primary key of employee", required = true)
	private Integer id1;

	@NotEmpty(message = "employee.id2 must not be null/empty")
	@ApiModelProperty(value = "alternate primary key of employee", required = true)
	private String id2;

	@Valid
	@JsonProperty(value = "manager")
	@JacksonXmlElementWrapper(useWrapping = false)
	private List<ManagerDto> managers;

	@Valid
	@JsonProperty(value = "team-member")
	@JacksonXmlElementWrapper(useWrapping = false)
	private List<TeamMemberDto> teamMembers;

	public Integer getId1() {
		return id1;
	}

	public void setId1(Integer id1) {
		this.id1 = id1;
	}

	public String getId2() {
		return id2;
	}

	public void setId2(String id2) {
		this.id2 = id2;
	}

	public List<ManagerDto> getManagers() {
		return managers;
	}

	public void setManagers(List<ManagerDto> managers) {
		this.managers = managers;
	}

	public List<TeamMemberDto> getTeamMembers() {
		return teamMembers;
	}

	public void setTeamMembers(List<TeamMemberDto> teamMembers) {
		this.teamMembers = teamMembers;
	}
}
