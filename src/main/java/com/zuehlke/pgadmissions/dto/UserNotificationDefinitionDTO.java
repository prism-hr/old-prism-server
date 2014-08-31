package com.zuehlke.pgadmissions.dto;

import org.joda.time.LocalDate;

import com.google.common.base.Objects;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationTemplate;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole;

public class UserNotificationDefinitionDTO {

    private Integer userId;

    private PrismRole roleId;

    private PrismNotificationTemplate notificationTemplateId;
    
    private LocalDate lastNotifiedDate;

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

    public final LocalDate getLastNotifiedDate() {
        return lastNotifiedDate;
    }

    public final void setLastNotifiedDate(LocalDate lastNotifiedDate) {
        this.lastNotifiedDate = lastNotifiedDate;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(userId, roleId, notificationTemplateId);
    }

    @Override
    public boolean equals(Object object) {
        if (object == null) {
            return false;
        }
        if (getClass() != object.getClass()) {
            return false;
        }
        final UserNotificationDefinitionDTO other = (UserNotificationDefinitionDTO) object;
        return Objects.equal(userId, other.getUserId()) && Objects.equal(roleId, other.getRoleId())
                && Objects.equal(notificationTemplateId, other.getNotificationTemplateId());
    }

}
