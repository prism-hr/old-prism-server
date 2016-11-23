package uk.co.alumeni.prism.workflow.notification.property;

import com.google.common.base.Joiner;
import org.springframework.stereotype.Component;
import uk.co.alumeni.prism.services.helpers.NotificationPropertyLoader;

import static java.util.stream.Collectors.toList;
import static uk.co.alumeni.prism.PrismConstants.COMMA;
import static uk.co.alumeni.prism.PrismConstants.SPACE;

@Component
public class ApplicationManagerBuilder implements NotificationPropertyBuilder {

    @Override
    public String build(NotificationPropertyLoader propertyLoader) {
        return Joiner.on(COMMA + SPACE).join(
                propertyLoader.getNotificationDefinitionDTO().getResource().getApplication().getHiringManagers().stream()
                        .map(ahr -> ahr.getUser().getFullName()).collect(toList()));
    }

}
