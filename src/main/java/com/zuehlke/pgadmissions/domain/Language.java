package com.zuehlke.pgadmissions.domain;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import com.zuehlke.pgadmissions.validators.ESAPIConstraint;

@Entity(name="LANGUAGE")
@Access(AccessType.FIELD) 
public class Language extends DomainObject<Integer> implements ImportedObject {
	
	private static final long serialVersionUID = -4719304115154138995L;
	
	@Column(name = "enabled")
	private Boolean enabled;
	
	@Column(name = "code")
    private String code;
	
	@ESAPIConstraint(rule = "ExtendedAscii", maxLength = 70)
	@Column(name = "name")
	private String name;
	
	@Override
	public void setId(Integer id) {
		this.id = id;
	}

	@Override
	@Id
	@GeneratedValue
	@Access(AccessType.PROPERTY)
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
