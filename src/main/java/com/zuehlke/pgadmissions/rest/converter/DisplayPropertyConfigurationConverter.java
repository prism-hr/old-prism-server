package com.zuehlke.pgadmissions.rest.converter;

import com.zuehlke.pgadmissions.domain.display.DisplayPropertyConfiguration;
import com.zuehlke.pgadmissions.rest.dto.DisplayPropertyConfigurationDTO;
import org.dozer.DozerConverter;

public class DisplayPropertyConfigurationConverter extends DozerConverter<DisplayPropertyConfigurationDTO, DisplayPropertyConfiguration> {

    public DisplayPropertyConfigurationConverter() {
        super(DisplayPropertyConfigurationDTO.class, DisplayPropertyConfiguration.class);
    }

    @Override
    public DisplayPropertyConfiguration convertTo(DisplayPropertyConfigurationDTO source, DisplayPropertyConfiguration destination) {
        return new DisplayPropertyConfiguration().withValue(source.getValue());
    }

    @Override
    public DisplayPropertyConfigurationDTO convertFrom(DisplayPropertyConfiguration source, DisplayPropertyConfigurationDTO destination) {
        throw new UnsupportedOperationException();
    }

}
