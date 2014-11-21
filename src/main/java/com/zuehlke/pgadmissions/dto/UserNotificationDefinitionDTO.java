package com.zuehlke.pgadmissions.dto;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationDefinition;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole;

public class UserNotificationDefinitionDTO {

    private Integer userId;

    private PrismRole roleId;

    private PrismNotificationDefinition notificationDefinitionId;

    private PrismAction actionId;

    public final Integer getUserId() {
        return userId;
    }

    public final void setUserId(Integer userId) {
        this.userId = userId;
    }

    public final PrismRole getRoleId() {
        return roleId;
    }

    public final void setRoleId(PrismRole roleId) {
        this.roleId = roleId;
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
