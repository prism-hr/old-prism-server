package uk.co.alumeni.prism.event;

import org.springframework.context.ApplicationEvent;

import uk.co.alumeni.prism.domain.workflow.NotificationDefinition;
import uk.co.alumeni.prism.dto.NotificationDefinitionDTO;

public class NotificationEvent extends ApplicationEvent {

    private static final long serialVersionUID = -6201013453673728033L;

    private NotificationDefinition notificationDefinition;
    
    private NotificationDefinitionDTO notificationDefinitionDTO;

    public NotificationEvent(Object source, NotificationDefinition notificationDefinition, NotificationDefinitionDTO notificationDefinitionDTO) {
        super(source);
        this.notificationDefinition = notificationDefinition;
        this.notificationDefinitionDTO = notificationDefinitionDTO;
    }

    public NotificationDefinition getNotificationDefinition() {
        return notificationDefinition;
    }

    public void setNotificationDefinition(NotificationDefinition notificationDefinition) {
        this.notificationDefinition = notificationDefinition;
    }

    public NotificationDefinitionDTO getNotificationDefinitionDTO() {
        return notificationDefinitionDTO;
    }

    public void setNotificationDefinitionDTO(NotificationDefinitionDTO notificationDefinitionDTO) {
        this.notificationDefinitionDTO = notificationDefinitionDTO;
    }
    
}
