package com.zuehlke.pgadmissions.rest.converter;

import org.dozer.DozerConverter;

import com.zuehlke.pgadmissions.domain.definitions.PrismOpportunityType;
import com.zuehlke.pgadmissions.domain.imported.ImportedOpportunityType;

public class OpportunityTypeConverter extends DozerConverter<ImportedOpportunityType, PrismOpportunityType> {

    public OpportunityTypeConverter() {
        super(ImportedOpportunityType.class, PrismOpportunityType.class);
    }

    @Override
    public PrismOpportunityType convertTo(ImportedOpportunityType source, PrismOpportunityType destination) {
        return PrismOpportunityType.valueOf(source.getName());
    }

    @Override
    public ImportedOpportunityType convertFrom(PrismOpportunityType source, ImportedOpportunityType destination) {
        throw new UnsupportedOperationException();
    }

}
