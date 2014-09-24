package com.zuehlke.pgadmissions.domain.builders;

import com.zuehlke.pgadmissions.domain.Disability;

public class DisabilityBuilder {
    
	private String name;
	
	private Integer id;
	
	private Boolean enabled;

	private String code;
	
	private Disability enabledObject;
	
	public DisabilityBuilder code(String code) {
        this.code = code;
        return this;
    }
	
	public DisabilityBuilder name(String disabilityName) {
		this.name = disabilityName;
		return this;
	}

	public DisabilityBuilder id(Integer disabilityId) {
		this.id = disabilityId;
		return this;
	}
	
	public DisabilityBuilder enabled(Boolean enabled){
        this.enabled = enabled;
        return this;
    }
	
	public DisabilityBuilder enabledObject(Disability enabledObject){
	    this.enabledObject = enabledObject;
	    return this;
	}

	public Disability build() {
		Disability disability = new Disability();
		disability.setId(id);
		disability.setName(name);
		disability.setEnabled(enabled);
		disability.setCode(code);
		disability.setEnabledObject(enabledObject);
		return disability;
	}
}