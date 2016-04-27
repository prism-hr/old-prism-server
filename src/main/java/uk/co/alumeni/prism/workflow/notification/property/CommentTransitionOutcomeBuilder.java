package uk.co.alumeni.prism.workflow.notification.property;

import static com.google.common.collect.Iterables.getLast;
import static com.google.common.collect.Lists.newArrayList;

import org.springframework.stereotype.Component;

import uk.co.alumeni.prism.domain.definitions.PrismDisplayPropertyDefinition;
import uk.co.alumeni.prism.dto.NotificationDefinitionDTO;
import uk.co.alumeni.prism.services.helpers.NotificationPropertyLoader;

@Component
public class CommentTransitionOutcomeBuilder implements NotificationPropertyBuilder {

    @Override
    public String build(NotificationPropertyLoader propertyLoader) {
        NotificationDefinitionDTO notificationDefinitionDTO = propertyLoader.getNotificationDefinitionDTO();
        String resourceReference = notificationDefinitionDTO.getResource().getResourceScope().name();
        String outcomePostfix = getLast(newArrayList(notificationDefinitionDTO.getComment().getTransitionState().getId().name().split("_")));
        return propertyLoader.getPropertyLoader().loadLazy(PrismDisplayPropertyDefinition.valueOf(resourceReference + "_COMMENT_" + outcomePostfix));
    }

}
