package uk.co.alumeni.prism.workflow.notification.property;

import static java.util.stream.Collectors.toList;
import static uk.co.alumeni.prism.PrismConstants.COMMA;
import static uk.co.alumeni.prism.PrismConstants.SPACE;

import org.springframework.stereotype.Component;

import uk.co.alumeni.prism.services.helpers.NotificationPropertyLoader;

import com.google.common.base.Joiner;

@Component
public class ApplicationManagerBuilder implements NotificationPropertyBuilder {

    @Override
    public String build(NotificationPropertyLoader propertyLoader) {
        return Joiner.on(COMMA + SPACE).join(
                propertyLoader.getNotificationDefinitionDTO().getResource().getApplication().getHiringManagers().stream()
                        .map(ahr -> ahr.getUser().getFullName()).collect(toList()));
    }

}
