package com.zuehlke.pgadmissions.mapping.helpers;

import org.springframework.stereotype.Component;

import com.zuehlke.pgadmissions.domain.imported.ImportedAgeRange;

@Component
public class ImportedAgeRangeTransformer implements ImportedEntityTransformer<uk.co.alumeni.prism.api.model.imported.ImportedAgeRange, ImportedAgeRange> {

    @Override
    public void transform(uk.co.alumeni.prism.api.model.imported.ImportedAgeRange concreteSource, ImportedAgeRange concreteTarget) {
        concreteTarget.setLowerBound(concreteSource.getLowerBound());
        concreteTarget.setUpperBound(concreteSource.getUpperBound());
    }

}
