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

    @Column(name = "country_name")
    private String country_name;
    
    private String name;

    private Boolean enabled;
    
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
    
    public String getCountry_name() {
        return country_name;
    }

    public void setCountryName(String country_name) {
        this.country_name = country_name;
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
}

