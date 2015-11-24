package uk.co.alumeni.prism.workflow.notification.property;

import static org.apache.commons.lang.BooleanUtils.toBoolean;
import static uk.co.alumeni.prism.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_NOTIFICATION_APPLICANT_ACCEPTED;
import static uk.co.alumeni.prism.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_NOTIFICATION_APPLICANT_DECLINED;

import org.springframework.stereotype.Component;

import uk.co.alumeni.prism.services.helpers.NotificationPropertyLoader;

@Component
public class ApplicationOfferAcceptanceBuilder implements NotificationPropertyBuilder {

    @Override
    public String build(NotificationPropertyLoader propertyLoader) {
        return propertyLoader.getPropertyLoader().loadLazy(SYSTEM_NOTIFICATION_APPLICANT_ACCEPTED, SYSTEM_NOTIFICATION_APPLICANT_DECLINED,
                toBoolean(propertyLoader.getNotificationDefinitionDTO().getComment().getApplicantAcceptAppointment()));
    }

}
