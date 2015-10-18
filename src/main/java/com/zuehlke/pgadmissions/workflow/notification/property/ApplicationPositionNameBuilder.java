package com.zuehlke.pgadmissions.workflow.notification.property;

import org.springframework.stereotype.Component;

import com.zuehlke.pgadmissions.domain.comment.CommentPositionDetail;
import com.zuehlke.pgadmissions.services.helpers.NotificationPropertyLoader;

@Component
public class ApplicationPositionNameBuilder implements NotificationPropertyBuilder {

    @Override
    public String build(NotificationPropertyLoader propertyLoader) throws Exception {
        CommentPositionDetail positionDetail = propertyLoader.getNotificationDefinitionDTO().getComment().getPositionDetail();
        return positionDetail == null ? null : positionDetail.getPositionName();
    }

}
