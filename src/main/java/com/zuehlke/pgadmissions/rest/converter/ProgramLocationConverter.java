package com.zuehlke.pgadmissions.rest.converter;

import org.dozer.DozerConverter;

import com.zuehlke.pgadmissions.domain.program.ResourceLocation;

public class ProgramLocationConverter extends DozerConverter<ResourceLocation, String> {

    public ProgramLocationConverter() {
        super(ResourceLocation.class, String.class);
    }

    @Override
    public String convertTo(ResourceLocation source, String destination) {
        return source != null ? source.getLocation() : null;
    }

    @Override
    public ResourceLocation convertFrom(String source, ResourceLocation destination) {
        throw new UnsupportedOperationException();
    }

}
