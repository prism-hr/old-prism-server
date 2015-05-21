package com.zuehlke.pgadmissions.domain.definitions.workflow;

import com.google.common.base.Objects;

public class PrismStateActionNotification {

    private PrismRole role;

    private Boolean partnerMode = false;

    private PrismNotificationDefinition notification;

    public PrismRole getRole() {
        return role;
    }

    public PrismNotificationDefinition getNotification() {
        return notification;
    }

    public Boolean getPartnerMode() {
        return partnerMode;
    }

    public PrismStateActionNotification withRole(PrismRole role) {
        this.role = role;
        return this;
    }

    public PrismStateActionNotification withPartnerMode() {
        this.partnerMode = true;
        return this;
    }

    public PrismStateActionNotification withPartnerMode(Boolean partnerMode) {
        this.partnerMode = partnerMode;
        return this;
    }

    public PrismStateActionNotification withDefinition(PrismNotificationDefinition notification) {
        this.notification = notification;
        return this;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(role, partnerMode, notification);
    }

    @Override
    public boolean equals(Object object) {
        if (object == null) {
            return false;
        }
        if (getClass() != object.getClass()) {
            return false;
        }
        final PrismStateActionNotification other = (PrismStateActionNotification) object;
        return Objects.equal(role, other.getRole()) && Objects.equal(partnerMode, other.getPartnerMode())
                && Objects.equal(notification, other.getNotification());
    }

}
