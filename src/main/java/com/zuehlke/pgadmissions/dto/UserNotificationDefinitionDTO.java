package com.zuehlke.pgadmissions.dto;

import com.google.common.base.Objects;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction;

public class UserNotificationDefinitionDTO extends UserNotificationDTO {

    private PrismAction actionId;

    public PrismAction getActionId() {
        return actionId;
    }

    public void setActionId(PrismAction actionId) {
        this.actionId = actionId;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(super.hashCode(), actionId);
    }

    @Override
    public boolean equals(Object object) {
        return super.equals(object) && Objects.equal(actionId, ((UserNotificationDefinitionDTO) object).getActionId());
    }

}
