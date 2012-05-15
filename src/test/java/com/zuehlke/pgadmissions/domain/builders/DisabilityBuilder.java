package com.zuehlke.pgadmissions.domain.builders;

import com.zuehlke.pgadmissions.domain.Disability;

public class DisabilityBuilder {
	private String name;
	private Integer id;

	public DisabilityBuilder name(String disabilityName) {
		this.name = disabilityName;
		return this;
	}

	public DisabilityBuilder id(Integer disabilityId) {
		this.id = disabilityId;
		return this;
	}

	public Disability toDisability() {
		Disability disability = new Disability();
		disability.setId(id);
		disability.setName(name);
		return disability;
	}
}
