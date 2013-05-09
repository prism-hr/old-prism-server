package com.zuehlke.pgadmissions.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity(name = "DISABILITY")
public class Disability implements ImportedObject, Serializable {
    
	private static final long serialVersionUID = 6141410638125684970L;

    @Column(name = "enabled")
    private Boolean enabled;
    
    @Column(name = "code")
    private Integer code;
    
    @Column(name = "name")
    private String name;

    @Id
    @GeneratedValue
    private Integer id;

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
