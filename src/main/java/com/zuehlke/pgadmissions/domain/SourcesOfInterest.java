package com.zuehlke.pgadmissions.domain;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity(name = "SOURCES_OF_INTEREST")
@Access(AccessType.FIELD)
public class SourcesOfInterest extends DomainObject<Integer> implements ImportedObject {

    private static final long serialVersionUID = -3309557608853073374L;

    @Column(name = "enabled")
    private Boolean enabled;
    
    @Column(name = "code")
    private String code;
    
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

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
    
    public boolean isFreeText() {
        return code.equalsIgnoreCase("OTHER");
    }

	@Override
	public String getStringCode() {
		return code;
	}
}
