package com.zuehlke.pgadmissions.domain.builders;

import com.zuehlke.pgadmissions.domain.Ethnicity;

public class EthnicityBuilder {
	private String name;
	private Integer id;

	public EthnicityBuilder name(String ethName) {
		this.name = ethName;
		return this;
	}

	public EthnicityBuilder id(Integer ethId) {
		this.id = ethId;
		return this;
	}

	public Ethnicity toEthnicity() {
		Ethnicity eth = new Ethnicity();
		eth.setId(id);
		eth.setName(name);
		return eth;
	}
}
