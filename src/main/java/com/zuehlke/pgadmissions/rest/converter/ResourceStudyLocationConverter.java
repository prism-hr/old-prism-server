package com.zuehlke.pgadmissions.rest.converter;

import org.dozer.DozerConverter;

import com.zuehlke.pgadmissions.domain.resource.ResourceStudyLocation;

public class ResourceStudyLocationConverter extends DozerConverter<ResourceStudyLocation, String> {

    public ResourceStudyLocationConverter() {
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
