package com.zuehlke.pgadmissions.rest.converter;

import org.dozer.DozerConverter;

import com.zuehlke.pgadmissions.domain.workflow.StateDurationConfiguration;
import com.zuehlke.pgadmissions.rest.dto.StateDurationConfigurationDTO.StateDurationConfigurationValueDTO;

public class StateDurationConfigurationConverter extends DozerConverter<StateDurationConfigurationValueDTO, StateDurationConfiguration> {

    public StateDurationConfigurationConverter() {
        super(StateDurationConfigurationValueDTO.class, StateDurationConfiguration.class);
    }

    @Override
    public StateDurationConfiguration convertTo(StateDurationConfigurationValueDTO source, StateDurationConfiguration destination) {
        return new StateDurationConfiguration().withDuration(source.getDuration());
    }

    @Override
    public StateDurationConfigurationValueDTO convertFrom(StateDurationConfiguration source, StateDurationConfigurationValueDTO destination) {
        throw new UnsupportedOperationException();
    }

}
