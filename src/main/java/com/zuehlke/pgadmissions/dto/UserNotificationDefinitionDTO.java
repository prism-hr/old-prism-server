package com.zuehlke.pgadmissions.dto;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationDefinition;

public class UserNotificationDefinitionDTO {

    private Integer userId;

    private PrismNotificationDefinition notificationDefinitionId;

    private PrismAction actionId;

    public final Integer getUserId() {
        return userId;
    }

    public final void setUserId(Integer userId) {
        this.userId = userId;
    }

    public PrismNotificationDefinition getNotificationDefinitionId() {
        return notificationDefinitionId;
    }

    public void setNotificationDefinitionId(PrismNotificationDefinition notificationDefinitionId) {
        this.notificationDefinitionId = notificationDefinitionId;
    }

    public PrismAction getActionId() {
        return actionId;
    }

    public void setActionId(PrismAction actionId) {
        this.actionId = actionId;
    }

}
