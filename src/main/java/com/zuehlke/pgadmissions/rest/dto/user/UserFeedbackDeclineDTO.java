package com.zuehlke.pgadmissions.rest.dto.user;

import javax.validation.constraints.NotNull;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleCategory;

public class UserFeedbackDeclineDTO {

	@NotNull
	private Integer user;
	
	@NotNull
	private PrismRoleCategory roleCategory;
	
	@NotNull
	private Integer institution;

	public Integer getUser() {
		return user;
	}

	public void setUser(Integer user) {
		this.user = user;
	}

	public PrismRoleCategory getRoleCategory() {
		return roleCategory;
	}

	public void setRoleCategory(PrismRoleCategory roleCategory) {
		this.roleCategory = roleCategory;
	}

	public Integer getInstitution() {
		return institution;
	}

	public void setInstitution(Integer institution) {
		this.institution = institution;
	}
	
}
