package com.zuehlke.pgadmissions.rest.dto.user;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleCategory;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

public class UserFeedbackDTO {

	@NotNull
	private Integer user;

	@NotNull
	private PrismRoleCategory roleCategory;

    @NotNull
    private PrismScope resourceScope;

	@NotNull
	private Integer resourceId;

    @Valid
    private UserFeedbackContentDTO content;

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

    public PrismScope getResourceScope() {
        return resourceScope;
    }

    public void setResourceScope(PrismScope resourceScope) {
        this.resourceScope = resourceScope;
    }

    public Integer getResourceId() {
        return resourceId;
    }

    public void setResourceId(Integer resourceId) {
        this.resourceId = resourceId;
    }

    public UserFeedbackContentDTO getContent() {
        return content;
    }

    public void setContent(UserFeedbackContentDTO content) {
        this.content = content;
    }
}
