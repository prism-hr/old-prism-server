package com.zuehlke.pgadmissions.workflow.notification.property;

import org.springframework.stereotype.Component;

import com.zuehlke.pgadmissions.domain.comment.CommentOfferDetail;
import com.zuehlke.pgadmissions.services.helpers.NotificationPropertyLoader;

@Component
public class ApplicationOfferConditionBuilder implements NotificationPropertyBuilder {

    @Override
    public String build(NotificationPropertyLoader propertyLoader) throws Exception {
        CommentOfferDetail offerDetail = propertyLoader.getNotificationDefinitionModelDTO().getComment().getOfferDetail();
        return offerDetail == null ? null : offerDetail.getAppointmentConditions();
    }

}
