package com.zuehlke.pgadmissions.workflow.notification.property;

import static com.google.common.collect.Iterables.getLast;
import static com.google.common.collect.Lists.newArrayList;

import org.springframework.stereotype.Component;

import com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition;
import com.zuehlke.pgadmissions.dto.NotificationDefinitionDTO;
import com.zuehlke.pgadmissions.services.helpers.NotificationPropertyLoader;

@Component
public class CommentTransitionOutcomeBuilder implements NotificationPropertyBuilder {

    @Override
    public String build(NotificationPropertyLoader propertyLoader) throws Exception {
        NotificationDefinitionDTO notificationDefinitionDTO = propertyLoader.getNotificationDefinitionDTO();
        String resourceReference = notificationDefinitionDTO.getResource().getResourceScope().name();
        String outcomePostfix = getLast(newArrayList(notificationDefinitionDTO.getComment().getTransitionState().getId().name().split("_")));
        return propertyLoader.getPropertyLoader().loadLazy(PrismDisplayPropertyDefinition.valueOf(resourceReference + "_COMMENT_" + outcomePostfix));
    }

}
