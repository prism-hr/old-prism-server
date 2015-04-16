package com.zuehlke.pgadmissions.rest.converter;

import org.dozer.DozerConverter;

import com.zuehlke.pgadmissions.domain.definitions.PrismStudyOption;
import com.zuehlke.pgadmissions.domain.program.AdvertStudyOption;

public class StudyOptionConverter extends DozerConverter<AdvertStudyOption, PrismStudyOption> {

    public StudyOptionConverter() {
        super(AdvertStudyOption.class, PrismStudyOption.class);
    }

    @Override
    public PrismStudyOption convertTo(AdvertStudyOption source, PrismStudyOption destination) {
        return source.getStudyOption().getPrismStudyOption();
    }

    @Override
    public AdvertStudyOption convertFrom(PrismStudyOption source, AdvertStudyOption destination) {
        throw new UnsupportedOperationException();
    }

}
