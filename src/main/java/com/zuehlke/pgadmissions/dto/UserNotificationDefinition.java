package com.zuehlke.pgadmissions.dto;

import com.google.common.base.Objects;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationTemplate;

public class UserNotificationDefinition {

    private Integer resourceId;

    private Integer userRoleId;

    private PrismNotificationTemplate notificationTemplateId;

    public final Integer getResourceId() {
        return resourceId;
    }

    public final void setResourceId(Integer resourceId) {
        this.resourceId = resourceId;
    }

    public Integer getUserRoleId() {
        return userRoleId;
    }

    public void setUserRoleId(Integer userRoleId) {
        this.userRoleId = userRoleId;
    }

    public PrismNotificationTemplate getNotificationTemplateId() {
        return notificationTemplateId;
    }

    public void setNotificationTemplateId(PrismNotificationTemplate notificationTemplateId) {
        this.notificationTemplateId = notificationTemplateId;
    }

    public UserNotificationDefinition withUserRoleId(Integer userRoleId) {
        this.userRoleId = userRoleId;
        return this;
    }

    public UserNotificationDefinition withNotificationTemplateId(PrismNotificationTemplate notificationTemplateId) {
        this.notificationTemplateId = notificationTemplateId;
        return this;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(resourceId, userRoleId, notificationTemplateId);
    }

    @Override
    public boolean equals(Object object) {
        if (object == null) {
            return false;
        }
        if (getClass() != object.getClass()) {
            return false;
        }
        final UserNotificationDefinition other = (UserNotificationDefinition) object;
        return Objects.equal(resourceId, other.getResourceId()) && Objects.equal(userRoleId, other)
                && Objects.equal(notificationTemplateId, other.getNotificationTemplateId());
    }

}
