package com.zuehlke.pgadmissions.workflow.notification.property;

import org.springframework.stereotype.Component;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition;
import com.zuehlke.pgadmissions.dto.NotificationDefinitionModelDTO;
import com.zuehlke.pgadmissions.services.helpers.NotificationPropertyLoader;

@Component
public class CommentTransitionOutcomeBuilder implements NotificationPropertyBuilder {

    @Override
    public String build(NotificationPropertyLoader propertyLoader) throws Exception {
        NotificationDefinitionModelDTO modelDTO = propertyLoader.getNotificationDefinitionModelDTO();
        String resourceName = modelDTO.getResource().getResourceScope().name();
        String outcomePostfix = Iterables.getLast(Lists.newArrayList(modelDTO.getComment().getTransitionState().getId().name().split("_")));
        return propertyLoader.getPropertyLoader().loadLazy(PrismDisplayPropertyDefinition.valueOf(resourceName + "_COMMENT_" + outcomePostfix));
    }

}
