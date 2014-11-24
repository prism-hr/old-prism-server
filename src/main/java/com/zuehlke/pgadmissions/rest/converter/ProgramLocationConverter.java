package com.zuehlke.pgadmissions.rest.converter;

import com.zuehlke.pgadmissions.domain.advert.AdvertFinancialDetail;
import com.zuehlke.pgadmissions.domain.definitions.PrismDurationUnit;
import com.zuehlke.pgadmissions.domain.program.ProgramLocation;
import com.zuehlke.pgadmissions.rest.representation.resource.advert.FinancialDetailsRepresentation;
import org.apache.commons.beanutils.PropertyUtils;
import org.dozer.DozerConverter;

import java.math.BigDecimal;

public class ProgramLocationConverter extends DozerConverter<ProgramLocation, String> {

    public ProgramLocationConverter() {
        super(ProgramLocation.class, String.class);
    }

    @Override
    public String convertTo(ProgramLocation source, String destination) {
        return source != null ? source.getLocation() : null;
    }

    @Override
    public ProgramLocation convertFrom(String source, ProgramLocation destination) {
        throw new UnsupportedOperationException();
    }

}
