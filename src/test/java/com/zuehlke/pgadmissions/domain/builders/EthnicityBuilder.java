package com.zuehlke.pgadmissions.domain.builders;

import com.zuehlke.pgadmissions.domain.Ethnicity;

public class EthnicityBuilder {
	private String name;
	private Integer id;
	private Boolean enabled;
	private String code;

	public EthnicityBuilder code(String code) {
        this.code = code;
        return this;
    }
	
	public EthnicityBuilder name(String ethName) {
		this.name = ethName;
		return this;
	}

	public EthnicityBuilder id(Integer ethId) {
		this.id = ethId;
		return this;
	}
	
	public EthnicityBuilder enabled(Boolean enabled){
        this.enabled = enabled;
        return this;
    }

	public Ethnicity build() {
		Ethnicity eth = new Ethnicity();
		eth.setId(id);
		eth.setName(name);
		eth.setEnabled(enabled);
		eth.setCode(code);
		return eth;
	}
}
