package com.zuehlke.pgadmissions.rest.converter;

import org.dozer.DozerConverter;

import com.zuehlke.pgadmissions.domain.definitions.PrismStudyOption;
import com.zuehlke.pgadmissions.domain.resource.ResourceStudyOption;

public class StudyOptionConverter extends DozerConverter<ResourceStudyOption, PrismStudyOption> {

    public StudyOptionConverter() {
        super(ResourceStudyOption.class, PrismStudyOption.class);
    }

    @Override
    public PrismStudyOption convertTo(ResourceStudyOption source, PrismStudyOption destination) {
        return source.getStudyOption().getPrismStudyOption();
    }

    @Override
    public ResourceStudyOption convertFrom(PrismStudyOption source, ResourceStudyOption destination) {
        throw new UnsupportedOperationException();
    }

}
