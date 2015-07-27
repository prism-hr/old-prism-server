package com.zuehlke.pgadmissions.mapping.helpers;

import org.springframework.stereotype.Component;

import uk.co.alumeni.prism.api.model.imported.request.ImportedAgeRangeRequest;

import com.zuehlke.pgadmissions.domain.imported.ImportedAgeRange;

@Component
public class ImportedAgeRangeTransformer implements ImportedEntityTransformer<ImportedAgeRangeRequest, ImportedAgeRange> {

    @Override
    public void transform(ImportedAgeRangeRequest concreteSource, ImportedAgeRange concreteTarget) {
        concreteTarget.setLowerBound(concreteSource.getLowerBound());
        concreteTarget.setUpperBound(concreteSource.getUpperBound());
    }

}
