package com.zuehlke.pgadmissions.rest.dto.user;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;

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
