package com.zuehlke.pgadmissions.dto;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationTemplate;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole;

public class UserNotificationDefinitionDTO {

    private Integer userId;

    private PrismRole roleId;

    private PrismNotificationTemplate notificationTemplateId;

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

    public PrismNotificationTemplate getNotificationTemplateId() {
        return notificationTemplateId;
    }

    public void setNotificationTemplateId(PrismNotificationTemplate notificationTemplateId) {
        this.notificationTemplateId = notificationTemplateId;
    }

}
