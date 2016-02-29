package uk.co.alumeni.prism.workflow.notification.property;

import org.springframework.stereotype.Component;

import uk.co.alumeni.prism.domain.comment.CommentPositionDetail;
import uk.co.alumeni.prism.services.helpers.NotificationPropertyLoader;

@Component
public class ApplicationPositionNameBuilder implements NotificationPropertyBuilder {

    @Override
    public String build(NotificationPropertyLoader propertyLoader) {
        CommentPositionDetail positionDetail = propertyLoader.getNotificationDefinitionDTO().getComment().getPositionDetail();
        return positionDetail == null ? null : positionDetail.getPositionName();
    }

}
