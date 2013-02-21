package com.zuehlke.pgadmissions.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import com.zuehlke.pgadmissions.validators.ESAPIConstraint;

@Entity(name="LANGUAGE")
public class Language implements ImportedObject, Serializable {
	
	private static final long serialVersionUID = -4719304115154138995L;

	@Id
	@GeneratedValue
	private Integer id;
	
	@Column(name = "enabled")
	private Boolean enabled;
	
	@Column(name = "code")
    private String code;
	
	@ESAPIConstraint(rule = "ExtendedAscii", maxLength = 70)
	@Column(name = "name")
	private String name;
	
	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

	@Override
	public String getStringCode() {
		return code;
	}

}
