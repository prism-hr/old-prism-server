package uk.co.alumeni.prism.workflow.notification.property;

import org.springframework.stereotype.Component;
import uk.co.alumeni.prism.services.helpers.NotificationPropertyLoader;

import static uk.co.alumeni.prism.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_COMMENT_CONTENT_NOT_PROVIDED;

@Component
public class CommentContentBuilder implements NotificationPropertyBuilder {

    @Override
    public String build(NotificationPropertyLoader propertyLoader) {
        String content = propertyLoader.getNotificationDefinitionDTO().getComment().getContent();
        return content == null ? propertyLoader.getPropertyLoader().loadLazy(SYSTEM_COMMENT_CONTENT_NOT_PROVIDED) : "\"" + content + "\"";
    }

}
