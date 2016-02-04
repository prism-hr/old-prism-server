package com.zuehlke.pgadmissions.workflow.notification.property;

import org.springframework.stereotype.Component;

import com.zuehlke.pgadmissions.domain.comment.CommentSponsorship;
import com.zuehlke.pgadmissions.services.helpers.NotificationPropertyLoader;

@Component
public class CommentSponsorshipBuilder implements NotificationPropertyBuilder {

    @Override
    public String build(NotificationPropertyLoader propertyLoader) throws Exception {
        CommentSponsorship sponsorship = propertyLoader.getNotificationDefinitionModelDTO().getComment().getSponsorship();
        return sponsorship == null ? null : sponsorship.toString();
    }

}
