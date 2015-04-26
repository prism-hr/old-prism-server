package com.zuehlke.pgadmissions.rest.converter;

import org.dozer.DozerConverter;

import com.zuehlke.pgadmissions.domain.program.ResourceStudyLocation;

public class ProgramLocationConverter extends DozerConverter<ResourceStudyLocation, String> {

    public ProgramLocationConverter() {
        super(ResourceStudyLocation.class, String.class);
    }

    @Override
    public String convertTo(ResourceStudyLocation source, String destination) {
        return source != null ? source.getStudyLocation() : null;
    }

    @Override
    public ResourceStudyLocation convertFrom(String source, ResourceStudyLocation destination) {
        throw new UnsupportedOperationException();
    }

}
