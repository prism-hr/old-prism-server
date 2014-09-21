package com.zuehlke.pgadmissions.rest.converter;

import org.apache.commons.beanutils.PropertyUtils;
import org.dozer.DozerConverter;

import com.zuehlke.pgadmissions.domain.FinancialDetails;
import com.zuehlke.pgadmissions.domain.definitions.DurationUnit;
import com.zuehlke.pgadmissions.rest.representation.FinancialDetailsRepresentation;

public class FinancialDetailsRepresentationConverter extends DozerConverter<FinancialDetails, FinancialDetailsRepresentation> {

    public FinancialDetailsRepresentationConverter() {
        super(FinancialDetails.class, FinancialDetailsRepresentation.class);
    }

    @Override
    public FinancialDetailsRepresentation convertTo(FinancialDetails source, FinancialDetailsRepresentation destination) {
        if(source == null){
            return null;
        }
        destination = new FinancialDetailsRepresentation();
        DurationUnit interval = source.getInterval();

        destination.setCurrency(source.getCurrencySpecified());
        destination.setInterval(interval);
        if (interval != null) {
            try {
                destination.setMinimum((java.math.BigDecimal) PropertyUtils.getSimpleProperty(source, interval.name().toLowerCase() + "MinimumSpecified"));
                destination.setMaximum((java.math.BigDecimal) PropertyUtils.getSimpleProperty(source, interval.name().toLowerCase() + "MaximumSpecified"));
            } catch (Exception e) {
                throw new Error(e);
            }
        }

        return destination;
    }

    @Override
    public FinancialDetails convertFrom(FinancialDetailsRepresentation source, FinancialDetails destination) {
        throw new UnsupportedOperationException();
    }

}
