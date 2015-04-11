package com.zuehlke.pgadmissions.rest.converter;

import org.dozer.DozerConverter;

import com.zuehlke.pgadmissions.domain.program.ProgramLocation;

public class ProgramLocationConverter extends DozerConverter<ProgramLocation, String> {

    public ProgramLocationConverter() {
        super(ProgramLocation.class, String.class);
    }

    @Override
    public String convertTo(ProgramLocation source, String destination) {
        return source != null ? source.getLocation() : null;
    }

    @Override
    public ProgramLocation convertFrom(String source, ProgramLocation destination) {
        throw new UnsupportedOperationException();
    }

}
