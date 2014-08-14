package com.zuehlke.pgadmissions.dto;

import com.zuehlke.pgadmissions.domain.Action;
import com.zuehlke.pgadmissions.domain.NotificationTemplate;

public class ResourceActionDTO {

    private Integer resourceId;
    
    private Action action;
    
    private NotificationTemplate notificationTemplate;

    public final Integer getResourceId() {
        return resourceId;
    }

    public final void setResourceId(Integer resourceId) {
        this.resourceId = resourceId;
    }

    public final Action getAction() {
        return action;
    }

    public final void setAction(Action action) {
        this.action = action;
    }

    public final NotificationTemplate getNotificationTemplate() {
        return notificationTemplate;
    }

    public final void setNotificationTemplate(NotificationTemplate notificationTemplate) {
        this.notificationTemplate = notificationTemplate;
    }
    
}
