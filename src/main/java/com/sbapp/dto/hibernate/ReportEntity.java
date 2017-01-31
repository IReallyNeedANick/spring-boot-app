package com.sbapp.dto.hibernate;

import com.sbapp.dto.enumeration.SourceEnum;

import javax.persistence.*;
import java.util.Date;

@Entity
public class ReportEntity {

	@Id
	@Column(name = "name", unique = true, nullable = false)
	private String name;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "process_time", nullable = false, length = 26)
	private Date processTime;

	@Enumerated(EnumType.STRING)
	@Column(name = "source", nullable = false, length = 10)
	private SourceEnum source;

	@Column(name = "success", nullable = false)
	private boolean success;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Date getProcessTime() {
		return processTime;
	}

	public void setProcessTime(Date processTime) {
		this.processTime = processTime;
	}

	public SourceEnum getSource() {
		return source;
	}

	public void setSource(SourceEnum source) {
		this.source = source;
	}

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

}
