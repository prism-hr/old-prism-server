package com.zuehlke.pgadmissions.workflow.notification.property;

import com.zuehlke.pgadmissions.services.helpers.NotificationPropertyLoader;

public interface NotificationPropertyBuilder {

    public String build(NotificationPropertyLoader propertyLoader);

}
