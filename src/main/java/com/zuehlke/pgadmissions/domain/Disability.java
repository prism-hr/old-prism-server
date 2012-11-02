package com.zuehlke.pgadmissions.domain;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity(name = "DISABILITY")
@Access(AccessType.FIELD)
public class Disability extends DomainObject<Integer> implements ImportedObject {
	private static final long serialVersionUID = 6141410638125684970L;

    @Column(name = "enabled")
    private Boolean enabled;
    
    @Column(name = "code")
    private Integer code;
    
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

    public Integer getCode() {
        return code;
    }
    
    public String getStringCode() {
        return code.toString();
    }

    public void setCode(Integer code) {
        this.code = code;
    }
	
}
