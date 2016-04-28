package uk.co.alumeni.prism.rest.dto.user;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import uk.co.alumeni.prism.domain.definitions.workflow.PrismAction;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismRole.PrismRoleCategory;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismScope;

public class UserFeedbackDTO {

	@NotNull
	private PrismAction action;

	@NotNull
	private PrismRoleCategory roleCategory;

    @NotNull
    private PrismScope resourceScope;

	@NotNull
	private Integer resourceId;

    @Valid
    private UserFeedbackContentDTO content;

	public PrismAction getAction() {
		return action;
	}

	public void setAction(PrismAction action) {
		this.action = action;
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
