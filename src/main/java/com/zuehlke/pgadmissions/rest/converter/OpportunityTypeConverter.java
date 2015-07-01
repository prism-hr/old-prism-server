package com.zuehlke.pgadmissions.rest.converter;

import org.dozer.DozerConverter;

import com.zuehlke.pgadmissions.domain.definitions.PrismOpportunityType;
import com.zuehlke.pgadmissions.domain.imported.OpportunityType;

public class OpportunityTypeConverter extends DozerConverter<OpportunityType, PrismOpportunityType> {

    public OpportunityTypeConverter() {
        super(OpportunityType.class, PrismOpportunityType.class);
    }

    @Override
    public PrismOpportunityType convertTo(OpportunityType source, PrismOpportunityType destination) {
        return PrismOpportunityType.valueOf(source.getCode());
    }

    @Override
    public OpportunityType convertFrom(PrismOpportunityType source, OpportunityType destination) {
        throw new UnsupportedOperationException();
    }
    
}
