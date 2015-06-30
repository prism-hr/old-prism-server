package com.zuehlke.pgadmissions.workflow.notification.property;

import org.springframework.stereotype.Component;

import com.zuehlke.pgadmissions.domain.comment.CommentApplicationPositionDetail;
import com.zuehlke.pgadmissions.services.helpers.NotificationPropertyLoader;

@Component
public class ApplicationConfirmedPositionTitleBuilder implements NotificationPropertyBuilder {

    @Override
    public String build(NotificationPropertyLoader propertyLoader) throws Exception {
        CommentApplicationPositionDetail positionDetail = propertyLoader.getNotificationDefinitionModelDTO().getComment().getPositionDetail();
        return positionDetail == null ? null : positionDetail.getPositionTitle();
    }

}
