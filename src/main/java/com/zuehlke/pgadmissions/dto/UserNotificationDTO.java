package com.zuehlke.pgadmissions.dto;

import com.google.common.base.Objects;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationDefinition;

public class UserNotificationDTO {

    private Integer userId;

    private PrismNotificationDefinition notificationDefinitionId;

    private Long sentCount;

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public PrismNotificationDefinition getNotificationDefinitionId() {
        return notificationDefinitionId;
    }

    public void setNotificationDefinitionId(PrismNotificationDefinition notificationDefinitionId) {
        this.notificationDefinitionId = notificationDefinitionId;
    }

    public Long getSentCount() {
        return sentCount;
    }

    public void setSentCount(Long sentCount) {
        this.sentCount = sentCount;
    }

    public UserNotificationDTO withUserId(Integer userId) {
        this.userId = userId;
        return this;
    }

    public UserNotificationDTO withNotificationDefinitionId(PrismNotificationDefinition notificationDefinitionId) {
        this.notificationDefinitionId = notificationDefinitionId;
        return this;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(userId, notificationDefinitionId);
    }

    @Override
    public boolean equals(Object object) {
        if (object == null) {
            return false;
        }
        if (getClass() != object.getClass()) {
            return false;
        }
        UserNotificationDTO other = (UserNotificationDTO) object;
        return Objects.equal(userId, other.getUserId()) && Objects.equal(notificationDefinitionId, other.getNotificationDefinitionId());
    }

}
