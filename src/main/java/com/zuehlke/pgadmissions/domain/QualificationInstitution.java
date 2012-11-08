package com.zuehlke.pgadmissions.domain;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity(name="INSTITUTION")
@Access(AccessType.FIELD) 
public class QualificationInstitution extends DomainObject<Integer>{

    private static final long serialVersionUID = 2746228908173552617L;

    @Column(name = "domicile_code")
    private String domicileCode;
    
    @Column(name = "name")
    private String name;

    @Column(name = "enabled")
    private Boolean enabled;
    
    @Column(name = "code")
    private String code;
    
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

    public String getDomicileCode() {
        return domicileCode;
    }

    public void setDomicileCode(String domicileCode) {
        this.domicileCode = domicileCode;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}

