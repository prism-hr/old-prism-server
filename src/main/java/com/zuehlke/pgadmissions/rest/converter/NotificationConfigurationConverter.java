package com.zuehlke.pgadmissions.rest.converter;

import org.dozer.DozerConverter;

import com.zuehlke.pgadmissions.domain.workflow.NotificationConfiguration;
import com.zuehlke.pgadmissions.rest.dto.NotificationConfigurationDTO;

public class NotificationConfigurationConverter extends DozerConverter<NotificationConfigurationDTO, NotificationConfiguration> {

    public NotificationConfigurationConverter() {
        super(NotificationConfigurationDTO.class, NotificationConfiguration.class);
    }

    @Override
    public NotificationConfiguration convertTo(NotificationConfigurationDTO source, NotificationConfiguration destination) {
        return new NotificationConfiguration().withSubject(source.getSubject()).withContent(source.getContent())
                .withReminderInterval(source.getReminderInterval());
    }

    @Override
    public NotificationConfigurationDTO convertFrom(NotificationConfiguration source, NotificationConfigurationDTO destination) {
        throw new UnsupportedOperationException();
    }

}
