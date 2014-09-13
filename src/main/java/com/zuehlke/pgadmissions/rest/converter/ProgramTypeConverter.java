package com.zuehlke.pgadmissions.rest.converter;

import com.zuehlke.pgadmissions.domain.ProgramType;
import com.zuehlke.pgadmissions.domain.definitions.PrismProgramType;
import org.dozer.DozerConverter;

public class ProgramTypeConverter extends DozerConverter<ProgramType, PrismProgramType> {

    public ProgramTypeConverter() {
        super(ProgramType.class, PrismProgramType.class);
    }

    @Override
    public PrismProgramType convertTo(ProgramType source, PrismProgramType destination) {
        return PrismProgramType.valueOf(source.getCode());
    }

    @Override
    public ProgramType convertFrom(PrismProgramType source, ProgramType destination) {
        throw new UnsupportedOperationException();
    }
}
