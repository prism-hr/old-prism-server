package uk.co.alumeni.prism.rest.dto.user;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

import uk.co.alumeni.prism.domain.definitions.workflow.PrismAction;

public class UserActivateDTO {

    @NotEmpty
    private String activationCode;

    @NotNull
    private PrismAction actionId;

    @NotNull
    private Integer resourceId;

    public String getActivationCode() {
        return activationCode;
    }

    public void setActivationCode(String activationCode) {
        this.activationCode = activationCode;
    }

    public PrismAction getActionId() {
        return actionId;
    }

    public void setActionId(PrismAction actionId) {
        this.actionId = actionId;
    }

    public Integer getResourceId() {
        return resourceId;
    }

    public void setResourceId(Integer resourceId) {
        this.resourceId = resourceId;
    }
}
