package com.zuehlke.pgadmissions.rest.converter;

import org.dozer.DozerConverter;

import com.zuehlke.pgadmissions.domain.advert.AdvertLocation;

public class ProgramLocationConverter extends DozerConverter<AdvertLocation, String> {

    public ProgramLocationConverter() {
        super(AdvertLocation.class, String.class);
    }

    @Override
    public String convertTo(AdvertLocation source, String destination) {
        return source != null ? source.getLocation() : null;
    }

    @Override
    public AdvertLocation convertFrom(String source, AdvertLocation destination) {
        throw new UnsupportedOperationException();
    }

}
