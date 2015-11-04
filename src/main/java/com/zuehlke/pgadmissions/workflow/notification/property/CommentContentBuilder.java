package com.zuehlke.pgadmissions.workflow.notification.property;

import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_COMMENT_CONTENT_NOT_PROVIDED;

import org.springframework.stereotype.Component;

import com.zuehlke.pgadmissions.services.helpers.NotificationPropertyLoader;

@Component
public class CommentContentBuilder implements NotificationPropertyBuilder {

    @Override
    public String build(NotificationPropertyLoader propertyLoader) {
        String content = propertyLoader.getNotificationDefinitionDTO().getComment().getContent();
        return content == null ? propertyLoader.getPropertyLoader().loadLazy(SYSTEM_COMMENT_CONTENT_NOT_PROVIDED) : "\"" + content + "\"";
    }

}
