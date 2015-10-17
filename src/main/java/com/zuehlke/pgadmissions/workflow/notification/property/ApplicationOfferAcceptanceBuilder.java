package com.zuehlke.pgadmissions.workflow.notification.property;

import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_NOTIFICATION_APPLICANT_ACCEPTED;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_NOTIFICATION_APPLICANT_DECLINED;
import static org.apache.commons.lang.BooleanUtils.toBoolean;

import org.springframework.stereotype.Component;

import com.zuehlke.pgadmissions.services.helpers.NotificationPropertyLoader;

@Component
public class ApplicationOfferAcceptanceBuilder implements NotificationPropertyBuilder {

    @Override
    public String build(NotificationPropertyLoader propertyLoader) throws Exception {
        return propertyLoader.getPropertyLoader().loadLazy(SYSTEM_NOTIFICATION_APPLICANT_ACCEPTED, SYSTEM_NOTIFICATION_APPLICANT_DECLINED,
                toBoolean(propertyLoader.getNotificationDefinitionModelDTO().getComment().getApplicantAcceptAppointment()));
    }

}
