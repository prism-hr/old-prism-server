package com.zuehlke.pgadmissions.domain.builders;

import com.zuehlke.pgadmissions.domain.Action;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormAction;
import com.zuehlke.pgadmissions.domain.enums.NotificationMethod;

public class ActionBuilder {

    private ApplicationFormAction id;
    private NotificationMethod notification;

    public ActionBuilder id(ApplicationFormAction id) {
        this.id = id;
        return this;
    }

    public ActionBuilder notification(NotificationMethod notification) {
        this.notification = notification;
        return this;
    }

    public Action build() {
        Action action = new Action();
        action.setId(id);
        action.setNotificationMethod(notification);
        return action;
    }

}