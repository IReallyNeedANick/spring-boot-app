package com.sbapp.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;

@ApiModel(value = "manager")
public class ManagerDto {

	@NotNull(message = "manager.fk1 must not be null")
	@ApiModelProperty(value = "foreign key of employee", required = true)
	private Integer fk1;

	@NotEmpty(message = "manager.fk2 must not be null/empty")
	@ApiModelProperty(value = "alternate foreign key of employee", required = true)
	private String fk2;

	public Integer getFk1() {
		return fk1;
	}

	public void setFk1(Integer fk1) {
		this.fk1 = fk1;
	}

	public String getFk2() {
		return fk2;
	}

	public void setFk2(String fk2) {
		this.fk2 = fk2;
	}
}
