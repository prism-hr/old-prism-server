package com.zuehlke.pgadmissions.rest.converter;

import org.dozer.DozerConverter;

import com.zuehlke.pgadmissions.domain.definitions.PrismStudyOption;
import com.zuehlke.pgadmissions.domain.program.ProgramStudyOption;

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
