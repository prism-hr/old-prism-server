package com.zuehlke.pgadmissions.domain;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity(name = "ETHNICITY")
@Access(AccessType.FIELD)
public class Ethnicity extends DomainObject<Integer> {
    private static final long serialVersionUID = -3605895863492842105L;

    @Column(name = "enabled")
    private Boolean enabled;
    
    @Column(name = "code")
    private Integer code;
    
    @Column(name = "name")
    private String name;
    
    @Override
    public final void setId(Integer id) {
        this.id = id;
    }

    @Override
    @Id
    @GeneratedValue
    @Access(AccessType.PROPERTY)
    public final Integer getId() {
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

    public int getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }
}
