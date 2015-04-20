package com.zuehlke.pgadmissions.rest.converter;

import org.dozer.DozerConverter;

import com.zuehlke.pgadmissions.domain.definitions.PrismAdvertType;
import com.zuehlke.pgadmissions.domain.imported.AdvertType;

public class AdvertTypeConverter extends DozerConverter<AdvertType, PrismAdvertType> {

    public AdvertTypeConverter() {
        super(AdvertType.class, PrismAdvertType.class);
    }

    @Override
    public PrismAdvertType convertTo(AdvertType source, PrismAdvertType destination) {
        return PrismAdvertType.valueOf(source.getCode());
    }

    @Override
    public AdvertType convertFrom(PrismAdvertType source, AdvertType destination) {
        throw new UnsupportedOperationException();
    }
    
}
