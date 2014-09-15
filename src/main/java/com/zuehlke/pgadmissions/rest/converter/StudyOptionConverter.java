package com.zuehlke.pgadmissions.rest.converter;

import com.zuehlke.pgadmissions.domain.ProgramStudyOption;
import com.zuehlke.pgadmissions.domain.definitions.PrismStudyOption;
import org.dozer.DozerConverter;

public class StudyOptionConverter extends DozerConverter<ProgramStudyOption, PrismStudyOption> {

    public StudyOptionConverter() {
        super(ProgramStudyOption.class, PrismStudyOption.class);
    }

    @Override
    public PrismStudyOption convertTo(ProgramStudyOption source, PrismStudyOption destination) {
        return source.getStudyOption().getPrismStudyOption();
    }

    @Override
    public ProgramStudyOption convertFrom(PrismStudyOption source, ProgramStudyOption destination) {
        throw new UnsupportedOperationException();
    }

}
