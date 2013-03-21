package com.zuehlke.pgadmissions.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity(name = "THROTTLE")
public class Throttle implements Serializable {

	private static final long serialVersionUID = 6043305345938615563L;

	@Id
	@GeneratedValue
	private Integer id;
	
	@Column(name = "enabled")
	private Boolean enabled;
	
	@Column(name = "batch_size")
	private Integer batchSize;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Boolean getEnabled() {
		return enabled;
	}

	public void setEnabled(Boolean enabled) {
		this.enabled = enabled;
	}

	public Integer getBatchSize() {
		return batchSize;
	}

	public void setBatchSize(Integer batchSize) {
		this.batchSize = batchSize;
	}
}
