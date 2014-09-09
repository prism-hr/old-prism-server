package com.zuehlke.pgadmissions.rest.converter;

import org.dozer.DozerConverter;
import org.springframework.beans.factory.annotation.Autowired;

import com.zuehlke.pgadmissions.domain.ProgramType;
import com.zuehlke.pgadmissions.domain.definitions.PrismProgramType;
import com.zuehlke.pgadmissions.services.EntityService;

public class ProgramTypeConverter extends DozerConverter<ProgramType, PrismProgramType> {

    @Autowired
    private EntityService entityService;

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
