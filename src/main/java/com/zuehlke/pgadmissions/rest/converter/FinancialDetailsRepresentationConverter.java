package com.zuehlke.pgadmissions.rest.converter;

import java.math.BigDecimal;

import org.apache.commons.beanutils.PropertyUtils;
import org.dozer.DozerConverter;

import com.zuehlke.pgadmissions.domain.advert.AdvertFinancialDetail;
import com.zuehlke.pgadmissions.domain.definitions.PrismDurationUnit;
import com.zuehlke.pgadmissions.rest.representation.resource.advert.FinancialDetailsRepresentation;

public class FinancialDetailsRepresentationConverter extends DozerConverter<AdvertFinancialDetail, FinancialDetailsRepresentation> {

    public FinancialDetailsRepresentationConverter() {
        super(AdvertFinancialDetail.class, FinancialDetailsRepresentation.class);
    }

    @Override
    public FinancialDetailsRepresentation convertTo(AdvertFinancialDetail source, FinancialDetailsRepresentation destination) {
        if(source == null) {
            return null;
        }
        destination = new FinancialDetailsRepresentation();
        PrismDurationUnit interval = source.getInterval();

        destination.setCurrency(source.getCurrencySpecified());
        destination.setInterval(interval);
        if (interval != null) {
            try {
                destination.setMinimum((BigDecimal) PropertyUtils.getSimpleProperty(source, interval.name().toLowerCase() + "MinimumSpecified"));
                destination.setMaximum((BigDecimal) PropertyUtils.getSimpleProperty(source, interval.name().toLowerCase() + "MaximumSpecified"));
            } catch (Exception e) {
                throw new Error(e);
            }
        }

        return destination;
    }

    @Override
    public AdvertFinancialDetail convertFrom(FinancialDetailsRepresentation source, AdvertFinancialDetail destination) {
        throw new UnsupportedOperationException();
    }

}
