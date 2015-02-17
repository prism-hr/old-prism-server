package com.zuehlke.pgadmissions.rest.converter;

import org.dozer.DozerConverter;

import com.zuehlke.pgadmissions.domain.display.DisplayPropertyConfiguration;
import com.zuehlke.pgadmissions.rest.dto.DisplayPropertyConfigurationDTO.DisplayPropertyConfigurationValueDTO;

public class DisplayPropertyConfigurationConverter extends DozerConverter<DisplayPropertyConfigurationValueDTO, DisplayPropertyConfiguration> {

    public DisplayPropertyConfigurationConverter() {
        super(DisplayPropertyConfigurationValueDTO.class, DisplayPropertyConfiguration.class);
    }

    @Override
    public DisplayPropertyConfiguration convertTo(DisplayPropertyConfigurationValueDTO source, DisplayPropertyConfiguration destination) {
        return new DisplayPropertyConfiguration().withValue(source.getValue());
    }

    @Override
    public DisplayPropertyConfigurationValueDTO convertFrom(DisplayPropertyConfiguration source, DisplayPropertyConfigurationValueDTO destination) {
        throw new UnsupportedOperationException();
    }

}
