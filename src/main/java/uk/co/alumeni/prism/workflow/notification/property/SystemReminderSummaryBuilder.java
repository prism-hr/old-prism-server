package uk.co.alumeni.prism.workflow.notification.property;

import static uk.co.alumeni.prism.PrismConstants.SPACE;
import static uk.co.alumeni.prism.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_FOR;

import java.util.List;

import org.springframework.stereotype.Component;

import uk.co.alumeni.prism.domain.definitions.workflow.PrismAction;
import uk.co.alumeni.prism.dto.NotificationDefinitionDTO;
import uk.co.alumeni.prism.rest.representation.user.UserActivityRepresentation;
import uk.co.alumeni.prism.rest.representation.user.UserActivityRepresentation.ResourceActivityRepresentation;
import uk.co.alumeni.prism.rest.representation.user.UserActivityRepresentation.ResourceActivityRepresentation.ActionActivityRepresentation;
import uk.co.alumeni.prism.services.helpers.NotificationPropertyLoader;
import uk.co.alumeni.prism.services.helpers.PropertyLoader;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;

@Component
public class SystemReminderSummaryBuilder implements NotificationPropertyBuilder {

    @Override
    public String build(NotificationPropertyLoader propertyLoader) {
        NotificationDefinitionDTO notificationDefinitionDTO = propertyLoader.getNotificationDefinitionDTO();
        UserActivityRepresentation userActivityRepresentation = notificationDefinitionDTO.getUserActivityRepresentation();

        List<String> bullets = Lists.newLinkedList();
        PropertyLoader loader = propertyLoader.getPropertyLoader();
        for (ResourceActivityRepresentation resourceActivity : userActivityRepresentation.getResourceActivities()) {
            for (ActionActivityRepresentation actionActivity : resourceActivity.getActions()) {
                PrismAction action = actionActivity.getAction().getId();
                bullets.add(loader.loadLazy(action.getDisplayProperty()) + SPACE + loader.loadEager(SYSTEM_FOR) + SPACE
                        + actionActivity.getUrgentCount().toString() + SPACE + loader.loadEager(action.getScope().getDisplayProperty()).toLowerCase() + "(s).");
            }
        }

        return "<li>" + Joiner.on("</li><li>").join(bullets) + "</li>";
    }

}
