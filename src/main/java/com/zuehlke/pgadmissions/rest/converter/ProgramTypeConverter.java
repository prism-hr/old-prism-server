package com.zuehlke.pgadmissions.rest.converter;

import org.dozer.DozerConverter;

import com.zuehlke.pgadmissions.domain.definitions.PrismProgramType;
import com.zuehlke.pgadmissions.domain.imported.ProgramType;

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
