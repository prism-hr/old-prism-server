package com.zuehlke.pgadmissions.workflow.notification.property;

import static com.zuehlke.pgadmissions.PrismConstants.SPACE;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_NOTIFICATION_ACTIONS;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_NOTIFICATION_APPOINTMENTS;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_NOTIFICATION_CONNECTS;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_NOTIFICATION_JOINS;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_NOTIFICATION_UPDATES;
import static org.apache.commons.collections.CollectionUtils.isNotEmpty;
import static org.jboss.util.Strings.EMPTY;

import java.util.List;

import org.springframework.stereotype.Component;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.zuehlke.pgadmissions.dto.NotificationDefinitionDTO;
import com.zuehlke.pgadmissions.rest.representation.advert.AdvertTargetRepresentation;
import com.zuehlke.pgadmissions.rest.representation.user.UserActivityRepresentation;
import com.zuehlke.pgadmissions.rest.representation.user.UserActivityRepresentation.AppointmentActivityRepresentation;
import com.zuehlke.pgadmissions.rest.representation.user.UserActivityRepresentation.ResourceActivityRepresentation;
import com.zuehlke.pgadmissions.rest.representation.user.UserActivityRepresentation.ResourceActivityRepresentation.ActionActivityRepresentation;
import com.zuehlke.pgadmissions.rest.representation.user.UserActivityRepresentation.ResourceUnverifiedUserRepresentation;
import com.zuehlke.pgadmissions.services.helpers.NotificationPropertyLoader;
import com.zuehlke.pgadmissions.services.helpers.PropertyLoader;

@Component
public class SystemActivitySummaryBuilder implements NotificationPropertyBuilder {

    @Override
    public String build(NotificationPropertyLoader propertyLoader) {
        NotificationDefinitionDTO notificationDefinitionDTO = propertyLoader.getNotificationDefinitionDTO();
        UserActivityRepresentation userActivityRepresentation = notificationDefinitionDTO.getUserActivityRepresentation();

        List<ResourceActivityRepresentation> resourceActivityRepresentations = userActivityRepresentation.getResourceActivities();
        List<AppointmentActivityRepresentation> appointmentActivityRepresentations = userActivityRepresentation.getAppointmentActivities();
        List<ResourceUnverifiedUserRepresentation> unverifiedUserActivities = userActivityRepresentation.getUnverifiedUserActivities();
        List<AdvertTargetRepresentation> advertTargetActivities = userActivityRepresentation.getAdvertTargetActivities();

        List<String> bullets = Lists.newLinkedList();
        PropertyLoader displayPropertyLoader = propertyLoader.getPropertyLoader();
        if (isNotEmpty(resourceActivityRepresentations)) {
            Integer actionCount = 0;
            Integer updateCount = 0;
            for (ResourceActivityRepresentation resourceActivityRepresentation : resourceActivityRepresentations) {
                List<ActionActivityRepresentation> actionActivityRepresentations = resourceActivityRepresentation.getActions();
                if (isNotEmpty(actionActivityRepresentations)) {
                    for (ActionActivityRepresentation actionActivityRepresentation : actionActivityRepresentations) {
                        actionCount = actionCount + actionActivityRepresentation.getUrgentCount();
                    }
                }

                Integer resourceUpdateCount = resourceActivityRepresentation.getUpdateCount();
                if (resourceUpdateCount != null) {
                    updateCount = updateCount + resourceUpdateCount;
                }
            }

            if (actionCount > 0) {
                bullets.add(actionCount.toString() + SPACE + displayPropertyLoader.loadLazy(SYSTEM_NOTIFICATION_ACTIONS));
            }

            if (updateCount > 0) {
                bullets.add(updateCount.toString() + SPACE + displayPropertyLoader.loadLazy(SYSTEM_NOTIFICATION_UPDATES));
            }
        }

        if (isNotEmpty(appointmentActivityRepresentations)) {
            bullets.add(appointmentActivityRepresentations.size() + SPACE + displayPropertyLoader.loadLazy(SYSTEM_NOTIFICATION_APPOINTMENTS));
        }

        if (isNotEmpty(unverifiedUserActivities)) {
            Integer joinCount = 0;
            for (ResourceUnverifiedUserRepresentation unverifiedUserActivity : unverifiedUserActivities) {
                joinCount = joinCount + unverifiedUserActivity.getUsers().size();
            }
            bullets.add(joinCount.toString() + SPACE + displayPropertyLoader.loadLazy(SYSTEM_NOTIFICATION_JOINS));
        }

        if (isNotEmpty(advertTargetActivities)) {
            Integer connectCount = 0;
            for (AdvertTargetRepresentation advertTargetActivity : advertTargetActivities) {
                connectCount = connectCount + advertTargetActivity.getConnections().size();
            }
            bullets.add(connectCount.toString() + SPACE + displayPropertyLoader.loadLazy(SYSTEM_NOTIFICATION_CONNECTS));
        }

        if (isNotEmpty(bullets)) {
            return "<li>" + Joiner.on("</li><li>").join(bullets) + "</li>";
        }

        return EMPTY;
    }

}
